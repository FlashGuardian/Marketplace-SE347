import apiClient from './apiClient';

export interface ProductListItem {
  id: number;
  name: string;
  price: number;
  conditionLevel: string;
  status: 'SELLING' | 'HELD';
  thumbnailUrl: string | null;
  createdAt: string;
}

export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface CategoryOption {
  id: number;
  name: string;
}

export interface SearchParams {
  q?: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  conditionLevel?: string;
  sort?: 'newest' | 'priceAsc' | 'priceDesc';
  page?: number;
  size?: number;
}

// UC03, UC04: danh sách, tìm kiếm sản phẩm và danh mục công khai (không cần đăng nhập).
export const catalogService = {
  categories: async (): Promise<CategoryOption[]> => (await apiClient.get<CategoryOption[]>('/categories')).data,
  products: async (params: { categoryId?: number; page?: number; size?: number }): Promise<PageResponse<ProductListItem>> =>
    (await apiClient.get<PageResponse<ProductListItem>>('/products', { params })).data,
  search: async (params: SearchParams): Promise<PageResponse<ProductListItem>> =>
    (await apiClient.get<PageResponse<ProductListItem>>('/products/search', { params })).data,
};
