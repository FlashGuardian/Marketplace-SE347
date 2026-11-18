export const appRoutes = {
  home: '/', login: '/login', register: '/register', search: '/search', product: '/products/:productId',
  cart: '/cart', checkout: '/checkout', sell: '/sell', myProducts: '/my-products', orders: '/my-orders',
  chat: '/chat', notifications: '/notifications', profile: '/profile', changePassword: '/change-password',
  admin: '/admin', dashboard: '/admin/dashboard', accounts: '/admin/accounts', categories: '/admin/categories',
  moderation: '/admin/products', reports: '/admin/reports', statistics: '/admin/statistics', forbidden: '/403', notFound: '*',
} as const;
