import Foundation
import MailCore

struct MailTransaction: Identifiable {
    var id: UInt32
    var from: String
    var receivedDate: Date
    var subject: String

    var rawMessage: String?
    var transaction: Transaction?
}

struct Transaction: Identifiable, Hashable { // TODO don't define here, move some other place, somewhere near the DB
    var id: UInt32
    var expense: Bool
    var transactionDate: Date
    var bankAccount: String
    var amount: Double
    var receiver: String
}

protocol TransactionMailParser {
    func isValidTransactionMail(mailHeader: MCOMessageHeader) -> Bool
    func parseHtmlContent(htmlContent: String) -> Transaction
    init(dateFormat: String)
}

class TransactionMailImporter: ObservableObject {
    @Published var transactions: [MailTransaction]
    @Published var startedImport: Bool = false
    
    private let imapServer: String
    private let imapPort: UInt32
    private let mailAddress: String
    private let mailPassword: String
    private let transactionsFolder: String
    private let parser: TransactionMailParser

    init(imapServer: String, imapPort: UInt32, mailAddress: String, mailPassword: String,
         parser: TransactionMailParser, transactionsFolder: String = "INBOX", transactions: [MailTransaction] = []) {
        self.imapServer = imapServer
        self.imapPort = imapPort
        self.mailAddress = mailAddress
        self.mailPassword = mailPassword
        self.transactionsFolder = transactionsFolder
        self.parser = parser
        self.transactions = transactions
    }

    func loadTransactionMails() {
        startedImport = true
        transactions.removeAll(keepingCapacity: true)
        
        let session = MCOIMAPSession()
        session.hostname = imapServer
        session.port = imapPort
        session.username = mailAddress
        session.password = mailPassword
        session.connectionType = .TLS
        
        let uids: MCOIndexSet = MCOIndexSet(range: MCORange(location: 1, length: UInt64.max))
        
        if let fetchOperation = session.fetchMessagesOperation(
            withFolder: self.transactionsFolder, requestKind: .fullHeaders, uids: uids) {
            
            print("fetching mails")
            fetchOperation.start { error, fetchedMessages, vanishedMessages in
                if error != nil {
                    print("failed to fetch mails", error.debugDescription)
                    return
                }
                
                if let messages = fetchedMessages {
                    print("found \(messages.count) mails")
                    
                    for message in messages {
                        if self.parser.isValidTransactionMail(mailHeader: message.header) {
                            print("parsing email (id:\(message.uid))")
                            let transaction: MailTransaction = MailTransaction(
                                id: message.uid, from: message.header.from.mailbox,
                                receivedDate: message.header.receivedDate, subject: message.header.subject )
                            
                            if let parsedMessageOperation = session.fetchParsedMessageOperation(
                                withFolder: self.transactionsFolder, uid: message.uid) {
                                
                                self.loadMessageBody(currentTransaction: transaction, messageOperation: parsedMessageOperation)
                            }
                        } else {
                            print("mail not valid from \(message.header.from.mailbox ?? "undefined")")
                        }
                    }
                    self.startedImport = false
                }
            }
        }
    }
    
    private func loadMessageBody(currentTransaction: MailTransaction, messageOperation: MCOIMAPFetchParsedContentOperation) {
        var transaction = currentTransaction // pls help, why do I have to do this???
        
        messageOperation.start { error, fetchedMessageParser in
            if error != nil {
                print("could not fetch body for transaction: \(transaction.id)")
                return
            }
            
            if let messageParser = fetchedMessageParser {
                let messageBody = messageParser.htmlBodyRendering()
                transaction.rawMessage = messageBody
                transaction.transaction = self.parser.parseHtmlContent(htmlContent: messageBody ?? "")
                transaction.transaction?.id = transaction.id
                self.transactions.append(transaction)
            }
        }
    }
}

struct RaiffeisenMailParser: TransactionMailParser {
    let dateFormatter: DateFormatter
    
    init(dateFormat: String = "dd.MM.yyyy") {
        dateFormatter = DateFormatter()
        dateFormatter.dateFormat = dateFormat
    }
    
    func isValidTransactionMail(mailHeader: MCOMessageHeader) -> Bool {
        if let from = mailHeader.from.mailbox, from == "noreply@ebanking.raiffeisen.ch",
           let subject = mailHeader.subject, subject.starts(with: "Raiffeisen E-Banking") {
            return true
        }
        return false
    }
    
    func parseHtmlContent(htmlContent: String) -> Transaction {
        var expense: Bool = false;
        var transactionDate: Date = Date();
        var bankAccount: String = "";
        var amount: Double = 0.0;
        var receiver: String = "";

        expense = htmlContent.contains("Details der Belastung:")

        var transactionDateQuery: String = " ist am ";
        if expense {
            transactionDateQuery = " wurde am "
        }
        transactionDate = dateFormatter.date(from: substringFromString(original: htmlContent, from: transactionDateQuery, distance: 10)) ?? Date()
        bankAccount = substringFromString(original: htmlContent, from: "<br/><br/>Auf Ihrem ", to: transactionDateQuery)
        amount = Double(substringFromString(original: htmlContent, from: "<br/><br/>Betrag: ", to: " CHF<br/>")) ?? 0.0
        receiver = substringFromString(original: htmlContent, from: "<br/>Buchung:<br/>", to: "<br/><br/>Freundliche ")

        return Transaction(id: 0, expense: expense, transactionDate: transactionDate, bankAccount: bankAccount, amount: amount, receiver: receiver)
    }
}

private func substringFromString(original: String, from: String, distance: Int) -> String {
    if let range = original.range(of: from) {
        let startIndex = original.index(original.startIndex, offsetBy: original.distance(from: original.startIndex, to: range.upperBound))
        let endIndex = original.index(startIndex, offsetBy: distance)
        return String(original[startIndex..<endIndex])
    }
    return "";
}

private func substringFromString(original: String, from: String, to: String) -> String {
    var startIndex = String.Index(utf16Offset: 0, in: original)
    var endIndex = String.Index(utf16Offset: 0, in: original)
    if let range = original.range(of: from) {
        startIndex = original.index(original.startIndex, offsetBy: original.distance(from: original.startIndex, to: range.upperBound))
    }
    if let range = original.range(of: to) {
        endIndex = original.index(original.startIndex, offsetBy: original.distance(from: original.startIndex, to: range.lowerBound))
    }

    return String(original[startIndex..<endIndex])
}
