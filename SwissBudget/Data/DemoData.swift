import Foundation
import SwiftUI

struct Transaction: Identifiable { // TODO don't define here, move some other place, somewhere near the DB
    var id: UInt32
    var expense: Bool
    var transactionDate: Date
    var bankAccount: String
    var amount: Double
    var receiver: String
    var tag: Tag
    var matchingTagKeyword: String = ""
    var alias: String = ""
    var note: String = ""
}

struct Tag: Identifiable, Equatable, Hashable {
    var id: UInt32
    var systemIcon: String
    var color: Color
    var name: String
    var keywords: [String]
    
    public func calcForegroundColor() -> Color {
        if color.isDarkColor {
            return .white
        }
        return .black
    }
    
    static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.id == rhs.id
    }
}

extension UIColor
{
    var isDarkColor: Bool {
        var r, g, b, a: CGFloat
        (r, g, b, a) = (0, 0, 0, 0)
        self.getRed(&r, green: &g, blue: &b, alpha: &a)
        let lum = 0.2126 * r + 0.7152 * g + 0.0722 * b
        return  lum < 0.50
    }
}

extension Color{
    var isDarkColor : Bool {
        return UIColor(self).isDarkColor
    }
}

class DataLoader {
    public static let defaultTag: Tag = Tag(id: 0, systemIcon: "questionmark.app", color: .appPrimary, name: "Undefined", keywords: ["*"])
    
    let dateFormatter = DateFormatter()
    open var transactions: [Transaction] = []
    
    open var tags: [Tag] = [
        DataLoader.defaultTag,
        Tag(id: 1, systemIcon: "car", color: .appPrimary, name: "Travel", keywords: ["sbb", "transport", "gondelbahn", "bahn", "shell", "aral"]),
        Tag(id: 2, systemIcon: "wineglass", color: .appPrimary, name: "Restaurant", keywords: ["subway", "andy's place", "pizzeria", "restaurant", "mcdonalds", "burger king", "pizza", "café", "cafe", "take away", "diner", "sushi", "gelateria"]),
        Tag(id: 3, systemIcon: "cart", color: .appPrimary, name: "Groceries", keywords: ["coop", "migros", "landi", "lidl", "migrolino", "supermarkt", "aldi", "denner", "spar ", "bäckerei", "müller"]),
        Tag(id: 4, systemIcon: "tshirt", color: .appPrimary, name: "Clothes", keywords: ["zalando", "nike", "addidas", "puma", "h&m", "zara", "c&a", "manor", "globus", "vögele", "fashion", "decathlon", "sportx", "gucci", "prada", "louis vitton", "broki", "ochsner"]),
        Tag(id: 5, systemIcon: "figure.dance", color: .appPrimary, name: "Hobby", keywords: ["digitec", "galaxus", "interdiscount", "media", "decatlon", "bike", "kino", "coiffeur", "amazon", "aliexpress", "apple", "ebay", "museum", "google", "domain", "buch"]),
        Tag(id: 6, systemIcon: "infinity", color: .appPrimary, name: "Subscriptions", keywords: ["sky", "netflix", "disney", "swisscom", "sunrise", "salt", "spotify", "prime", "hulu"]),
        Tag(id: 7, systemIcon: "heart", color: .appPrimary, name: "Health", keywords: ["apotheke", "arzt", "krankenhaus", "medizin", "spital", "drogerie", "pharmacie", "klinik", "physio", "zahn", "auge", "brille", "fielman", "visilab"]),
        Tag(id: 8, systemIcon: "water.waves", color: .appPrimary, name: "Vacation", keywords: [ ]),
    ]
    
    init() {
        dateFormatter.dateFormat = "dd.MM.yyyy"
        loadTransactions()
    }
    
    func loadTransactions() {
        transactions = [
            Transaction(id: 0, expense: true, transactionDate: dateFormatter.date(from: "10.08.2023") ?? Date(), bankAccount: "money-bag", amount: 16.3, receiver: """
                        Einkauf SUBWAY
                        10.08.2023, 18:38, Visa Debit-Nr. 1234xxxxxx4321
                        Keine
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 1, expense: true, transactionDate: dateFormatter.date(from: "10.08.2023") ?? Date(), bankAccount: "money-bag", amount: 84, receiver: """
                        Einkauf MTB Shop
                        09.08.2023, 11:52, Visa Debit-Nr. 1234xxxxxx4321
                        Keine
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 2, expense: true, transactionDate: dateFormatter.date(from: "01.08.2023") ?? Date(), bankAccount: "money-bag", amount: 0.92, receiver: """
                        Online Einkauf Google Domains
                        01.08.2023, 12:54, Visa Debit-Nr. 1234xxxxxx4321
                        Keine
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 3, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 12, receiver: """
                        Online Einkauf Google Domains
                        31.07.2023, 16:20, Visa Debit-Nr. 1234xxxxxx4321
                        Keine
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 4, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 21, receiver: """
                        E-Banking Auftrag an Digitec Galaxus AG

                        - Pfingstweidstrasse 60b, 8005 Zürich CH
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 5, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 130.0, receiver: """
                        E-Banking Dauerauftrag an someone

                        - 4601 Olten CH
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 7, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 110.0, receiver: """
                        E-Banking Dauerauftrag an Some Dude

                        - 4800 Zofingen, Rent (;
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 6, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 600.0, receiver: """
                        E-Banking Auftrag (Kontoübertrag)
                        Micha Schweizer
                        YoungMember Sparkonto IBAN HERE

                        - Micha Schweizer,
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 8, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 19.95, receiver: """
                        E-Banking Auftrag (eBill)
                        Wingo
                        
                        - Contact Center, 3050 Bern CH, Bezahlt für Some name
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 9, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 14.00, receiver: """
                        Einkauf TWINT, SBB MOBILE
                        TWINT Nr. 90656154
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 10, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 15.00, receiver: """
                        Überweisung TWINT an , JOSHI
                        TWINT Nr. 90656154
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 11, expense: true, transactionDate: dateFormatter.date(from: "31.07.2023") ?? Date(), bankAccount: "money-bag", amount: 29.00, receiver: """
                        Einkauf GONDELBAHN
                        29.07.2023, 10:38, Visa Debit-Nr. 1234xxxxxx4321
                        Keine
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 12, expense: false, transactionDate: dateFormatter.date(from: "28.07.2023") ?? Date(), bankAccount: "money-bag", amount: 53.00, receiver: """
                        Gutschrift TWINT von Some Name

                        - SIX PAYMENT SERVICES AG
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 13, expense: true, transactionDate: dateFormatter.date(from: "28.07.2023") ?? Date(), bankAccount: "money-bag", amount: 99.00, receiver: """
                        Einkauf TWINT, SBB CFF FFS
                        TWINT Nr. 90656154
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 14, expense: true, transactionDate: dateFormatter.date(from: "26.07.2023") ?? Date(), bankAccount: "money-bag", amount: 135.00, receiver: """
                        E-Banking Auftrag an someone

                        - Address to Cool people
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 15, expense: true, transactionDate: dateFormatter.date(from: "26.07.2023") ?? Date(), bankAccount: "money-bag", amount: 23.15, receiver: """
                        E-Banking Auftrag (eBill)
                        Schweizerische Mobiliar

                        - Bundesgasse35, 3001 Bern CH, Bezahlt für Micha Schweizer
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 16, expense: false, transactionDate: dateFormatter.date(from: "25.07.2023") ?? Date(), bankAccount: "money-bag", amount: 1110.15, receiver: """
                        Gutschrift Company

                        - Company Address, SALAERZAHLUNG
                        """, tag: DataLoader.defaultTag),
            Transaction(id: 17, expense: true, transactionDate: dateFormatter.date(from: "15.08.2023") ?? Date(), bankAccount: "money-bag", amount: 20.9, receiver: """
                        Online Einkauf SKY
                        13.08.2023, 08:39, Visa Debit-Nr. 1234xxxxxx4321
                        Keine
                        """, tag: DataLoader.defaultTag),
        ]
        
        mapTags(transactions: &transactions)
    }
    
    open func dateToString(_ date: Date, format: String = "dd.MM.yyyy", useLiterals: Bool = false) -> String {
        if useLiterals {
            let calendar = Calendar.current
            
            if calendar.isDateInToday(date) {
                return "Today"
            } else if calendar.isDateInYesterday(date) {
                return "Yesterday"
            } else if calendar.isDateInTomorrow(date) {
                return "Tomorrow"
            }                
        }
        
        let oldFormat = dateFormatter.dateFormat
        defer { dateFormatter.dateFormat = oldFormat }
        
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: date)
    }
    
    open func stringToDate(_ string: String) -> Date? {
        return dateFormatter.date(from: string)
    }
    
    func mapDataToDates(_ toMap: [Transaction]) -> [Date: [Transaction]] {
        let sorted = toMap.sorted(by: { $0.transactionDate < $1.transactionDate })
        if sorted.isEmpty{
            return [:]
        }
        
        var mapped: [Date: [Transaction]] = [:]
        for t in sorted {
            var currentTransactions: [Transaction] = mapped[t.transactionDate] ?? []
            currentTransactions.append(t)
            mapped[t.transactionDate] = currentTransactions
        }
        
        return mapped
    }
    
    open func mapTags(transactions: inout [Transaction]) {
        for (i, _) in transactions.enumerated() {
            var found = false
            for tag in tags {
                if let keyword = doesKeyworkMatch(transaction: transactions[i], keywords: tag.keywords) {
                    transactions[i].tag = tag
                    transactions[i].matchingTagKeyword = keyword
                    found = true
                    break
                }
            }
            
            if !found {
                transactions[i].tag = DataLoader.defaultTag
            }
        }
    }
    
    private func doesKeyworkMatch(transaction: Transaction, keywords: [String]) -> String? {
        for keyword in keywords {
            if transaction.receiver.lowercased().contains(keyword) {
                return keyword
            }
        }
        return nil
    }
}

