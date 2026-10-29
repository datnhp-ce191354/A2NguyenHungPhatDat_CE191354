import axios from "axios";

export const SESSION_KEY = "a2_nguyenhungphatdat_ce191354_session";

// Point the client at a different backend with VITE_API_BASE_URL when needed.
// The default matches the local Spring Boot server.
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || "http://localhost:8080/api";
const http = axios.create({ baseURL: apiBaseUrl.replace(/\/+$/, "") });

// Every request carries the id of the signed-in account; the backend checks its role.
http.interceptors.request.use((config) => {
  try {
    const session = JSON.parse(localStorage.getItem(SESSION_KEY));
    if (session?.accountId) {
      config.headers["X-Account-Id"] = session.accountId;
    }
  } catch {
    // broken JSON in storage: send the request without the header
  }
  return config;
});

// Always returns a plain string, so it is safe to render inside <Alert>.
// (Rendering an object there is what makes React show a blank page.)
export function getErrorMessage(error) {
  const data = error?.response?.data;
  if (typeof data === "string" && data) return data;
  if (data && typeof data === "object") {
    return data.message || data.error || "Request failed.";
  }
  if (error?.request) return "Cannot connect to the server.";
  return "Unexpected error.";
}

export default http;
