import SwiftUI

struct TransactionEditTagView: View {
    @Binding var transaction: Transaction
    
    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea()
            
            Text("Edit Tag")
        }
    }
}

struct TransactionEditAliasView: View {
    @Binding var transaction: Transaction
    @Binding var open: Bool
    @State var currentAlias: String = ""
    
    @FocusState var focusedField: String?
    
    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea()
            
            VStack(alignment: .leading, spacing: 5.0) {
                HStack {
                    Button {
                        open = false
                    } label: {
                        Text("Cancel")
                    }
                    Spacer()
                    Text("Change Alias")
                        .font(.headline)
                    Spacer()
                    Button {
                        transaction.alias = currentAlias
                        open = false
                    } label: {
                        Text("Save")
                    }
                }
                
                Form {
                    TextField("Alias", text: $currentAlias)
                        .focused($focusedField, equals: "alias")
                        .lineLimit(1)
                }
                
                Text("""
                    An alias lets you customize how a transaction is referred to.
                    'Your name for a transaction.'
                    Of course you can also search for an alias.
                    """)
                    .font(.footnote)
                
                
                Spacer()
            }
            .padding()
        }.onAppear {
            currentAlias = transaction.alias
            focusedField = "alias"
        }
    }
}

struct TransactionEditNoteView: View {
    @Binding var transaction: Transaction
    @Binding var open: Bool
    @State var currentNote: String = ""
    
    @FocusState var focusedField: String?
    
    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea()
            
            VStack(alignment: .leading, spacing: 5.0) {
                HStack {
                    Button {
                        open = false
                    } label: {
                        Text("Cancel")
                    }
                    Spacer()
                    Text("Change Note")
                        .font(.headline)
                    Spacer()
                    Button {
                        transaction.note = currentNote
                        open = false
                    } label: {
                        Text("Save")
                    }
                }
                
                Form {
                    Text("Note")
                    TextEditor(text: $currentNote)
                        .focused($focusedField, equals: "note")
                        .lineLimit(5)
                }
                
                Text("""
                    Add notes to your transaction to let the future you know about an important detail regarding this transaction.
                    """)
                .font(.footnote)
                
                
                Spacer()
            }
            .padding()
        }.onAppear {
            currentNote = transaction.note
            focusedField = "note"
        }
    }
}

struct TransactionEditView_Previews: PreviewProvider {
    static var previews: some View {
        TransactionEditAliasView(transaction:
                .constant(Transaction(id: 13,
                                            expense: true,
                                            transactionDate: Date(),
                                            bankAccount: "money-bag",
                                            amount: 99.00,
                                            receiver: """
                            Einkauf TWINT, SBB CFF FFS
                            TWINT
                            """,
                                            tag: Tag(id: 0, systemIcon: "questionmark.app", color: .appPrimary, name: "Undefined", keywords: ["*"]),
                                            alias: "SBB GA Night")), open: .constant(true))
    }
}
