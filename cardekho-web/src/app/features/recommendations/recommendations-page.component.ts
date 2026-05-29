import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RecommendationService } from '../../services/recommendation.service';
import { SearchHistoryService } from '../../core/search-history.service';
import { ShortlistService } from '../../services/shortlist.service';
import { ChatRecommendationResponse, RecommendationItem } from '../../models/api.models';
import { InrPipe } from '../../shared/inr.pipe';
import { CarImageComponent } from '../../shared/car-image.component';

type ChatRole = 'user' | 'assistant';

interface ChatTurn {
  role: ChatRole;
  text: string;
  response?: ChatRecommendationResponse;
}

@Component({
  selector: 'app-recommendations-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    InrPipe,
    DecimalPipe,
    CarImageComponent,
  ],
  templateUrl: './recommendations-page.component.html',
  styleUrl: './recommendations-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationsPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(RecommendationService);
  readonly history = inject(SearchHistoryService);
  private readonly shortlist = inject(ShortlistService);
  private readonly snack = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    query: ['', [Validators.required, Validators.maxLength(2000)]],
  });

  readonly turns = signal<ChatTurn[]>([]);
  readonly busy = signal(false);
  readonly lastCards = signal<RecommendationItem[]>([]);

  submit(): void {
    const q = this.form.controls.query.value.trim();
    if (!q || this.busy()) {
      return;
    }
    this.history.add(q);
    this.turns.update((t) => [...t, { role: 'user', text: q }]);
    this.form.controls.query.setValue('');
    this.busy.set(true);
    this.api.chat(q).subscribe({
      next: (res) => {
        this.turns.update((t) => [...t, { role: 'assistant', text: this.summaryLine(res), response: res }]);
        this.lastCards.set(res.recommendations);
        this.busy.set(false);
      },
      error: () => {
        this.snack.open('Could not reach recommendation service', 'Dismiss', { duration: 4000 });
        this.busy.set(false);
      },
    });
  }

  useSuggestion(text: string): void {
    this.form.controls.query.setValue(text);
    this.submit();
  }

  rerunFromHistory(text: string): void {
    this.form.controls.query.setValue(text);
    this.submit();
  }

  addShortlist(id: number): void {
    this.shortlist.add(id).subscribe({
      next: () => this.snack.open('Saved to shortlist', 'OK', { duration: 2500 }),
      error: () => this.snack.open('Save failed — try logging in for cloud shortlist', 'Dismiss', { duration: 4000 }),
    });
  }

  trackTurn = (_: number, turn: ChatTurn) => turn.text + turn.role;

  private summaryLine(res: ChatRecommendationResponse): string {
    const parts: string[] = [];
    const i = res.extractedIntent;
    if (i.budgetMaxInr) {
      parts.push(`budget up to ₹${i.budgetMaxInr.toLocaleString('en-IN')}`);
    }
    if (i.bodyTypes?.length) {
      parts.push(`body: ${i.bodyTypes.join(', ')}`);
    }
    if (i.featureSignals?.length) {
      parts.push(`signals: ${i.featureSignals.join(', ')}`);
    }
    return parts.length ? `Understood — ${parts.join(' · ')}.` : 'Here are tailored picks based on your message.';
  }
}
