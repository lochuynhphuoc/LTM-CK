# Lý thuyết & Thực hành TCP/UDP trong project

Repository bao gồm 4 lớp Java demo (`com.ltm.tcp.*`, `com.ltm.udp.*`) minh hoạ lý thuyết mạng song song với phần web. Nội dung dưới đây mô tả cách các ví dụ được cài đặt và cách chạy chúng.

## 1. So sánh nhanh TCP vs UDP

| Đặc điểm | TCP (Transmission Control Protocol) | UDP (User Datagram Protocol) |
| --- | --- | --- |
| **Độ tin cậy** | Có ACK, cơ chế truyền lại, đảm bảo đúng thứ tự. Phù hợp giao dịch yêu cầu tính toàn vẹn. | Không có ACK hay truyền lại. Có thể mất gói, đổi thứ tự nhưng đổi lại độ trễ thấp. |
| **Kết nối** | Connection-oriented. Luồng bắt đầu bằng 3-way handshake (SYN, SYN-ACK, ACK). | Connectionless. Server chỉ cần mở socket và nhận datagram từ bất kỳ client nào. |
| **Đơn vị dữ liệu** | Luồng byte liên tục, developer phải chia logic gói tin. | Datagram rời rạc, giới hạn kích thước (thường ≤ 65 KB). |
| **Chi phí** | Header lớn (20B+) và nhiều cơ chế kiểm soát nên chậm hơn. | Header nhỏ (8B), không kiểm soát lỗi nên nhanh hơn. |
| **Ứng dụng điển hình** | HTTP(S), SSH, FTP, email. | VoIP, streaming, game real-time, DNS. |

## 2. TCP Echo Demo (cổng 12345)

### Server: `com.ltm.tcp.TcpEchoServer`
- Tạo `ServerSocket` cố định trên port `12345`.
- Mỗi khi `accept()` trả về một `Socket`, server spawn một `ClientHandler` (Thread mới) → hỗ trợ nhiều client song song.
- `ClientHandler` dùng `BufferedReader`/`PrintWriter` để đọc dòng văn bản và echo lại chuỗi `Echo: <message>`.
- Token “bye” kết thúc session và đóng socket bằng `try-with-resources` để tránh rò rỉ.

### Client: `com.ltm.tcp.TcpEchoClient`
- Kết nối `localhost:12345`, đọc input từ `Scanner`, gửi từng dòng qua `PrintWriter`.
- Nhận phản hồi bằng `BufferedReader` và in ra console.

### Cách chạy
```bash
# Terminal 1
mvn -q compile
java -cp target/classes com.ltm.tcp.TcpEchoServer

# Terminal 2
java -cp target/classes com.ltm.tcp.TcpEchoClient
```
Gõ vài câu, quan sát client và server cùng hiển thị dữ liệu nhận/echo. Gõ `bye` để đóng kết nối an toàn.

## 3. UDP Echo Demo (cổng 9876)

### Server: `com.ltm.udp.UdpEchoServer`
- Tạo `DatagramSocket` port `9876` và vòng lặp vô hạn.
- Mỗi `socket.receive(packet)` trả về dữ liệu từ client bất kỳ. Server chuyển thành chuỗi và in log.
- Tạo `DatagramPacket` phản hồi với nội dung `Echo: ...` tới đúng địa chỉ và port của client (lấy từ packet nhận).

### Client: `com.ltm.udp.UdpEchoClient`
- Mở `DatagramSocket` cục bộ, nhập văn bản qua `Scanner`.
- Đóng gói thành `DatagramPacket` gửi tới `localhost:9876`.
- Chờ nhận packet trả về. Vì UDP không đảm bảo, client luôn chủ động xử lý trường hợp timeout/lỗi (trong ví dụ hiện tại là `SocketException`).

### Cách chạy
```bash
# Terminal 1
java -cp target/classes com.ltm.udp.UdpEchoServer

# Terminal 2
java -cp target/classes com.ltm.udp.UdpEchoClient
```
UDP không có khái niệm kết thúc phiên nên cả hai chương trình chỉ dừng khi người dùng gõ `bye` (client) hoặc nhấn `Ctrl+C` (server).

## 4. Ý nghĩa đối với đồ án
- Các demo giúp sinh viên thấy rõ khác biệt giữa socket hướng luồng (TCP) và datagram (UDP) trước khi làm việc với task queue/worker trong phần web.
- Mẫu code sử dụng **try-with-resources**, **multi-threading**, và **buffer** chuẩn, có thể tái sử dụng khi cần tích hợp chức năng mạng thực thụ vào ứng dụng chính.
- Ports `12345` và `9876` chỉ dùng cho demo cục bộ; khi deploy cần mở firewall tương ứng hoặc đổi port cho phù hợp hạ tầng.
- Khi chuyển sang module web, hàng đợi nhiệm vụ truyền cho `WorkerThread` các đối tượng `Task` với hai field nội dung là `sourceContent` và `targetContent` tương ứng hai cột `source_content` và `target_content`. Điều này giúp người học liên hệ rõ giữa phần lý thuyết mạng và cơ chế kiểm tra đạo văn.
