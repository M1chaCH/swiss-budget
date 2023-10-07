export type TransactionDto = {
  id: string,
  expense: boolean,
  transactionDate: Date,
  bankAccount: string,
  receiver: string,
  amount: number,

  tagId?: number,
  matchingKeywordId?: number,
  alias?: string,
  note?: string,
}
