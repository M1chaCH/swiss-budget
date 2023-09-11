import {animate, animateChild, group, query, style, transition, trigger} from "@angular/animations";


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
