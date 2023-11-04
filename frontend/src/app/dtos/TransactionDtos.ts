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

  duplicatedTagMatches?: TransactionTagDuplicateDto[],
}

export type TagDto = {
  id: number,
  icon: string,
  color: string,
  name: string,
  defaultTag?: boolean,
  keywords?: KeywordDto[],
}

export type KeywordDto = {
  id: number,
  keyword: string,
  tagId?: number,
}

export type TransactionTagDuplicateDto = {
  transactionId: string,
  tag: TagDto,
  matchingKeyword: KeywordDto,
}