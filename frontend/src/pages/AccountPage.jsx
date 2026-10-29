import { Badge } from "react-bootstrap";
import CrudPage from "../components/CrudPage";
import { accountApi } from "../api/resources";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const fields = [
  { name: "accountName", label: "Account name", required: true },
  { name: "accountEmail", label: "Email", required: true },
  {
    name: "accountRole",
    label: "Role",
    type: "select",
    options: [
      { value: "1", label: "Admin" },
      { value: "2", label: "Staff" },
    ],
  },
  {
    name: "accountPassword",
    label: "Password",
    type: "password",
    hint: "Required when creating. When editing, leave empty to keep the current password.",
  },
];

// Client-side validation for fast feedback. The server validates again (it is the real authority).
const validate = (v, mode) => {
  const e = {};
  if (!v.accountName.trim()) e.accountName = "Account name is required.";
  else if (v.accountName.trim().length > 100) e.accountName = "Account name must be 100 characters or fewer.";

  if (!v.accountEmail.trim()) e.accountEmail = "Email is required.";
  else if (!EMAIL_PATTERN.test(v.accountEmail.trim())) e.accountEmail = "Email is not valid.";
  else if (v.accountEmail.trim().length > 100) e.accountEmail = "Email must be 100 characters or fewer.";

  if (mode === "create" && !v.accountPassword) e.accountPassword = "Password is required.";
  else if (v.accountPassword && (v.accountPassword.length < 6 || v.accountPassword.length > 100))
    e.accountPassword = "Password must be 6-100 characters.";
  return e;
};

export default function AccountPage() {
  return (
    <CrudPage
      title="Account management"
      entityName="Account"
      api={accountApi}
      idKey="accountId"
      columns={[
        { key: "accountName", label: "Name" },
        { key: "accountEmail", label: "Email" },
        {
          key: "accountRole",
          label: "Role",
          render: (a) => <Badge bg={a.accountRole === 1 ? "dark" : "info"}>{a.accountRole === 1 ? "Admin" : "Staff"}</Badge>,
        },
      ]}
      fields={fields}
      emptyForm={{ accountName: "", accountEmail: "", accountRole: "2", accountPassword: "" }}
      toForm={(a) => ({
        accountName: a.accountName,
        accountEmail: a.accountEmail,
        accountRole: String(a.accountRole),
        accountPassword: "",
      })}
      toPayload={(v) => ({
        accountName: v.accountName.trim(),
        accountEmail: v.accountEmail.trim(),
        accountRole: Number(v.accountRole),
        // send the password only when something was typed (empty = keep the old one)
        ...(v.accountPassword ? { accountPassword: v.accountPassword } : {}),
      })}
      validate={validate}
      deleteLabel={(a) => `Delete account "${a.accountName}"? It cannot be deleted if it already created news.`}
    />
  );
}
