# CINEMA CNJ65 — Hệ thống quản lý vận hành rạp chiếu phim

Đồ án môn học: Java Servlet + JSP + JSTL + JDBC + MySQL (kiến trúc MVC Model 2).

---

## 1. Yêu cầu môi trường

| Công cụ | Phiên bản tối thiểu | Ghi chú |
|---|---|---|
| JDK | 11+ | `java -version` để kiểm tra |
| Apache Maven | 3.6+ | Dùng để build file `.war` |
| Apache Tomcat | 9.x | Servlet 4.0, tương thích `javax.servlet.*` dùng trong project |
| MySQL | 8.0+ | Cũng chạy được với MySQL 5.7 nếu chỉnh lại driver |
| IDE | VS Code / Eclipse / IntelliJ | VS Code cần cài thêm "Extension Pack for Java" |

---

## 2. Cài đặt Database

1. Mở MySQL (Workbench, DBeaver, hoặc dòng lệnh `mysql -u root -p`).
2. Chạy toàn bộ file `database/cinema_cnj65.sql`:
   ```bash
   mysql -u root -p < database/cinema_cnj65.sql
   ```
   Script sẽ tự **tạo database `cinema_cnj65`**, tạo đủ 10 bảng, và chèn sẵn dữ liệu mẫu (phim, phòng, ghế, suất chiếu, tài khoản demo).

3. Tài khoản demo có sẵn (mật khẩu chung: `123456`):

   | Vai trò | Username |
   |---|---|
   | Quản trị (Admin) | `admin` |
   | Nhân viên (Staff) | `staff01` |
   | Khách hàng | `khang`, `linh` |

---

## 3. Cấu hình kết nối Database

Mở file `src/main/resources/db.properties`, sửa lại cho khớp MySQL của bạn:

```properties
db.url=jdbc:mysql://localhost:3306/cinema_cnj65?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
db.username=root
db.password=root        <-- đổi thành mật khẩu MySQL thật của bạn
```

**Lưu ý:** file này nằm trong `src/main/resources` (không phải `webapp`) — khi build Maven nó sẽ tự được đóng gói vào `WEB-INF/classes/db.properties` bên trong file `.war`.

---

## 4. Build project

Tại thư mục gốc (nơi có file `pom.xml`):

```bash
mvn clean package
```

Nếu thành công, file `cinema-cnj65.war` sẽ nằm trong thư mục `target/`.

**Nếu gặp lỗi biên dịch:** vì mình không có môi trường Java/Maven thật để build thử trước khi giao code, đây là bước có khả năng phát sinh lỗi nhất (ví dụ thiếu dependency, phiên bản JDK không khớp). Hãy copy nguyên văn lỗi và gửi lại — mình sẽ sửa ngay.

---

## 5. Deploy lên Tomcat

**Cách 1 — Copy file .war (đơn giản nhất):**
1. Copy `target/cinema-cnj65.war` vào thư mục `<TOMCAT_HOME>/webapps/`
2. Khởi động Tomcat (`startup.bat` trên Windows hoặc `./startup.sh` trên macOS/Linux, trong `<TOMCAT_HOME>/bin`)
3. Tomcat sẽ tự giải nén thành thư mục `cinema-cnj65/`
4. Truy cập: `http://localhost:8080/cinema-cnj65/home`

**Cách 2 — Deploy từ VS Code:**
1. Cài extension **"Community Server Connectors"** hoặc **"Tomcat for Java"**
2. Trỏ tới thư mục cài Tomcat của bạn
3. Click phải vào project → **Run on Server**

**Cách 3 — Deploy từ Eclipse/IntelliJ:** thêm project vào Server Runtime như bình thường (Add and Remove → chọn `cinema-cnj65`).

---

## 6. Kiểm tra hệ thống đã chạy đúng chưa

Sau khi deploy, thử lần lượt các luồng sau để xác nhận mọi thứ kết nối đúng:

1. **Trang chủ** (`/home`) → phải thấy danh sách phim đang chiếu / sắp chiếu (dữ liệu mẫu có sẵn 5 phim)
2. **Đăng nhập** bằng `khang` / `123456` → **Phim** → chọn 1 phim đang chiếu → chọn suất chiếu → **thử chọn ghế** (kiểm tra AJAX giữ ghế hoạt động, đồng hồ đếm ngược 5 phút chạy) → xác nhận → thanh toán → xem vé điện tử
3. **Đăng xuất**, đăng nhập lại bằng `admin` / `123456` → vào **Quản trị** → thử thêm 1 phim mới, thêm 1 suất chiếu (thử tạo trùng giờ 1 phòng đã có suất — hệ thống phải báo lỗi trùng lịch)
4. Đăng nhập bằng `staff01` / `123456` → **Quầy vé** → thử bán 1 vé tại quầy → **Check-in vé** bằng mã vừa tạo

Nếu có bước nào lỗi, gửi lại mình thông tin lỗi (log ở console Tomcat + ảnh chụp màn hình nếu có) để sửa nhanh nhất.

---

## 7. Cấu trúc thư mục

```
cinema-cnj65/
├── pom.xml                          # Khai báo dependency (Maven)
├── database/cinema_cnj65.sql        # Script tạo DB + dữ liệu mẫu
└── src/main/
    ├── java/com/cnj65/cinema/
    │   ├── config/DBContext.java    # Connection Pool (HikariCP)
    │   ├── model/                   # POJO tương ứng các bảng DB
    │   ├── dao/                     # Truy vấn JDBC (PreparedStatement)
    │   ├── filter/                  # Phân quyền (Auth/Admin/Staff Filter)
    │   ├── listener/                # Tự động nhả ghế giữ quá hạn
    │   ├── util/                    # Constants, FormatUtil, PasswordUtil...
    │   └── controller/
    │       ├── auth/                # Đăng ký, đăng nhập...
    │       ├── customer/            # Đặt vé, xem vé...
    │       ├── admin/               # Quản trị
    │       └── staff/               # Quầy vé, check-in
    ├── resources/db.properties      # Cấu hình kết nối MySQL
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml              # Session timeout, trang lỗi
        │   └── views/               # File .jsp (theo từng khu vực)
        └── assets/                  # CSS/JS/ảnh dùng chung
```

---

## 8. Quy ước code (để bạn tự thêm chức năng mới nhất quán với phần đã có)

- **Session key, trạng thái** (PAID, LOCKED, ADMIN...): luôn lấy từ `util/Constants.java`, không gõ chuỗi tay.
- **Định dạng ngày/giờ/tiền** trên JSP: luôn qua getter `*Formatted()` của Model (gọi `util/FormatUtil.java`), **không dùng** `<fmt:formatDate>` vì không tương thích `LocalDate`/`LocalTime`.
- **CSS class**: luôn có tiền tố `cnj-`, theo cú pháp BEM. **`id`** chỉ dùng làm mốc cho JavaScript, không dùng để canh CSS.
- **Servlet → JSP**: tên `request.setAttribute(...)` luôn được ghi chú bằng comment ở đầu mỗi file JSP tương ứng.
- Thêm 1 trang mới? Nhớ include `common/header.jsp` + `common/footer.jsp` (khu vực Khách hàng) hoặc `common/control-header.jsp` + `common/control-footer.jsp` (khu vực Admin/Staff).

---

## 9. Những phần có thể nâng cấp thêm (không bắt buộc)

- Tích hợp cổng thanh toán thật (VNPay/Momo sandbox) — hiện tại `PaymentServlet` ghi nhận thanh toán ngay, không gọi API bên ngoài.
- Gửi email vé điện tử (JavaMail API).
- Sinh mã QR thật cho vé (thư viện ZXing) — hiện tại `.cnj-ticket__qr` chỉ là hoa văn CSS minh họa.
- Combo bắp nước, mã giảm giá (đã phân tích ở phần đầu nhưng chưa nằm trong phạm vi bảng nghiệp vụ gốc).
