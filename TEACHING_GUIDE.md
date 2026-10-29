# Teaching guide

## Backend: 3 layers (explain top to bottom)

| Layer | Package | Job | Example |
|-------|---------|-----|---------|
| Controller | `controller` | HTTP in/out, role check, no business rules | `CategoryController` |
| Service | `service` | business rules and transactions | `CategoryService.delete` |
| Repository | `repository` | database access (Spring Data JPA) | `NewsArticleRepository` |

Also: `entity` maps tables, `dto` are the request/response shapes (the account response never contains a password),
`exception` turns every error into JSON `{ "message": "..." }`, `config` holds CORS, role constants and sample data.

## Follow one request end to end: delete a category

1. React: Delete click -> `deleteTarget` -> ConfirmDialog -> `categoryApi.remove(id)` (axios `DELETE /api/categories/{id}`).
2. `http.js` interceptor adds `X-Account-Id`.
3. `CategoryController.delete` -> `authService.requireRole(accountId, STAFF)` -> 401/403 if not allowed.
4. `CategoryService.delete`: used by news? -> 409 "Cannot delete...". Used as a parent? -> 409. Otherwise delete.
5. `GlobalExceptionHandler` returns `{ "message": ... }` with the status.
6. React `catch`: `getErrorMessage(error)` -> yellow Alert. On success -> reload the list.

Do the same walk for: login, create news (tags + category check + `createdBy`), account delete rule, public news (status 1 only).

## Frontend ideas to teach

- `createResource` (api/resources.js): one factory gives list/create/update/remove for any endpoint.
- Why `getErrorMessage` always returns a string: rendering an object in `<Alert>` blanks the page.
- `CrudPage`: server search (`?keyword=`) with an input state and an applied `keyword` state; `loading` state; `dialog = {mode, item}`.
- Role based routing: `ProtectedRoute roles={[...]}` plus `MENU` per role.
- Client validation is for UX, server validation is the authority (duplicate names/emails are only detected by the server, shown inside the dialog).

## Practice change requests (do live, without AI)

- Add `NewsSource` filter or a status filter to news search: repository query -> service -> controller param -> React.
- Add pagination (`Pageable`) to `GET /api/news`.
- Hash passwords with BCrypt (`PasswordEncoder`) and explain the migration issue for existing rows.
- Replace the `X-Account-Id` header with a JWT.
- Add a "delete tag" endpoint with the rule "cannot delete a tag used by an article".

Say the impact first: database -> entity -> repository -> service -> controller -> DTO -> React form/list -> validation -> tests.
