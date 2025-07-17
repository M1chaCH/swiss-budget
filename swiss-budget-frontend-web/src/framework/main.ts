import {CommandAutocompleteProvider} from './command-autocomplete.ts';

const consoleInput = document.getElementById('console-input')! as HTMLInputElement;
const consoleSuggestions = document.getElementById('console-suggestion-container')! as HTMLDivElement;
// const consoleCursor = document.getElementById('console-cursor')! as HTMLSpanElement;

const autocomplete = new CommandAutocompleteProvider();

autocomplete.registerObserver(result => {
  consoleSuggestions.innerHTML = '';
  for (let suggestion of result) {
    consoleSuggestions.innerHTML += `<p>${suggestion}</p>`;
  }
});

document.getElementById('console-background')!.addEventListener('click', _ => {
  console.log('clicked', consoleInput);
  consoleInput.focus();
});

consoleInput.addEventListener('focusin', _ => {
  // consoleCursor.style.visibility = 'visible';
});

consoleInput.addEventListener('input', _ => {
  autocomplete.updateInput(consoleInput.value);
});

consoleInput.addEventListener('keydown', e => {
  console.log('key', e);

  if (e.key == 'ArrowUp' || e.key == 'ArrowDown') {
    e.preventDefault();
    e.stopPropagation();
  } else if (e.key == 'Enter') {
    consoleInput.value = '';
  }
});

consoleInput.addEventListener('focusout', _ => {
  // consoleCursor.style.visibility = 'hidden';
});
