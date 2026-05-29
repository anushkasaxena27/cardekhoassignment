import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule],
  template: `
    <div class="wrap">
      <mat-card appearance="outlined">
        <mat-card-header>
          <mat-card-title>Account</mat-card-title>
          <mat-card-subtitle>JWT session (access + refresh in local storage)</mat-card-subtitle>
        </mat-card-header>
        <mat-card-content>
          <p>You are signed in.</p>
        </mat-card-content>
        <mat-card-actions align="end">
          <button mat-button type="button" (click)="logout()">Logout</button>
          <a mat-flat-button color="primary" routerLink="/shortlist">View shortlist</a>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .wrap {
        max-width: 640px;
        margin: 0 auto;
        padding: 1.5rem 1rem;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfilePageComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    void this.router.navigate(['/']);
  }
}
