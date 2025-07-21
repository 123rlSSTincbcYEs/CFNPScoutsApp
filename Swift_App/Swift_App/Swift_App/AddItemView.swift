import SwiftUI

struct AddItemView: View {
    @State private var normalCount = ""
    @State private var missingCount = ""
    @State private var damagedCount = ""
    @State private var name = ""
    @State private var description = ""
    @State private var isSubmitting = false
    
    let scriptUrl = "https://script.google.com/macros/s/AKfycbzOY9i7u072jGU0H5ECJ9Nvaud1lnfZ-L1r2ex63PasYMm_ZLQhiFgtYXvRR7fEaS7Zmw/exec"

    var total: Int {
        (Int(normalCount) ?? 0) + (Int(missingCount) ?? 0) + (Int(damagedCount) ?? 0)
    }
    
    var body: some View {
        VStack(spacing: 12) {
            Text("Add New Item")
                .font(.headline)
            Rectangle()
                .fill(Color.brown.opacity(0.7))
                .frame(height: 150)
                .overlay(
                    Image(systemName: "photo")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 50, height: 50)
                        .foregroundColor(.gray)
                )
                .cornerRadius(8)
            
            HStack(spacing: 8) {
                VStack {
                    Text("Normal:")
                    TextField("", text: $normalCount)
                        .textFieldStyle(.roundedBorder)
                        .keyboardType(.numberPad)
                }
                VStack {
                    Text("Missing:")
                    TextField("", text: $missingCount)
                        .textFieldStyle(.roundedBorder)
                        .keyboardType(.numberPad)
                }
                VStack {
                    Text("Damaged:")
                    TextField("", text: $damagedCount)
                        .textFieldStyle(.roundedBorder)
                        .keyboardType(.numberPad)
                }
            }
            Text("Total: \(total)")
                .font(.subheadline)
            
            TextField("Name", text: $name)
                .padding()
                .background(Color.brown.opacity(0.3))
                .cornerRadius(5)
            
            TextEditor(text: $description)
                .frame(height: 100)
                .padding()
                .background(Color.brown.opacity(0.3))
                .cornerRadius(5)
            
            HStack(spacing: 20) {
				NavigationLink {
					MenuView()
				} label: {
	                Button("Cancel") {
	                    clearFields()
	                }
				}
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(8)
                
                Button("Create") {
                    submitItem()
                }
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.green)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding(.top, 10)
            
            if isSubmitting {
                ProgressView("Submitting...")
            }
            
            Spacer()
        }
        .padding()
    }
    
    func clearFields() {
        normalCount = ""
        missingCount = ""
        damagedCount = ""
        name = ""
        description = ""
    }
    
    func submitItem() {
        guard let url = URL(string: scriptUrl) else { return }
        isSubmitting = true
        
        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!
        components.queryItems = [
            URLQueryItem(name: "Name", value: name),
            URLQueryItem(name: "Description", value: description),
            URLQueryItem(name: "Normal", value: normalCount),
            URLQueryItem(name: "Missing", value: missingCount),
            URLQueryItem(name: "Damaged", value: damagedCount),
            URLQueryItem(name: "ImageBase64", value: "\(total)")
        ]
        
        var request = URLRequest(url: components.url!)
        request.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: request) { _, response, error in
            DispatchQueue.main.async {
                isSubmitting = false
                if let error = error {
                    print("Failed to submit: \(error.localizedDescription)")
                } else if let httpResponse = response as? HTTPURLResponse {
                    print("Submitted, response code: \(httpResponse.statusCode)")
                    clearFields()
                }
            }
        }.resume()
    }
}

#Preview {
	AddItemView()
}
