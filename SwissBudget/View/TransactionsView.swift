import SwiftUI

struct TransactionsView: View {
    @Binding var transactions: [Date: [Transaction]]
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
                                transactions: self.transactions[key] ?? [])
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
        TransactionsView(transactions: .constant([:]))
    }
}

struct DayTransactionView: View {
    @State var day: String
    @State var transactions: [Transaction]
    
    @State var editTag: Bool = false
    @State var editAlias: Bool = false
    @State var editNote: Bool = false
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(day)
                .font(.headline)
                .padding([.top, .leading])
            
            ForEach($transactions, id: \.self) { t in
                TransactionLinkView(transaction: t)
                    .contextMenu {
                        Button {
                            self.editTag = true
                        } label: {
                            Label("Change Tag", systemImage: "tag.fill")
                        }
                        Button {
                            self.editAlias = true
                        } label: {
                            Label("Change Alias", systemImage: "pencil")
                        }
                        Button {
                            self.editNote = true
                        } label: {
                            Label("Change Note", systemImage: "note.text")
                        }
                    } preview: {
                        TransactionLinkPreviewView(transaction: t)
                    }
                    .popover(isPresented: self.$editAlias) {
                        TransactionEditAliasView(transaction: t, open: self.$editAlias)
                    }
                    .popover(isPresented: self.$editTag) {
                        TransactionEditTagView(transaction: t)
                    }
                    .popover(isPresented: self.$editNote) {
                        TransactionEditNoteView(transaction: t, open: self.$editNote)
                    }
            }
        }
    }
    
    init(day: String, transactions: [Transaction]) {
        self.day = day
        self.transactions = transactions
    }
}

struct TransactionLinkView: View {
    @Binding var transaction: Transaction
    
    var body: some View {
        NavigationLink {
            TransactionView(transaction: $transaction)
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
    }
}
