Dự án chúng em được build trên maven vì nhóm chúng em không dùng eclipse mà dùng VScode, nhưng dự án vẫn có thể import và eclipse bằng maven được bao gồm thêm hai file là
    Deployment_Guide.md (Hướng dẫn để set up mà không cần file build lại trên môi trường dev, có thể gửi file war tới khách hàng để build)
    Installation_Guide.md (Hướng dẫn cách build app nếu cần sửa đổi thêm trong mã nguồn dành cho dev)
    MVC_Architecture.md (Mô hình MVC của dự án)
Mô tả sơ về dự án:
Hệ thống sẽ cho người dùng có thể đăng nhập để kiểm tra một file text, doc,... lên để kiểm tra độ tương tự với các tài liệu đã có sẵn trên hệ thống theo một vài  topic, các tài liệu có thể thêm thủ công lên server Tomcat (sẽ có thể thêm phần giao diện để quản lý việc tải văn bản lên sau)
Trong folder target chỉ giữa lại file war để giảm dung lượng đăng tải