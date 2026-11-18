import type { AuthUser } from '../types/auth';

// Demo-only identities. This is not a secure authentication mechanism.
const DEMO_ACCOUNTS: Array<{ user: AuthUser; password: string }> = [
  { user: { id: 1001, email: 'user@cho2hand.local', displayName: 'Nguyễn Minh Anh', role: 'USER' }, password: 'User123!' },
  { user: { id: 1002, email: 'admin@cho2hand.local', displayName: 'Quản trị viên', role: 'ADMIN' }, password: 'Admin123!' },
  // Kept for compatibility with the existing backend's documented sample identity; it is a USER demo only.
  { user: { id: 1, email: 'demo@marketplace.local', displayName: 'Demo User', role: 'USER' }, password: 'password' },
];

const SESSION_KEY = 'cho2hand.auth.session';

export const mockAuthService = {
  async login(email: string, password: string): Promise<AuthUser> {
    await new Promise((resolve) => window.setTimeout(resolve, 250));
    const account = DEMO_ACCOUNTS.find((item) => item.user.email.toLowerCase() === email.trim().toLowerCase() && item.password === password);
    if (!account) throw new Error('Email hoặc mật khẩu không chính xác.');
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(account.user));
    return account.user;
  },
  getSession(): AuthUser | null {
    try {
      const raw = sessionStorage.getItem(SESSION_KEY);
      if (!raw) return null;
      const value = JSON.parse(raw) as AuthUser;
      if (!value || typeof value.id !== 'number' || !value.email || !['USER', 'ADMIN'].includes(value.role)) return null;
      return value;
    } catch { return null; }
  },
  logout(): void { sessionStorage.removeItem(SESSION_KEY); },
};
