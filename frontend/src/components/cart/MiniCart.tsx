import { useState } from 'react';
import { Button, Popover, Spin } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../../services/cartService';
import type { CartPreview } from '../../types/cart';

const formatPrice = (price: number) => new Intl.NumberFormat('vi-VN').format(price) + ' đ';

export default function MiniCart() {
  const navigate = useNavigate();
  const [preview, setPreview] = useState<CartPreview | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadPreview = async () => {
    setLoading(true);
    try {
      setPreview(await cartService.getPreview());
      setError(null);
    } catch {
      setError('Không thể tải giỏ hàng.');
    } finally {
      setLoading(false);
    }
  };

  const content = (
    <div className="w-72">
      {loading ? (
        <div className="grid place-items-center py-4"><Spin /></div>
      ) : error ? (
        <p role="alert" className="mb-3 text-red-600">{error}</p>
      ) : preview?.items.length ? (
        <ul className="mb-3 divide-y divide-slate-100">
          {preview.items.map((item) => (
            <li key={item.productId} className="flex items-center justify-between gap-3 py-2">
              <span className="truncate">{item.name}</span>
              <strong className="whitespace-nowrap">{formatPrice(item.price)}</strong>
            </li>
          ))}
        </ul>
      ) : (
        <p className="mb-3 text-slate-500">Giỏ hàng đang trống</p>
      )}
      <Button block type="primary" onClick={() => navigate('/cart')}>Xem giỏ hàng</Button>
    </div>
  );

  return (
    <Popover content={content} trigger="hover" onOpenChange={(open) => { if (open) void loadPreview(); }}>
      <Button type="text" icon={<ShoppingCartOutlined />} aria-label="Giỏ hàng" onClick={() => navigate('/cart')}>
        Giỏ hàng
      </Button>
    </Popover>
  );
}
