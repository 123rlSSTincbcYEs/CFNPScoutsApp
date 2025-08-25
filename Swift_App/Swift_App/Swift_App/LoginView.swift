import SwiftUI
import FirebaseAuth

struct LoginView: View {
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var errorMessage: String? = nil
    @State private var isLoggedIn = false
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.96, green: 0.95, blue: 0.92)
                    .ignoresSafeArea()
                
                VStack(spacing: 40) {
                    Spacer()
                    
                    Text("Login")
                        .font(.system(size: 48, weight: .regular))
                        .foregroundColor(.black)
                    
                    VStack(spacing: 20) {
                        TextField("Email", text: $email)
                            .padding()
                            .background(Color(red: 0.82, green: 0.68, blue: 0.47))
                            .cornerRadius(6)
                            .overlay(RoundedRectangle(cornerRadius: 6).stroke(Color.black, lineWidth: 1.5))
                            .autocapitalization(.none)
                            .keyboardType(.emailAddress)
                            .padding(.horizontal, 40)
                        
                        SecureField("Password", text: $password)
                            .padding()
                            .background(Color(red: 0.82, green: 0.68, blue: 0.47))
                            .cornerRadius(6)
                            .overlay(RoundedRectangle(cornerRadius: 6).stroke(Color.black, lineWidth: 1.5))
                            .padding(.horizontal, 40)
                    }
                    
                    if let error = errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.caption)
                            .padding(.horizontal)
                    }
                    
                    Button(action: handleLogin) {
                        Text("Login")
                            .foregroundColor(.white)
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(Color(red: 0.18, green: 0.55, blue: 0.30))
                            .cornerRadius(15)
                    }
                    .padding(.horizontal, 40)
                    
                    NavigationLink(isPresented: $isLoggedIn) {
                        MenuView()
                    }
                    .navigationBarBackButtonHidden(true)
                    Spacer()
                }
            }
            .navigationBarHidden(true)
            .navigationBarBackButtonHidden(true)
        }
        .navigationBarBackButtonHidden(true)
    }
    
    func handleLogin() {
        errorMessage = nil
        
        guard !email.isEmpty, !password.isEmpty else {
            errorMessage = "Please enter both email and password."
            return
        }
        
        Auth.auth().signIn(withEmail: email, password: password) { result, error in
            if let error = error {
                errorMessage = error.localizedDescription
            } else {
                errorMessage = nil
                isLoggedIn = true
            }
        }
    }
}

#Preview {
    LoginView()
}
