import SwiftUI

@main
struct SwissBudgetApp: App {
    // let persistenceController = PersistenceController.shared
    
    @AppStorage("setup-complete")
    var setupComplete: Bool = false
    
    var body: some Scene {
        WindowGroup {
            // ContentView()
            //    .environment(\.managedObjectContext, persistenceController.container.viewContext)
            if setupComplete {
                ContentView()
                    .background(Color.appBackground)
            } else {
                WelcomeView()
            }
        }
    }
}

extension Color {
    static let appForeground = Color("ForegroundColor")
    static let appBackground = Color("BackgroundColor")
    static let appPrimary = Color("PrimaryColor")
    static let appAccent = Color("AccentColor")
    static let appWarn = Color("WarnColor")
}
