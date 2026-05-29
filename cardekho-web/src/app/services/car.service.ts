import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  apiUrl,
  CarDetail,
  CarFilter,
  CarSummary,
  Page,
  TextSearchRequest,
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class CarService {
  private readonly http = inject(HttpClient);

  list(page = 0, size = 12): Observable<Page<CarSummary>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<Page<CarSummary>>(apiUrl('/api/cars'), { params });
  }

  getById(id: number): Observable<CarDetail> {
    return this.http.get<CarDetail>(apiUrl(`/api/cars/${id}`));
  }

  filter(filter: CarFilter, page = 0, size = 12): Observable<Page<CarSummary>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.post<Page<CarSummary>>(apiUrl('/api/cars/filter'), filter, { params });
  }

  search(q: string, page = 0, size = 12): Observable<Page<CarSummary>> {
    const body: TextSearchRequest = { q };
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.post<Page<CarSummary>>(apiUrl('/api/cars/search'), body, { params });
  }

  compare(variantIds: number[]): Observable<CarDetail[]> {
    return this.http.post<CarDetail[]>(apiUrl('/api/cars/compare'), { variantIds });
  }
}
