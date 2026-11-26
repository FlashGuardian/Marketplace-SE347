// Tình trạng sản phẩm (ProductCondition) và định dạng giá hiển thị.
export const conditionLabels: Record<string, string> = {
  LIKE_NEW: 'Như mới',
  GOOD: 'Tốt',
  FAIR: 'Khá',
  POOR: 'Cũ',
};

export const conditionLabel = (level: string): string => conditionLabels[level] ?? level;

export const formatPrice = (price: number): string => `${new Intl.NumberFormat('vi-VN').format(price)} ₫`;
