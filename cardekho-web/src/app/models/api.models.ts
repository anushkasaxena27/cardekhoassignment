import { environment } from '../../environments/environment';

/** Build absolute API path; empty `apiBaseUrl` uses same-origin + dev proxy `/api`. */
export function apiUrl(suffix: string): string {
  const path = suffix.startsWith('/') ? suffix : `/${suffix}`;
  const base = environment.apiBaseUrl.trim();
  if (!base) {
    return path;
  }
  return `${base.replace(/\/$/, '')}${path}`;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName?: string | null;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first?: boolean;
  last?: boolean;
}

export interface CarSummary {
  id: number;
  makeName: string;
  modelName: string;
  variantName: string;
  bodyType: string;
  segment: string;
  fuelType: string;
  transmission: string;
  exShowroomPrice: number;
  onRoadPrice: number | null;
  mileage: number | null;
  imageUrl: string | null;
  availabilityStatus: string;
  sunroof: boolean | null;
  panoramicSunroof: boolean | null;
}

export interface CarFilter {
  makeId?: number | null;
  bodyType?: string | null;
  fuelType?: string | null;
  transmission?: string | null;
  minPrice?: number | null;
  maxPrice?: number | null;
  segment?: string | null;
}

export interface SafetyRating {
  agency: string;
  rating: number | null;
  airbags: number | null;
  abs: boolean | null;
  esc: boolean | null;
  adasFeatures: string | null;
}

export interface CarFeature {
  sunroof: boolean | null;
  ventilatedSeats: boolean | null;
  connectedCarTech: boolean | null;
  infotainmentSize: number | null;
  wirelessCharging: boolean | null;
  cruiseControl: boolean | null;
  panoramicSunroof: boolean | null;
  audioSystem: string | null;
}

export interface CarImage {
  imageUrl: string;
  imageType: string | null;
}

export interface CarDetail extends CarSummary {
  launchYear: number | null;
  seatingCapacity: number | null;
  engineCc: number | null;
  horsepower: number | null;
  torque: number | null;
  cityMileage: number | null;
  highwayMileage: number | null;
  bootSpace: number | null;
  groundClearance: number | null;
  drivetrain: string | null;
  safetyRatings: SafetyRating[];
  features: CarFeature | null;
  images: CarImage[];
  averageUserRating: number | null;
  reviewCount: number;
}

export interface ExtractedIntent {
  budgetMinInr: number | null;
  budgetMaxInr: number | null;
  /** Backend may omit empty collections. */
  bodyTypes?: string[] | null;
  fuelTypes?: string[] | null;
  usageIntents?: string[] | null;
  featureSignals?: string[] | null;
  sentimentSignals?: string[] | null;
  transmissionPreference: string | null;
}

export interface RecommendationItem {
  variantId: number;
  car: string;
  matchScore: number;
  reasons: string[];
  /** Variant hero image from catalog (same as browse / detail). */
  imageUrl?: string | null;
  exShowroomPrice?: number | null;
  mileage?: number | null;
}

export interface ChatRecommendationResponse {
  extractedIntent: ExtractedIntent;
  appliedFilters: CarFilter;
  recommendations: RecommendationItem[];
}

export interface TextSearchRequest {
  q: string;
}
