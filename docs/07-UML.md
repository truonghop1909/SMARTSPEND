# UML Design

# SmartSpend

### AI Personal Finance Manager

---

# 1. Giới thiệu

## 1.1 Mục đích

Tài liệu này mô tả các sơ đồ UML của hệ thống SmartSpend.

Các sơ đồ giúp trực quan hóa:

- Chức năng hệ thống.
- Luồng xử lý.
- Quan hệ giữa các lớp.
- Tương tác giữa các thành phần.

Tài liệu này được xây dựng dựa trên:

- Business Rules
- Database Design
- Module Design
- API Design

---

# 2. Danh sách sơ đồ

SmartSpend sử dụng các loại UML sau:

| STT | Sơ đồ | Mục đích |
|---:|---|---|
| 1 | Use Case Diagram | Chức năng của người dùng |
| 2 | Class Diagram | Quan hệ giữa các Entity |
| 3 | Sequence Diagram | Luồng xử lý nghiệp vụ |
| 4 | Activity Diagram | Quy trình xử lý |
| 5 | Package Diagram | Quan hệ giữa các module |

---

# 3. Use Case Diagram

## 3.1 Actor

Hệ thống hiện có một Actor chính.

```text
User
```

Trong tương lai có thể bổ sung:

- Administrator
- AI Provider (External System)

---

## 3.2 Danh sách Use Case

| Use Case | Mô tả |
|---|---|
| Register | Đăng ký |
| Login | Đăng nhập |
| Logout | Đăng xuất |
| Manage Categories | Quản lý danh mục |
| Manage Transactions | Quản lý giao dịch |
| Manage Budgets | Quản lý ngân sách |
| View Dashboard | Xem Dashboard |
| View Notifications | Xem thông báo |
| Chat With AI | Trò chuyện với AI |
| Export Report | Xuất báo cáo |

---

## 3.3 Use Case Diagram

```text
                    +----------------------+
                    |      SmartSpend      |
                    +----------------------+

      +---------------------------------------------+
      |                                             |
      |   (Register)                                |
      |   (Login)                                   |
      |   (Manage Categories)                       |
      |   (Manage Transactions)                     |
      |   (Manage Budgets)                          |
      |   (View Dashboard)                          |
      |   (View Notifications)                      |
      |   (Chat With AI)                            |
      |   (Export Report)                           |
      |   (Logout)                                  |
      |                                             |
      +---------------------------------------------+

                    ^
                    |
                  User
```

---

# 4. Class Diagram

## 4.1 Danh sách Entity

```text
User

Category

Transaction

Budget

Notification

ChatSession

ChatMessage

RefreshToken

UserSession
```

---

## 4.2 Class Diagram

```text
+----------------+
| User           |
+----------------+
| id             |
| email          |
| password       |
| fullName       |
+----------------+
        |
        | 1
        |
        |---------------------------+
        |                           |
        |                           |
       *                           *
+---------------+           +----------------+
| Category      |           | Transaction    |
+---------------+           +----------------+
| id            |           | id             |
| name          |           | amount         |
| type          |           | type           |
+---------------+           | date           |
                            +----------------+
                                   |
                                   |
                                   | *
                                   |
                                   | 1
                             +-------------+
                             | Category    |
                             +-------------+

User
 |
 |1
 |
 |*
Budget

User
 |
 |1
 |
 |*
Notification

User
 |
 |1
 |
 |*
ChatSession
 |
 |1
 |
 |*
ChatMessage

User
 |
 |1
 |
 |*
RefreshToken

User
 |
 |1
 |
 |*
UserSession
```

---

# 5. Package Diagram

Hệ thống được tổ chức theo Feature Package Structure.

```text
com.smartspend
│
├── auth
├── category
├── transaction
├── budget
├── dashboard
├── notification
├── ai
├── common
├── config
└── security
```

Quan hệ:

```text
Authentication
      │
      ▼
Category

Authentication
      │
      ▼
Transaction

Transaction
      │
      ▼
Budget

Transaction
      │
      ▼
Dashboard

Budget
      │
      ▼
Notification

Transaction
      │
      ▼
AI
```

---

# 6. Sequence Diagram

## 6.1 Đăng nhập

```text
User
 │
 │ Login
 ▼
AuthController
 │
 ▼
AuthService
 │
 ├── UserRepository
 │
 ▼
Database
 │
 ▲
 │
Generate JWT
 │
 ▼
Return Token
```

---

## 6.2 Tạo giao dịch

```text
User
 │
 ▼
TransactionController
 │
 ▼
TransactionService
 │
 ├── Validate
 │
 ├── CategoryRepository
 │
 ├── TransactionRepository
 │
 └── BudgetService
          │
          ▼
 NotificationService
```

---

## 6.3 Chat với AI

```text
User
 │
 ▼
ChatController
 │
 ▼
ChatService
 │
 ▼
FinancialContextBuilder
 │
 ▼
PromptBuilder
 │
 ▼
OpenAI
 │
 ▼
Parser
 │
 ▼
Save Message
 │
 ▼
Response
```

---

# 7. Activity Diagram

## 7.1 Tạo giao dịch

```text
Start

↓

Nhập thông tin

↓

Validate

↓

Category hợp lệ?

├── Không

│

▼

Thông báo lỗi

│

└──────────────

↓

Có

↓

Lưu giao dịch

↓

Kiểm tra Budget

↓

Tạo Notification (nếu cần)

↓

Hoàn thành
```

---

## 7.2 AI Chat

```text
Start

↓

Nhập câu hỏi

↓

Lưu Message

↓

Lấy dữ liệu tài chính

↓

Sinh Prompt

↓

Gửi AI

↓

Nhận phản hồi

↓

Lưu Response

↓

Trả kết quả

↓

End
```

---

# 8. Component Diagram

```text
Frontend
      │
      ▼
REST API
      │
      ▼
Spring Boot
      │
 ┌────┴────┐
 │         │
 ▼         ▼
MySQL    Redis
      │
      ▼
 OpenAI API
```

---

# 9. Deployment Diagram

```text
+----------------------+

Client Browser

+----------+-----------+
           |
           |
           ▼

+----------------------+

Spring Boot

+----------+-----------+
           |
      +----+----+
      |         |
      ▼         ▼

MySQL      Redis

           |
           ▼

      OpenAI API
```

---

# 10. Tổng kết

Tài liệu UML giúp trực quan hóa toàn bộ kiến trúc của SmartSpend.

Các sơ đồ được xây dựng dựa trên Business Rules và Module Design, đảm bảo thống nhất với kiến trúc và cơ sở dữ liệu của hệ thống.

Khi dự án phát triển thêm chức năng hoặc thay đổi kiến trúc, các sơ đồ UML cần được cập nhật để phản ánh đúng trạng thái của hệ thống.