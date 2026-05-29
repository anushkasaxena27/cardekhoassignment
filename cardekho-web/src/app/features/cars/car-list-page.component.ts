import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CarService } from '../../services/car.service';
import { CarFilter, CarSummary, Page } from '../../models/api.models';
import { InrPipe } from '../../shared/inr.pipe';
import { CarImageComponent } from '../../shared/car-image.component';

@Component({
  selector: 'app-car-list-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    InrPipe,
    CarImageComponent,
  ],
  templateUrl: './car-list-page.component.html',
  styleUrl: './car-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CarListPageComponent {
  private readonly carsApi = inject(CarService);
  private readonly fb = inject(FormBuilder);

  readonly filterForm = this.fb.nonNullable.group({
    bodyType: [''],
    fuelType: [''],
    transmission: [''],
    segment: [''],
    maxPrice: [''],
  });

  readonly page = signal<Page<CarSummary> | undefined>(undefined);
  readonly loading = signal(true);
  pageIndex = 0;
  pageSize = 12;

  constructor() {
    this.load();
  }

  onPage(ev: PageEvent): void {
    this.pageIndex = ev.pageIndex;
    this.pageSize = ev.pageSize;
    this.load();
  }

  applyFilter(): void {
    this.pageIndex = 0;
    this.load();
  }

  clearFilter(): void {
    this.filterForm.reset({ bodyType: '', fuelType: '', transmission: '', segment: '', maxPrice: '' });
    this.pageIndex = 0;
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    const f = this.buildFilter();
    const hasFilter = Object.values(f).some((v) => v !== undefined && v !== null && `${v}`.length > 0);
    const req$ = hasFilter
      ? this.carsApi.filter(f as CarFilter, this.pageIndex, this.pageSize)
      : this.carsApi.list(this.pageIndex, this.pageSize);
    req$.subscribe({
      next: (p) => {
        this.page.set(p);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  private buildFilter(): Partial<CarFilter> {
    const v = this.filterForm.getRawValue();
    const max = v.maxPrice ? Number(v.maxPrice) : undefined;
    return {
      bodyType: v.bodyType || undefined,
      fuelType: v.fuelType || undefined,
      transmission: v.transmission || undefined,
      segment: v.segment || undefined,
      maxPrice: max !== undefined && !Number.isNaN(max) ? max : undefined,
    };
  }
}
