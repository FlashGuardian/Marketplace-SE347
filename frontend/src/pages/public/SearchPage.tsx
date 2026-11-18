import { Input, Select } from 'antd';
import { useSearchParams } from 'react-router-dom';
import PagePlaceholder from '../../components/common/PagePlaceholder';
export default function SearchPage() { const [params] = useSearchParams(); return <div className="space-y-5"><div className="flex flex-wrap gap-3"><Input.Search defaultValue={params.get('q') ?? ''} placeholder="Tìm tên sản phẩm..." className="max-w-xl" /><Select placeholder="Danh mục" className="w-52" options={['Sách và giáo trình','Điện tử','Đồ gia dụng','Quần áo','Dụng cụ học tập','Xe đạp','Khác'].map(x=>({label:x,value:x}))} /></div><PagePlaceholder title="Tìm kiếm và lọc sản phẩm" description="UC03–UC05 · Xem danh sách, tìm kiếm, lọc và xem chi tiết sản phẩm." /></div>; }
