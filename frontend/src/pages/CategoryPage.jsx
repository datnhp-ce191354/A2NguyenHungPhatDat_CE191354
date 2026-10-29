import { Badge } from "react-bootstrap";
import CrudPage from "../components/CrudPage";
import { categoryApi } from "../api/resources";

// Parent options depend on the loaded list, so `fields` is a function here.
const buildFields = (items, editing) => [
  { name: "categoryName", label: "Category name", required: true },
  { name: "categoryDescription", label: "Description", type: "textarea" },
  {
    name: "parentCategoryId",
    label: "Parent category",
    type: "select",
    options: [
      { value: "", label: "(none)" },
      ...items
        .filter((c) => !editing || c.categoryId !== editing.categoryId) // a category cannot be its own parent
        .map((c) => ({ value: String(c.categoryId), label: c.categoryName })),
    ],
  },
  {
    name: "active",
    label: "Status",
    type: "select",
    options: [
      { value: "true", label: "Active" },
      { value: "false", label: "Inactive" },
    ],
  },
];

const validate = (v) => {
  const e = {};
  if (!v.categoryName.trim()) e.categoryName = "Category name is required.";
  else if (v.categoryName.trim().length > 100) e.categoryName = "Category name must be 100 characters or fewer.";
  if (v.categoryDescription.length > 250) e.categoryDescription = "Description must be 250 characters or fewer.";
  return e;
};

export default function CategoryPage() {
  return (
    <CrudPage
      title="Category management"
      entityName="Category"
      api={categoryApi}
      idKey="categoryId"
      columns={[
        { key: "categoryName", label: "Name" },
        { key: "categoryDescription", label: "Description" },
        { key: "parentCategoryName", label: "Parent" },
        {
          key: "active",
          label: "Status",
          render: (c) => <Badge bg={c.active ? "success" : "secondary"}>{c.active ? "Active" : "Inactive"}</Badge>,
        },
      ]}
      fields={buildFields}
      emptyForm={{ categoryName: "", categoryDescription: "", parentCategoryId: "", active: "true" }}
      toForm={(c) => ({
        categoryName: c.categoryName,
        categoryDescription: c.categoryDescription ?? "",
        parentCategoryId: c.parentCategoryId ? String(c.parentCategoryId) : "",
        active: String(c.active),
      })}
      toPayload={(v) => ({
        categoryName: v.categoryName.trim(),
        categoryDescription: v.categoryDescription.trim() || null,
        parentCategoryId: v.parentCategoryId ? Number(v.parentCategoryId) : null,
        active: v.active === "true",
      })}
      validate={validate}
      deleteLabel={(c) => `Delete category "${c.categoryName}"? It cannot be deleted if news uses it.`}
    />
  );
}
