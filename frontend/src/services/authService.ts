import type { LoginCredentials, AuthUser } from '../types/auth';
import { mockAuthService } from './mockAuthService';

/** Frontend-only demo adapter. Replace this adapter with the real API when backend auth is ready. */
export const authService = {
  login: ({ email, password }: LoginCredentials): Promise<AuthUser> => mockAuthService.login(email, password),
  getSession: (): AuthUser | null => mockAuthService.getSession(),
  logout: (): void => mockAuthService.logout(),
};
