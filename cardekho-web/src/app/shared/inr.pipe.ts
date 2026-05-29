import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'inr',
  standalone: true,
})
export class InrPipe implements PipeTransform {
  transform(value: number | string | null | undefined): string {
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    const n = typeof value === 'number' ? value : Number(value);
    if (Number.isNaN(n)) {
      return '—';
    }
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(n);
  }
}
