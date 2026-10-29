import { Button, Nav } from "react-bootstrap";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { MENU } from "./menu";
import AuthorCredit from "./AuthorCredit";
import logo from "../assets/logo.svg";

const ROLE_LABEL = { 1: "Admin", 2: "Staff" };

export default function AppLayout() {
  const { session, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="admin-shell">
      <header className="admin-header">
        <div className="brand">
          <img src={logo} alt="logo" />
          <div className="brand-copy">
            <span>FUNewsManagementSystem</span>
            <AuthorCredit />
          </div>
        </div>
        <div className="d-flex align-items-center gap-3">
          <span>
            {session.accountName} ({ROLE_LABEL[session.accountRole]})
          </span>
          <Button size="sm" variant="outline-light" onClick={handleLogout}>
            Log out
          </Button>
        </div>
      </header>
      <div className="admin-body">
        <aside className="admin-sidebar">
          <Nav className="flex-column flex-md-column flex-row">
            {(MENU[session.accountRole] || []).map((item) => (
              <NavLink key={item.to} to={item.to} className="nav-link">
                {item.label}
              </NavLink>
            ))}
          </Nav>
        </aside>
        <main className="admin-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
