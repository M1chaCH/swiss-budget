export class CommandAutocompleteProvider {
  private readonly observers: CommandAutocompleteObserver[] = [];
  private readonly ws: WebSocket;

  private currentTimeout: number | undefined;

  constructor() {
    this.ws = new WebSocket('ws://localhost:8080/command/autocomplete');
    this.ws.onmessage = (event: MessageEvent) => this.handleWsMessage(JSON.parse(event.data));
    // TODO
    // this.ws.onclose =
    // this.ws.onerror =
  }

  registerObserver(observer: CommandAutocompleteObserver) {
    this.observers.push(observer);
  }

  updateInput(input: string) {
    if (this.currentTimeout) {
      clearTimeout(this.currentTimeout);
    }

    this.currentTimeout = setTimeout(() => this.updateInputImmediately(input), 200);
  }

  private updateInputImmediately(input: string) {
    this.ws.send(input);
  }

  private handleWsMessage(message: any) {
    if (message instanceof Array) {
      this.observers.forEach((observer) => observer(message));
    } else {
      console.warn('received unexpected data structure from ws');
    }
  }
}

export type CommandAutocompleteObserver = (result: string[]) => void