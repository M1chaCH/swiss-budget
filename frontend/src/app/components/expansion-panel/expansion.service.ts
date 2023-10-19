import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ExpansionService {
  private expansionLists: Map<string, Expandable[]> = new Map<string, Expandable[]>();

  changeTopic(topic: string, open: boolean) {
    this.expansionLists.get(topic)?.forEach(expandable => expandable.changeState(open));
  }

  registerExpandable(topic: string, expandable: Expandable) {
    const expandableComponents = this.expansionLists.get(topic) ?? [];
    expandableComponents.push(expandable);
    this.expansionLists.set(topic, expandableComponents);
  }

  removeExpandable(topic: string, expandable: Expandable) {
    let expandableComponents = this.expansionLists.get(topic);
    if (!expandableComponents || expandableComponents.length === 0)
      return;

    this.expansionLists.set(topic, expandableComponents.filter(e => e !== expandable));
  }
}

export interface Expandable {
  changeState: (open: boolean) => void
}
