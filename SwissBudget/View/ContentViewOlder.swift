import SwiftUI

struct ContentOlderView: View {
    @StateObject var mailTransactionLoader = TransactionMailImporter(
                    imapServer: "outlook.office365.com",
                    imapPort: 993,
                    mailAddress: "somemail@outlook.com",
                    mailPassword: "password",
                    parser: RaiffeisenMailParser(),
                    transactionsFolder: "folder"
                )

    var body: some View {
        VStack {
            Text("Hello World!")
                .padding(.all, 20.0)
            if mailTransactionLoader.startedImport {
                ProgressView("importing mails...")
            } else if mailTransactionLoader.transactions.count == 0 {
                Text("no mails loaded")
            } else {
                List(mailTransactionLoader.transactions) { transaction in
                    HStack {
                        Text(transaction.from)
                        Spacer()
                        Text(transaction.subject)
                        Spacer()
                        Text(transaction.receivedDate.formatted(.dateTime.day().month(.wide).year()))
                    }
                }.refreshable {
                    mailTransactionLoader.loadTransactionMails()
                }
            }
            
        }.onAppear {
            mailTransactionLoader.loadTransactionMails()
        }
    }
}

struct ContentOlderView_Previews: PreviewProvider {
    static let transactions = [
        MailTransaction(id: 1, from: "test", receivedDate: Date(), subject: "test"),
        MailTransaction(id: 2, from: "test2", receivedDate: Date(), subject: "test2"),
        MailTransaction(id: 3, from: "test3", receivedDate: Date(), subject: "test3"),
        MailTransaction(id: 4, from: "test4", receivedDate: Date(), subject: "test4"),
    ]
    static let transactionsImporter = TransactionMailImporter(imapServer: "", imapPort: 0, mailAddress: "", mailPassword: "", parser: RaiffeisenMailParser(), transactions: transactions)
    
    
    static var previews: some View {
        ContentOlderView(mailTransactionLoader: transactionsImporter)
    }
}
