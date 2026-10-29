import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import AppLayout from "./components/AppLayout";
import { useAuth, ROLE_ADMIN, ROLE_STAFF } from "./context/AuthContext";
import { homeFor } from "./components/menu";
import PublicNewsPage from "./pages/PublicNewsPage";
import LoginPage from "./pages/LoginPage";
import AccountPage from "./pages/AccountPage";
import CategoryPage from "./pages/CategoryPage";
import NewsPage from "./pages/NewsPage";
import HistoryPage from "./pages/HistoryPage";
import ProfilePage from "./pages/ProfilePage";

// "/app" index: send each role to its own first page
function AppIndex() {
  const { session } = useAuth();
  return <Navigate to={homeFor(session.accountRole)} replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<PublicNewsPage />} />
      <Route path="/login" element={<LoginPage />} />

      <Route
        path="/app"
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<AppIndex />} />
        <Route
          path="accounts"
          element={
            <ProtectedRoute roles={[ROLE_ADMIN]}>
              <AccountPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="categories"
          element={
            <ProtectedRoute roles={[ROLE_STAFF]}>
              <CategoryPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="news"
          element={
            <ProtectedRoute roles={[ROLE_STAFF]}>
              <NewsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="history"
          element={
            <ProtectedRoute roles={[ROLE_STAFF]}>
              <HistoryPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="profile"
          element={
            <ProtectedRoute roles={[ROLE_STAFF]}>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
