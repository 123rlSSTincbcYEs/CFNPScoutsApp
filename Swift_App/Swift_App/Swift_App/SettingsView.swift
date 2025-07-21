//
//  SettingsView.swift
//  Swift Ap
//
//  Created by Dessen Tan on 21/7/25.
//

import SwiftUI
import FirebaseAuth

struct SettingsView: View {
    @Environment(\.dismiss) var dismiss
    @State private var displayName: String = ""
    @State private var errorMessage: String? = nil
    @State private var successMessage: String? = nil
    @State private var navigateToLogin = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 40) {
                Spacer()
                
                Text("Settings")
                    .font(.system(size: 36, weight: .bold))
                
                TextField("New Name", text: $displayName)
                    .padding()
                    .background(Color(red: 0.82, green: 0.68, blue: 0.47))
                    .cornerRadius(6)
                    .overlay(RoundedRectangle(cornerRadius: 6).stroke(Color.black, lineWidth: 1.5))
                    .autocapitalization(.none)
                    .padding(.horizontal, 40)
                
                Button("Update Name") {
                    updateDisplayName()
                }
                .font(.headline)
                .padding(.vertical, 16)
                .padding(.horizontal, 32)
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(15)
                
                if let error = errorMessage {
                    Text(error)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                if let success = successMessage {
                    Text(success)
                        .foregroundColor(.green)
                        .font(.caption)
                }
                
                Spacer()
                
                Button(action: logout) {
                    Text("Log Out")
                        .foregroundColor(.white)
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(Color.green)
                        .cornerRadius(15)
                        .padding(.horizontal, 40)
                }

                NavigationLink(destination: LoginView(), isActive: $navigateToLogin) {
                    EmptyView()
                }

                Spacer()
            }
        }
        .navigationBarBackButtonHidden(true)
    }

    func updateDisplayName() {
        errorMessage = nil
        successMessage = nil
        
        guard !displayName.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "Display name cannot be empty."
            return
        }
        
        if let user = Auth.auth().currentUser {
            let changeRequest = user.createProfileChangeRequest()
            changeRequest.displayName = displayName
            changeRequest.commitChanges { error in
                if let error = error {
                    errorMessage = error.localizedDescription
                } else {
                    successMessage = "Display name updated successfully."
                }
            }
        } else {
            errorMessage = "User not logged in."
        }
    }

    func logout() {
        do {
            try Auth.auth().signOut()
            navigateToLogin = true
        } catch {
            errorMessage = "Logout failed: \(error.localizedDescription)"
        }
    }
}

