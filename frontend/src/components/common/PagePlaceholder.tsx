import { Card, Empty, Tag } from 'antd';
import type { ReactNode } from 'react';

interface PagePlaceholderProps { title: string; description: string; icon?: ReactNode; note?: string; }
export default function PagePlaceholder({ title, description, icon, note = 'Khung giao diện đã được khởi tạo. Nghiệp vụ và dữ liệu sẽ được triển khai ở giai đoạn tiếp theo.' }: PagePlaceholderProps) {
  return <div className="space-y-5">
    <div><div className="mb-2"><Tag color="blue">PAGE SKELETON</Tag></div><h1 className="m-0 text-2xl font-bold text-slate-800">{title}</h1><p className="mb-0 mt-2 text-slate-500">{description}</p></div>
    <Card className="content-card" bordered={false}>
      <div className="flex min-h-[300px] flex-col items-center justify-center gap-4 text-center">
        <div className="placeholder-icon">{icon ?? '◇'}</div>
        <div><h2 className="mb-2 text-lg font-semibold text-slate-700">{title}</h2><p className="max-w-xl text-slate-500">{note}</p></div>
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Chưa có dữ liệu để hiển thị" />
      </div>
    </Card>
  </div>;
}
