import SwiftUI

struct LoginView: View {
    @State private var username: String = ""
    @State private var password: String = ""
    @State private var errorMessage: String? = nil
    @State private var isLoggedIn = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Text("Login")
                    .font(.largeTitle)
                    .bold()
                
                TextField("Username", text: $username)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                    .padding(.horizontal)
                
                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding(.horizontal)
                
                if let error = errorMessage {
                    Text(error)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                Button(action: handleLogin) {
                    Text("Login")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding(.horizontal)
                NavigationLink(
                    destination: MenuView(),
                    isActive: $isLoggedIn
                ) {
                    EmptyView()
                }
                
                Spacer()
            }
            .padding()
            .navigationBarHidden(true)
        }
    }
    
    func handleLogin() {
        if username.isEmpty || password.isEmpty {
            errorMessage = "Please enter both username and password."
            return
        }
        
        if username == "admin" && password == "password" {
            errorMessage = nil
            isLoggedIn = true
        } else {
            errorMessage = "Invalid username or password."
        }
    }
}


struct ContentView: View {
    var body: some View {
        LoginView()
    }
}
#Preview {
    ContentView()
}
