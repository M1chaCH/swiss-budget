import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CurrentPageService {
  private currentPage: BehaviorSubject<CurrentPage> = new BehaviorSubject<CurrentPage>({
    title: "Home",
    route: "home"
  });

  constructor() {
  }

  pageChanges(): Observable<CurrentPage> {
    return this.currentPage.asObservable();
  }

  subscribePageChanges(subscriber: (page: CurrentPage) => {}) {
    this.currentPage.subscribe(subscriber);
  }

  registerCurrentPage(title: string, route: string) {
    this.currentPage.next({title, route});
  }
}

export type CurrentPage = {
  title: string,
  route: string,
}
