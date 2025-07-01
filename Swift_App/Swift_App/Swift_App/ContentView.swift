import SwiftUI
struct ContentView: View {
    @State private var correctEmail = "goh_rui_jie_thaddeus@s2024.ssts.edu.sg"
    @State private var email = ""
    @State private var password = ""
    @State private var correctPassword = "iloveswift"
    @State private var selection:String? = nil
    var body: some View {
        NavigationStack{
            VStack {
                Text("Login").font(.title)
                TextField("Email", text: self.$email)
                TextField("Password", text: self.$password)
                Button("Sign in")
                {
                    if (email == correctEmail) && (password == correctPassword) {selection = "A"}
                }
                NavigationLink(destination: MenuView(), tag: "A", selection: $selection) {}
            }
            .padding()
        }
    }
}
#Preview {
    ContentView()
}
