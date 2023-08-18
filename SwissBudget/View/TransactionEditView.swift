import SwiftUI

struct TransactionEditTagView: View {
    @Binding var transaction: Transaction
    @Binding var tags: [Tag]
    @Binding var open: Bool
    @State var selectedTagId: UInt32 = DataLoader.defaultTag.id
    @State var changed: Bool = false
    @State var changeForAll: Bool = false
    
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
                    Text("Change Tag")
                        .font(.headline)
                    Spacer()
                    Button {
                        // transaction.tag = currentTag TODO
                        open = false
                    } label: {
                        Text("Save")
                    }
                    .disabled(!changed)
                }
                
                Form {
                    Picker("Tag", selection: $selectedTagId) {
                        ForEach(tags, id: \.id) { tag in
                            Label(tag.name, systemImage: tag.systemIcon)
                                .tag(tag.id)
                        }
                    }
                    .onChange(of: selectedTagId) { newValue in
                        changed = newValue != transaction.tag.id
                    }
                    Button("Create Tag") {
                        
                    }
                    if changed && !transaction.matchingTagKeyword.isEmpty && selectedTagId != DataLoader.defaultTag.id {
                        Toggle(isOn: $changeForAll) {
                            Text("Change for **\(transaction.matchingTagKeyword)**")
                        }
                    }
                    if selectedTagId != DataLoader.defaultTag.id && !changed {
                        Text("Current tag was applied due to the keyword **\(transaction.matchingTagKeyword)**.")
                            .font(.caption)
                    }
                }
                
                if transaction.tag.id == DataLoader.defaultTag.id {
                    Text("""
                        Tags group transactions. You can create a budget for a tag.
                        The app uses keywords to automatically add a transaction to a group. (You can edit these keywords in the settings.) In this case no kown keyword was found related with any tag. You can now either create a new tag with keywords, add a keyword to an existing tag in the settings or just manually assign a tag.
                        """)
                        .font(.footnote)
                        .frame(alignment: .leading)
                        .padding(.top)
                } else {
                    Text("""
                        Tags group transactions. You can create a budget for a tag.
                        The app uses keywords to automatically add a transaction to a group. (You can edit these keywords in the settings.) In this case the keyword **\(transaction.matchingTagKeyword)** was found related with the tag **\(transaction.tag.name)**. You can now choose if you want to change the tag just for this transaction or if you want to change it for all transactions with the keyword **\(transaction.matchingTagKeyword)**.
                        """)
                        .font(.footnote)
                        .frame(alignment: .leading)
                        .padding(.top)
                }
                
                Spacer()
            }
            .padding()
        }.onAppear {
            selectedTagId = transaction.tag.id
        }
    }
}

struct TransactionEditAliasView: View {
    @Binding var transaction: Transaction
    @Binding var open: Bool
    @State var currentAlias: String = ""
    @State var changed: Bool = false
    
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
                    .disabled(!changed)
                }
                
                Form {
                    TextField("Alias", text: $currentAlias)
                        .focused($focusedField, equals: "alias")
                        .lineLimit(1)
                        .onChange(of: currentAlias) { newValue in
                            changed = newValue != transaction.alias
                        }
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
    @State var changed: Bool = false
    
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
                    .disabled(!changed)
                }
                
                Form {
                    Text("Note")
                    TextEditor(text: $currentNote)
                        .focused($focusedField, equals: "note")
                        .lineLimit(5)
                        .onChange(of: currentNote) { newValue in
                            changed = newValue != transaction.note
                        }
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
        TransactionEditTagView(transaction:
                .constant(Transaction(id: 13,
                                            expense: true,
                                            transactionDate: Date(),
                                            bankAccount: "money-bag",
                                            amount: 99.00,
                                            receiver: """
                            Einkauf TWINT, SBB CFF FFS
                            TWINT
                            """,
                                            tag: Tag(id: 1, systemIcon: "car", color: .appPrimary, name: "Travel", keywords: ["sbb", "transport", "gondelbahn", "bahn", "shell", "aral"]),
                                            matchingTagKeyword: "sbb",
                                      alias: "SBB GA Night")), tags: .constant(DataLoader().tags), open: .constant(true))
    }
}
