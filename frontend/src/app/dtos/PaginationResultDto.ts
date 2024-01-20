export interface PaginationResultDto<Data> {
  pageSize: number;
  totalSize: number;
  pageData: Data[];
}