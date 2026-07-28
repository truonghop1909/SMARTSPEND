# Lộ trình phát triển

## SmartSpend

### AI Personal Finance Manager

---

## 1. Giới thiệu

### 1.1 Mục đích

Tài liệu này mô tả lộ trình phát triển dự án **SmartSpend** từ giai đoạn chuẩn bị đến khi hoàn thiện phiên bản đầu tiên.

Mục tiêu của lộ trình là:

- Chia dự án thành các giai đoạn rõ ràng.
- Xác định thứ tự phát triển hợp lý.
- Theo dõi tiến độ.
- Hạn chế việc làm nhiều chức năng cùng lúc.
- Ưu tiên hoàn thành các module cốt lõi trước.
- Đảm bảo dự án đủ chất lượng để đưa vào CV và GitHub Portfolio.

---

## 1.2 Phạm vi phiên bản đầu tiên

Phiên bản đầu tiên tập trung vào các chức năng:

- Đăng ký và đăng nhập.
- Quản lý danh mục.
- Quản lý thu nhập và chi tiêu.
- Quản lý ngân sách.
- Dashboard thống kê.
- Thông báo.
- AI Financial Assistant.
- Xuất báo cáo CSV và Excel.
- Docker hóa môi trường phát triển.
- Kiểm thử các luồng quan trọng.

Phiên bản đầu không bao gồm:

- Ví điện tử.
- Chuyển tiền.
- Thanh toán trực tuyến.
- Liên kết tài khoản ngân hàng.
- OCR hóa đơn.
- Ứng dụng di động.
- Microservices.
- Hệ thống phân quyền nhiều vai trò.

---

## 2. Nguyên tắc phát triển

Dự án được phát triển theo các nguyên tắc sau:

1. Hoàn thành từng module trước khi chuyển sang module tiếp theo.
2. Ưu tiên chức năng cốt lõi trước tính năng mở rộng.
3. Mỗi module phải có kiểm thử cơ bản.
4. Mọi thay đổi Database phải thực hiện qua Flyway.
5. Mọi API mới phải được cập nhật Swagger.
6. Không thêm tính năng ngoài phạm vi khi phiên bản đầu chưa hoàn thành.
7. Luôn giữ project ở trạng thái có thể chạy được.
8. Mỗi giai đoạn phải có tiêu chí hoàn thành rõ ràng.

---

## 3. Tổng quan các giai đoạn

| Giai đoạn | Nội dung chính | Kết quả |
|---:|---|---|
| 0 | Chuẩn bị và thiết kế | Bộ tài liệu và cấu trúc dự án |
| 1 | Khởi tạo nền tảng | Project chạy với MySQL, Redis và Swagger |
| 2 | Authentication | Đăng ký, đăng nhập, JWT |
| 3 | Category | Quản lý danh mục |
| 4 | Transaction | Quản lý thu và chi |
| 5 | Budget và Notification | Ngân sách và cảnh báo |
| 6 | Dashboard | Báo cáo và thống kê |
| 7 | AI Assistant | Phân tích tài chính bằng AI |
| 8 | Export | Xuất CSV và Excel |
| 9 | Kiểm thử và tối ưu | Test, bảo mật, hiệu năng |
| 10 | Hoàn thiện Portfolio | README, ảnh, demo và CV |

---

## 4. Thời gian dự kiến

Thời gian đề xuất:

```text
10 đến 12 tuần
```

Khối lượng học tập phù hợp:

```text
10 đến 15 giờ mỗi tuần
```

Đây chỉ là thời gian tham khảo. Có thể điều chỉnh theo lịch học và mức độ quen thuộc với Spring Boot.

---

# 5. Giai đoạn 0 — Chuẩn bị và thiết kế

## Mục tiêu

Hoàn thành tài liệu và thống nhất thiết kế trước khi viết code.

## Công việc

- [x] Xác định tên dự án.
- [x] Chốt phạm vi ứng dụng.
- [x] Viết `README.md`.
- [x] Viết `00-Project-Overview.md`.
- [x] Viết `01-Architecture.md`.
- [x] Viết `02-Business-Rules.md`.
- [x] Viết `03-Database-Design.md`.
- [x] Viết `04-Module-Design.md`.
- [x] Viết `05-API-Design.md`.
- [x] Viết `06-AI-Module.md`.
- [x] Viết `07-UML.md`.
- [x] Viết `08-Coding-Conventions.md`.
- [ ] Viết `10-Interview-Notes.md`.

## Kết quả cần đạt

- Phạm vi dự án rõ ràng.
- Database được chốt.
- API được xác định.
- Cấu trúc package được thống nhất.
- Không còn mâu thuẫn giữa các tài liệu.

---

# 6. Giai đoạn 1 — Khởi tạo nền tảng

## Thời gian dự kiến

```text
Tuần 1
```

## Mục tiêu

Khởi tạo project Spring Boot và cấu hình các thành phần nền tảng.

## Công việc

### Khởi tạo Spring Boot

- [ ] Tạo project bằng Spring Initializr.
- [ ] Sử dụng Java 21.
- [ ] Sử dụng Maven.
- [ ] Thiết lập package gốc `com.smartspend`.
- [ ] Kiểm tra project chạy thành công.

### Dependency

Thêm các dependency:

- [ ] Spring Web.
- [ ] Spring Security.
- [ ] Spring Data JPA.
- [ ] Jakarta Validation.
- [ ] MySQL Driver.
- [ ] Spring Data Redis.
- [ ] Flyway.
- [ ] Lombok.
- [ ] MapStruct.
- [ ] Springdoc OpenAPI.
- [ ] Spring Boot Test.

### Cấu hình môi trường

- [ ] Tạo `application.yml`.
- [ ] Tạo profile `dev`.
- [ ] Tạo profile `test`.
- [ ] Tạo file cấu hình mẫu.
- [ ] Không commit secret thật.

### Docker

- [ ] Tạo `docker-compose.yml`.
- [ ] Cấu hình MySQL 8.
- [ ] Cấu hình Redis.
- [ ] Kiểm tra kết nối từ Backend.

### Common Package

- [ ] Tạo `ApiResponse`.
- [ ] Tạo `PageResponse`.
- [ ] Tạo `AppException`.
- [ ] Tạo `ErrorCode`.
- [ ] Tạo `GlobalExceptionHandler`.
- [ ] Tạo Trace ID Filter.

### Swagger

- [ ] Cấu hình OpenAPI.
- [ ] Truy cập được Swagger UI.
- [ ] Cấu hình Bearer JWT Security Scheme.

## Tiêu chí hoàn thành

- Ứng dụng chạy thành công.
- Kết nối được MySQL.
- Kết nối được Redis.
- Flyway chạy migration.
- Swagger UI hoạt động.
- API kiểm tra sức khỏe trả về thành công.

---

# 7. Giai đoạn 2 — Authentication

## Thời gian dự kiến

```text
Tuần 2
```

## Mục tiêu

Hoàn thành cơ chế xác thực và bảo vệ API.

## Công việc

### Entity và Repository

- [ ] Tạo Entity `User`.
- [ ] Tạo Entity `RefreshToken`.
- [ ] Tạo Entity `UserSession`.
- [ ] Tạo Repository tương ứng.

### Security

- [ ] Tạo `UserPrincipal`.
- [ ] Tạo `CustomUserDetailsService`.
- [ ] Tạo `JwtService`.
- [ ] Tạo `JwtAuthenticationFilter`.
- [ ] Cấu hình `SecurityFilterChain`.
- [ ] Cấu hình CORS.
- [ ] Cấu hình endpoint public và protected.

### Authentication Service

- [ ] Đăng ký.
- [ ] Mã hóa mật khẩu bằng BCrypt.
- [ ] Đăng nhập.
- [ ] Sinh Access Token.
- [ ] Sinh Refresh Token.
- [ ] Băm Refresh Token trước khi lưu.
- [ ] Refresh Token Rotation.
- [ ] Đăng xuất.
- [ ] Lấy thông tin người dùng hiện tại.

### Rate Limit

- [ ] Giới hạn số lần đăng nhập thất bại.
- [ ] Lưu bộ đếm trên Redis.
- [ ] Thiết lập thời gian hết hạn.

### Kiểm thử

- [ ] Đăng ký hợp lệ.
- [ ] Email bị trùng.
- [ ] Đăng nhập đúng.
- [ ] Đăng nhập sai.
- [ ] Access Token không hợp lệ.
- [ ] Refresh Token hết hạn.
- [ ] Refresh Token đã bị thu hồi.
- [ ] API bảo vệ không cho truy cập khi thiếu token.

## Tiêu chí hoàn thành

- Người dùng đăng ký được.
- Người dùng đăng nhập và nhận token.
- Có thể làm mới token.
- Logout thu hồi Refresh Token.
- API protected hoạt động đúng.
- Không lộ mật khẩu hoặc Token Hash.

---

# 8. Giai đoạn 3 — Category

## Thời gian dự kiến

```text
Tuần 3
```

## Mục tiêu

Hoàn thành quản lý danh mục thu nhập và chi tiêu.

## Công việc

### Database

- [ ] Tạo migration bảng `categories`.
- [ ] Seed danh mục mặc định.
- [ ] Tạo unique constraint phù hợp.

### Backend

- [ ] Tạo Entity `Category`.
- [ ] Tạo Enum `CategoryType`.
- [ ] Tạo DTO.
- [ ] Tạo Mapper.
- [ ] Tạo Repository.
- [ ] Tạo Service.
- [ ] Tạo Controller.

### Chức năng

- [ ] Lấy danh sách danh mục.
- [ ] Lọc theo `INCOME` hoặc `EXPENSE`.
- [ ] Tạo danh mục cá nhân.
- [ ] Cập nhật danh mục.
- [ ] Xóa danh mục chưa được sử dụng.
- [ ] Không cho sửa danh mục mặc định.
- [ ] Kiểm tra quyền sở hữu.

### Kiểm thử

- [ ] Tạo danh mục hợp lệ.
- [ ] Tên danh mục bị trùng.
- [ ] Không sửa danh mục mặc định.
- [ ] Không xóa danh mục của người khác.
- [ ] Lấy được cả danh mục hệ thống và danh mục cá nhân.

## Tiêu chí hoàn thành

- CRUD Category hoạt động.
- Danh mục mặc định được seed thành công.
- Quyền sở hữu được kiểm tra.
- API xuất hiện đầy đủ trên Swagger.

---

# 9. Giai đoạn 4 — Transaction

## Thời gian dự kiến

```text
Tuần 4 đến tuần 5
```

## Mục tiêu

Hoàn thành module trung tâm của hệ thống.

## Công việc

### Database

- [ ] Tạo migration bảng `transactions`.
- [ ] Thêm Foreign Key.
- [ ] Thêm index theo người dùng và ngày.
- [ ] Thêm cột Soft Delete.

### Backend

- [ ] Tạo Entity `Transaction`.
- [ ] Tạo Enum `TransactionType`.
- [ ] Tạo Request DTO.
- [ ] Tạo Response DTO.
- [ ] Tạo Mapper.
- [ ] Tạo Repository.
- [ ] Tạo Specification.
- [ ] Tạo Service.
- [ ] Tạo Controller.

### Chức năng

- [ ] Tạo khoản thu.
- [ ] Tạo khoản chi.
- [ ] Xem chi tiết.
- [ ] Cập nhật.
- [ ] Xóa mềm.
- [ ] Phân trang.
- [ ] Lọc theo thời gian.
- [ ] Lọc theo danh mục.
- [ ] Lọc theo loại.
- [ ] Tìm kiếm ghi chú.
- [ ] Lọc theo khoảng tiền.

### Business Rules

- [ ] Số tiền lớn hơn 0.
- [ ] Không tạo giao dịch trong tương lai.
- [ ] Loại giao dịch khớp loại danh mục.
- [ ] Danh mục hợp lệ.
- [ ] Chỉ truy cập dữ liệu của chính mình.
- [ ] Giao dịch đã xóa không xuất hiện trong truy vấn.

### Cache

- [ ] Xóa cache Dashboard khi giao dịch thay đổi.
- [ ] Xóa cache Financial Context nếu có.

### Kiểm thử

- [ ] Tạo thu nhập.
- [ ] Tạo chi tiêu.
- [ ] Loại không khớp danh mục.
- [ ] Số tiền không hợp lệ.
- [ ] Ngày ở tương lai.
- [ ] Truy cập giao dịch người khác.
- [ ] Soft Delete.
- [ ] Filter và Pagination.

## Tiêu chí hoàn thành

- Toàn bộ CRUD và Filter hoạt động.
- Soft Delete đúng.
- Query không lấy giao dịch đã xóa.
- Business Rules được kiểm thử.
- Transaction API ổn định.

---

# 10. Giai đoạn 5 — Budget và Notification

## Thời gian dự kiến

```text
Tuần 6
```

## Mục tiêu

Theo dõi ngân sách và tạo cảnh báo chi tiêu.

## Công việc

### Budget

- [ ] Tạo migration bảng `budgets`.
- [ ] Tạo Entity và Repository.
- [ ] Tạo DTO và Mapper.
- [ ] Tạo Service và Controller.
- [ ] Tạo ngân sách theo tháng.
- [ ] Cập nhật hạn mức.
- [ ] Xóa ngân sách.
- [ ] Tính số tiền đã chi.
- [ ] Tính số tiền còn lại.
- [ ] Tính phần trăm sử dụng.

### Notification

- [ ] Tạo migration bảng `notifications`.
- [ ] Tạo Entity và Repository.
- [ ] Tạo Service và Controller.
- [ ] Lấy danh sách thông báo.
- [ ] Đếm thông báo chưa đọc.
- [ ] Đánh dấu đã đọc.
- [ ] Đánh dấu tất cả đã đọc.
- [ ] Xóa thông báo.

### Budget Alert

- [ ] Cảnh báo tại 80%.
- [ ] Cảnh báo tại 100%.
- [ ] Cảnh báo khi vượt 100%.
- [ ] Hạn chế tạo thông báo trùng.

### Kiểm thử

- [ ] Tạo ngân sách hợp lệ.
- [ ] Ngân sách trùng tháng.
- [ ] Danh mục không phải Expense.
- [ ] Tính tiến độ đúng.
- [ ] Tạo thông báo đúng ngưỡng.
- [ ] Không xem thông báo của người khác.

## Tiêu chí hoàn thành

- Ngân sách được tính đúng từ Transaction.
- Thông báo được tạo đúng lúc.
- Không có thông báo trùng không cần thiết.
- Budget API và Notification API hoạt động.

---

# 11. Giai đoạn 6 — Dashboard

## Thời gian dự kiến

```text
Tuần 7
```

## Mục tiêu

Cung cấp số liệu tổng quan và dữ liệu cho biểu đồ.

## Công việc

### Query tổng hợp

- [ ] Tổng thu.
- [ ] Tổng chi.
- [ ] Chênh lệch thu chi.
- [ ] Chi tiêu theo danh mục.
- [ ] Xu hướng theo tháng.
- [ ] Giao dịch gần đây.
- [ ] So sánh kỳ hiện tại với kỳ trước.

### DTO

- [ ] `DashboardSummaryResponse`.
- [ ] `CategoryStatisticResponse`.
- [ ] `TrendResponse`.
- [ ] `PeriodComparisonResponse`.

### Cache

- [ ] Cache Dashboard bằng Redis.
- [ ] Thiết kế Cache Key.
- [ ] Thiết lập TTL.
- [ ] Xóa cache khi Transaction thay đổi.

### Kiểm thử

- [ ] Không tính giao dịch đã xóa.
- [ ] Tổng thu đúng.
- [ ] Tổng chi đúng.
- [ ] Phần trăm danh mục đúng.
- [ ] So sánh kỳ đúng.
- [ ] Cache hoạt động.

## Tiêu chí hoàn thành

- Dashboard API trả dữ liệu chính xác.
- Query có hiệu năng phù hợp.
- Redis Cache hoạt động đúng.
- Dữ liệu đủ cho Frontend vẽ biểu đồ.

---

# 12. Giai đoạn 7 — AI Financial Assistant

## Thời gian dự kiến

```text
Tuần 8 đến tuần 9
```

## Mục tiêu

Tích hợp AI để phân tích dữ liệu tài chính của người dùng.

## Công việc

### Database

- [ ] Tạo bảng `chat_sessions`.
- [ ] Tạo bảng `chat_messages`.
- [ ] Thêm index lịch sử hội thoại.

### Chat Module

- [ ] Tạo phiên chat.
- [ ] Lấy danh sách phiên.
- [ ] Lấy lịch sử tin nhắn.
- [ ] Xóa phiên chat.
- [ ] Kiểm tra quyền sở hữu.

### Context Builder

- [ ] Tổng hợp thu và chi.
- [ ] Tổng hợp theo danh mục.
- [ ] Tổng hợp ngân sách.
- [ ] So sánh với kỳ trước.
- [ ] Giảm dữ liệu không cần thiết.

### Prompt Builder

- [ ] Tạo System Prompt.
- [ ] Thêm Financial Context.
- [ ] Thêm lịch sử hội thoại.
- [ ] Thêm câu hỏi hiện tại.
- [ ] Giới hạn độ dài Prompt.

### AI Provider

- [ ] Tạo `AIProviderClient`.
- [ ] Tích hợp OpenAI hoặc Gemini.
- [ ] Cấu hình timeout.
- [ ] Cấu hình retry.
- [ ] Xử lý Provider lỗi.
- [ ] Parse response.

### Bảo mật và chi phí

- [ ] Không gửi dữ liệu nhạy cảm.
- [ ] Rate Limit AI.
- [ ] Giới hạn độ dài câu hỏi.
- [ ] Giới hạn lịch sử chat.
- [ ] Log số token và thời gian phản hồi.

### Kiểm thử

- [ ] Mock AI Provider.
- [ ] AI trả lời thành công.
- [ ] Không đủ dữ liệu.
- [ ] Provider timeout.
- [ ] Provider lỗi.
- [ ] Phiên chat thuộc người khác.
- [ ] Rate Limit.

## Tiêu chí hoàn thành

- Người dùng có thể tạo phiên chat.
- AI trả lời dựa trên dữ liệu thật.
- Lịch sử hội thoại được lưu.
- AI không sửa dữ liệu.
- Provider lỗi không làm ứng dụng bị crash.
- Không lộ thông tin nhạy cảm.

---

# 13. Giai đoạn 8 — Export

## Thời gian dự kiến

```text
Tuần 10
```

## Mục tiêu

Cho phép người dùng tải dữ liệu giao dịch.

## Công việc

- [ ] Tạo Export Service.
- [ ] Tạo CSV Generator.
- [ ] Tạo Excel Generator.
- [ ] Lọc theo ngày.
- [ ] Lọc theo loại.
- [ ] Lọc theo danh mục.
- [ ] Thiết lập Content Type.
- [ ] Thiết lập tên file.
- [ ] Chỉ xuất giao dịch chưa xóa.
- [ ] Chỉ xuất dữ liệu người dùng hiện tại.

## Kiểm thử

- [ ] Xuất CSV.
- [ ] Xuất Excel.
- [ ] Khoảng ngày sai.
- [ ] Không có dữ liệu.
- [ ] Dữ liệu của người khác không bị xuất.
- [ ] Nội dung file đúng.

## Tiêu chí hoàn thành

- File tải xuống được.
- Dữ liệu đúng bộ lọc.
- Định dạng file hợp lệ.
- Không cần lưu Export Job trong Database.

---

# 14. Giai đoạn 9 — Kiểm thử và tối ưu

## Thời gian dự kiến

```text
Tuần 11
```

## Mục tiêu

Đảm bảo ứng dụng ổn định trước khi demo.

## Công việc

### Unit Test

- [ ] Authentication Service.
- [ ] Category Service.
- [ ] Transaction Service.
- [ ] Budget Service.
- [ ] Dashboard Service.
- [ ] Chat Service.
- [ ] Prompt Builder.
- [ ] Context Builder.

### Integration Test

- [ ] Register và Login.
- [ ] CRUD Category.
- [ ] CRUD Transaction.
- [ ] Budget Alert.
- [ ] Dashboard.
- [ ] Notification.
- [ ] AI Chat.
- [ ] Export.

### Security Review

- [ ] Kiểm tra JWT.
- [ ] Kiểm tra quyền sở hữu.
- [ ] Kiểm tra Validation.
- [ ] Kiểm tra CORS.
- [ ] Kiểm tra secret.
- [ ] Kiểm tra log nhạy cảm.
- [ ] Kiểm tra Rate Limit.

### Database Review

- [ ] Kiểm tra index.
- [ ] Kiểm tra Foreign Key.
- [ ] Kiểm tra query chậm.
- [ ] Kiểm tra N+1 Query.
- [ ] Kiểm tra Soft Delete.
- [ ] Kiểm tra Flyway Migration.

### Hiệu năng

- [ ] Đo thời gian Dashboard.
- [ ] Kiểm tra Redis Cache.
- [ ] Kiểm tra Pagination.
- [ ] Không tải toàn bộ dữ liệu không cần thiết.
- [ ] Kiểm tra timeout AI.

## Tiêu chí hoàn thành

- Các luồng chính có test.
- Không còn lỗi nghiêm trọng.
- Không có dữ liệu người dùng bị truy cập chéo.
- Query chính hoạt động ổn định.
- API trả lỗi rõ ràng.

---

# 15. Giai đoạn 10 — Hoàn thiện Portfolio

## Thời gian dự kiến

```text
Tuần 12
```

## Mục tiêu

Chuẩn bị dự án để đưa lên GitHub, CV và demo.

## Công việc

### GitHub

- [ ] Hoàn thiện README.
- [ ] Thêm ảnh Dashboard.
- [ ] Thêm ảnh Swagger.
- [ ] Thêm ảnh AI Chat.
- [ ] Thêm ERD.
- [ ] Thêm sơ đồ kiến trúc.
- [ ] Kiểm tra toàn bộ link tài liệu.
- [ ] Xóa secret và dữ liệu cá nhân.
- [ ] Thêm file cấu hình mẫu.

### Demo Data

- [ ] Tạo tài khoản mẫu.
- [ ] Seed danh mục.
- [ ] Tạo giao dịch mẫu.
- [ ] Tạo ngân sách mẫu.
- [ ] Chuẩn bị câu hỏi AI mẫu.

### Docker

- [ ] Tạo Dockerfile.
- [ ] Hoàn thiện Docker Compose.
- [ ] Kiểm tra chạy project bằng một lệnh.
- [ ] Viết hướng dẫn chạy.

### API

- [ ] Hoàn thiện Swagger.
- [ ] Export Postman Collection.
- [ ] Thêm ví dụ Request và Response.

### CV

- [ ] Viết mô tả dự án.
- [ ] Liệt kê Tech Stack.
- [ ] Liệt kê đóng góp chính.
- [ ] Gắn link GitHub.
- [ ] Gắn link demo nếu có.

## Tiêu chí hoàn thành

- Người khác clone và chạy được.
- README dễ hiểu.
- Có ảnh minh họa.
- Swagger đầy đủ.
- Không chứa secret.
- Dự án sẵn sàng để đưa vào CV.

---

# 16. Milestone

## Milestone 1 — Backend Foundation

Bao gồm:

- Project Setup.
- Docker.
- MySQL.
- Redis.
- Flyway.
- Swagger.
- Common Response.

Trạng thái:

```text
Chưa bắt đầu
```

---

## Milestone 2 — Core Finance

Bao gồm:

- Authentication.
- Category.
- Transaction.

Trạng thái:

```text
Chưa bắt đầu
```

---

## Milestone 3 — Financial Analysis

Bao gồm:

- Budget.
- Notification.
- Dashboard.

Trạng thái:

```text
Chưa bắt đầu
```

---

## Milestone 4 — AI Integration

Bao gồm:

- Chat Session.
- Chat Message.
- Financial Context.
- Prompt Builder.
- AI Provider.

Trạng thái:

```text
Chưa bắt đầu
```

---

## Milestone 5 — Portfolio Release

Bao gồm:

- Export.
- Testing.
- Docker.
- Documentation.
- Demo.
- CV.

Trạng thái:

```text
Chưa bắt đầu
```

---

# 17. Definition of Done

Một chức năng chỉ được xem là hoàn thành khi đáp ứng đầy đủ:

- [ ] Code đúng kiến trúc.
- [ ] Business Rules được xử lý.
- [ ] Validation đầy đủ.
- [ ] Kiểm tra quyền sở hữu.
- [ ] Không trả Entity trực tiếp.
- [ ] Exception có Error Code.
- [ ] Swagger được cập nhật.
- [ ] Có Unit Test hoặc Integration Test phù hợp.
- [ ] Không có secret trong mã nguồn.
- [ ] Code đã được review lại.
- [ ] Tài liệu liên quan được cập nhật.
- [ ] Chức năng chạy được trong môi trường Docker hoặc Development.

---

# 18. Cách theo dõi tiến độ

Có thể sử dụng GitHub Projects với các cột:

```text
Backlog

Ready

In Progress

Review

Testing

Done
```

Mỗi task nên có:

- Tên rõ ràng.
- Module.
- Mô tả.
- Acceptance Criteria.
- Branch.
- Pull Request.
- Trạng thái.

Ví dụ task:

```text
Tên:
Create Transaction API

Module:
Transaction

Acceptance Criteria:
- Tạo được INCOME.
- Tạo được EXPENSE.
- amount > 0.
- Category đúng type.
- Không cho dùng Category của người khác.
- Có Swagger.
- Có test.
```

---

# 19. Branch Strategy

Các branch chính:

```text
main

develop
```

Feature branch:

```text
feature/authentication

feature/category

feature/transaction

feature/budget

feature/dashboard

feature/ai-assistant
```

Bug fix:

```text
fix/transaction-filter

fix/refresh-token
```

Quy trình:

```text
feature branch
      │
      ▼
Pull Request
      │
      ▼
develop
      │
      ▼
main
```

Với project cá nhân, có thể bỏ `develop` nếu muốn đơn giản và merge trực tiếp feature branch vào `main` qua Pull Request.

---

# 20. Thứ tự ưu tiên

## Ưu tiên bắt buộc

1. Authentication.
2. Category.
3. Transaction.
4. Budget.
5. Dashboard.
6. AI Assistant.

## Ưu tiên nên có

7. Notification.
8. Export.
9. Redis Cache.
10. Docker.

## Có thể làm sau

- OCR hóa đơn.
- Voice Input.
- Mobile App.
- Google Login.
- CI/CD nâng cao.
- Cloud Deployment.
- Dự báo chi tiêu.

---

# 21. Phương án rút gọn khi thiếu thời gian

Nếu không đủ thời gian, giữ lại:

- Authentication.
- Category.
- Transaction.
- Budget.
- Dashboard.
- AI Chat cơ bản.
- Swagger.
- Docker.
- README.

Có thể tạm hoãn:

- User Session UI.
- Export Excel.
- AI Structured Output.
- Circuit Breaker.
- Thông báo cuối tháng.
- Test Coverage cao.
- Cloud Deployment.

Không nên bỏ:

- Kiểm tra quyền sở hữu.
- Validation.
- Soft Delete.
- Business Rules.
- Error Handling.
- Bảo mật JWT.

---

# 22. Phiên bản phát hành

## Version 0.1.0

```text
Project Setup
Authentication
```

## Version 0.2.0

```text
Category
Transaction
```

## Version 0.3.0

```text
Budget
Notification
Dashboard
```

## Version 0.4.0

```text
AI Assistant
```

## Version 0.5.0

```text
Export
Testing
Docker
Documentation
```

## Version 1.0.0

```text
Portfolio Release
```

---

# 23. Tổng kết

SmartSpend được phát triển theo lộ trình từ nền tảng đến nghiệp vụ cốt lõi, sau đó mới tích hợp AI và hoàn thiện Portfolio.

Thứ tự phát triển chính:

```text
Project Setup
      │
      ▼
Authentication
      │
      ▼
Category
      │
      ▼
Transaction
      │
      ▼
Budget
      │
      ▼
Dashboard
      │
      ▼
Notification
      │
      ▼
AI Assistant
      │
      ▼
Export
      │
      ▼
Testing
      │
      ▼
Portfolio Release
```

Mục tiêu cuối cùng không chỉ là hoàn thành một ứng dụng quản lý thu chi, mà còn xây dựng một dự án thể hiện rõ năng lực:

- Phân tích nghiệp vụ.
- Thiết kế cơ sở dữ liệu.
- Xây dựng REST API.
- Bảo mật với Spring Security và JWT.
- Làm việc với MySQL và Redis.
- Tích hợp AI Provider.
- Viết kiểm thử.
- Docker hóa ứng dụng.
- Tổ chức mã nguồn và tài liệu chuyên nghiệp.