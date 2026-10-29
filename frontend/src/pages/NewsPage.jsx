import { useCallback, useEffect, useState } from "react";
import { Alert, Badge, Button, Form, InputGroup } from "react-bootstrap";
import CrudPage from "../components/CrudPage";
import { getErrorMessage } from "../api/http";
import { categoryApi, newsApi, tagApi } from "../api/resources";

const validate = (v) => {
  const e = {};
  if (!v.newsTitle.trim()) e.newsTitle = "News title is required.";
  else if (v.newsTitle.trim().length > 400) e.newsTitle = "News title must be 400 characters or fewer.";
  if (!v.headline.trim()) e.headline = "Headline is required.";
  else if (v.headline.trim().length > 150) e.headline = "Headline must be 150 characters or fewer.";
  if (!v.newsContent.trim()) e.newsContent = "News content is required.";
  else if (v.newsContent.trim().length > 4000) e.newsContent = "News content must be 4000 characters or fewer.";
  if (v.newsSource.length > 400) e.newsSource = "News source must be 400 characters or fewer.";
  if (!v.categoryId) e.categoryId = "Please choose a category.";
  return e;
};

export default function NewsPage() {
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [newTag, setNewTag] = useState("");
  const [tagError, setTagError] = useState("");

  const loadLookups = useCallback(async () => {
    try {
      const [c, t] = await Promise.all([categoryApi.list(""), tagApi.list()]);
      setCategories(c);
      setTags(t);
    } catch (error) {
      setTagError(getErrorMessage(error));
    }
  }, []);

  useEffect(() => {
    loadLookups();
  }, [loadLookups]);

  const addTag = async (e) => {
    e.preventDefault();
    if (!newTag.trim()) return;
    try {
      await tagApi.create({ tagName: newTag.trim() });
      setNewTag("");
      setTagError("");
      await loadLookups();
    } catch (error) {
      setTagError(getErrorMessage(error));
    }
  };

  // Active categories only; when editing, keep the article's current category even if it became inactive.
  const buildFields = (items, editing) => [
    { name: "newsTitle", label: "News title", required: true },
    { name: "headline", label: "Headline", required: true },
    { name: "newsContent", label: "Content", type: "textarea", required: true },
    { name: "newsSource", label: "Source" },
    {
      name: "categoryId",
      label: "Category",
      type: "select",
      required: true,
      placeholder: "Select a category...",
      options: categories
        .filter((c) => c.active || (editing && c.categoryId === editing.categoryId))
        .map((c) => ({ value: String(c.categoryId), label: c.active ? c.categoryName : `${c.categoryName} (inactive)` })),
    },
    {
      name: "newsStatus",
      label: "Status",
      type: "select",
      options: [
        { value: "1", label: "Active (visible to everyone)" },
        { value: "0", label: "Inactive (hidden)" },
      ],
    },
    {
      name: "tagIds",
      label: "Tags",
      type: "checkboxes",
      options: tags.map((t) => ({ value: String(t.tagId), label: t.tagName })),
    },
  ];

  return (
    <div>
      <div className="mb-3" style={{ maxWidth: 420 }}>
        <Form onSubmit={addTag}>
          <InputGroup size="sm">
            <Form.Control placeholder="New tag name..." value={newTag} onChange={(e) => setNewTag(e.target.value)} />
            <Button type="submit" variant="outline-secondary">
              Add tag
            </Button>
          </InputGroup>
        </Form>
        {tagError && (
          <Alert variant="warning" className="mt-2 py-1 mb-0">
            {tagError}
          </Alert>
        )}
      </div>

      <CrudPage
        title="News management"
        entityName="News"
        api={newsApi}
        idKey="newsArticleId"
        columns={[
          { key: "newsTitle", label: "Title" },
          { key: "categoryName", label: "Category" },
          {
            key: "tags",
            label: "Tags",
            render: (n) => n.tags.map((t) => (
              <Badge key={t.tagId} bg="secondary" className="me-1">
                {t.tagName}
              </Badge>
            )),
          },
          { key: "createdByName", label: "Created by" },
          {
            key: "newsStatus",
            label: "Status",
            render: (n) => <Badge bg={n.newsStatus === 1 ? "success" : "secondary"}>{n.newsStatus === 1 ? "Active" : "Inactive"}</Badge>,
          },
        ]}
        fields={buildFields}
        emptyForm={{
          newsTitle: "",
          headline: "",
          newsContent: "",
          newsSource: "",
          categoryId: "",
          newsStatus: "1",
          tagIds: [],
        }}
        toForm={(n) => ({
          newsTitle: n.newsTitle,
          headline: n.headline,
          newsContent: n.newsContent,
          newsSource: n.newsSource ?? "",
          categoryId: String(n.categoryId),
          newsStatus: String(n.newsStatus),
          tagIds: n.tags.map((t) => String(t.tagId)),
        })}
        toPayload={(v) => ({
          newsTitle: v.newsTitle.trim(),
          headline: v.headline.trim(),
          newsContent: v.newsContent.trim(),
          newsSource: v.newsSource.trim() || null,
          categoryId: Number(v.categoryId),
          newsStatus: Number(v.newsStatus),
          tagIds: v.tagIds.map(Number),
        })}
        validate={validate}
        deleteLabel={(n) => `Delete news "${n.newsTitle}"? This cannot be undone.`}
      />
    </div>
  );
}
