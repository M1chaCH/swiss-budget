import {animate, animateChild, group, query, style, transition, trigger} from "@angular/animations";
import {Directive, Input, TemplateRef, ViewContainerRef} from "@angular/core";


export const fadePageTransition =
    trigger('routeChangeTrigger', [
      transition('* <=> *', [
        query(":enter", [ // initial style for entering child
          style({
            opacity: 0,
          })
        ], {optional: true}),
        query(":leave", animateChild(), {optional: true}), // run all animations in leaving child comp.
        group([
          query(":leave", animate("250ms ease-in-out", style({
            opacity: 0,
          })), {optional: true}),
          query(":enter", animate("250ms 150ms ease-in-out", style({
            opacity: 1,
          })), {optional: true}),
        ]),
      ])
    ]);

export const switchAnimation =
    trigger('switchAnimation', [
      transition('* <=> *', [
        query(':enter', [
          style({
            opacity: 0,
            transform: 'translateX(-150px)',
          })
        ], {optional: true}),
        query(':leave', animateChild(), {optional: true}),
        group([
          query(':leave', animate('250ms ease-out', style({
            opacity: 0,
            transform: 'translateX(150px)',
          })), {optional: true}),
          query(':enter', animate('250ms ease-out', style({
            opacity: 1,
            transform: 'translateX(0)',
          })), {optional: true}),
        ]),
      ])
    ]);

export const stepSliderAnimation =
    trigger("stepSliderAnimation", [
      transition(":increment", group([
        query(":leave", [
          style({transform: "translateX(0)"}),
          animate("300ms ease-in-out", style({transform: "translateX(-100%)"}))
        ]),
        query(":enter", [
          style({transform: "translateX(100%)"}),
          animate("300ms ease-in-out", style({transform: "translateX(0)"}))
        ]),
      ])),

      transition(":decrement", group([
        query(":leave", [
          style({transform: "translateX(0)"}),
          animate("300ms ease-in-out", style({transform: "translateX(100%)"}))
        ]),
        query(":enter", [
          style({transform: "translateX(-100%)"}),
          animate("300ms ease-in-out", style({transform: "translateX(0)"}))
        ]),
      ])),
    ]);


// util for angular, if I replace the text content of a h1 f.ex. then it won't register a new
// entry / leave element it will just change the content, therefore, the queries in the animation
// don't work. this directive manually deletes the old element and creates a new element
// 🤦‍♂️
@Directive({
  selector: '[contentReplacer]'
})
export class ContentReplacerDirective {
  private currentValue: any;
  private hasView = false;

  constructor(
      private viewContainer: ViewContainerRef,
      private templateRef: TemplateRef<any>
  ) {
  }

  @Input() set contentReplacer(val: any) {
    if (!this.hasView) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.hasView = true;
    } else if (val !== this.currentValue) {
      this.viewContainer.clear();
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.currentValue = val;
    }
  }
}
