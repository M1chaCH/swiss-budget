import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_STORAGE_KEY = "color-theme";

  get prefersDark(): boolean {
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  public init() {
    const storedTheme: MichuTechColorTheme | undefined = localStorage.getItem(this.THEME_STORAGE_KEY) as MichuTechColorTheme;
    if (storedTheme)
      this.applyColorTheme(storedTheme)
    else if (this.prefersDark)
      this.applyColorTheme("dark")
  }

  public applyColorTheme(theme: MichuTechColorTheme) {
    document.documentElement.setAttribute(this.THEME_STORAGE_KEY, theme)
    localStorage.setItem(this.THEME_STORAGE_KEY, theme)
  }

  public getCurrentAppliedColorTheme(): MichuTechColorTheme {
    const currentAttribute = document.documentElement.getAttribute(this.THEME_STORAGE_KEY);
    if (currentAttribute)
      return currentAttribute as MichuTechColorTheme;

    return "default";
  }

  public toggleDarkTheme(): boolean {
    if (this.getCurrentAppliedColorTheme() === "dark") {
      this.applyColorTheme("default")
      return false;
    } else {
      this.applyColorTheme("dark")
      return true;
    }
  }
}

export type MichuTechColorTheme = "default" | "dark";
