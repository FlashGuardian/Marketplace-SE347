import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, Form, Input, Modal, Popconfirm, Space, Table, Tag, Tooltip, Typography, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined } from '@ant-design/icons';
import { categoryService, readApiError } from '../../services/categoryService';
import type { Category } from '../../services/categoryService';

interface FormValues {
  name: string;
  description?: string;
}

// UC19 Quản lý danh mục: thêm, sửa, vô hiệu hóa hoặc kích hoạt, xóa khi chưa có sản phẩm (BR-10).
export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Category | null>(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm<FormValues>();

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    try {
      setCategories(await categoryService.list());
    } catch (error) {
      setLoadError(readApiError(error).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    setModalOpen(true);
  };

  const openEdit = (category: Category) => {
    setEditing(category);
    form.setFieldsValue({ name: category.name, description: category.description ?? '' });
    setModalOpen(true);
  };

  const submit = async (values: FormValues) => {
    setSaving(true);
    try {
      if (editing) {
        await categoryService.update(editing.id, values);
        message.success('Đã cập nhật danh mục.');
      } else {
        await categoryService.create(values);
        message.success('Đã thêm danh mục.');
      }
      setModalOpen(false);
      await load();
    } catch (error) {
      const { code, message: text } = readApiError(error);
      if (code === 'CATEGORY_NAME_EXISTS' || code === 'NAME_REQUIRED') {
        form.setFields([{ name: 'name', errors: [text] }]);
      } else {
        message.error(text);
      }
    } finally {
      setSaving(false);
    }
  };

  const toggle = async (category: Category) => {
    try {
      await categoryService.setActive(category.id, !category.isActive);
      message.success(category.isActive ? 'Đã vô hiệu hóa danh mục.' : 'Đã kích hoạt danh mục.');
      await load();
    } catch (error) {
      message.error(readApiError(error).message);
    }
  };

  const remove = async (category: Category) => {
    try {
      await categoryService.remove(category.id);
      message.success('Đã xóa danh mục.');
      await load();
    } catch (error) {
      message.error(readApiError(error).message);
      await load();
    }
  };

  const columns: ColumnsType<Category> = [
    { title: 'Tên danh mục', dataIndex: 'name', key: 'name' },
    { title: 'Mô tả', dataIndex: 'description', key: 'description', render: (value: string | null) => value ?? '' },
    { title: 'Số sản phẩm', dataIndex: 'productCount', key: 'productCount', align: 'right' },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (active: boolean) => (active ? <Tag color="green">Hoạt động</Tag> : <Tag>Vô hiệu</Tag>),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      render: (_: unknown, category: Category) => (
        <Space>
          <Button size="small" onClick={() => openEdit(category)}>Sửa</Button>
          {category.isActive ? (
            <Popconfirm
              title="Vô hiệu hóa danh mục?"
              description="Danh mục sẽ không còn được chọn khi đăng sản phẩm mới."
              okText="Vô hiệu hóa"
              cancelText="Hủy"
              onConfirm={() => toggle(category)}
            >
              <Button size="small">Vô hiệu hóa</Button>
            </Popconfirm>
          ) : (
            <Button size="small" onClick={() => toggle(category)}>Kích hoạt</Button>
          )}
          {category.productCount > 0 ? (
            <Tooltip title="Danh mục đang có sản phẩm chỉ được vô hiệu hóa, không xóa.">
              <Button size="small" danger disabled>Xóa</Button>
            </Tooltip>
          ) : (
            <Popconfirm
              title="Xóa danh mục?"
              description="Thao tác này không thể hoàn tác."
              okText="Xóa"
              cancelText="Hủy"
              okButtonProps={{ danger: true }}
              onConfirm={() => remove(category)}
            >
              <Button size="small" danger>Xóa</Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Space style={{ width: '100%', justifyContent: 'space-between' }}>
        <Typography.Title level={3} style={{ margin: 0 }}>Quản lý danh mục</Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Thêm danh mục</Button>
      </Space>
      <Alert
        type="info"
        showIcon
        message="Danh mục đang có sản phẩm chỉ được vô hiệu hóa, không xóa. Tên danh mục không được trùng."
      />
      {loadError && (
        <Alert type="error" showIcon message={loadError} action={<Button size="small" onClick={() => void load()}>Thử lại</Button>} />
      )}
      <Table<Category>
        rowKey="id"
        columns={columns}
        dataSource={categories}
        loading={loading}
        pagination={false}
        locale={{ emptyText: 'Chưa có danh mục nào.' }}
      />
      <Modal
        title={editing ? 'Sửa danh mục' : 'Thêm danh mục'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        okText="Lưu"
        cancelText="Hủy"
        confirmLoading={saving}
        destroyOnHidden
        forceRender
      >
        <Form<FormValues> form={form} layout="vertical" onFinish={submit} preserve={false}>
          <Form.Item
            label="Tên danh mục"
            name="name"
            rules={[
              { required: true, whitespace: true, message: 'Tên danh mục không được để trống.' },
              { max: 100, message: 'Tên danh mục tối đa 100 ký tự.' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Mô tả" name="description" rules={[{ max: 500, message: 'Mô tả tối đa 500 ký tự.' }]}>
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
