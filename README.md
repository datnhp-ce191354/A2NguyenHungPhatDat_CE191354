# A2NguyenHungPhatDat_CE191354 — FUNewsManagementSystem

**Tác giả:** Nguyen Hung Phat Dat — CE191354.

Tên tác giả hiển thị ở đầu trang tin, trang đăng nhập và các trang Admin/Staff của FE; trên BE, tên hiện trong tiêu đề Swagger UI (`/swagger-ui/index.html`).

Bài Assignment 02 gồm **backend Spring Boot 3 REST API** (Controller → Service → Repository, Spring Data JPA, SQL Server) và **frontend React/Vite** gọi API bằng axios. Hai phần nằm cạnh nhau:

```text
A2NguyenHungPhatDat_CE191354/
├── backend/     Maven, Java 17+, REST API, Swagger
└── frontend/    React, Vite, axios
```

## Chạy với SQL Server (cấu hình nộp bài)

Yêu cầu Java 17+, Maven, Node.js 20+ và SQL Server đang nghe ở cổng 1433.

1. Tạo database: `CREATE DATABASE A2NguyenHungPhatDat_CE191354;`
2. Trong PowerShell, từ thư mục dự án, đặt thông tin đăng nhập SQL Server và chạy BE:

```powershell
$env:DB_USERNAME = 'sa'
$env:DB_PASSWORD = '<mật khẩu SQL Server của bạn>'
cd backend
mvn spring-boot:run
```

Nếu SQL Server dùng địa chỉ/cổng khác, đặt thêm `DB_URL` (JDBC URL). Cấu hình mặc định nằm trong `backend/src/main/resources/application.properties`; JPA tạo/cập nhật bảng (`ddl-auto=update`) và `DataSeeder` tạo dữ liệu mẫu ở lần chạy đầu trên database trống.

3. Mở terminal thứ hai, từ thư mục dự án, chạy FE:

```powershell
cd frontend
npm ci
npm run dev
```

Mở `http://localhost:5173`. API mặc định ở `http://localhost:8080/api`, Swagger ở `http://localhost:8080/swagger-ui/index.html`. Nếu BE chạy cổng khác, sao chép `frontend/.env.example` thành `frontend/.env` và sửa `VITE_API_BASE_URL`, rồi khởi động lại Vite.

## Chạy demo khi chưa có SQL Server

Profile `demo` dùng H2 lưu trong `backend/funews-demo.mv.db` để thử FE–BE ngay trên máy. Đây là chế độ thử; cấu hình SQL Server ở trên vẫn là mặc định của bài.

```powershell
cd backend
mvn spring-boot:run '-Dspring-boot.run.profiles=demo'
```

Chạy frontend bằng các lệnh ở trên. Nếu cổng 8080 đang bận, đặt `$env:SERVER_PORT = '18080'` trước khi chạy BE và đặt `VITE_API_BASE_URL=http://localhost:18080/api` trong `frontend/.env`.

## Tài khoản mẫu

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | `admin@funews.com` | `Admin@123` |
| Staff | `staff@funews.com` | `Staff@123` |
| Staff | `staff2@funews.com` | `Staff@123` |

Đăng nhập bằng **email đầy đủ** (ví dụ `admin@funews.com`), không nhập tên vai trò `Admin`. Các tài khoản mẫu chỉ được tạo khi database chưa có tài khoản nào; database đã có dữ liệu thì dùng tài khoản hiện có.

## Chức năng

- Khách xem và tìm tin đang active mà không cần đăng nhập.
- Admin quản lý/tìm tài khoản. Không xóa được tài khoản đã tạo hoặc sửa tin; không tự xóa tài khoản đang đăng nhập.
- Staff quản lý/tìm danh mục và tin tức, gắn tag, sửa hồ sơ, xem lịch sử tin của chính mình. Danh mục đang được dùng hoặc là danh mục cha không thể bị xóa.
- Thêm/sửa bằng hộp thoại; xóa cần xác nhận. FE và BE đều kiểm tra dữ liệu nhập.
- Mô hình dữ liệu: `SystemAccount`, `Category`, `NewsArticle`, `Tag`, `NewsTag` theo ERD.

## Kiểm chứng

```powershell
cd backend
mvn test

cd ../frontend
npm ci
npm run build
```

Đã kiểm chứng ở môi trường hiện tại: 3 bài integration test API qua H2 đều pass; FE build thành công; đã mở ứng dụng và thử tin công khai, đăng nhập Staff/Admin, danh sách tin, lịch sử và tài khoản. Chưa chạy trực tiếp với SQL Server vì máy hiện tại không có dịch vụ trên cổng 1433.

### IntelliJ: lỗi `TypeTag :: UNKNOWN` hoặc cảnh báo Lombok

BE đã ghim Lombok `1.18.48` và khai báo annotation processor trong `pom.xml` để biên dịch được với JDK 25. Trong IntelliJ, mở cửa sổ **Maven** và chọn **Reload All Maven Projects**; nếu IDE hiện nút **Enable annotation processing**, hãy bật nó. Sau đó chọn **Build > Rebuild Project**. Đã kiểm chứng build trong IntelliJ với JDK 25.

## Giới hạn của bài mẫu

- BE dùng header `X-Account-Id` để kiểm tra vai trò, chưa có session/token được ký; không dùng cách này cho hệ thống thật.
- Mật khẩu tài khoản được lưu dạng văn bản thuần. Hệ thống thật cần hash bằng BCrypt.
- Chưa có phân trang.

Xem `TEACHING_GUIDE.md` để hiểu luồng xử lý và `TEST_MATRIX.md` để thử thủ công trước khi nộp.
