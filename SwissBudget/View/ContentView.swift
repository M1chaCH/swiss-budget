import SwiftUI

struct ContentView: View {
    let dataLoader = DataLoader()
    @State var transactions: [Date: [Transaction]]
    @State var tags: [Tag]
    
    var body: some View {
        TabView() {
            TransactionsView(transactions: $transactions, tags: $tags)
                .tabItem {
                    Label("Transactions", systemImage: "banknote.fill")
                }
            Text("Budget")
                .tabItem {
                    Label("Budget", systemImage: "chart.pie.fill")
                }
            Text("Safe")
                .tabItem {
                    Label("Safe", systemImage: "mail.stack.fill")
                }
            Text("Settings")
                .tabItem() {
                    Label("Settings", systemImage: "gear")
                }
        }
    }
    
    init() {
        transactions = dataLoader.mapDataToDates(dataLoader.transactions)
        tags = dataLoader.tags
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
