import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiUrl, ChatRecommendationResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class RecommendationService {
  private readonly http = inject(HttpClient);

  chat(query: string): Observable<ChatRecommendationResponse> {
    return this.http.post<ChatRecommendationResponse>(apiUrl('/api/recommendations/chat'), { query });
  }
}
