import { ChangeDetectionStrategy, Component, computed, effect, input, signal } from '@angular/core';
import { NgClass } from '@angular/common';

/** Lazy car photo with local SVG fallback (CarDekho-style listing cards). */
@Component({
  selector: 'app-car-image',
  standalone: true,
  imports: [NgClass],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <img
      [ngClass]="imgClass()"
      [src]="displaySrc()"
      [alt]="alt()"
      loading="lazy"
      decoding="async"
      (error)="onError()"
    />
  `,
  styles: `
    :host {
      display: block;
      overflow: hidden;
      background: var(--cd-surface-muted, #e8ecf1);
      aspect-ratio: 16 / 10;
    }
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      vertical-align: middle;
    }
  `,
})
export class CarImageComponent {
  private static readonly FALLBACK = '/car-placeholder.svg';

  readonly src = input<string | null | undefined>(null);
  readonly alt = input.required<string>();
  /** Extra CSS classes on the img element */
  readonly imgClass = input<string>('cd-car-img');

  private readonly loadFailed = signal(false);

  readonly displaySrc = computed(() => {
    const url = this.src();
    if (!url || this.loadFailed()) {
      return CarImageComponent.FALLBACK;
    }
    return url;
  });

  constructor() {
    effect(() => {
      this.src();
      this.loadFailed.set(false);
    });
  }

  onError(): void {
    this.loadFailed.set(true);
  }
}
