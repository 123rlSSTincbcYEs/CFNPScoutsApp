import SwiftUI
import Firebase
import FirebaseDatabase

struct AddItemView: View {
    @State private var normalCount = ""
    @State private var missingCount = ""
    @State private var damagedCount = ""
    @State private var name = ""
    @State private var description = ""
    @State private var isSubmitting = false
    @State private var selectedImage: UIImage? = nil

    var body: some View {
        VStack(spacing: 12) {
            Text("Add New Item")
                .font(.headline)

            Button(action: {
            }) {
                if let img = selectedImage {
                    Image(uiImage: img)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 150)
                        .cornerRadius(8)
                } else {
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
                }
            }
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
                Button("Cancel") {
                    clearFields()
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

    var total: Int {
        (Int(normalCount) ?? 0) + (Int(missingCount) ?? 0) + (Int(damagedCount) ?? 0)
    }

    func clearFields() {
        normalCount = ""
        missingCount = ""
        damagedCount = ""
        name = ""
        description = ""
        selectedImage = nil
    }

    func submitItem() {
        guard !name.isEmpty else { return }

        isSubmitting = true
        let db = Database.database().reference()
        let itemRef = db.child("items").childByAutoId()

        let normal = Int(normalCount) ?? 0
        let missing = Int(missingCount) ?? 0
        let damaged = Int(damagedCount) ?? 0

        let imageBase64: String
        if let img = selectedImage,
           let data = img.jpegData(compressionQuality: 0.8) {
            imageBase64 = data.base64EncodedString()
        } else {
            imageBase64 = ""
        }

        let itemData: [String: Any] = [
            "name": name,
            "description": description,
            "normal": normal,
            "missing": missing,
            "damaged": damaged,
            "image": imageBase64
        ]

        itemRef.setValue(itemData) { error, _ in
            DispatchQueue.main.async {
                isSubmitting = false
                if let error = error {
                    print("Failed to add item: \(error.localizedDescription)")
                } else {
                    print("Item added successfully")
                    clearFields()
                }
            }
        }
    }
}
