import { createContext, useContext, useState } from "react";
import { authApi } from "../api/resources";
import { SESSION_KEY } from "../api/http";

const AuthContext = createContext(null);

export const ROLE_ADMIN = 1;
export const ROLE_STAFF = 2;

const loadSession = () => {
  try {
    return JSON.parse(localStorage.getItem(SESSION_KEY));
  } catch {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [session, setSession] = useState(loadSession);

  const save = (value) => {
    if (value) localStorage.setItem(SESSION_KEY, JSON.stringify(value));
    else localStorage.removeItem(SESSION_KEY);
    setSession(value);
  };

  // Throws when the backend rejects the credentials; LoginPage shows the message.
  const login = async (email, password) => {
    const account = await authApi.login(email, password);
    save(account);
    return account;
  };

  const logout = () => save(null);

  const updateSession = (partial) => save({ ...session, ...partial });

  return (
    <AuthContext.Provider value={{ session, login, logout, updateSession }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
