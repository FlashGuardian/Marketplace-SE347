import { Button, Tag } from 'antd';
import { MessageOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import PagePlaceholder from '../../components/common/PagePlaceholder';
export default function ProductDetailPage() { const navigate=useNavigate(); return <div className="space-y-4"><div className="flex flex-wrap gap-2"><Tag color="green">Đang bán</Tag><Button icon={<ShoppingCartOutlined />} onClick={()=>navigate('/cart')}>Thêm vào giỏ</Button><Button icon={<MessageOutlined />} onClick={()=>navigate('/chat')}>Chat người bán</Button></div><PagePlaceholder title="Chi tiết sản phẩm" description="UC05 · Thông tin sản phẩm, người bán và các thao tác liên quan. Dữ liệu hiện đang là khung mẫu." /></div>; }
