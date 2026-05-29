import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layouts/main-layout.component').then((m) => m.MainLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home-page.component').then((m) => m.HomePageComponent),
      },
      {
        path: 'chat',
        loadComponent: () =>
          import('./features/recommendations/recommendations-page.component').then((m) => m.RecommendationsPageComponent),
      },
      {
        path: 'cars',
        loadComponent: () => import('./features/cars/car-list-page.component').then((m) => m.CarListPageComponent),
      },
      {
        path: 'cars/:id',
        loadComponent: () => import('./features/cars/car-detail-page.component').then((m) => m.CarDetailPageComponent),
      },
      {
        path: 'compare',
        loadComponent: () => import('./features/compare/compare-page.component').then((m) => m.ComparePageComponent),
      },
      {
        path: 'shortlist',
        loadComponent: () =>
          import('./features/shortlist/shortlist-page.component').then((m) => m.ShortlistPageComponent),
      },
      {
        path: 'profile',
        canActivate: [authGuard],
        loadComponent: () => import('./features/profile/profile-page.component').then((m) => m.ProfilePageComponent),
      },
    ],
  },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login-page.component').then((m) => m.LoginPageComponent),
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./features/auth/register-page.component').then((m) => m.RegisterPageComponent),
  },
  { path: '**', redirectTo: '' },
];
