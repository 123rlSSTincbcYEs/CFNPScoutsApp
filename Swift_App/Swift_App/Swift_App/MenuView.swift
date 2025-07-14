import SwiftUI

struct DashboardView: View {
    var body: some View {
        VStack {
            Text("Dashboard")
                .font(.largeTitle)
                .bold()
                .padding(.top)
            
            ScrollView {
                VStack(spacing: 16) {
                    DashboardCard(title: "suufned", qty: 0, description: "udidhr9d")
                    DashboardCard(title: "isaac", qty: 0, description: "example")
                    DashboardCard(title: "isaac", qty: 0, description: "isaac")
                }
                .padding()
            }
            
            Button(action: {
                // TODO: Add action
            }) {
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
                )
                .padding()
            }
        }
        .background(Color(.systemGray6))
    }
}

struct DashboardCard: View {
    var title: String
    var qty: Int
    var description: String
    var body: some View {
        HStack {
            ZStack{
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
