import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, Checkbox, Empty, Modal, Spin } from 'antd';
import { DeleteOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import type { Cart } from '../../types/cart';
import { cartService } from '../../services/cartService';

const formatPrice = (price: number) => new Intl.NumberFormat('vi-VN').format(price) + ' đ';

export default function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [changingProductId, setChangingProductId] = useState<number | null>(null);

  const loadCart = useCallback(async () => {
    setLoading(true);
    try {
      setCart(await cartService.getCart());
      setError(null);
    } catch {
      setError('Không thể tải giỏ hàng. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadCart();
  }, [loadCart]);

  const updateSelection = async (productId: number, selected: boolean) => {
    setChangingProductId(productId);
    try {
      await cartService.updateSelection(productId, selected);
      await loadCart();
    } catch {
      setError('Không thể cập nhật lựa chọn. Vui lòng thử lại.');
    } finally {
      setChangingProductId(null);
    }
  };

  const removeItem = (productId: number, name: string) => {
    Modal.confirm({
      title: 'Xóa sản phẩm khỏi giỏ?',
      content: `Bạn có chắc muốn xóa "${name}" khỏi giỏ hàng không?`,
      okText: 'Xóa',
      cancelText: 'Giữ lại',
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          await cartService.removeItem(productId);
          await loadCart();
        } catch {
          setError('Không thể xóa sản phẩm. Vui lòng thử lại.');
        }
      },
    });
  };

  if (loading && !cart) {
    return <div className="grid min-h-[45vh] place-items-center"><Spin size="large" /></div>;
  }

  return (
    <section className="mx-auto max-w-5xl px-4 py-8">
      <h1 className="mb-6 flex items-center gap-3 text-2xl font-semibold">
        <ShoppingCartOutlined /> Giỏ hàng
      </h1>
      {error && <Alert className="mb-4" type="error" showIcon message={error} action={<Button onClick={() => void loadCart()}>Thử lại</Button>} />}
      {cart && cart.sellers.length === 0 ? (
        <Empty description="Giỏ hàng của bạn đang trống" />
      ) : (
        <div className="space-y-5">
          {cart?.sellers.map((seller) => (
            <section key={seller.sellerId} className="rounded-lg border border-slate-200 bg-white p-4">
              <h2 className="mb-3 text-lg font-medium">{seller.sellerName}</h2>
              <div className="divide-y divide-slate-100">
                {seller.items.map((item) => (
                  <div key={item.productId} className="flex items-center gap-4 py-4">
                    <Checkbox
                      checked={item.selected}
                      disabled={!item.available || changingProductId === item.productId}
                      onChange={(event) => void updateSelection(item.productId, event.target.checked)}
                      aria-label={`Chọn ${item.name}`}
                    />
                    <div className="min-w-0 flex-1">
                      <p className="mb-1 truncate font-medium">{item.name}</p>
                      {!item.available && <span className="text-sm text-red-600">Không khả dụng</span>}
                    </div>
                    <strong className="whitespace-nowrap">{formatPrice(item.price)}</strong>
                    <Button
                      danger
                      type="text"
                      icon={<DeleteOutlined />}
                      aria-label={`Xóa ${item.name}`}
                      onClick={() => removeItem(item.productId, item.name)}
                    />
                  </div>
                ))}
              </div>
            </section>
          ))}
          {cart && (
            <div className="flex flex-wrap items-center justify-between gap-4 rounded-lg bg-white p-4 shadow-sm">
              <div>
                <span className="mr-3 text-slate-600">Tổng tiền đã chọn</span>
                <strong className="text-xl">{formatPrice(cart.selectedTotal)}</strong>
              </div>
              <Button type="primary" size="large" disabled={!cart.canCheckout}>
                Đặt hàng
              </Button>
            </div>
          )}
        </div>
      )}
    </section>
  );
}
