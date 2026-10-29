# Test matrix (fill the Actual column when you run it)

| # | Area | Case | Steps | Expected | Actual |
|---|------|------|-------|----------|--------|
| 1 | Public | Active only | Open `/` | Inactive news is not listed | |
| 2 | Public | Search | Search a title word / nonsense | Filtered / "No news found" | |
| 3 | Login | Empty / bad email | Submit | Message, no request needed | |
| 4 | Login | Wrong password | staff@funews.com + wrong | "Wrong email or password." | |
| 5 | Login | Admin / Staff | Sign in with each | Admin -> Accounts; Staff -> News | |
| 6 | Security | Wrong role | Staff opens `/app/accounts` | Redirected away | |
| 7 | API | No header | `GET /api/accounts` in Swagger without the header | 401 | |
| 8 | API | Wrong role | Staff id on `/api/accounts` | 403 | |
| 9 | Account | Create | Valid data | Row appears | |
| 10 | Account | Duplicate email | Reuse an email | 409 message inside the dialog | |
| 11 | Account | Invalid | Bad email, short password | Field errors | |
| 12 | Account | Edit without password | Change name only | Saved, old password still works | |
| 13 | Account | Delete with news | Delete staff@funews.com | Warning, row stays | |
| 14 | Account | Delete unused / self | Delete a new account / yourself | Deleted / blocked | |
| 15 | Category | Create / duplicate | New name / same name | Created / 409 | |
| 16 | Category | Parent | Choose a parent, edit it | Parent shown, cannot pick itself | |
| 17 | Category | Delete used | Delete "Campus" | Warning, row stays | |
| 18 | Category | Delete unused | Delete a new category | Deleted after confirm | |
| 19 | News | Create | Fill all, pick tags | Appears with tags and your name | |
| 20 | News | Inactive category | Category list in dialog | Only active categories offered | |
| 21 | News | Update | Edit title and tags | Changes saved | |
| 22 | News | Delete cancel / confirm | | Cancel keeps; confirm removes | |
| 23 | News | Search | Title or headline word | Filtered | |
| 24 | History | Own news only | Staff Two opens history | Does not see Staff One's news | |
| 25 | Profile | Update name | Change name | Header shows the new name | |
| 26 | Persistence | Reload | F5 after login | Still signed in | |
| 27 | Error | Backend stopped | Stop API, open a page | "Cannot connect to the server." | |
