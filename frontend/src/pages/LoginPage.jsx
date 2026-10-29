import { useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { getErrorMessage } from "../api/http";
import { useAuth } from "../context/AuthContext";
import { homeFor } from "../components/menu";
import AuthorCredit from "../components/AuthorCredit";
import logo from "../assets/logo.svg";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const DEMO_ACCOUNTS = [
  { label: "Admin", email: "admin@funews.com", password: "Admin@123" },
  { label: "Staff", email: "staff@funews.com", password: "Staff@123" },
];

export default function LoginPage() {
  const { session, login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (session) return <Navigate to={homeFor(session.accountRole)} replace />;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.email.trim() || !form.password) {
      setError("Please enter both email and password.");
      return;
    }
    if (!EMAIL_PATTERN.test(form.email.trim())) {
      setError("Enter the full email address, for example admin@funews.com.");
      return;
    }
    setSubmitting(true);
    try {
      const account = await login(form.email.trim(), form.password);
      navigate(homeFor(account.accountRole), { replace: true }); // Admin and Staff land on different pages
    } catch (err) {
      setError(getErrorMessage(err)); // e.g. "Wrong email or password."
      setSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <header className="public-top">
        <div className="brand">
          <img src={logo} alt="logo" />
          <div className="brand-copy">
            <span>FUNewsManagementSystem</span>
            <AuthorCredit />
          </div>
        </div>
        <Button as={Link} to="/" size="sm" variant="outline-light">
          Read the news
        </Button>
      </header>

      <div className="login-wrap">
        <div className="login-card">
          <div className="text-center mb-4">
            <img src={logo} alt="logo" />
            <h4 className="mt-3 mb-0">FUNewsManagementSystem</h4>
            <small className="text-muted">Staff and admin sign in</small>
          </div>

          {error && <Alert variant="danger">{error}</Alert>}

          <Form onSubmit={handleSubmit} noValidate>
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                placeholder="admin@funews.com"
                autoComplete="username"
                value={form.email}
                onChange={handleChange}
                autoFocus
              />
              <Form.Text muted>Use your account email, not the role name.</Form.Text>
            </Form.Group>
            <Form.Group className="mb-4">
              <Form.Label>Password</Form.Label>
              <Form.Control type="password" name="password" autoComplete="current-password" value={form.password} onChange={handleChange} />
            </Form.Group>
            <Button type="submit" className="btn-forest w-100" disabled={submitting}>
              {submitting ? "Signing in..." : "Sign in"}
            </Button>
          </Form>

          <div className="mt-3 pt-3 border-top">
            <small className="text-muted d-block mb-2">Demo accounts for a new database:</small>
            <div className="d-flex flex-wrap gap-2">
              {DEMO_ACCOUNTS.map(({ label, email, password }) => (
                <Button
                  key={label}
                  type="button"
                  size="sm"
                  variant="outline-secondary"
                  onClick={() => {
                    setForm({ email, password });
                    setError("");
                  }}
                >
                  Fill {label}: {email}
                </Button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
