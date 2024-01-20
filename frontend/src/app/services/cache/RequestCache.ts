import {BehaviorSubject, finalize, map, Observable, shareReplay} from 'rxjs';
import {BaseRequestCache, DEFAULT_CACHE_KEY, mergeFilter, stringifyFilter} from './BaseRequestCache';

export abstract class RequestCache<TDto, TFilter extends object = {}>
  extends BaseRequestCache<TDto[], CachedRequest<TDto, TFilter>, TFilter> {

  protected constructor(
    endpoint: string,
  ) {
    super(endpoint);
  }

  override get(filter?: TFilter): CachedRequest<TDto, TFilter> {
    const key = stringifyFilter(filter);
    const currentCount = this.consumerCount.get(key) ?? 0;
    this.consumerCount.set(key, currentCount + 1);

    let result: BehaviorSubject<TDto[] | undefined> | undefined = this.data.get(key);
    if (!result) {
      result = new BehaviorSubject<TDto[] | undefined>(undefined);

      this.loadData(filter).subscribe(r => result!.next(r));
      this.data.set(key, result);
    }

    const obs = result.pipe(
      map(v => v ?? []),
      shareReplay(1),
      finalize(() => this.subscriptionEnded(key)),
    );

    return new CachedRequest(obs, this, key);
  }
}

export class CachedRequest<TDto, TFilter extends object> {
  protected currentCacheKey: string;

  constructor(
    protected data$: Observable<TDto[]>,
    protected cache: RequestCache<TDto, TFilter>,
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

  updateFilter(filter: Partial<TFilter>): void {
    const oldCacheKey = this.currentCacheKey;

    this._currentFilter = mergeFilter(this._currentFilter, filter);
    this.currentCacheKey = stringifyFilter(this._currentFilter);

    this.cache.updateAndCopyCache(oldCacheKey, this.currentCacheKey);
  }
}