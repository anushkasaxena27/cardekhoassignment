import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ShortlistService } from '../../services/shortlist.service';
import { InrPipe } from '../../shared/inr.pipe';
import { CarImageComponent } from '../../shared/car-image.component';

@Component({
  selector: 'app-shortlist-page',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule, MatProgressSpinnerModule, MatSnackBarModule, InrPipe, CarImageComponent],
  templateUrl: './shortlist-page.component.html',
  styleUrl: './shortlist-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShortlistPageComponent {
  readonly shortlist = inject(ShortlistService);
  private readonly snack = inject(MatSnackBar);

  readonly loading = signal(true);
  readonly compareQuery = computed(() => ({
    ids: this.shortlist.items().map((c) => c.id).join(','),
  }));

  constructor() {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.shortlist.refresh().subscribe({
      next: () => this.loading.set(false),
      error: () => this.loading.set(false),
    });
  }

  remove(id: number): void {
    this.shortlist.remove(id).subscribe({
      next: () => this.snack.open('Removed', 'OK', { duration: 2000 }),
      error: () => this.snack.open('Remove failed', 'Dismiss', { duration: 3000 }),
    });
  }
}
