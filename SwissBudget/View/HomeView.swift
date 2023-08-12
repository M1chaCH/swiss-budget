import SwiftUI

struct HomeView: View {
    var body: some View {
        TabView() {
            TransactionsView()
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
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
