import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenStoreService } from '../core/token-store.service';

const AUTH_PUBLIC = ['/api/auth/login', '/api/auth/register', '/api/auth/refresh'];

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const tokens = inject(TokenStoreService);
  const token = tokens.accessToken();
  if (!token) {
    return next(req);
  }
  const url = req.url;
  const isApi = url.includes('/api/');
  const isPublicAuth = AUTH_PUBLIC.some((p) => url.includes(p));
  if (isApi && !isPublicAuth) {
    return next(
      req.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      }),
    );
  }
  return next(req);
};
