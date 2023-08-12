import SwiftUI

struct WelcomeView: View {
    @AppStorage("setup-complete")
    var setupComplete: Bool = false
    @AppStorage("demo-account")
    var demoAccount: Bool = false
    
    @State var setupOpen: Bool = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 5) {
            Spacer()
            Spacer()
            
            Text("Welcome 🙋‍♂️")
                .font(.custom("Oswald", size: 42))
                .fontWeight(.bold)
                .foregroundColor(.appForeground)
                .padding(.leading, 20)
            Text("Get started with your budget manager.")
                .foregroundColor(.appForeground)
                .padding(.leading, 20)
            
            Spacer()
            Spacer()
            
            VStack(alignment: .center, spacing: 10) {
                Button("Demo Account") {
                    demoAccount = true
                    setupComplete = true
                }
                .frame(width: 250, height: 50, alignment: .center)
                .background(Color.appPrimary)
                .foregroundColor(.appForeground)
                .fontWeight(.semibold)
                .cornerRadius(25)
                .opacity(80 / 100)
                
                Button("Setup") {
                    setupOpen = true
                }
                .frame(width: 250, height: 50, alignment: .center)
                .background(Color.appAccent)
                .foregroundColor(.appForeground)
                .fontWeight(.semibold)
                .cornerRadius(25)
                .popover(isPresented: $setupOpen) {
                    Text("setup not yet implemented")
                }
            }
            .frame(maxWidth: .infinity)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(
            Image("WelcomeBackround")
                .resizable()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        ).edgesIgnoringSafeArea(.all)
    }
}

struct WelcomeView_Previews: PreviewProvider {
    static var previews: some View {
        WelcomeView()
    }
}
