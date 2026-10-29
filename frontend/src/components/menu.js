import { ROLE_ADMIN, ROLE_STAFF } from "../context/AuthContext";

// What each role sees in the sidebar. The first item is the landing page after login.
export const MENU = {
  [ROLE_ADMIN]: [{ to: "/app/accounts", label: "Accounts" }],
  [ROLE_STAFF]: [
    { to: "/app/news", label: "News" },
    { to: "/app/categories", label: "Categories" },
    { to: "/app/history", label: "My news history" },
    { to: "/app/profile", label: "My profile" },
  ],
};

export const homeFor = (role) => (MENU[role] || [])[0]?.to || "/login";
