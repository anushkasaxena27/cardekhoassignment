import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CarService } from '../../services/car.service';
import { ShortlistService } from '../../services/shortlist.service';
import { CarDetail, CarSummary } from '../../models/api.models';
import { InrPipe } from '../../shared/inr.pipe';

@Component({
  selector: 'app-car-detail-page',
  standalone: true,
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    InrPipe,
  ],
  templateUrl: './car-detail-page.component.html',
  styleUrl: './car-detail-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CarDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly cars = inject(CarService);
  private readonly shortlist = inject(ShortlistService);
  private readonly snack = inject(MatSnackBar);

  readonly car = signal<CarDetail | undefined>(undefined);
  readonly similar = signal<CarSummary[]>([]);
  readonly loading = signal(true);

  constructor() {
    this.route.paramMap
      .pipe(
        map((p) => Number(p.get('id'))),
        switchMap((id) => this.cars.getById(id)),
      )
      .subscribe({
        next: (c) => {
          this.car.set(c);
          this.loading.set(false);
          this.loadSimilar(c);
        },
        error: () => this.loading.set(false),
      });
  }

  addShortlist(): void {
    const c = this.car();
    if (!c) {
      return;
    }
    this.shortlist.add(c.id).subscribe({
      next: () => this.snack.open('Added to shortlist', 'OK', { duration: 2500 }),
      error: () => this.snack.open('Could not save shortlist', 'Dismiss', { duration: 3500 }),
    });
  }

  specRows(car: CarDetail): { k: string; v: string }[] {
    return [
      { k: 'Fuel', v: car.fuelType },
      { k: 'Transmission', v: car.transmission },
      { k: 'Seats', v: `${car.seatingCapacity ?? '—'}` },
      { k: 'Engine (cc)', v: `${car.engineCc ?? '—'}` },
      { k: 'Power (hp)', v: `${car.horsepower ?? '—'}` },
      { k: 'Torque (Nm)', v: `${car.torque ?? '—'}` },
      { k: 'Mileage', v: car.mileage != null ? `${car.mileage} km/l` : '—' },
      { k: 'City / Highway', v: `${car.cityMileage ?? '—'} / ${car.highwayMileage ?? '—'}` },
      { k: 'Boot (L)', v: `${car.bootSpace ?? '—'}` },
      { k: 'Ground clearance (mm)', v: `${car.groundClearance ?? '—'}` },
      { k: 'Drivetrain', v: car.drivetrain ?? '—' },
      { k: 'On-road', v: car.onRoadPrice != null ? new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(car.onRoadPrice) : '—' },
    ];
  }

  private loadSimilar(c: CarDetail): void {
    this.cars
      .filter({ bodyType: c.bodyType, segment: c.segment }, 0, 6)
      .subscribe((p) => this.similar.set(p.content.filter((x) => x.id !== c.id).slice(0, 4)));
  }
}
