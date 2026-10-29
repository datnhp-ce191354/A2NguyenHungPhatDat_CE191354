import http from "./http";

// One small factory builds list / create / update / remove for any REST resource.
function createResource(path) {
  return {
    list: (keyword) =>
      http.get(path, { params: keyword ? { keyword } : {} }).then((r) => r.data),
    create: (data) => http.post(path, data).then((r) => r.data),
    update: (id, data) => http.put(`${path}/${id}`, data).then((r) => r.data),
    remove: (id) => http.delete(`${path}/${id}`),
  };
}

export const accountApi = createResource("/accounts");
export const categoryApi = createResource("/categories");

export const newsApi = {
  ...createResource("/news"),
  history: (keyword) =>
    http.get("/news/my-history", { params: keyword ? { keyword } : {} }).then((r) => r.data),
  listPublic: (keyword) =>
    http.get("/news/public", { params: keyword ? { keyword } : {} }).then((r) => r.data),
};

export const tagApi = {
  list: () => http.get("/tags").then((r) => r.data),
  create: (data) => http.post("/tags", data).then((r) => r.data),
};

export const authApi = {
  login: (email, password) => http.post("/auth/login", { email, password }).then((r) => r.data),
};

export const profileApi = {
  get: () => http.get("/profile").then((r) => r.data),
  update: (data) => http.put("/profile", data).then((r) => r.data),
};
