import { useEffect, useState } from "react";
import { Alert, Badge, Button, Container, Form, InputGroup, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom";
import { getErrorMessage } from "../api/http";
import { newsApi } from "../api/resources";
import { useAuth } from "../context/AuthContext";
import { homeFor } from "../components/menu";
import AuthorCredit from "../components/AuthorCredit";
import logo from "../assets/logo.svg";

// No login needed. The backend only returns ACTIVE news from /api/news/public.
export default function PublicNewsPage() {
  const { session } = useAuth();
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [input, setInput] = useState("");
  const [keyword, setKeyword] = useState("");

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    newsApi
      .listPublic(keyword)
      .then((data) => !cancelled && (setNews(data), setError("")))
      .catch((e) => !cancelled && setError(getErrorMessage(e)))
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true; // ignore an old response if the keyword changed meanwhile
    };
  }, [keyword]);

  return (
    <div>
      <div className="public-top">
        <div className="brand">
          <img src={logo} alt="logo" />
          <div className="brand-copy">
            <span>FUNews</span>
            <AuthorCredit />
          </div>
        </div>
        {session ? (
          <Button as={Link} to={homeFor(session.accountRole)} size="sm" variant="outline-light">
            Go to management
          </Button>
        ) : (
          <Button as={Link} to="/login" size="sm" variant="outline-light">
            Staff / Admin sign in
          </Button>
        )}
      </div>

      <Container className="py-4" style={{ maxWidth: 860 }}>
        <h2 className="page-title">Latest news</h2>

        <Form
          onSubmit={(e) => {
            e.preventDefault();
            setKeyword(input.trim());
          }}
          className="mb-3"
        >
          <InputGroup>
            <Form.Control placeholder="Search by title or headline..." value={input} onChange={(e) => setInput(e.target.value)} />
            <Button type="submit" variant="outline-secondary">
              Search
            </Button>
          </InputGroup>
        </Form>

        {error && <Alert variant="danger">{error}</Alert>}
        {loading && (
          <div className="text-center py-4">
            <Spinner size="sm" /> Loading...
          </div>
        )}
        {!loading && !error && news.length === 0 && <p className="text-muted">No news found.</p>}

        <div className="d-grid gap-3">
          {news.map((n) => (
            <article className="news-card" key={n.newsArticleId}>
              <h5>{n.newsTitle}</h5>
              <div className="news-meta mb-2">
                {n.categoryName} · by {n.createdByName} · {new Date(n.createdDate).toLocaleDateString()}
              </div>
              <p className="mb-2 fw-semibold">{n.headline}</p>
              <p className="mb-2" style={{ whiteSpace: "pre-line" }}>
                {n.newsContent}
              </p>
              {n.tags.map((t) => (
                <Badge key={t.tagId} bg="secondary" className="me-1">
                  {t.tagName}
                </Badge>
              ))}
            </article>
          ))}
        </div>
      </Container>
    </div>
  );
}
