import { createContext, useCallback, useEffect, useMemo, useState, type PropsWithChildren } from 'react';
import type { AuthContextValue, AuthUser, LoginCredentials } from '../types/auth';
import { authService } from '../services/authService';

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: PropsWithChildren) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isInitializing, setIsInitializing] = useState(true);

  useEffect(() => {
    setUser(authService.getSession());
    setIsInitializing(false);
  }, []);

  const login = useCallback(async (credentials: LoginCredentials) => {
    const authenticatedUser = await authService.login(credentials);
    setUser(authenticatedUser);
    return authenticatedUser;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const value = useMemo<AuthContextValue>(() => ({ user, isAuthenticated: Boolean(user), isInitializing, login, logout }), [user, isInitializing, login, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
