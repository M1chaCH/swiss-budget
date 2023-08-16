import SwiftUI

struct HomeView: View {
    let dataLoader = DataLoader()
    @State var transactions: [Date: [Transaction]]
    
    var body: some View {
        TabView() {
            TransactionsView(transactions: $transactions)
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
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
