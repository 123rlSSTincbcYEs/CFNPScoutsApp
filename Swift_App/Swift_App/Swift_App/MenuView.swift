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
                        DashboardCard(
                            title: item.name,
                            qty: item.total,
                            description: item.description
                        )
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
        guard let url = URL(string: scriptUrl) else { return }
        isLoading = true
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            DispatchQueue.main.async {
                isLoading = false
                if let error = error {
                    print("Error fetching items: \(error.localizedDescription)")
                    return
                }
                
                guard let data = data else {
                    print("No data received")
                    return
                }
                
                do {
                    let decodedItems = try JSONDecoder().decode([DashboardItem].self, from: data)
                    self.items = decodedItems
                } catch {
                    print("Failed to decode JSON: \(error)")
                }
            }
        }.resume()
    }
}

struct DashboardItem: Codable, Identifiable {
    var id: UUID = UUID() // local ID for SwiftUI
    var name: String
    var description: String
    var total: Int

    enum CodingKeys: String, CodingKey {
        case name
        case description
        case total
    }
}

struct DashboardCard: View {
    var title: String
    var qty: Int
    var description: String
    
    var body: some View {
        HStack {
            ZStack {
                Rectangle()
                    .fill(Color.brown)
                    .frame(width: 60, height: 60)
                    .cornerRadius(8)
                    .padding(.leading, 8)
                Text("Image")
                    .foregroundColor(.black)
                    .font(.caption)
            }
            VStack(alignment: .leading) {
                Text(title)
                    .font(.headline)
                Text("Qty: \(qty)")
                    .font(.subheadline)
                Text(description)
                    .font(.subheadline)
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
