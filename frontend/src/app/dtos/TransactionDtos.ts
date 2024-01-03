import {Moment} from "moment/moment";

export type TransactionDto = {
  id: string,
  expense: boolean,
  transactionDate: Moment,
  bankAccount: string,
  receiver: string,
  amount: number,

  tagId?: string,
  tag: TagDto,
  matchingKeywordId?: string,
  matchingKeyword: KeywordDto,
  alias?: string,
  note?: string,
  needsAttention?: boolean,

  duplicatedTagMatches?: TransactionTagDuplicateDto[],
}

export type TagDto = {
  id: string,
  icon: string,
  color: string,
  name: string,
  defaultTag?: boolean,
  keywords?: KeywordDto[],
}

export type KeywordDto = {
  id: string,
  keyword: string,
  tagId?: string,
}

export type TransactionTagDuplicateDto = {
  transactionId: string,
  tag: TagDto,
  matchingKeyword: KeywordDto,
}