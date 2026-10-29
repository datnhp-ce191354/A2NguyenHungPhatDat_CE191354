import { useState } from "react";
import { Alert, Button, Form, Modal } from "react-bootstrap";

// Popup used for both Create and Update (the assignment requires a popup dialog).
// onSubmit(values) may return a string: that is an error message from the server,
// shown at the top of the dialog while the dialog stays open.
export default function FormDialog({
  title,
  fields,
  initialValues,
  validate,
  submitLabel,
  onSubmit,
  onClose,
}) {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [saving, setSaving] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setValues((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
    setServerError("");
  };

  const toggleCheckbox = (name, optionValue) => {
    setValues((prev) => {
      const current = prev[name] || [];
      const next = current.includes(optionValue)
        ? current.filter((v) => v !== optionValue)
        : [...current, optionValue];
      return { ...prev, [name]: next };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const found = validate(values);
    if (Object.keys(found).length > 0) {
      setErrors(found);
      return;
    }
    setSaving(true);
    const message = await onSubmit(values);
    if (message) {
      setServerError(message);
      setSaving(false);
    }
  };

  const renderControl = (field) => {
    if (field.type === "checkboxes") {
      return (
        <div className="d-flex flex-wrap gap-3">
          {field.options.length === 0 && <span className="text-muted">No options yet.</span>}
          {field.options.map((opt) => (
            <Form.Check
              key={opt.value}
              type="checkbox"
              id={`${field.name}-${opt.value}`}
              label={opt.label}
              checked={(values[field.name] || []).includes(opt.value)}
              onChange={() => toggleCheckbox(field.name, opt.value)}
            />
          ))}
        </div>
      );
    }
    const common = {
      name: field.name,
      value: values[field.name] ?? "",
      onChange: handleChange,
      isInvalid: !!errors[field.name],
    };
    if (field.type === "select") {
      return (
        <Form.Select {...common}>
          {field.placeholder && <option value="">{field.placeholder}</option>}
          {field.options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </Form.Select>
      );
    }
    if (field.type === "textarea") {
      return <Form.Control as="textarea" rows={4} {...common} />;
    }
    return <Form.Control type={field.type || "text"} placeholder={field.placeholder} {...common} />;
  };

  return (
    <Modal show onHide={onClose} centered size="lg">
      <Form onSubmit={handleSubmit} noValidate>
        <Modal.Header closeButton>
          <Modal.Title>{title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {serverError && <Alert variant="danger">{serverError}</Alert>}
          {fields.map((field) => (
            <Form.Group className="mb-3" key={field.name}>
              <Form.Label>
                {field.label}
                {field.required && <span className="text-danger"> *</span>}
              </Form.Label>
              {renderControl(field)}
              <Form.Control.Feedback type="invalid" style={{ display: errors[field.name] ? "block" : "none" }}>
                {errors[field.name]}
              </Form.Control.Feedback>
              {field.hint && <Form.Text className="text-muted">{field.hint}</Form.Text>}
            </Form.Group>
          ))}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button className="btn-forest" type="submit" disabled={saving}>
            {saving ? "Saving..." : submitLabel}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
