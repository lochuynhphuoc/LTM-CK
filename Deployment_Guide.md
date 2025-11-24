# Hướng dẫn Triển khai WAR (Không cần build)

Tài liệu này dành cho người dùng chỉ muốn triển khai file WAR đã build sẵn và thêm dữ liệu corpus (tập tin .txt) vào máy chủ Tomcat của họ mà không phải thao tác Maven.

## 1. Chuẩn bị

- Môi trường chạy **Tomcat 9+** (Servlet API javax). Nếu dùng Tomcat 10 hãy chạy công cụ migration hoặc build lại với Jakarta.
- **MySQL 8+** đã tạo schema `ltm_final_project` bằng script `database.sql` (có thể chạy `mysql -u <root-user> -p < database.sql>` từ thư mục dự án). File script này đồng thời tạo tài khoản `ltm_app_user/ChangeMe123!` và cấp quyền; hãy đổi lại trực tiếp trong file SQL trước khi chạy (hoặc chạy lệnh `ALTER USER` sau đó) để tránh dùng chung mật khẩu mặc định.
- File WAR `final-project.war` đã build sẵn (lấy từ thư mục `target/` hoặc từ người khác cung cấp).

## 2. Triển khai WAR vào Tomcat

1. Dừng Tomcat nếu đang chạy (Windows: `bin\shutdown.bat`, Linux/Mac: `bin/shutdown.sh`).
2. Sao chép `final-project.war` vào thư mục `TOMCAT_HOME/webapps/`.
3. Khởi động Tomcat (`bin/startup.bat` hoặc `bin/startup.sh`). Tomcat sẽ tự giải nén WAR thành thư mục `webapps/final-project/`.
4. Mở trình duyệt: `http://<server>:8080/final-project/` để xác nhận trang login xuất hiện. Nếu thấy lỗi DB, kiểm tra `src/main/java/com/ltm/dao/DatabaseConnection.java` hoặc thiết lập biến môi trường tương ứng rồi redeploy.

## 3. Thêm file corpus (.txt) sau khi deploy

Ứng dụng đọc dữ liệu nguồn từ thư mục `WEB-INF/corpus` bên trong webapp đã giải nén. Bạn có thể thêm/xoá file mà không cần build lại.

1. Đi tới `TOMCAT_HOME/webapps/final-project/WEB-INF/corpus/`.
2. Chọn chủ đề phù hợp (ví dụ `technology`, `science`, `history`). Có thể tạo thư mục mới nếu muốn mở rộng chủ đề; tên thư mục sẽ xuất hiện trong dropdown nếu bạn cập nhật JSP.
3. Tạo file `.txt` mới trong thư mục đó hoặc copy từ máy bạn vào, dùng encoding UTF-8.
4. Không cần restart Tomcat: worker đọc toàn bộ file mỗi khi tác vụ mới tới. Nếu vừa thêm file nhưng chưa thấy trong kết quả, chỉ cần chạy lại tác vụ kiểm tra đạo văn.

### Gợi ý quản lý file corpus

- Đặt tên file phản ánh nội dung (`ai.txt`, `quantum_mechanics.txt`, ...).
- Nội dung nên là plain text, mỗi đoạn cách nhau bằng dòng trống để dễ đọc.
- Nếu chỉnh sửa trực tiếp trên server, nên backup thư mục corpus trước khi ghi đè.

## 4. Kiểm tra nhanh sau khi thêm file

1. Đăng nhập bằng tài khoản `admin/123456` (hoặc tài khoản bạn đã tạo).
2. Upload file nguồn và chọn chủ đề tương ứng.
3. Khi tác vụ hoàn tất, mở cột **Comparison Details** trên Dashboard để xác nhận file mới xuất hiện trong danh sách cùng phần trăm giống nhau.

## 5. Xử lý sự cố

- **404 /final-project/**: đảm bảo WAR có tên đúng (`final-project.war`) và Tomcat đã khởi động lại sau khi chép file.
- **Lỗi DB**: kiểm tra `logs/catalina.*` để xem chi tiết JDBC. Đảm bảo MySQL chạy và user có quyền `SELECT/INSERT/ALTER`.
- **Không thấy file corpus mới**: chắc chắn file nằm trong đúng thư mục chủ đề, định dạng `.txt`, và quyền đọc cho user chạy Tomcat.

Sau khi hoàn thành các bước trên, bạn có thể triển khai nhanh ứng dụng lên bất kỳ server Tomcat nào mà không cần mở IDE hay chạy Maven build.
