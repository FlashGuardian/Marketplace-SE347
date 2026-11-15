import axiosClient from "./axiosClient";

// Mỗi module một file như này (productApi.js, orderApi.js, ...). Đường dẫn theo danh sách API của nhóm.
export const authApi = {
  login: (payload) => axiosClient.post("/auth/login", payload),
  register: (payload) => axiosClient.post("/auth/register", payload),
  me: () => axiosClient.get("/users/me"),
};
