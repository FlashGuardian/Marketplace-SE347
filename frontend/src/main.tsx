import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';
import 'antd/dist/reset.css';
import './index.css';
import App from './App';
import { AuthProvider } from './contexts/AuthContext';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider locale={viVN} theme={{ token: { colorPrimary: '#1677ff', borderRadius: 8, fontFamily: 'Inter, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif' } }}>
      <AuthProvider><App /></AuthProvider>
    </ConfigProvider>
  </React.StrictMode>,
);