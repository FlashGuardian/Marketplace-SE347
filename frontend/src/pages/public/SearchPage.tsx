import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, Empty, Input, InputNumber, Pagination, Select, Skeleton } from 'antd';
import { useSearchParams } from 'react-router-dom';
import ProductCard from '../../components/common/ProductCard';
import { conditionLabels } from '../../components/common/productLabels';
import { catalogService } from '../../services/catalogService';
import type { CategoryOption, PageResponse, ProductListItem, SearchParams } from '../../services/catalogService';

const PAGE_SIZE = 12;
const SORTS = [
  { value: 'newest', label: 'Mới nhất' },
  { value: 'priceAsc', label: 'Giá tăng dần' },
  { value: 'priceDesc', label: 'Giá giảm dần' },
];

const numberParam = (value: string | null): number | undefined => {
  if (value === null || value.trim() === '') return undefined;
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : undefined;
};

// UC04 Tìm kiếm và lọc: từ khóa (tên và mô tả, không phân biệt hoa thường), danh mục, khoảng giá,
// tình trạng, sắp xếp. Điều kiện nằm trên địa chỉ trang nên tải lại hoặc quay lại vẫn giữ nguyên.
export default function SearchPage() {
  const [params, setParams] = useSearchParams();
  const q = params.get('q') ?? '';
  const categoryId = numberParam(params.get('categoryId'));
  const minPrice = numberParam(params.get('minPrice'));
  const maxPrice = numberParam(params.get('maxPrice'));
  const conditionLevel = params.get('conditionLevel') || undefined;
  const sort = (params.get('sort') as SearchParams['sort']) || 'newest';
  const page = Math.max(1, numberParam(params.get('page')) ?? 1);

  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [result, setResult] = useState<PageResponse<ProductListItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Giá đang nhập chưa áp dụng; chỉ ghi lên địa chỉ trang khi bấm Áp dụng.
  const [minInput, setMinInput] = useState<number | null>(minPrice ?? null);
  const [maxInput, setMaxInput] = useState<number | null>(maxPrice ?? null);
  const [priceError, setPriceError] = useState<string | null>(null);

  useEffect(() => {
    setMinInput(minPrice ?? null);
    setMaxInput(maxPrice ?? null);
  }, [minPrice, maxPrice]);

  useEffect(() => {
    catalogService.categories().then(setCategories).catch(() => setCategories([]));
  }, []);

  const loadProducts = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      setResult(await catalogService.search({ q: q || undefined, categoryId, minPrice, maxPrice, conditionLevel, sort, page, size: PAGE_SIZE }));
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [q, categoryId, minPrice, maxPrice, conditionLevel, sort, page]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  // Cập nhật một số điều kiện, các điều kiện còn lại giữ nguyên; đổi điều kiện thì về trang 1.
  const update = (changes: Record<string, string | number | undefined>, keepPage = false) => {
    const next = new URLSearchParams(params);
    Object.entries(changes).forEach(([key, value]) => {
      if (value === undefined || value === '') next.delete(key);
      else next.set(key, String(value));
    });
    if (!keepPage) next.delete('page');
    setParams(next);
  };

  const applyPrice = () => {
    if (minInput !== null && maxInput !== null && minInput > maxInput) {
      setPriceError('Giá thấp nhất không được lớn hơn giá cao nhất.');
      return;
    }
    setPriceError(null);
    update({ minPrice: minInput ?? undefined, maxPrice: maxInput ?? undefined });
  };

  const clearAll = () => {
    setPriceError(null);
    setParams(q ? { q } : {});
  };

  const filtered = Boolean(categoryId || minPrice !== undefined || maxPrice !== undefined || conditionLevel);
  const priceFormatter = (value?: number | string) => `${value ?? ''}`.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  const priceParser = (value?: string) => Number((value ?? '').replace(/\./g, ''));

  return (
    <div className="space-y-5">
      <Input.Search
        key={q}
        defaultValue={q}
        placeholder="Tìm theo tên hoặc mô tả sản phẩm..."
        size="large"
        allowClear
        maxLength={100}
        enterButton="Tìm kiếm"
        onSearch={(value) => update({ q: value.trim() })}
        className="max-w-2xl"
      />

      <div className="flex flex-wrap items-start gap-3 rounded-xl bg-slate-50 p-4">
        <Select
          placeholder="Danh mục"
          allowClear
          className="w-52"
          value={categoryId}
          options={categories.map((category) => ({ value: category.id, label: category.name }))}
          onChange={(value) => update({ categoryId: value })}
        />
        <Select
          placeholder="Tình trạng"
          allowClear
          className="w-40"
          value={conditionLevel}
          options={Object.entries(conditionLabels).map(([value, label]) => ({ value, label }))}
          onChange={(value) => update({ conditionLevel: value })}
        />
        <div>
          <div className="flex items-center gap-2">
            <InputNumber<number> min={0} placeholder="Giá từ" className="w-32" value={minInput} formatter={priceFormatter} parser={priceParser} onChange={setMinInput} />
            <span>đến</span>
            <InputNumber<number> min={0} placeholder="Giá đến" className="w-32" value={maxInput} formatter={priceFormatter} parser={priceParser} onChange={setMaxInput} />
            <Button onClick={applyPrice}>Áp dụng</Button>
          </div>
          {priceError && <div role="alert" className="mt-1 text-sm text-red-600">{priceError}</div>}
        </div>
        <Select
          className="w-44"
          value={sort}
          options={SORTS}
          onChange={(value) => update({ sort: value === 'newest' ? undefined : value })}
          aria-label="Sắp xếp"
        />
        {(filtered || q) && <Button type="link" onClick={clearAll}>Xóa bộ lọc</Button>}
      </div>

      {result && !loading && !error && (
        <div className="text-sm text-slate-500">
          {result.totalItems > 0 ? `${result.totalItems} sản phẩm${q ? ` cho "${q}"` : ''}` : ''}
        </div>
      )}

      {error && (
        <Alert type="error" showIcon message="Không tải được kết quả tìm kiếm." action={<Button size="small" onClick={() => void loadProducts()}>Thử lại</Button>} />
      )}

      {loading && !error && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {Array.from({ length: 4 }, (_, index) => <Skeleton key={index} active />)}
        </div>
      )}

      {!loading && !error && result && result.items.length === 0 && (
        <Empty description="Không tìm thấy sản phẩm phù hợp." />
      )}

      {!loading && !error && result && result.items.length > 0 && (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {result.items.map((product) => <ProductCard key={product.id} product={product} />)}
          </div>
          {result.totalPages > 1 && (
            <div className="mt-6 flex justify-center">
              <Pagination
                current={result.page}
                pageSize={result.size}
                total={result.totalItems}
                showSizeChanger={false}
                onChange={(next) => {
                  update({ page: next }, true);
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
}
