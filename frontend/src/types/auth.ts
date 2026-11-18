export type UserRole = 'USER' | 'ADMIN';

export interface AuthUser {
  id: number;
  email: string;
  displayName: string;
  role: UserRole;
}

export interface LoginCredentials { email: string; password: string; }
export interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isInitializing: boolean;
  login: (credentials: LoginCredentials) => Promise<AuthUser>;
  logout: () => void;
}
