import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, Empty, Pagination, Skeleton, Tag } from 'antd';
import { ArrowRightOutlined, ShoppingOutlined } from '@ant-design/icons';
import { useNavigate, useSearchParams } from 'react-router-dom';
import ProductCard from '../../components/common/ProductCard';
import { catalogService } from '../../services/catalogService';
import type { CategoryOption, PageResponse, ProductListItem } from '../../services/catalogService';

const PAGE_SIZE = 12;

// UC03 Xem danh sách sản phẩm: công khai, mới nhất trước, lọc theo danh mục, có phân trang.
// Bộ lọc nằm trên địa chỉ trang (?categoryId=&page=) để tải lại hoặc quay lại vẫn giữ nguyên.
export default function HomePage() {
  const navigate = useNavigate();
  const [params, setParams] = useSearchParams();
  const categoryId = Number(params.get('categoryId')) || undefined;
  const page = Math.max(1, Number(params.get('page')) || 1);

  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [result, setResult] = useState<PageResponse<ProductListItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    catalogService.categories().then(setCategories).catch(() => setCategories([]));
  }, []);

  const loadProducts = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      setResult(await catalogService.products({ categoryId, page, size: PAGE_SIZE }));
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [categoryId, page]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  const select = (id?: number) => setParams(id ? { categoryId: String(id) } : {});
  const goToPage = (next: number) => {
    const nextParams: Record<string, string> = { page: String(next) };
    if (categoryId) nextParams.categoryId = String(categoryId);
    setParams(nextParams);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="space-y-8">
      <section className="grid items-center gap-6 rounded-2xl bg-blue-50 p-8 md:grid-cols-[1fr_1fr_auto]">
        <div>
          <Tag color="blue">GÓC ĐỒ CŨ • GIÁ SINH VIÊN</Tag>
          <h1 className="mb-2 mt-3 text-3xl font-bold leading-tight text-slate-800 md:text-4xl">Đồ bạn cần.<br />Giá bạn thích.</h1>
        </div>
        <div>
          <p className="text-lg text-slate-500">Mua bán đồ đã qua sử dụng, kết nối cùng cộng đồng sinh viên ngay quanh bạn.</p>
          <Button type="primary" size="large" onClick={() => navigate('/search')}>Khám phá ngay <ArrowRightOutlined /></Button>
        </div>
        <div className="hidden h-36 w-48 items-center justify-center rounded-xl bg-blue-100 text-6xl text-blue-400 md:flex"><ShoppingOutlined /></div>
      </section>

      <section>
        <h2 className="mb-4 text-xl font-semibold">Bạn đang tìm gì?</h2>
        <div className="flex flex-wrap gap-2">
          <Button type={categoryId ? 'default' : 'primary'} onClick={() => select()}>Tất cả</Button>
          {categories.map((category) => (
            <Button key={category.id} type={categoryId === category.id ? 'primary' : 'default'} onClick={() => select(category.id)}>
              {category.name}
            </Button>
          ))}
        </div>
      </section>

      <section>
        <div className="mb-4 flex items-end justify-between">
          <div>
            <h2 className="mb-1 text-xl font-semibold">Sản phẩm mới đăng</h2>
            <p className="m-0 text-sm text-slate-500">Hiện cả sản phẩm Đang bán và Đang giữ; sản phẩm Đang giữ chưa thể mua.</p>
          </div>
          <Button type="link" onClick={() => navigate('/search')}>Tìm kiếm nâng cao</Button>
        </div>

        {error && (
          <Alert
            type="error"
            showIcon
            message="Không tải được danh sách sản phẩm."
            action={<Button size="small" onClick={() => void loadProducts()}>Thử lại</Button>}
          />
        )}

        {loading && !error && (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {Array.from({ length: 4 }, (_, index) => <Skeleton key={index} active />)}
          </div>
        )}

        {!loading && !error && result && result.items.length === 0 && (
          <Empty description="Chưa có sản phẩm nào trong mục này." />
        )}

        {!loading && !error && result && result.items.length > 0 && (
          <>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              {result.items.map((product) => <ProductCard key={product.id} product={product} />)}
            </div>
            {result.totalPages > 1 && (
              <div className="mt-6 flex justify-center">
                <Pagination current={result.page} pageSize={result.size} total={result.totalItems} showSizeChanger={false} onChange={goToPage} />
              </div>
            )}
          </>
        )}
      </section>
    </div>
  );
}
