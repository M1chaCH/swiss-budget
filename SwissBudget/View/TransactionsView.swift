//
//  TransactionsView.swift
//  SwissBudget
//
//  Created by Micha Schweizer on 12.08.23.
//

import SwiftUI

struct TransactionsView: View {
    let transactions: [String: [Transaction]] = [
        "Today": [
            Transaction(id: 0, expense: true, transactionDate: Date(), bankAccount: "main", amount: 15.5, receiver: "Twint Mama"),
            Transaction(id: 1, expense: true, transactionDate: Date(), bankAccount: "main", amount: 154, receiver: "DJI Controller"),
            Transaction(id: 2, expense: false, transactionDate: Date(), bankAccount: "main", amount: 1250, receiver: "Swisslog"),
            Transaction(id: 3, expense: true, transactionDate: Date(), bankAccount: "main", amount: 22.9, receiver: "Steam"),
        ],
        "11.08.23": [
            Transaction(id: 4, expense: false, transactionDate: Date(), bankAccount: "main", amount: 100, receiver: "Swisscom"),
            Transaction(id: 5, expense: true, transactionDate: Date(), bankAccount: "main", amount: 52.25, receiver: "Coop"),
        ],
    ]
    
    var body: some View {
        NavigationStack {
            ZStack() {
                Color.appBackground.ignoresSafeArea()
                
                VStack(alignment: .leading, spacing: 0.0) {
                    ForEach(Array(transactions.keys), id: \.self) { key in
                        Text(key)
                            .font(.headline)
                            .padding(.leading)
                            
                        List(transactions[key] ?? []) { t in
                            NavigationLink {
                                Text("hello")
                            } label: {
                                HStack(spacing: 5.0) {
                                    if t.expense {
                                        RoundedRectangle(cornerRadius: 5)
                                            .foregroundColor(.appWarn)
                                            .frame(width: 10, height: .infinity, alignment: .leading)
                                        
                                        Text("- \(String(t.amount))")
                                            .foregroundColor(.appWarn)
                                    } else {
                                        RoundedRectangle(cornerRadius: 5)
                                            .foregroundColor(.appPrimary)
                                            .frame(width: 10, height: .infinity, alignment: .leading)
                                        
                                        Text("+ \(String(t.amount))")
                                            .foregroundColor(.appForeground)
                                    }
                                    Spacer()
                                    
                                    Text(t.receiver)
                                        .foregroundColor(.appForeground)
                                    
                                    Spacer()
                                    
                                    Image(systemName: "basket.fill") // todo: replace with tag icon
                                        .frame(width: 30, height: 30, alignment: .center)
                                        .aspectRatio(contentMode: .fit)
                                        .foregroundColor(Color.appForeground)
                                        .background(Color.appAccent) // todo: replace with tag color
                                        .cornerRadius(5)
                                }
                            }
                        }
                        .scrollDisabled(true)
                    }
                }
            }
            .navigationTitle("Transactions")
            .navigationBarTitleDisplayMode(.large)
        }
        .scrollContentBackground(.hidden)
    }
}

struct TransactionsView_Previews: PreviewProvider {
    static var previews: some View {
        TransactionsView()
    }
}
