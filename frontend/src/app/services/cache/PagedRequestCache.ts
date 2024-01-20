import {BehaviorSubject, finalize, firstValueFrom, map, Observable, shareReplay} from 'rxjs';
import {PaginationResultDto} from '../../dtos/PaginationResultDto';
import {BaseRequestCache, DEFAULT_CACHE_KEY, mergeFilter, parseFilter, stringifyFilter} from './BaseRequestCache';


export abstract class PagedRequestCache<TDto, TFilter extends object>
  extends BaseRequestCache<PaginationResultDto<TDto>, PagedCachedRequest<TDto, TFilter>, TFilter> {
  private readonly requestPageState: Map<string, number> = new Map<string, number>();

  protected constructor(
    endpoint: string,
    private resultMapper: (dto: TDto) => TDto = (d) => d,
  ) {
    super(endpoint);
  }

  public override get(filter?: TFilter): PagedCachedRequest<TDto, TFilter> {
    const key = stringifyFilter(filter);
    const currentCount = this.consumerCount.get(key) ?? 0;
    this.consumerCount.set(key, currentCount + 1);

    let result: BehaviorSubject<PaginationResultDto<TDto> | undefined> | undefined = this.data.get(key);
    if (!result) {
      result = new BehaviorSubject<PaginationResultDto<TDto> | undefined>(undefined);

      this.loadData(filter).subscribe(r => result!.next(r));
      this.data.set(key, result);
    }

    const obs = result.pipe(
      map(v => (v?.pageData ?? []).map(v => this.resultMapper(v))),
      shareReplay(1),
      finalize(() => this.subscriptionEnded(key)),
    );

    return new PagedCachedRequest(obs, this, key);
  }

  public async loadNextPage(cacheKey: string): Promise<void> {
    const currentRequest: BehaviorSubject<PaginationResultDto<TDto> | undefined> | undefined = this.data.get(cacheKey);
    if (!currentRequest)
      return;

    const pageToLoad: number = (this.requestPageState.get(cacheKey) ?? 1) + 1;
    const parsedFilter = parseFilter(cacheKey) ?? {};
    const filter = {
      ...parsedFilter,
      page: pageToLoad,
    };

    const loadedData = await firstValueFrom(this.loadData(filter));
    loadedData.pageData = [...currentRequest.getValue()?.pageData ?? [], ...loadedData.pageData];
    this.requestPageState.set(cacheKey, pageToLoad);
    currentRequest.next(loadedData);
  }

  override invalidate(): void {
    super.invalidate();
    this.requestPageState.clear();
  }

  getCurrentPage(cacheKey: string): number {
    return this.requestPageState.get(cacheKey) ?? 1;
  }
}

export class PagedCachedRequest<TDto, TFilter extends object> {
  protected currentCacheKey: string;

  constructor(
    protected data$: Observable<TDto[]>,
    protected cache: PagedRequestCache<TDto, TFilter>,
    initialCacheKey: string = DEFAULT_CACHE_KEY,
  ) {
    this.currentCacheKey = initialCacheKey;
  }

  protected _currentFilter: TFilter | undefined;

  get currentFilter() {
    return this._currentFilter;
  }

  get result$(): Observable<TDto[]> {
    return this.data$;
  }

  loadNextPage(): void {
    if (!this.hasNextPage()) {
      return;
    }

    this.cache.loadNextPage(this.currentCacheKey);
  }

  hasNextPage(): boolean {
    const currentValue = this.cache.getCurrent(this._currentFilter);
    if (!currentValue) {
      return false;
    }
    const currentPage = this.cache.getCurrentPage(this.currentCacheKey);

    const pages = Math.ceil(currentValue.totalSize / currentValue.pageSize);
    return currentPage < pages;
  }

  updateFilter(filter: Partial<TFilter>): void {
    const oldCacheKey = this.currentCacheKey;

    this._currentFilter = mergeFilter(this._currentFilter, filter);
    this.currentCacheKey = stringifyFilter(this._currentFilter);

    this.cache.updateAndCopyCache(oldCacheKey, this.currentCacheKey);
  }
}