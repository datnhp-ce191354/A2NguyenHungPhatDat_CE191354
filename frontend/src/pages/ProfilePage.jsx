import { useEffect, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import { getErrorMessage } from "../api/http";
import { profileApi } from "../api/resources";
import { useAuth } from "../context/AuthContext";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function ProfilePage() {
  const { updateSession } = useAuth();
  const [form, setForm] = useState({ accountName: "", accountEmail: "", newPassword: "" });
  const [errors, setErrors] = useState({});
  const [notice, setNotice] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    profileApi
      .get()
      .then((p) => setForm({ accountName: p.accountName, accountEmail: p.accountEmail, newPassword: "" }))
      .catch((e) => setNotice({ variant: "danger", text: getErrorMessage(e) }))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};
    if (!form.accountName.trim()) e.accountName = "Account name is required.";
    else if (form.accountName.trim().length > 100) e.accountName = "Account name must be 100 characters or fewer.";
    if (!form.accountEmail.trim()) e.accountEmail = "Email is required.";
    else if (!EMAIL_PATTERN.test(form.accountEmail.trim())) e.accountEmail = "Email is not valid.";
    if (form.newPassword && (form.newPassword.length < 6 || form.newPassword.length > 100))
      e.newPassword = "Password must be 6-100 characters.";
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const found = validate();
    if (Object.keys(found).length > 0) {
      setErrors(found);
      return;
    }
    try {
      const saved = await profileApi.update({
        accountName: form.accountName.trim(),
        accountEmail: form.accountEmail.trim(),
        ...(form.newPassword ? { newPassword: form.newPassword } : {}),
      });
      updateSession({ accountName: saved.accountName, accountEmail: saved.accountEmail }); // header shows the new name
      setForm((prev) => ({ ...prev, newPassword: "" }));
      setNotice({ variant: "success", text: "Profile updated." });
    } catch (error) {
      setNotice({ variant: "danger", text: getErrorMessage(error) });
    }
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div style={{ maxWidth: 480 }}>
      <h2 className="page-title">My profile</h2>
      {notice && (
        <Alert variant={notice.variant} dismissible onClose={() => setNotice(null)}>
          {notice.text}
        </Alert>
      )}
      <Form onSubmit={handleSubmit} noValidate>
        <Form.Group className="mb-3">
          <Form.Label>Account name</Form.Label>
          <Form.Control name="accountName" value={form.accountName} onChange={handleChange} isInvalid={!!errors.accountName} />
          <Form.Control.Feedback type="invalid">{errors.accountName}</Form.Control.Feedback>
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Email</Form.Label>
          <Form.Control name="accountEmail" value={form.accountEmail} onChange={handleChange} isInvalid={!!errors.accountEmail} />
          <Form.Control.Feedback type="invalid">{errors.accountEmail}</Form.Control.Feedback>
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>New password</Form.Label>
          <Form.Control type="password" name="newPassword" value={form.newPassword} onChange={handleChange} isInvalid={!!errors.newPassword} />
          <Form.Control.Feedback type="invalid">{errors.newPassword}</Form.Control.Feedback>
          <Form.Text className="text-muted">Leave empty to keep the current password.</Form.Text>
        </Form.Group>
        <Button type="submit" className="btn-forest">
          Save profile
        </Button>
      </Form>
    </div>
  );
}
