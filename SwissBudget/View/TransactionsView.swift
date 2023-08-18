import SwiftUI

struct TransactionsView: View {
    @Binding var transactions: [Date: [Transaction]]
    @Binding var tags: [Tag]
    @State var sortedTransactionKeys: [Date] = []
    
    let dataLoader = DataLoader()
    
    var body: some View {
        NavigationStack {
            ZStack() {
                Color.appBackground.ignoresSafeArea()
                
                ScrollView() {
                    VStack(alignment: .leading, spacing: 0.0) {
                        ForEach(sortedTransactionKeys, id: \.self) { key in
                            DayTransactionView(
                                day: dataLoader.dateToString(key, format: "dd. MMM yyyy", useLiterals: true),
                                transactions: self.transactions[key] ?? [], tags: tags)
                            .padding()
                        }
                    }
                }
                .scrollContentBackground(.hidden)
            }
            .navigationTitle("Transactions")
            .navigationBarTitleDisplayMode(.large)
        }
        .scrollContentBackground(.hidden)
        .onAppear {
            sortedTransactionKeys = Array(transactions.keys).sorted(by: {$0 > $1})
        }
    }
}

struct TransactionsView_Previews: PreviewProvider {
    static var previews: some View {
        TransactionsView(transactions: .constant([:]), tags: .constant(DataLoader().tags))
    }
}

struct DayTransactionView: View {
    @State var day: String
    @State var transactions: [Transaction]
    @State var tags: [Tag]
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(day)
                .font(.headline)
                .padding([.top, .leading])
            
            ForEach($transactions, id: \.self.id) { t in
                TransactionLinkView(transaction: t, tags: $tags)
            }
        }
    }
    
    init(day: String, transactions: [Transaction], tags: [Tag]) {
        self.day = day
        self.transactions = transactions
        self.tags = tags
    }
}

struct TransactionLinkView: View {
    @Binding var transaction: Transaction
    @Binding var tags: [Tag]
    
    @State var editTag: Bool = false
    @State var editAlias: Bool = false
    @State var editNote: Bool = false
    
    var body: some View {
        NavigationLink {
            TransactionView(transaction: $transaction, tags: $tags)
                .navigationTitle("Transaction")
                .navigationBarTitleDisplayMode(.inline)
        } label: {
            HStack(spacing: 5.0) {
                if transaction.expense {
                    RoundedRectangle(cornerRadius: 5)
                        .foregroundColor(.appWarn)
                        .frame(width: 10, height: 40, alignment: .leading)
                    
                    Text("- \(String(transaction.amount))")
                        .foregroundColor(.appWarn)
                } else {
                    RoundedRectangle(cornerRadius: 5)
                        .foregroundColor(.appPrimary)
                        .frame(width: 10, height: 40, alignment: .leading)
                    
                    Text("+ \(String(transaction.amount))")
                        .foregroundColor(.appForeground)
                }
                Spacer()
                
                if transaction.alias.isEmpty {
                    Text(transaction.receiver)
                        .foregroundColor(.appForeground)
                        .lineLimit(2)
                        .font(.caption)
                        .frame(alignment: .leading)
                } else {
                    Text(transaction.alias)
                        .foregroundColor(.appForeground)
                        .lineLimit(2)
                        .font(.caption)
                        .frame(alignment: .leading)
                }
                
                Spacer()
                
                Image(systemName: transaction.tag.systemIcon)
                    .frame(width: 30, height: 30, alignment: .center)
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(transaction.tag.calcForegroundColor())
                    .background(transaction.tag.color)
                    .cornerRadius(5)
            }
        }
        .padding(5)
        .frame(height: 50)
        .contextMenu {
            Button {
                editTag = true
            } label: {
                Label("Change Tag", systemImage: "tag.fill")
            }
            Button {
                editAlias = true
            } label: {
                Label("Change Alias", systemImage: "pencil")
            }
            Button {
                editNote = true
            } label: {
                Label("Change Note", systemImage: "note.text")
            }
        } preview: {
            TransactionLinkPreviewView(transaction: $transaction)
        }
        .popover(isPresented: self.$editAlias) {
            TransactionEditAliasView(transaction: $transaction, open: $editAlias)
        }
        .popover(isPresented: self.$editTag) {
            TransactionEditTagView(transaction: $transaction, tags: $tags, open: $editTag)
        }
        .popover(isPresented: self.$editNote) {
            TransactionEditNoteView(transaction: $transaction, open: $editNote)
        }
    }
}
