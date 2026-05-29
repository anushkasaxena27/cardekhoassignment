import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenStoreService } from '../core/token-store.service';

export const authGuard: CanActivateFn = () => {
  const tokens = inject(TokenStoreService);
  const router = inject(Router);
  if (tokens.isAuthenticated()) {
    return true;
  }
  return router.createUrlTree(['/auth/login'], {
    queryParams: { returnUrl: router.url },
  });
};
