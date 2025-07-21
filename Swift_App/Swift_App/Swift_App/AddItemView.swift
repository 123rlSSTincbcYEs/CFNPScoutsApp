import SwiftUI
import FirebaseDatabase

struct AddItemView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var normal = ""
    @State private var missing = ""
    @State private var damaged = ""
    @State private var name = ""
    @State private var description = ""
    @State private var showingPicker = false
    @State private var selectedImage: UIImage? = nil
    @State private var isSubmitting = false

    var onComplete: () -> Void = {}

    var total: Int {
        (Int(normal) ?? 0) + (Int(missing) ?? 0) + (Int(damaged) ?? 0)
    }

    var body: some View {
        VStack(spacing: 16) {
            Text("Add New Item").font(.title2).bold().padding(.top)

            Button(action: { showingPicker = true }) {
                Group {
                    if let img = selectedImage {
                        Image(uiImage: img)
                            .resizable()
                            .scaledToFit()
                    } else {
                        Rectangle()
                            .fill(Color.brown.opacity(0.4))
                            .overlay(
                                Image(systemName: "photo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 40, height: 40)
                                    .foregroundColor(.white.opacity(0.8))
                            )
                    }
                }
                .frame(height: 200)
                .cornerRadius(12)
            }
            .sheet(isPresented: $showingPicker) {
                ImagePicker(image: $selectedImage)
            }

            HStack(spacing: 12) {
                NumberInput(title: "Normal", text: $normal)
                NumberInput(title: "Missing", text: $missing)
                NumberInput(title: "Damaged", text: $damaged)
            }

            Text("Total: \(total)")
                .font(.subheadline)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.vertical, 4)

            Group {
                TextField("Name", text: $name)
                    .padding()
                    .background(Color.brown.opacity(0.2))
                    .cornerRadius(8)
                TextEditor(text: $description)
                    .frame(height: 100)
                    .padding()
                    .background(Color.brown.opacity(0.2))
                    .cornerRadius(8)
            }
            .padding(.horizontal)

            HStack(spacing: 16) {
                Button("Cancel") {
                    dismiss()
                }
                .foregroundColor(.white)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.red)
                .cornerRadius(8)

                Button("Create") {
                    submitItem()
                }
                .foregroundColor(.white)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.green)
                .cornerRadius(8)
                .disabled(name.isEmpty)
            }
            .padding(.horizontal)

            if isSubmitting {
                ProgressView("Submitting...").padding()
            }

            Spacer()
        }
        .navigationBarBackButtonHidden(true)
        .padding(.horizontal)
    }

    func submitItem() {
        isSubmitting = true
        let db = Database.database().reference().child("items").childByAutoId()

        let imgData = selectedImage?.jpegData(compressionQuality: 0.8)
        let imgBase64 = imgData?.base64EncodedString() ?? ""

        let item: [String: Any] = [
            "name": name,
            "description": description,
            "normal": Int(normal) ?? 0,
            "missing": Int(missing) ?? 0,
            "damaged": Int(damaged) ?? 0,
            "image": imgBase64
        ]

        db.setValue(item) { err, _ in
            DispatchQueue.main.async {
                isSubmitting = false
                if let err = err {
                    print("❌ Error submitting item:", err.localizedDescription)
                } else {
                    print("✅ Item submitted successfully.")
                    onComplete()
                    dismiss()
                }
            }
        }
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    func makeCoordinator() -> Coordinator { Coordinator(self) }

    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        var parent: ImagePicker
        init(_ p: ImagePicker) { parent = p }
        func imagePickerController(_ picker: UIImagePickerController,
                                   didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let img = info[.originalImage] as? UIImage {
                parent.image = img
            }
            picker.dismiss(animated: true)
        }
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }

    func makeUIViewController(context: Context) -> some UIViewController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}

struct NumberInput: View {
    var title: String
    @Binding var text: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.subheadline)
            TextField("", text: $text)
                .keyboardType(.numberPad)
                .padding(8)
                .background(Color.white)
                .cornerRadius(4)
                .frame(width: 60)
        }
    }
}

#Preview{
    AddItemView()
}
