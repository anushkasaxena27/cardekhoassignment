import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiUrl, CarSummary } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly http = inject(HttpClient);

  trending(): Observable<CarSummary[]> {
    return this.http.get<CarSummary[]>(apiUrl('/api/analytics/trending'));
  }

  mostSearched(): Observable<CarSummary[]> {
    return this.http.get<CarSummary[]>(apiUrl('/api/analytics/most-searched'));
  }

  topRated(): Observable<CarSummary[]> {
    return this.http.get<CarSummary[]>(apiUrl('/api/analytics/top-rated'));
  }
}
