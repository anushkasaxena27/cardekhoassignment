import { Injectable, computed, signal } from '@angular/core';

const ACCESS = 'cd_access_token';
const REFRESH = 'cd_refresh_token';

@Injectable({ providedIn: 'root' })
export class TokenStoreService {
  private readonly access = signal<string | null>(this.read(ACCESS));
  private readonly refresh = signal<string | null>(this.read(REFRESH));

  readonly accessToken = computed(() => this.access());
  readonly refreshToken = computed(() => this.refresh());
  readonly isAuthenticated = computed(() => !!this.access());

  setTokens(access: string, refresh: string): void {
    localStorage.setItem(ACCESS, access);
    localStorage.setItem(REFRESH, refresh);
    this.access.set(access);
    this.refresh.set(refresh);
  }

  setAccess(access: string): void {
    localStorage.setItem(ACCESS, access);
    this.access.set(access);
  }

  clear(): void {
    localStorage.removeItem(ACCESS);
    localStorage.removeItem(REFRESH);
    this.access.set(null);
    this.refresh.set(null);
  }

  private read(key: string): string | null {
    return localStorage.getItem(key);
  }
}
