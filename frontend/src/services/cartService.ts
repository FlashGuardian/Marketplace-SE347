import apiClient from './apiClient';
import type { Cart, CartPreview, CartItem } from '../types/cart';

export const cartService = {
  async getCart(): Promise<Cart> {
    const response = await apiClient.get<Cart>('/cart');
    return response.data;
  },

  async updateSelection(productId: number, selected: boolean): Promise<CartItem> {
    const response = await apiClient.patch<CartItem>(`/cart/items/${productId}/selection`, { selected });
    return response.data;
  },

  async removeItem(productId: number): Promise<void> {
    await apiClient.delete(`/cart/items/${productId}`);
  },

  async getPreview(): Promise<CartPreview> {
    const response = await apiClient.get<CartPreview>('/cart/preview');
    return response.data;
  },
};
