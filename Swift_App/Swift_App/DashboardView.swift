import SwiftUI
import FirebaseDatabase

struct DashboardItem: Identifiable {
    var id: String
    var name: String
    var description: String
    var normal: Int
    var missing: Int
    var damaged: Int
    var imageBase64: String
}

struct DashboardView: View {
    @State private var showAddItem = false
    @State private var items: [DashboardItem] = []
    @State private var selectedItem: DashboardItem? = nil
    @State private var showConfirmation = false

    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.96, green: 0.95, blue: 0.92)
                    .edgesIgnoringSafeArea(.all)

                VStack(spacing: 0) {
                    HStack {
                        Spacer()
                        Text("Dashboard")
                            .font(.system(size: 60))
                        Spacer()
                        NavigationLink(destination: SettingsView()) {
                            Image(systemName: "gearshape")
                                .font(.title)
                                .foregroundColor(.black)
                                .padding(.trailing, 16)
                        }
                    }
                    .padding(.bottom, 20)

                    VStack(spacing: 20) {
                        VStack(spacing: 16) {
                            ScrollView {
                                if items.isEmpty {
                                    DashboardCard(name: "Test", qty: 2000, description: "Bottle", imageBase64: "")
                                } else {
                                    ForEach(items) { item in
                                        Button {
                                            selectedItem = item
                                            showConfirmation = true
                                        } label: {
                                            DashboardCard(
                                                name: item.name,
                                                qty: item.normal + item.missing + item.damaged,
                                                description: item.description,
                                                imageBase64: item.imageBase64
                                            )
                                        }
                                    }
                                }
                            }
                            .frame(maxHeight: 370)
                        }
                        .padding(.horizontal)

                        Button {
                            showAddItem = true
                        } label: {
                            HStack {
                                Image(systemName: "plus")
                                Text("Add Item")
                            }
                            .font(.headline)
                            .padding(.vertical, 12)
                            .padding(.horizontal, 32)
                            .background(Color(red: 0.84, green: 0.7, blue: 0.5))
                            .foregroundColor(.white)
                            .cornerRadius(20)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color.white, lineWidth: 2)
                            )
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.horizontal)
                        .padding(.bottom, 30)
                    }
                    .padding(.top, 10)
                    .padding(.vertical)
                    .background(Color(red: 0.84, green: 0.7, blue: 0.5))
                    .cornerRadius(20)
                    .padding(.horizontal, 20)

                }

                
                NavigationLink(
                    destination: AddItemView(onComplete: {
                        showAddItem = false
                        fetchItems()
                    }),
                    isActive: $showAddItem,
                    label: {
                        EmptyView()
                    }
                )
            }
            .navigationBarBackButtonHidden(true)
            .onAppear(perform: fetchItems)
            .confirmationDialog(
                "Options for \(selectedItem?.name ?? "Item")",
                isPresented: $showConfirmation,
                titleVisibility: .visible
            ) {
                Button("Delete", role: .destructive) {
                    if let item = selectedItem {
                        deleteItem(item)
                    }
                }
                Button("Cancel", role: .cancel) { }
            }
        }
        .navigationBarBackButtonHidden(true)
    }
    func fetchItems() {
        let db = Database.database().reference().child("items")
        db.observe(.value) { snapshot in
            var newItems: [DashboardItem] = []
            for case let child as DataSnapshot in snapshot.children {
                if let dict = child.value as? [String: Any] {
                    newItems.append(DashboardItem(
                        id: child.key,
                        name: dict["name"] as? String ?? "",
                        description: dict["description"] as? String ?? "",
                        normal: dict["normal"] as? Int ?? 0,
                        missing: dict["missing"] as? Int ?? 0,
                        damaged: dict["damaged"] as? Int ?? 0,
                        imageBase64: dict["image"] as? String ?? ""
                    ))
                }
            }
            items = newItems
        }
    }

    func deleteItem(_ item: DashboardItem) {
        Database.database()
            .reference()
            .child("items")
            .child(item.id)
            .removeValue { error, _ in
                if let error = error {
                    print("Failed to delete:", error.localizedDescription)
                } else {
                    fetchItems()
                }
            }
    }
}

struct DashboardCard: View {
    var name: String
    var qty: Int
    var description: String
    var imageBase64: String

    var body: some View {
        HStack {
            if let data = Data(base64Encoded: imageBase64),
               let uiImage = UIImage(data: data) {
                Image(uiImage: uiImage)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 80, height: 80)
                    .clipped()
                    .cornerRadius(8)
            } else {
                Rectangle()
                    .fill(Color(red: 0.8, green: 0.65, blue: 0.45))
                    .frame(width: 80, height: 80)
                    .overlay(Text("Image").foregroundColor(.black))
                    .cornerRadius(8)
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(name).font(.headline).bold()
                Text("Qty: \(qty)")
                Text(description)
            }
            Spacer()
            Image(systemName: "ellipsis")
                .rotationEffect(.degrees(90))
                .padding(.trailing, 8)
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.blue, lineWidth: 2)
        )
        .shadow(radius: 4)
    }
}

#Preview {
    DashboardView()
}
