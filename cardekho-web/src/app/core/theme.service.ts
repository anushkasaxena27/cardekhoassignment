import { Injectable, signal } from '@angular/core';

const KEY = 'cd_theme_dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly dark = signal(this.readInitial());

  constructor() {
    this.apply(this.dark());
  }

  toggle(): void {
    const next = !this.dark();
    this.dark.set(next);
    localStorage.setItem(KEY, String(next));
    this.apply(next);
  }

  private readInitial(): boolean {
    return localStorage.getItem(KEY) === 'true';
  }

  private apply(dark: boolean): void {
    document.body.classList.toggle('cd-theme-dark', dark);
  }
}
