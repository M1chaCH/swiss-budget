export type TransactionDto = {
  id: string,
  expense: boolean,
  transactionDate: string,
  bankAccount: string,
  receiver: string,
  amount: number,

  tagId?: number,
  tag: TagDto,
  matchingKeywordId?: number,
  matchingKeyword: KeywordDto,
  alias?: string,
  note?: string,
}

export type TagDto = {
  id: number,
  icon: string,
  color: string,
  name: string,
  userId: string,
  keywords?: KeywordDto[],
}

export type KeywordDto = {
  id: number,
  keyword: string,
  tagId?: number,
  userId: string,
}
