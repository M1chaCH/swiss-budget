import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

export type LogOperatorOptions = {
  name?: string,
  dontLogValue?: boolean,
}

export function logged<T>(options?: LogOperatorOptions) {
  return (source: Observable<T>) => {
    if (!environment.IS_DEV) {
      return source;
    }

    return new Observable<T>(obs => {
      return source.subscribe({
        next: (v) => {
          logMessage("next value", options, v);
          obs.next(v);
        },
        error: (e) => {
          logMessage("error occurred", options, e);
          obs.error(e);
        },
        complete: () => {
          logMessage("completed", options);
          obs.complete();
        }
      });
    });
  };
}

function logMessage(messageStart: string, options?: LogOperatorOptions, value?: any) {
  let message = messageStart;
  if (options?.name)
    message += ` in '${options.name}'`;
  if (!options?.dontLogValue)
    message += " with";

  console.log(message, options?.dontLogValue ? "" : value);
}