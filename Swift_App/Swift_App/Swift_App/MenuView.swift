import SwiftUI

struct DashboardView: View {
    @State private var items: [DashboardItem] = []
    @State private var isLoading = false
    
    let scriptUrl = "https://script.google.com/macros/s/AKfycbzOY9i7u072jGU0H5ECJ9Nvaud1lnfZ-L1r2ex63PasYMm_ZLQhiFgtYXvRR7fEaS7Zmw/exec"

    var body: some View {
        VStack {
            Text("Dashboard")
                .font(.largeTitle)
                .bold()
                .padding(.top)
            
            if isLoading {
                ProgressView("Loading...")
                    .padding()
            }
            
             ScrollView {
                VStack(spacing: 16) {
                    ForEach(items) { item in
                        DashboardCard(item: item)
                    }
                }
                .padding()
            }
            
            NavigationLink {
                AddItemView()
            } label: {Button(action: {}) {
                HStack {
                        Image(systemName: "plus")
                        Text("Add Item")
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(Color.white)
                    .cornerRadius(10)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(Color.gray, lineWidth: 1)
                    ).padding()
                }
            }
        }
        .background(Color(.systemGray6))
        .onAppear {
            fetchItems()
        }
    }
    
    func fetchItems() {
        isLoading = true
        let db = Database.database().reference() // For Realtime Database
        // let db = Firestore.firestore() // if Firestore

        db.child("items").observeSingleEvent(of: .value) { snapshot in
            var fetchedItems: [DashboardItem] = []

            for case let child as DataSnapshot in snapshot.children {
                if let dict = child.value as? [String: Any],
                   let name = dict["name"] as? String,
                   let description = dict["description"] as? String,
                   let normal = dict["normal"] as? Int,
                   let damaged = dict["damaged"] as? Int,
                   let missing = dict["missing"] as? Int,
                   let imageBase64 = dict["image"] as? String {
                    
                    let item = DashboardItem(
                        name: name,
                        description: description,
                        normal: normal,
                        damaged: damaged,
                        missing: missing,
                        imageBase64: imageBase64
                    )
                    fetchedItems.append(item)
                }
            }

            DispatchQueue.main.async {
                self.items = fetchedItems
                self.isLoading = false
            }
        }
    }
}

struct DashboardItem: Identifiable {
    let id = UUID()
    var name: String
    var description: String
    var normal: Int
    var damaged: Int
    var missing: Int
    var imageBase64: String

    var total: Int {
        normal + damaged + missing
    }

    var uiImage: UIImage? {
        if let data = Data(base64Encoded: imageBase64),
           let image = UIImage(data: data) {
            return image
        }
        return nil
    }
}

struct DashboardCard: View {
    var item: DashboardItem

    var body: some View {
        HStack {
            if let uiImage = item.uiImage {
                Image(uiImage: uiImage)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 60, height: 60)
                    .clipped()
                    .cornerRadius(8)
                    .padding(.leading, 8)
            } else {
                Rectangle()
                    .fill(Color.brown)
                    .frame(width: 60, height: 60)
                    .cornerRadius(8)
                    .padding(.leading, 8)
                    .overlay(
                        Text("No Image")
                            .foregroundColor(.white)
                            .font(.caption)
                    )
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(item.name)
                    .font(.headline)
                Text("Total: \(item.total)")
                    .font(.subheadline)
                Text(item.description)
                    .font(.subheadline)
                HStack {
                    Text("Normal: \(item.normal)")
                    Text("Damaged: \(item.damaged)")
                    Text("Missing: \(item.missing)")
                }
                .font(.caption)
            }
            .padding(.leading, 8)

            Spacer()

            Image(systemName: "ellipsis")
                .rotationEffect(.degrees(90))
                .padding(.trailing, 8)
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 2)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.blue, lineWidth: 2)
        )
    }
}

#Preview {
    DashboardView()
}
