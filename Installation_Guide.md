# Hướng dẫn Cài đặt & Vận hành - Đồ án LTM

Ứng dụng hiện tại là **Plagiarism Checker** xây dựng bằng JSP/Servlet, MySQL và nền tảng TCP/UDP demo thuần Java. Giao diện chính sau khi đăng nhập:

![Dashboard UI](image/Installation_Guide/1763827372898.png)

## 1. Yêu cầu hệ thống
- **Java JDK 11+** và biến môi trường `JAVA_HOME` hợp lệ.
- **Apache Maven 3.8+** để build WAR.
- **Apache Tomcat 9+** (Servlet 4.0).
- **MySQL Server 8+** cùng công cụ quản lý (Workbench, CLI...).
- **Git** (khuyến nghị) để lấy mã nguồn.

## 2. Chuẩn bị mã nguồn
```bash
git clone <repo-url>
cd danhgiacuoiky
mvn clean package
```
Lệnh trên tạo file `target/final-project.war`. Nếu build lỗi, kiểm tra Java/Maven version hoặc chạy `mvn -v` để xác thực môi trường.

## 3. Cấu hình cơ sở dữ liệu
1. Khởi tạo schema:
   ```sql
   SOURCE database.sql;
   ```
   Script tạo DB `ltm_final_project`, bảng `users`, `tasks` và user admin mặc định (`admin/123456`).
2. Cập nhật thông tin kết nối tại `src/main/java/com/ltm/dao/DatabaseConnection.java` cho phù hợp server thật (`URL`, `USER`, `PASSWORD`).
3. Khi ứng dụng khởi động, `SchemaInitListener` sẽ tự động đổi kiểu cột `url/keyword` sang `LONGTEXT` để lưu nội dung file lớn. Đảm bảo account MySQL được cấp quyền `ALTER`.

## 4. Deploy lên Tomcat
1. Sao chép `target/final-project.war` vào `TOMCAT_HOME/webapps/` (hoặc cấu hình context riêng qua `conf/server.xml`).
2. Khởi động Tomcat bằng `bin/startup.bat` (Windows) hoặc `startup.sh` (Linux/Mac).
3. Kiểm tra log `logs/catalina.out` (Linux) hoặc `logs/catalina.YYYY-MM-DD.log` (Windows) để chắc chắn không có lỗi JDBC/POI.
4. Ứng dụng có thể truy cập tại `http://localhost:8080/final-project/`.

## 5. Sử dụng web app
1. **Đăng ký / Đăng nhập**
   - Đăng ký tại `/register` hoặc dùng tài khoản `admin/123456`.
2. **Tạo tác vụ kiểm tra**
   - Từ `dashboard.jsp`, upload **2 file** (hỗ trợ `.txt`, `.doc`, `.docx`, `.pdf`).
   - `TaskServlet` (được đánh dấu `@MultipartConfig`) đọc nội dung file. Với `.docx`, ứng dụng sử dụng **Apache POI** (`poi-ooxml`) để trích văn bản, các định dạng còn lại đọc dưới dạng UTF-8 text.
   - Nội dung được lưu trực tiếp vào các cột `url` (source) và `keyword` (target) của bảng `tasks`.
3. **Hàng đợi & xử lý nền**
   - `TaskQueue` lưu task mới, `AppContextListener` khởi động `WorkerThread` ngay khi webapp lên.
   - `WorkerThread` cập nhật trạng thái `PENDING → PROCESSING → COMPLETED/FAILED` và tính phần trăm giống nhau bằng **Jaccard similarity** trên tập từ khóa.
4. **Theo dõi kết quả**
   - Bảng “Your Tasks” hiển thị danh sách tác vụ của người dùng hiện tại, kèm nút **View** để xem nội dung đã upload.
   - Trường **Similarity (%)** thể hiện kết quả cuối cùng, `createdAt` lấy từ DB để dễ audit.

## 6. Mô hình thành phần
- **Model**: `User`, `Task`, DAO tương ứng.
- **Controller**: `LoginServlet`, `RegisterServlet`, `TaskServlet`, `LogoutServlet` phụ trách xác thực và enqueue task.
- **View**: `login.jsp`, `register.jsp`, `dashboard.jsp`, `db_update.jsp`.
- **Background services**: `WorkerThread` + `TaskQueue` xử lý nặng, `SchemaInitListener` đảm bảo DB tương thích.

## 7. Demo TCP/UDP song song
Các demo mạng được build cùng project và có thể chạy bằng `java -cp target/classes` sau khi `mvn package`:

| Thành phần | Lệnh chạy (ví dụ) | Ghi chú |
| --- | --- | --- |
| TCP Server | `java com.ltm.tcp.TcpEchoServer` | Lắng nghe port `12345`, mỗi client chạy ở một `ClientHandler` riêng. |
| TCP Client | `java com.ltm.tcp.TcpEchoClient` | Kết nối `localhost:12345`, gõ `bye` để thoát. |
| UDP Server | `java com.ltm.udp.UdpEchoServer` | Lắng nghe port `9876`, phản hồi “Echo: …”. |
| UDP Client | `java com.ltm.udp.UdpEchoClient` | Gửi datagram tới `localhost:9876`, kết thúc bằng `bye`. |

## 8. Xử lý sự cố thường gặp
- **Không upload được file lớn**: tăng `maxPostSize` của Tomcat Connector và chỉnh `@MultipartConfig(maxFileSize = …)` nếu cần.
- **Lỗi `Communications link failure`**: kiểm tra MySQL chạy trên port 3306 và cấu hình `allowPublicKeyRetrieval=true` như trong `DatabaseConnection`.
- **POI báo thiếu memory**: với file `.docx` lớn, cấp thêm RAM cho JVM (`set "JAVA_OPTS=-Xms512m -Xmx1024m"`).
- **Worker không chạy**: chắc chắn `AppContextListener` đã được Tomcat load (web.xml hoặc annotation). Kiểm tra log `Worker Thread started...`.

Sau khi hoàn tất các bước trên, hệ thống sẵn sàng để kiểm tra đạo văn và chạy song song các ví dụ TCP/UDP phục vụ môn LTM.
