import { Button, Card, Tag, Tooltip } from 'antd';
import { PictureOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { ProductListItem } from '../../services/catalogService';
import { conditionLabel, formatPrice } from './productLabels';

// Thẻ sản phẩm trong danh sách (UC03) và kết quả tìm kiếm (UC04).
// QĐ-05: sản phẩm Đang giữ có nhãn "Đang giữ" và nút mua bị vô hiệu hóa; chỉ sản phẩm Đang bán được mua.
export default function ProductCard({ product }: { product: ProductListItem }) {
  const navigate = useNavigate();
  const held = product.status === 'HELD';
  const open = () => navigate(`/products/${product.id}`);

  return (
    <Card hoverable onClick={open} data-testid="product-card" styles={{ body: { display: 'flex', flexDirection: 'column', gap: 8 } }}>
      <div className="relative grid h-40 place-items-center overflow-hidden rounded-lg bg-slate-100 text-5xl text-slate-300">
        {product.thumbnailUrl ? (
          <img src={product.thumbnailUrl} alt={product.name} className="h-full w-full object-cover" loading="lazy" />
        ) : (
          <PictureOutlined aria-label="Chưa có ảnh" />
        )}
        {held && (
          <Tag color="orange" className="absolute left-2 top-2 m-0">Đang giữ</Tag>
        )}
      </div>
      <h3 className="m-0 line-clamp-2 min-h-[48px] font-semibold">{product.name}</h3>
      <div className="text-xl font-bold text-blue-600">{formatPrice(product.price)}</div>
      <div className="flex items-center justify-between gap-2">
        <Tag color="green" className="m-0">{conditionLabel(product.conditionLevel)}</Tag>
        <Tooltip title={held ? 'Sản phẩm đang được giữ cho một đơn hàng khác.' : ''}>
          <span onClick={(event) => event.stopPropagation()}>
            <Button type="primary" disabled={held} onClick={open}>Mua ngay</Button>
          </span>
        </Tooltip>
      </div>
    </Card>
  );
}
