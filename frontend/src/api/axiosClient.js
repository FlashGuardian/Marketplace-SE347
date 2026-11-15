import axios from "axios";

export const TOKEN_KEY = "token";

// Instance axios dùng chung cho toàn bộ ứng dụng.
const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  timeout: 15000,
});

// Gắn token vào mọi request nếu đã đăng nhập.
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// BE trả về { success, message, data } nên ở đây trả thẳng response.data.
// Khi lỗi: reject ra object { status, message, data } để màn hình hiển thị message.
axiosClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status;
    const message =
      error.response?.data?.message ||
      (error.request ? "Không kết nối được máy chủ" : error.message);

    // Token hết hạn hoặc không hợp lệ: xóa token và về trang đăng nhập.
    if (status === 401 && window.location.pathname !== "/login") {
      localStorage.removeItem(TOKEN_KEY);
      window.location.href = "/login";
    }
    return Promise.reject({ status, message, data: error.response?.data });
  }
);

export default axiosClient;
