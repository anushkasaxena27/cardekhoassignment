import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import {
  apiUrl,
  LoginRequest,
  RefreshRequest,
  RegisterRequest,
  TokenResponse,
} from '../models/api.models';
import { TokenStoreService } from './token-store.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokens = inject(TokenStoreService);

  register(body: RegisterRequest): Observable<void> {
    return this.http.post<void>(apiUrl('/api/auth/register'), body);
  }

  login(body: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(apiUrl('/api/auth/login'), body).pipe(
      tap((t) => this.tokens.setTokens(t.accessToken, t.refreshToken)),
    );
  }

  refresh(): Observable<TokenResponse> {
    const rt = this.tokens.refreshToken();
    if (!rt) {
      throw new Error('No refresh token');
    }
    const body: RefreshRequest = { refreshToken: rt };
    return this.http.post<TokenResponse>(apiUrl('/api/auth/refresh'), body).pipe(
      tap((t) => this.tokens.setTokens(t.accessToken, t.refreshToken)),
    );
  }

  logout(): void {
    this.tokens.clear();
  }
}
