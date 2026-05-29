import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, forkJoin, map, of, tap } from 'rxjs';
import { apiUrl, CarSummary } from '../models/api.models';
import { TokenStoreService } from '../core/token-store.service';
import { CarService } from './car.service';

const LOCAL_KEY = 'cd_shortlist_ids';

@Injectable({ providedIn: 'root' })
export class ShortlistService {
  private readonly http = inject(HttpClient);
  private readonly tokens = inject(TokenStoreService);
  private readonly cars = inject(CarService);

  private readonly summaries = signal<CarSummary[]>([]);

  readonly items = computed(() => this.summaries());

  refresh(): Observable<CarSummary[]> {
    if (this.tokens.isAuthenticated()) {
      return this.http.get<CarSummary[]>(apiUrl('/api/shortlist')).pipe(
        tap((list) => this.summaries.set(list)),
        catchError(() => {
          this.summaries.set([]);
          return of([]);
        }),
      );
    }
    const ids = this.readLocalIds();
    if (ids.length === 0) {
      this.summaries.set([]);
      return of([]);
    }
    return forkJoin(ids.map((id) => this.cars.getById(id))).pipe(
      tap((details) =>
        this.summaries.set(
          details.map((d) => ({
            id: d.id,
            makeName: d.makeName,
            modelName: d.modelName,
            variantName: d.variantName,
            bodyType: d.bodyType,
            segment: d.segment,
            fuelType: d.fuelType,
            transmission: d.transmission,
            exShowroomPrice: d.exShowroomPrice,
            onRoadPrice: d.onRoadPrice,
            mileage: d.mileage,
            imageUrl: d.imageUrl,
            availabilityStatus: d.availabilityStatus,
            sunroof: d.features?.sunroof ?? null,
            panoramicSunroof: d.features?.panoramicSunroof ?? null,
          })),
        ),
      ),
      catchError(() => {
        this.summaries.set([]);
        return of([]);
      }),
    );
  }

  add(variantId: number): Observable<void> {
    if (this.tokens.isAuthenticated()) {
      return this.http.post<void>(apiUrl(`/api/shortlist/${variantId}`), {}).pipe(
        tap(() => {
          void this.refresh().subscribe();
        }),
      );
    }
    const ids = new Set(this.readLocalIds());
    ids.add(variantId);
    this.writeLocalIds([...ids]);
    return this.refresh().pipe(map(() => void 0));
  }

  remove(variantId: number): Observable<void> {
    if (this.tokens.isAuthenticated()) {
      return this.http.delete<void>(apiUrl(`/api/shortlist/${variantId}`)).pipe(
        tap(() => {
          void this.refresh().subscribe();
        }),
      );
    }
    this.writeLocalIds(this.readLocalIds().filter((id) => id !== variantId));
    return this.refresh().pipe(map(() => void 0));
  }

  has(variantId: number): boolean {
    return this.summaries().some((c) => c.id === variantId);
  }

  private readLocalIds(): number[] {
    try {
      const raw = localStorage.getItem(LOCAL_KEY);
      if (!raw) {
        return [];
      }
      const arr = JSON.parse(raw) as unknown;
      return Array.isArray(arr) ? (arr as number[]).filter((n) => typeof n === 'number') : [];
    } catch {
      return [];
    }
  }

  private writeLocalIds(ids: number[]): void {
    localStorage.setItem(LOCAL_KEY, JSON.stringify(ids));
  }
}
