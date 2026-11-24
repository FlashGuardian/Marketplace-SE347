export interface CartItem {
  productId: number;
  name: string;
  price: number;
  selected: boolean;
  available: boolean;
  addedAt: string;
}

export interface CartSellerGroup {
  sellerId: number;
  sellerName: string;
  items: CartItem[];
}

export interface Cart {
  sellers: CartSellerGroup[];
  selectedTotal: number;
  canCheckout: boolean;
}

export interface CartPreviewItem {
  productId: number;
  name: string;
  price: number;
}

export interface CartPreview {
  items: CartPreviewItem[];
}
