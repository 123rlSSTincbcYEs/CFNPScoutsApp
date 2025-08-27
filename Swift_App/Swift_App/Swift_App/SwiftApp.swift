import SwiftUI
import FirebaseCore

@main
struct SwiftApp: App {
    init() {FirebaseApp.configure()}
    var body: some Scene {
        WindowGroup {
            LoginView()
        }
    }
}
