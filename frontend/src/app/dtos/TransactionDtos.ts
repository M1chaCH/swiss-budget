import {Moment} from "moment/moment";

export type TransactionDto = {
  id: string,
  expense: boolean,
  transactionDate: Moment,
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
  defaultTag?: boolean,
  keywords?: KeywordDto[],
}

export type KeywordDto = {
  id: number,
  keyword: string,
  tagId?: number,
  userId: string,
}
