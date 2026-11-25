import apiClient from './apiClient';

export interface Category {
  id: number;
  name: string;
  description: string | null;
  isActive: boolean;
  productCount: number;
}

export interface CategoryInput {
  name: string;
  description?: string;
}

export interface ApiErrorBody {
  code?: string;
  message?: string;
  fields?: Record<string, string>;
}

// UC19: /api/admin/categories (chỉ Admin).
export const categoryService = {
  list: async (): Promise<Category[]> => (await apiClient.get<Category[]>('/admin/categories')).data,
  create: async (input: CategoryInput): Promise<number> =>
    (await apiClient.post<{ id: number }>('/admin/categories', input)).data.id,
  update: async (id: number, input: CategoryInput): Promise<void> => {
    await apiClient.patch(`/admin/categories/${id}`, input);
  },
  setActive: async (id: number, isActive: boolean): Promise<void> => {
    await apiClient.patch(`/admin/categories/${id}/active`, { isActive });
  },
  remove: async (id: number): Promise<void> => {
    await apiClient.delete(`/admin/categories/${id}`);
  },
};

// Lấy mã lỗi và thông báo tiếng Việt từ phản hồi lỗi {code, message, fields?} của backend.
export function readApiError(error: unknown): { status?: number; code?: string; message: string } {
  const response = (error as { response?: { status?: number; data?: ApiErrorBody } })?.response;
  if (!response) return { message: 'Không kết nối được máy chủ. Vui lòng thử lại.' };
  if (response.status === 401) return { status: 401, message: 'Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.' };
  if (response.status === 403) return { status: 403, message: 'Bạn không có quyền thực hiện thao tác này.' };
  return {
    status: response.status,
    code: response.data?.code,
    message: response.data?.message ?? 'Có lỗi xảy ra, vui lòng thử lại.',
  };
}
