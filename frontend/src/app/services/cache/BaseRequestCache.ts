import {inject} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {ApiService} from '../api.service';

export abstract class BaseRequestCache<TApiResult, TRequestType, TFilter extends object = {}> {
  protected readonly api: ApiService = inject(ApiService);

  protected readonly consumerCount = new Map<string, number>();
  protected readonly data = new Map<string, BehaviorSubject<TApiResult | undefined>>;

  protected constructor(
    protected endpoint: string,
  ) {
  }

  abstract get(filter?: TFilter): TRequestType;

  getCurrent(filter?: TFilter): TApiResult | undefined {
    return this.data.get(stringifyFilter(filter))?.getValue();
  }

  updateAndCopyCache(oldKey: string, newKey: string) {
    if (oldKey === newKey)
      return;

    const currentSubject = this.data.get(oldKey);
    if (!currentSubject)
      return;

    const oldValue = currentSubject.getValue();
    const cachedNewValue = this.data.get(newKey)?.getValue();

    if (cachedNewValue) {
      currentSubject.next(cachedNewValue);
      this.data.set(oldKey, new BehaviorSubject(oldValue));
      this.data.set(newKey, currentSubject);
      this.moveConsumer(oldKey, newKey);
    } else {
      const newFilter = parseFilter<TFilter>(newKey);
      this.loadData(newFilter ?? undefined).subscribe(r => {
        this.data.set(newKey, currentSubject);
        this.data.set(oldKey, new BehaviorSubject(oldValue));
        currentSubject.next(r);
        this.moveConsumer(oldKey, newKey);
      });
    }
  }

  invalidate() {
    const consumeCountEntries = Array.from(this.consumerCount.entries());
    const keysToRefresh = consumeCountEntries.filter(e => e[1] > 0).map(e => e[0]);
    const keysToRemove = consumeCountEntries.filter(e => e[1] === 0).map(e => e[0]);
    keysToRemove.forEach(k => {
      this.data.delete(k);
      this.consumerCount.delete(k);
    });

    const filtersToRefresh = keysToRefresh.map(parseFilter) as (TFilter | undefined)[];
    for (let i = 0; i < filtersToRefresh.length; i++) {
      const filter = filtersToRefresh[i];
      const key = keysToRefresh[i];
      this.loadData(filter).subscribe(r => this.data.get(key)!.next(r));
    }
  }

  protected loadData(filter: object | undefined): Observable<TApiResult> {
    return this.api.get<TApiResult>(this.endpoint, filter ?? undefined, true);
  }

  protected subscriptionEnded(key: string) {
    const currentCount = this.consumerCount.get(key) ?? 0;
    this.consumerCount.set(key, Math.max(0, currentCount - 1));
  }

  private moveConsumer(oldKey: string, newKey: string) {
    this.consumerCount.set(oldKey, Math.max((this.consumerCount.get(oldKey) ?? 0) - 1, 0));
    this.consumerCount.set(newKey, (this.consumerCount.get(newKey) ?? 0) + 1);
  }
}

export const DEFAULT_CACHE_KEY = 'empty';

export function stringifyFilter<Filter = object>(filter?: Filter): string {
  if (!filter)
    return DEFAULT_CACHE_KEY;

  return JSON.stringify(filter);
}

export function parseFilter<Filter>(filter: string): Filter | null {
  if (filter === DEFAULT_CACHE_KEY)
    return null;
  return JSON.parse(filter);
}

export function mergeFilter<TFilter>(existingFilter: TFilter, newFilter: Partial<TFilter>): TFilter | undefined {
  let filter: any = {
    ...existingFilter,
    ...newFilter,
  };

  // strip current filter (only keep truthy filter properties)
  // keep in mind, this will swap false to undefined, so a filter can't be explicitly set to false.
  let anyValueDefined: boolean = false;
  for (let key of Object.keys(filter)) {
    if (Array.isArray(filter[key])) {
      if (filter[key].length > 0)
        anyValueDefined = true;
      else
        filter[key] = undefined;
    } else {
      if (filter[key])
        anyValueDefined = true;
      else
        filter[key] = undefined;
    }
  }

  if (!anyValueDefined) filter = undefined;

  return filter;
}
