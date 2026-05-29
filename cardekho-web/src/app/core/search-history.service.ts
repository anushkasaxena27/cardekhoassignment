import { Injectable, signal } from '@angular/core';

const KEY = 'cd_search_history';
const MAX = 12;

@Injectable({ providedIn: 'root' })
export class SearchHistoryService {
  readonly items = signal<string[]>(this.load());

  add(query: string): void {
    const q = query.trim();
    if (!q) {
      return;
    }
    const next = [q, ...this.items().filter((x) => x.toLowerCase() !== q.toLowerCase())].slice(0, MAX);
    this.items.set(next);
    localStorage.setItem(KEY, JSON.stringify(next));
  }

  private load(): string[] {
    try {
      const raw = localStorage.getItem(KEY);
      if (!raw) {
        return [];
      }
      const parsed = JSON.parse(raw) as unknown;
      return Array.isArray(parsed) ? (parsed as string[]).filter((s) => typeof s === 'string') : [];
    } catch {
      return [];
    }
  }
}
