import { Badge } from "react-bootstrap";
import CrudPage from "../components/CrudPage";
import { newsApi } from "../api/resources";

// Read-only list of the news created by the signed-in staff (GET /api/news/my-history).
export default function HistoryPage() {
  return (
    <CrudPage
      title="My news history"
      entityName="News"
      api={newsApi}
      loadFn={newsApi.history}
      idKey="newsArticleId"
      readOnly
      columns={[
        { key: "newsTitle", label: "Title" },
        { key: "categoryName", label: "Category" },
        {
          key: "newsStatus",
          label: "Status",
          render: (n) => <Badge bg={n.newsStatus === 1 ? "success" : "secondary"}>{n.newsStatus === 1 ? "Active" : "Inactive"}</Badge>,
        },
        { key: "createdDate", label: "Created", render: (n) => new Date(n.createdDate).toLocaleString() },
        { key: "modifiedDate", label: "Modified", render: (n) => (n.modifiedDate ? new Date(n.modifiedDate).toLocaleString() : "") },
      ]}
    />
  );
}
