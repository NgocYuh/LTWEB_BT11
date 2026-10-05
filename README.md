# BÀI THI QUÁ TRÌNH / GIỮA KỲ — LẬP TRÌNH WEB
## ĐỀ SỐ 02: HỆ THỐNG QUẢN LÝ NHÀ SÁCH HNHBOOKSTORE

- **Họ và tên sinh viên:** Hoàng Ngọc Huy
- **Mã số sinh viên:** 24133023
- **Mã đề thi:** Đề 02
- **Trường:** Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)
- **Khoa:** Công nghệ Thông tin

---

## 1. Giới thiệu dự án

Dự án **HNHBOOKSTORE** là ứng dụng web quản lý và giới thiệu sách được xây dựng theo kiến trúc **MVC 3 tầng (Presentation &mdash; Business &mdash; Data Access Layer)** thuần Java Servlet & JSP, không sử dụng Spring Boot hay JPA theo đúng yêu cầu của đề thi.

Giao diện được thiết kế theo phong cách **Modern & Minimalist với tỉ lệ màu 60/30/10**:
- **60% Nền sáng:** `#FFFFFF` và `#F8F9FA` tạo cảm giác thanh lịch, sạch sẽ.
- **30% Cấu trúc & Định vị:** Chữ `#111827`, Slate `#1E293B` và điểm nhấn Cobalt Blue `#2563EB`.
- **10% Kêu gọi hành động (CTA):** Màu cam đậm `#F97316` nổi bật cho các nút Submit, Đánh giá, Thêm mới.

---

## 2. Công nghệ và Môi trường sử dụng

- **Ngôn ngữ lập trình:** Java (JDK 21 / 26)
- **Kiến trúc ứng dụng:** MVC Model 2 (Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0)
- **Quản lý Decorator / Layout:** SiteMesh 3.2.1 (Tách biệt layout `user.jsp` và `admin.jsp`)
- **Web Server:** Apache Tomcat 10.1.57 (triển khai dạng WAR với context `/HNHBOOKSTORE`)
- **Cơ sở dữ liệu:** Microsoft SQL Server (kết nối qua JDBC Driver 13.2.1 sử dụng Windows Authentication)
- **Dịch vụ Email:** Jakarta Mail 2.1.2 & Angus Mail 2.0.2 (Gửi mã OTP kích hoạt tài khoản qua Gmail SMTP thực tế)
- **Bảo mật:**
  - Băm mật khẩu an toàn với thuật toán **PBKDF2-HMAC-SHA256** (65.536 vòng lặp, khóa 128-bit khớp cột `passwd varchar(32)`) kết hợp chuỗi muối ngẫu nhiên (salt 16 bytes).
  - Băm mã OTP bằng SHA-256 lưu tạm trong Session (không lưu OTP dạng văn bản rõ).
  - Chống tấn công CSRF bằng CSRF Token trên toàn bộ form thay đổi dữ liệu.
  - Chống Session Fixation bằng cơ chế đổi session ID (`changeSessionId()`) sau khi đăng nhập.
  - Đăng xuất bằng phương thức POST và hủy session (`session.invalidate()`).
  - Phân quyền nghiêm ngặt bằng Filter: Chặn khách chuyển hướng 302 về trang đăng nhập; chặn người dùng thông thường truy cập trái phép khu vực quản trị với mã lỗi HTTP 403 Forbidden.

---

## 3. Cấu trúc thư mục mã nguồn

Toàn bộ các class, interface, servlet, filter, DAO, model và utility đều tuân thủ nghiêm ngặt quy tắc đặt tên kết thúc bằng hậu tố `_24133023`:

```text
HNHBOOKSTORE/
├── pom.xml                                  # Cấu hình Maven build WAR và dependencies Jakarta EE
├── README.md                                # Tài liệu hướng dẫn đồ án
├── 24133023.docx                            # File báo cáo kết quả nộp bài (kèm ảnh chụp màn hình)
├── sql/                                     # Script cơ sở dữ liệu theo từng câu
│   ├── 00-create-database.sql               # Tạo CSDL HNHBOOKSTORE
│   ├── 01-schema.sql                        # Cấu trúc 5 bảng gốc nguyên bản theo đề thi
│   ├── 02-seed-catalog.sql                  # Dữ liệu sách và tác giả ban đầu
│   ├── 03-alter-users-auth.sql              # Bổ sung cột is_verified, password_salt và tài khoản mẫu
│   ├── 04-seed-cau3.sql                     # Dữ liệu phân trang tác giả và reviews mẫu
│   └── 05-seed-cau6.sql                     # Dữ liệu kiểm thử phân trang Admin (10/10/1)
├── src/main/java/vn/hcmute/hnhbookstore/
│   ├── model/                               # Tầng Model: Author, Book, Rating, User, OtpSessionData...
│   ├── data/                                # Tầng Data Access: ConnectionFactory, AuthorDao, BookDao, RatingDao, UserDao
│   ├── business/                            # Tầng Business Logic: AuthService, CatalogService, SmtpMailService...
│   ├── presentation/controller/             # Tầng Controller (Servlet): Home, BookDetail, Admin, Login, Logout, Register, VerifyOtp
│   ├── presentation/filter/                 # Tầng Filter: EncodingFilter (UTF-8), SiteMeshFilter, AdminAuthFilter (Bảo vệ /admin/*)
│   └── util/                                # Utilities: PasswordUtil (PBKDF2/SHA-256), CsrfUtil (Token CSRF)
├── src/main/webapp/
│   ├── assets/                              # CSS (site.css), JavaScript, hình ảnh SVG
│   └── WEB-INF/
│       ├── web.xml                          # Khai báo Filters, Session-config, Error pages, UTF-8
│       ├── decorators/                      # Layout user.jsp và admin.jsp
│       ├── fragments/                       # header.jspf (menu động), footer.jspf (bản quyền)
│       └── views/                           # Các giao diện JSP (home, detail, admin, auth, error)
└── scripts/                                 # Các script hỗ trợ kiểm tra và khởi động ứng dụng
```

---

## 4. Hướng dẫn cài đặt và Chạy chương trình

### Bước 1: Khởi tạo Cơ sở dữ liệu SQL Server
Mở PowerShell tại thư mục dự án và chạy các script tạo bảng (sử dụng Windows Authentication):

```powershell
sqlcmd -S localhost -E -C -i .\sql\00-create-database.sql
sqlcmd -S localhost -E -C -d HNHBOOKSTORE -i .\sql\01-schema.sql
sqlcmd -S localhost -E -C -d HNHBOOKSTORE -i .\sql\02-seed-catalog.sql
sqlcmd -S localhost -E -C -d HNHBOOKSTORE -i .\sql\03-alter-users-auth.sql
sqlcmd -S localhost -E -C -d HNHBOOKSTORE -i .\sql\04-seed-cau3.sql
sqlcmd -S localhost -E -C -d HNHBOOKSTORE -i .\sql\06-create-orders-schema.sql
```

### Bước 2: Biên dịch và Đóng gói WAR
Chạy lệnh Maven để build gói ứng dụng `HNHBOOKSTORE.war`:

```powershell
mvn clean package
```

*(Hoặc dùng `mvn '-Dmaven.repo.local=.m2' -o -B -ntp clean package` nếu dùng cache thư viện offline).*

### Bước 3: Khởi động Web Server (Tomcat 10.1)
Chạy script khởi động máy chủ cục bộ:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Start-Local.ps1
```

### Bước 4: Mở ứng dụng trên trình duyệt
- Đường dẫn ứng dụng: **`http://localhost:8080/HNHBOOKSTORE`** (hoặc `http://localhost:8080`)
- Trang quản trị: **`http://localhost:8080/HNHBOOKSTORE/admin/books`**

---

## 5. Tài khoản kiểm thử có sẵn

| Vai trò | Email đăng nhập | Mật khẩu | Quyền hạn |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (Admin)** | `admin@hnhbookstore.vn` | `Admin@24133023` | Toàn quyền quản trị kho sách (`/admin/*`) |
| **Người dùng (User)** | `huyhoang.260806@gmail.com` | `User@24133023` | Xem sách, gửi đánh giá, bị chặn khi vào Admin (403) |

---

## 6. Tóm tắt kết quả thực hiện các câu hỏi

- **Câu 1 (1.5 điểm):** Khởi tạo cấu trúc dự án Maven WAR, cấu hình SiteMesh 3.2.1 với 2 giao diện người dùng và quản trị, kết nối CSDL SQL Server bằng JDBC Windows Authentication, trang chủ ban đầu hiển thị thông tin tác giả.
- **Câu 2 (1.5 điểm):** Chức năng đăng ký tài khoản, gửi mã OTP thực tế qua Gmail SMTP, xác thực mã kích hoạt tài khoản, đăng nhập băm mật khẩu PBKDF2-HMAC-SHA256 kèm salt, chống CSRF token, Session Fixation, phân quyền Admin Filter chặn khách 302 và chặn user thường 403 Forbidden.
- **Câu 3 (2.0 điểm):** Trang chủ nhóm theo từng tác giả (`Tác giả: [tên]`), bố cục 3 thẻ sách/hàng responsive, hiển thị đủ 8 trường dữ liệu và số lượng `Reviews (n)` đếm thực tế từ CSDL. Phân trang SQL Server độc lập cho từng tác giả (3 sách/trang).
- **Câu 4 (2.0 điểm):** Trang chi tiết sách `/books/detail?bookId=...` với bố cục ảnh bìa bên trái, thông tin sách bên phải. Danh sách nhận xét theo định dạng `[tên users]: [review_text]` kèm điểm số ★ X/10. Form đánh giá bảo vệ người dùng, hỗ trợ cập nhật đánh giá cũ theo khóa chính ghép `(userid, bookid)`, chống XSS.
- **Câu 6 (3.0 điểm):** Quản lý kho sách (CRUD) cho Admin tại `/admin/books` có phân trang 10 sách/trang (hỗ trợ kiểm thử 10/10/1 với 21 cuốn sách). Form thêm mới và sửa sách có dropdown chọn tác giả từ bảng `author` và lưu vào `book_author`. Chức năng xóa sách an toàn xử lý bằng Transaction 3 bước (xóa rating &rarr; xóa book_author &rarr; xóa books), cam kết không bao giờ phát sinh lỗi khóa ngoại (Foreign Key).

*(Ghi chú: Đề thi không có Câu 5).*

---

## 7. Các chức năng mở rộng phát triển thêm

### 1. Chức năng Giỏ hàng (Shopping Cart):
- Quản lý giỏ hàng an toàn theo Session (`Cart_24133023`, `CartItem_24133023`).
- Thêm sách vào giỏ hàng trực tiếp từ Trang chủ hoặc Trang chi tiết sách.
- Điều chỉnh số lượng tăng/giảm trong giỏ hàng với giới hạn kiểm tra tự động theo tồn kho thực tế (`quantity` trong CSDL).
- Xóa từng cuốn sách hoặc làm trống toàn bộ giỏ hàng.
- Hiển thị huy hiệu số lượng sách trên thanh điều hướng Header động.
- Tính tự động thành tiền và tổng thanh toán.

### 2. Chức năng Thanh toán đơn hàng bằng COD (Cash On Delivery):
- Yêu cầu đăng nhập trước khi thanh toán (chuyển hướng an toàn đến trang đăng nhập kèm thông báo).
- Form điền thông tin người nhận: Họ tên, số điện thoại giao hàng (kiểm tra định dạng SĐT Việt Nam), địa chỉ nhận hàng chi tiết và ghi chú giao hàng.
- Xử lý giao dịch đặt hàng nguyên tử (Transaction JDBC ACID):
  1. Kiểm tra tồn kho thời gian thực của từng đầu sách.
  2. Tạo đơn hàng trong bảng `dbo.orders` với phương thức `COD` và trạng thái `PENDING`.
  3. Lưu chi tiết sản phẩm vào bảng `dbo.order_details`.
  4. Trừ số lượng tồn kho tự động trong bảng `dbo.books`.
  5. Rollback toàn bộ nếu có bất kỳ lỗi nào hoặc số lượng tồn kho không đủ.
- Xóa sạch giỏ hàng khi đặt hàng thành công và chuyển hướng đến trang xác nhận đơn hàng `/checkout/success?orderId=...`.
- Trang xác nhận đơn hàng hiển thị đầy đủ thông tin: Mã đơn hàng, người nhận, địa chỉ, số điện thoại, bảng chi tiết từng cuốn sách, tổng tiền thanh toán và hướng dẫn đồng kiểm hàng khi nhận.

