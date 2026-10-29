import { useCallback, useEffect, useRef, useState } from "react";
import { Alert, Button, Form, InputGroup, Spinner, Table } from "react-bootstrap";
import { getErrorMessage } from "../api/http";
import FormDialog from "./FormDialog";
import ConfirmDialog from "./ConfirmDialog";

// Shared list + search + create/update popup + delete confirmation.
// Differences from Assignment 01: data comes from the REST API (axios), so every action is
// async, search is done by the server (?keyword=...), and server errors are shown to the user.
export default function CrudPage({
  title,
  entityName,
  api, // { list, create, update, remove } - must be a stable (module level) object
  loadFn, // optional replacement for api.list (e.g. news history)
  idKey,
  columns,
  fields, // array, or function (items, editingItem) => array
  emptyForm,
  toForm,
  toPayload,
  validate,
  deleteLabel,
  readOnly = false,
}) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [keywordInput, setKeywordInput] = useState("");
  const [keyword, setKeyword] = useState(""); // the keyword that was actually searched
  const [dialog, setDialog] = useState(null); // null | { mode: "create" | "update", item }
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [notice, setNotice] = useState(null);
  const requestId = useRef(0);

  const load = useCallback(async () => {
    const currentRequest = ++requestId.current;
    setLoading(true);
    try {
      const fn = loadFn || api.list;
      const data = await fn(keyword);
      if (currentRequest === requestId.current) {
        setItems(data);
        setNotice((previous) => previous?.variant === "danger" ? null : previous);
      }
    } catch (error) {
      if (currentRequest === requestId.current) {
        setItems([]);
        setNotice({ variant: "danger", text: getErrorMessage(error) });
      }
    } finally {
      if (currentRequest === requestId.current) setLoading(false);
    }
  }, [api, loadFn, keyword]);

  useEffect(() => {
    load();
    return () => { requestId.current += 1; };
  }, [load]);

  const handleSearch = (e) => {
    e.preventDefault();
    setKeyword(keywordInput.trim());
  };

  const clearSearch = () => {
    setKeywordInput("");
    setKeyword("");
  };

  const isUpdate = dialog?.mode === "update";

  const handleSubmit = async (values) => {
    try {
      if (isUpdate) {
        await api.update(dialog.item[idKey], toPayload(values, "update"));
        setNotice({ variant: "success", text: `${entityName} updated.` });
      } else {
        await api.create(toPayload(values, "create"));
        setNotice({ variant: "success", text: `${entityName} created.` });
      }
      setDialog(null);
      await load();
      return null;
    } catch (error) {
      return getErrorMessage(error); // shown inside the dialog
    }
  };

  const confirmDelete = async () => {
    try {
      await api.remove(deleteTarget[idKey]);
      setNotice({ variant: "success", text: `${entityName} deleted.` });
    } catch (error) {
      // e.g. 409: the category/account is still used by news articles
      setNotice({ variant: "warning", text: getErrorMessage(error) });
    }
    setDeleteTarget(null);
    await load();
  };

  const fieldList = typeof fields === "function" ? fields(items, dialog?.item) : fields;
  const colCount = columns.length + (readOnly ? 1 : 2);

  return (
    <div>
      <h2 className="page-title">{title}</h2>

      {notice && (
        <Alert variant={notice.variant} dismissible onClose={() => setNotice(null)}>
          {notice.text}
        </Alert>
      )}

      <div className="d-flex flex-wrap gap-2 justify-content-between mb-3">
        <Form onSubmit={handleSearch}>
          <InputGroup style={{ minWidth: 340 }}>
            <Form.Control
              placeholder={`Search ${entityName.toLowerCase()}...`}
              value={keywordInput}
              onChange={(e) => setKeywordInput(e.target.value)}
            />
            <Button type="submit" variant="outline-secondary">
              Search
            </Button>
            {(keyword || keywordInput) && (
              <Button variant="outline-secondary" onClick={clearSearch}>
                Clear
              </Button>
            )}
          </InputGroup>
        </Form>
        {!readOnly && (
          <Button className="btn-forest" onClick={() => setDialog({ mode: "create", item: null })}>
            Add {entityName.toLowerCase()}
          </Button>
        )}
      </div>

      <div className="table-wrap">
        <Table hover className="mb-0 align-middle">
          <thead>
            <tr>
              <th style={{ width: 60 }}>ID</th>
              {columns.map((col) => (
                <th key={col.key}>{col.label}</th>
              ))}
              {!readOnly && <th style={{ width: 160 }}>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={colCount} className="text-center py-4">
                  <Spinner size="sm" /> Loading...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={colCount} className="text-center text-muted py-4">
                  {keyword
                    ? `No ${entityName.toLowerCase()} matches "${keyword}".`
                    : `No ${entityName.toLowerCase()} yet.`}
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr key={item[idKey]}>
                  <td>{item[idKey]}</td>
                  {columns.map((col) => (
                    <td key={col.key}>{col.render ? col.render(item) : item[col.key]}</td>
                  ))}
                  {!readOnly && (
                    <td>
                      <Button
                        size="sm"
                        variant="outline-secondary"
                        className="me-2"
                        onClick={() => setDialog({ mode: "update", item })}
                      >
                        Edit
                      </Button>
                      <Button size="sm" variant="outline-danger" onClick={() => setDeleteTarget(item)}>
                        Delete
                      </Button>
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </Table>
      </div>

      {dialog && (
        <FormDialog
          key={isUpdate ? `edit-${dialog.item[idKey]}` : "create"}
          title={isUpdate ? `Edit ${entityName.toLowerCase()}` : `Add ${entityName.toLowerCase()}`}
          submitLabel={isUpdate ? "Save changes" : `Create ${entityName.toLowerCase()}`}
          fields={fieldList}
          initialValues={isUpdate ? toForm(dialog.item) : emptyForm}
          validate={(values) => validate(values, isUpdate ? "update" : "create")}
          onSubmit={handleSubmit}
          onClose={() => setDialog(null)}
        />
      )}

      <ConfirmDialog
        show={!!deleteTarget}
        message={deleteTarget ? deleteLabel(deleteTarget) : ""}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
