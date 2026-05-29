import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CarService } from '../../services/car.service';
import { CarDetail } from '../../models/api.models';
import { InrPipe } from '../../shared/inr.pipe';
import { CarImageComponent } from '../../shared/car-image.component';

@Component({
  selector: 'app-compare-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    InrPipe,
    CarImageComponent,
  ],
  templateUrl: './compare-page.component.html',
  styleUrl: './compare-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComparePageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly cars = inject(CarService);
  private readonly route = inject(ActivatedRoute);
  private readonly snack = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    ids: ['', [Validators.required]],
  });

  readonly rows = signal<CarDetail[] | undefined>(undefined);
  readonly loading = signal(false);

  constructor() {
    this.route.queryParamMap.subscribe((q) => {
      const raw = q.get('ids');
      if (raw) {
        this.form.controls.ids.setValue(raw);
        this.runCompare();
      }
    });
  }

  runCompare(): void {
    const ids = this.form.controls.ids.value
      .split(/[,\s]+/)
      .map((x) => Number(x.trim()))
      .filter((n) => !Number.isNaN(n) && n > 0);
    if (ids.length < 2 || ids.length > 4) {
      this.snack.open('Enter 2 to 4 variant IDs (comma-separated)', 'OK', { duration: 3500 });
      return;
    }
    this.loading.set(true);
    this.cars.compare(ids).subscribe({
      next: (list) => {
        this.rows.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.snack.open('Compare failed', 'Dismiss', { duration: 3500 });
        this.loading.set(false);
      },
    });
  }

  safetyMax(c: CarDetail): string {
    const best = c.safetyRatings?.map((s) => s.rating).filter((r): r is number => r != null);
    if (!best?.length) {
      return '—';
    }
    return `${Math.max(...best)}★`;
  }
}
