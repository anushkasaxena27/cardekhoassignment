import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AnalyticsService } from '../../services/analytics.service';
import { CarSummary } from '../../models/api.models';
import { InrPipe } from '../../shared/inr.pipe';
import { CarImageComponent } from '../../shared/car-image.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    InrPipe,
    CarImageComponent,
  ],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomePageComponent {
  private readonly analytics = inject(AnalyticsService);

  readonly trending = signal<CarSummary[] | undefined>(undefined);
  readonly loading = signal(true);

  constructor() {
    this.analytics.trending().subscribe({
      next: (v) => {
        this.trending.set(v);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
