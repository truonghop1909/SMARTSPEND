# Cấu trúc dự án

## SmartSpend

### AI Personal Finance Manager

---

## 1. Mục đích

Tài liệu này ghi lại cấu trúc thư mục và các file hiện có của dự án SmartSpend.

Tài liệu được sử dụng để:

- Theo dõi các file đã tạo.
- Xác định vị trí của từng thành phần.
- Tránh tạo file trùng lặp.
- Giữ cấu trúc package nhất quán.
- Cung cấp bối cảnh cho ChatGPT khi tạo code.
- Hỗ trợ kiểm tra tiến độ phát triển.

Tài liệu phải được cập nhật sau mỗi lần:

- Thêm file mới.
- Xóa file.
- Đổi tên file.
- Di chuyển package.
- Thêm module mới.

---

## 2. Cấu trúc tổng thể

```text
smartspend/
├── README.md
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── .gitignore
├── .env.example
│
├── docs/
│   ├── 00-Project-Overview.md
│   ├── 01-Architecture.md
│   ├── 02-Business-Rules.md
│   ├── 03-Database-Design.md
│   ├── 04-Module-Design.md
│   ├── 05-API-Design.md
│   ├── 06-AI-Module.md
│   ├── 07-UML.md
│   ├── 08-Coding-Conventions.md
│   ├── 09-Development-Roadmap.md
│   ├── 10-Interview-Notes.md
│   ├── 11-Code-Generation-Plan.md
│   └── 12-Project-Structure.md
│
├── assets/
│   ├── diagrams/
│   ├── screenshots/
│   └── logo/
│
├── postman/
│   ├── SmartSpend.postman_collection.json
│   └── SmartSpend.postman_environment.json
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── smartspend/
    │   │           ├── SmartSpendApplication.java
    │   │           ├── auth/
    │   │           ├── category/
    │   │           ├── transaction/
    │   │           ├── budget/
    │   │           ├── dashboard/
    │   │           ├── notification/
    │   │           ├── ai/
    │   │           ├── export/
    │   │           ├── common/
    │   │           ├── config/
    │   │           └── security/
    │   │
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-test.yml
    │       └── db/
    │           └── migration/
    │               ├── V1__create_initial_schema.sql
    │               └── V2__insert_default_categories.sql
    │
    └── test/
        └── java/
            └── com/
                └── smartspend/
```

---

## 3. Cấu trúc package theo module

### 3.1 Authentication

```text
auth/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── mapper/
├── repository/
└── service/
```

---

### 3.2 Category

```text
category/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── mapper/
├── repository/
└── service/
```

---

### 3.3 Transaction

```text
transaction/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── mapper/
├── repository/
├── service/
└── specification/
```

---

### 3.4 Budget

```text
budget/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── mapper/
├── repository/
└── service/
```

---

### 3.5 Dashboard

```text
dashboard/
├── controller/
├── dto/
│   └── response/
├── projection/
├── cache/
└── service/
```

Dashboard không có Entity riêng vì chỉ tổng hợp dữ liệu từ các module khác.

---

### 3.6 Notification

```text
notification/
├── controller/
├── dto/
│   └── response/
├── entity/
├── mapper/
├── repository/
└── service/
```

---

### 3.7 AI Assistant

```text
ai/
├── client/
├── config/
├── context/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── mapper/
├── model/
├── parser/
├── prompt/
├── repository/
└── service/
```

---

### 3.8 Export

```text
export/
├── controller/
├── dto/
│   └── request/
├── generator/
├── model/
└── service/
```

Export không có Entity hoặc Repository riêng trong phiên bản đầu.

---

### 3.9 Common

```text
common/
├── exception/
│   ├── AppException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java
├── filter/
│   └── TraceIdFilter.java
├── response/
│   ├── ApiResponse.java
│   └── PageResponse.java
├── constant/
├── util/
└── validation/
```

Chỉ tạo `constant`, `util` và `validation` khi thực sự có file cần đặt vào.

Không tạo package rỗng chỉ để đủ cấu trúc.

---

### 3.10 Config

```text
config/
├── JpaAuditingConfig.java
├── OpenApiConfig.java
├── RedisConfig.java
└── SecurityConfig.java
```

---

### 3.11 Security

```text
security/
├── CustomUserDetailsService.java
├── JwtAuthenticationFilter.java
├── JwtService.java
├── RestAccessDeniedHandler.java
├── RestAuthenticationEntryPoint.java
└── UserPrincipal.java
```

---

## 4. Trạng thái các module

| Module | Trạng thái | Ghi chú |
|---|---|---|
| Project Setup | Chưa bắt đầu | Tạo Spring Boot project |
| Common | Chưa bắt đầu | Response, Exception, Trace ID |
| Flyway | Chưa bắt đầu | Tạo schema ban đầu |
| Authentication | Chưa bắt đầu | JWT và Refresh Token |
| Category | Chưa bắt đầu | Danh mục mặc định và cá nhân |
| Transaction | Chưa bắt đầu | Module cốt lõi |
| Budget | Chưa bắt đầu | Ngân sách theo tháng |
| Dashboard | Chưa bắt đầu | Thống kê và Redis Cache |
| Notification | Chưa bắt đầu | Cảnh báo ngân sách |
| AI Assistant | Chưa bắt đầu | Phân tích tài chính |
| Export | Chưa bắt đầu | CSV và Excel |
| Testing | Chưa bắt đầu | Unit và Integration Test |
| Docker | Chưa bắt đầu | Backend, MySQL và Redis |

Các trạng thái sử dụng:

```text
Chưa bắt đầu
Đang thực hiện
Hoàn thành
Tạm hoãn
```

---

## 5. Quy tắc cập nhật cây thư mục

### Khi thêm file

Thêm file vào đúng vị trí trong cây thư mục.

Ví dụ sau khi tạo Authentication Entity:

```text
auth/
├── entity/
│   ├── User.java
│   ├── RefreshToken.java
│   └── UserSession.java
└── repository/
    ├── UserRepository.java
    ├── RefreshTokenRepository.java
    └── UserSessionRepository.java
```

---

### Khi đổi tên file

Cập nhật đồng thời:

- Cây thư mục.
- Các tài liệu có nhắc tới file.
- Import trong source code.
- Kế hoạch tạo code nếu chưa hoàn thành.

---

### Khi xóa file

Không chỉ xóa khỏi cây thư mục.

Cần kiểm tra:

- File nào đang import.
- Service nào đang sử dụng.
- Test nào liên quan.
- Tài liệu nào còn tham chiếu.

---

### Không thêm package rỗng

Chỉ thêm package khi có ít nhất một file thực tế.

Ví dụ chưa có Utility thì không cần tạo:

```text
common/util/
```

---

## 6. Nhật ký thay đổi cấu trúc

| Ngày | Thay đổi | Lý do |
|---|---|---|
| YYYY-MM-DD | Tạo cấu trúc ban đầu | Khởi tạo dự án |
| YYYY-MM-DD | Thêm module Authentication | Bắt đầu phát triển JWT |
| YYYY-MM-DD | Thêm Transaction Specification | Hỗ trợ tìm kiếm động |

Mỗi thay đổi lớn về cấu trúc nên được ghi lại trong bảng này.

---

## 7. Prompt cập nhật cây thư mục

Sau mỗi phiên tạo code, sử dụng prompt:

```text
Dựa trên danh sách file vừa tạo, hãy cập nhật nội dung file
docs/12-Project-Structure.md.

Yêu cầu:

1. Chỉ cập nhật cây thư mục và trạng thái module.
2. Không tự thêm file chưa tồn tại.
3. Giữ nguyên cấu trúc Package by Feature.
4. Đánh dấu đúng file đã hoàn thành.
5. Cập nhật nhật ký thay đổi.
6. Trả về toàn bộ file Markdown hoàn chỉnh để tôi thay thế file cũ.
```

---

## 8. Cách lấy cây thư mục thực tế

### Windows PowerShell

Chạy tại thư mục gốc của project:

```powershell
tree /F /A
```

Lưu vào file:

```powershell
tree /F /A > project-tree.txt
```

---

### Git Bash hoặc Linux

```bash
tree
```

Bỏ qua thư mục không cần thiết:

```bash
tree -I "target|.git|.idea|node_modules"
```

Lưu kết quả:

```bash
tree -I "target|.git|.idea|node_modules" > project-tree.txt
```

Nếu máy chưa có lệnh `tree`, có thể dùng:

```bash
find . -type f |
grep -v "./target/" |
grep -v "./.git/" |
sort
```

---

## 9. Các thư mục không đưa vào tài liệu

Không cần liệt kê chi tiết:

```text
target/
.git/
.idea/
.vscode/
logs/
tmp/
```

Các thư mục này được tạo tự động hoặc phụ thuộc môi trường cá nhân.

---

## 10. Tổng kết

`12-Project-Structure.md` là tài liệu phản ánh trạng thái thực tế của mã nguồn.

Tài liệu này phải:

- Khớp với project trong VS Code.
- Không chứa file chưa tồn tại.
- Được cập nhật sau mỗi nhóm code.
- Giúp ChatGPT biết file nào đã có.
- Giúp tránh tạo trùng class hoặc package.
- Giúp theo dõi tiến độ từng module.

Khi yêu cầu ChatGPT tạo code mới, nên gửi kèm:

```text
docs/12-Project-Structure.md
```

và các tài liệu thiết kế liên quan đến module đang phát triển.