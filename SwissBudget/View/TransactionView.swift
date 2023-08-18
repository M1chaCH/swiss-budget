import SwiftUI

struct TransactionLinkPreviewView: View {
    let dataLoader = DataLoader()
    @Binding var transaction: Transaction
    
    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea()
            
            VStack(alignment: .leading, spacing: 5) {
                HStack {
                    Image(systemName: transaction.tag.systemIcon)
                        .frame(width: 32, height: 32)
                        .background(transaction.tag.color)
                        .foregroundColor(transaction.tag.calcForegroundColor())
                        .cornerRadius(8)
                    
                    Text(transaction.tag.name)
                        .font(.body)
                    
                    Spacer()
                }
                .padding(.bottom, 12.0)
                
                
                if !transaction.alias.isEmpty {
                    Text(transaction.alias)
                        .font(.headline)
                }
                HStack {
                    if transaction.expense {
                        RoundedRectangle(cornerRadius: 4)
                            .foregroundColor(.appWarn)
                            .frame(width: 8, height: 49, alignment: .leading)
                        
                        Text("- \(String(transaction.amount))")
                            .foregroundColor(.appWarn)
                            .font(.largeTitle)
                    } else {
                        RoundedRectangle(cornerRadius: 4)
                            .foregroundColor(.appPrimary)
                            .frame(width: 8, height: 49, alignment: .leading)
                        
                        Text("+ \(String(transaction.amount))")
                            .foregroundColor(.appForeground)
                            .font(.largeTitle)
                    }
                    
                    Spacer()
                }
                .padding(.bottom, 24.0)
                
                Text(dataLoader.dateToString(transaction.transactionDate, format: "dd. MMM yyyy", useLiterals: true))
                    .font(.caption)
                Text(transaction.receiver)
                Text(transaction.bankAccount)
                    .font(.footnote)
                
                Spacer()
            }
            .padding()
            .scrollContentBackground(.hidden)
        }
    }
}

struct TransactionView: View {
    let dataLoader = DataLoader()
    
    @Binding var transaction: Transaction
    @Binding var tags: [Tag]
    
    @State var editTag: Bool = false
    @State var editAlias: Bool = false
    @State var editNote: Bool = false
    
    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea()
            
            VStack(alignment: .leading, spacing: 5) {
                HStack {
                    Image(systemName: transaction.tag.systemIcon)
                        .frame(width: 32, height: 32)
                        .background(transaction.tag.color)
                        .foregroundColor(transaction.tag.calcForegroundColor())
                        .cornerRadius(8)
                    
                    Text(transaction.tag.name)
                        .font(.body)
                    
                    Spacer()
                }
                .padding(.bottom, 12.0)
                
                
                if !transaction.alias.isEmpty {
                    Text(transaction.alias)
                        .font(.headline)
                }
                HStack {
                    if transaction.expense {
                        RoundedRectangle(cornerRadius: 4)
                            .foregroundColor(.appWarn)
                            .frame(width: 8, height: 49, alignment: .leading)
                        
                        Text("- \(String(transaction.amount))")
                            .foregroundColor(.appWarn)
                            .font(.largeTitle)
                    } else {
                        RoundedRectangle(cornerRadius: 4)
                            .foregroundColor(.appPrimary)
                            .frame(width: 8, height: 49, alignment: .leading)
                        
                        Text("+ \(String(transaction.amount))")
                            .foregroundColor(.appForeground)
                            .font(.largeTitle)
                    }
                    
                    Spacer()
                }
                .padding(.bottom, 24.0)
                
                Text(dataLoader.dateToString(transaction.transactionDate, format: "dd. MMM yyyy", useLiterals: true))
                    .font(.caption)
                Text(transaction.receiver)
                Text(transaction.bankAccount)
                    .font(.footnote)
                
                if !transaction.note.isEmpty {
                    VStack(alignment: .leading) {
                        Text("Note")
                            .font(.caption)
                            .padding([.top, .leading, .trailing], 8.0)
                        Text(transaction.note)
                            .padding([.leading, .bottom, .trailing], 8.0)
                    }
                    .background {
                        Color.appBackground
                            .brightness(-0.05)
                            .cornerRadius(5)
                    }
                    .padding(.top, 20.0)
                }
                
                Spacer()
            }
            .padding(/*@START_MENU_TOKEN@*/.all/*@END_MENU_TOKEN@*/)
        }
        .toolbarRole(.editor)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
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
                } label: {
                    Label("Menu", systemImage: "ellipsis.circle")
                }
            }
        }
        .popover(isPresented: $editAlias) {
            TransactionEditAliasView(transaction: $transaction, open: $editAlias)
                .navigationTitle("change alias")
        }
        .popover(isPresented: $editTag) {
            TransactionEditTagView(transaction: $transaction, tags: $tags, open: $editTag)
        }
        .popover(isPresented: $editNote) {
            TransactionEditNoteView(transaction: $transaction, open: $editNote)
        }
    }
    
}
struct TransactionView_Previews: PreviewProvider {
    static var defaultTransaction = Transaction(id: 13,
                                               expense: true,
                                               transactionDate: Date(),
                                               bankAccount: "YoungMember Privatkonto root-bank",
                                               amount: 99.00,
                                               receiver: """
                               Einkauf TWINT, SBB CFF FFS
                               TWINT Nr. 90656154
                               """,
                                               tag: Tag(id: 0, systemIcon: "questionmark.app", color: .appPrimary, name: "Undefined", keywords: ["*"]),
                                               alias: "SBB GA Night")
    
    static var previews: some View {
        TransactionView(transaction: .constant(defaultTransaction), tags: .constant(DataLoader().tags))
    }
}
