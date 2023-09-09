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
        query(":leave", animate("250ms ease-out", style({
          opacity: 0,
        })), {optional: true}),
        query(":enter", animate("250ms 150ms ease-in", style({
          opacity: 1,
        })), {optional: true}),
      ]),
    ])
  ]);
