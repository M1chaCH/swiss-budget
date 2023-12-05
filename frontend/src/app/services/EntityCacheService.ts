import {BehaviorSubject, Observable, ReplaySubject, shareReplay, startWith, Subject, Subscription, switchMap} from "rxjs";
import {logged} from "../operators/logger-operator";

export abstract class EntityCacheService<T> {
  private readonly source = new BehaviorSubject<T | undefined>(undefined);
  private readonly resetter = new Subject();
  private data?: Observable<T | undefined>;
  private subscription?: Subscription;

  protected constructor() {
  }

  async init() {
    const destination = this.factory();
    this.subscription = this.source.pipe(
        logged({
          name: `cache[${this.constructor.name}] - source`,
        })
    ).subscribe(destination);

    this.data = this.resetter.asObservable().pipe(
        startWith(undefined),
        switchMap(() => destination),
        logged({
          name: `cache[${this.constructor.name}] - resetter`,
        }),
        shareReplay(1),
    );
    this.source.next(await this.loadData());
  }

  public get$(): Observable<T | undefined> {
    if (!this.data)
      throw new Error("cache service never initialized");
    return this.pipeResult(this.data);
  }

  public getCurrent(): T | undefined {
    return this.source.getValue();
  }

  public async invalidateCache() {
    this.subscription?.unsubscribe();
    this.subscription = this.source.subscribe(this.factory());

    const newData: T = await this.loadData();
    this.resetter.next(newData);
  }

  /**
   * called when get$() is executed, can be used to prepare the observable for the user of the service.
   * NOTE (is not shared by default)
   * @param data the data observable to pipe
   * @protected
   */
  protected pipeResult(data: Observable<T | undefined>): Observable<T | undefined> {
    return data;
  }

  protected abstract loadData(): Promise<T>;

  protected updateData(data: T | undefined) {
    this.source.next(data);
  }

  private readonly factory = () => new ReplaySubject<T | undefined>();
}