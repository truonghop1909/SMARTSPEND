# Kiến trúc hệ thống

# SmartSpend

### AI Personal Finance Manager

---

## 1. Giới thiệu

### 1.1 Mục đích

Tài liệu này mô tả kiến trúc tổng thể của hệ thống **SmartSpend**.

Đây là tài liệu quan trọng nhất trong giai đoạn thiết kế vì nó định nghĩa cách tổ chức mã nguồn, luồng xử lý dữ liệu và cách các thành phần trong hệ thống tương tác với nhau.

Trong suốt quá trình phát triển, mọi module mới đều phải tuân theo kiến trúc đã được thống nhất trong tài liệu này.

---

### 1.2 Mục tiêu

Kiến trúc của SmartSpend hướng đến các mục tiêu sau:

- Dễ phát triển.
- Dễ bảo trì.
- Dễ mở rộng.
- Dễ kiểm thử.
- Tuân theo các Best Practices của Spring Boot.

Đồng thời, kiến trúc cũng cần đủ đơn giản để phù hợp với một dự án Portfolio nhưng vẫn phản ánh cách tổ chức của các dự án thực tế.

---

## 2. Kiến trúc tổng thể

SmartSpend được xây dựng theo mô hình **Layered Architecture (Kiến trúc phân tầng)**.

Đây là mô hình kiến trúc được sử dụng rộng rãi trong các dự án Spring Boot nhờ tính đơn giản, rõ ràng và khả năng mở rộng tốt.

Hệ thống được chia thành nhiều tầng độc lập, trong đó mỗi tầng chỉ đảm nhiệm một trách nhiệm duy nhất.

Luồng xử lý của một yêu cầu từ người dùng sẽ đi qua các tầng theo thứ tự sau:

```text
Client
   │
   ▼
Spring Security
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
MySQL
```

Ngoài luồng xử lý chính, hệ thống còn có một **AI Layer** riêng để giao tiếp với OpenAI API.

```text
Client
   │
   ▼
Controller
   │
   ▼
AI Service
   │
   ▼
OpenAI API
```

Việc tách AI thành một thành phần riêng giúp mã nguồn dễ mở rộng khi cần thay đổi nhà cung cấp AI trong tương lai.

---

## 3. Tại sao chọn Layered Architecture?

Có nhiều mô hình kiến trúc như:

- Clean Architecture
- Hexagonal Architecture
- Microservices
- Event-Driven Architecture

Tuy nhiên, SmartSpend lựa chọn **Layered Architecture** vì những lý do sau:

### Phù hợp với quy mô dự án

SmartSpend là một ứng dụng quản lý tài chính cá nhân với số lượng module vừa phải.

Việc sử dụng các kiến trúc quá phức tạp sẽ làm tăng độ khó mà không mang lại nhiều giá trị.

---

### Dễ học và dễ bảo trì

Spring Boot được thiết kế rất phù hợp với Layered Architecture.

Hầu hết các dự án Spring Boot đều sử dụng mô hình này.

Điều này giúp:

- Dễ đọc code.
- Dễ onboarding.
- Dễ bảo trì.

---

### Phù hợp với Portfolio

Mục tiêu của dự án là thể hiện khả năng phát triển Backend.

Những kiến thức quan trọng cần thể hiện là:

- RESTful API
- Spring Security
- JPA
- Redis
- Docker
- AI Integration

Thay vì tập trung vào những kiến trúc quá phức tạp.

---

## 4. Kiến trúc phân tầng

SmartSpend được chia thành năm tầng chính.

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
Database
```

Mỗi tầng chỉ giao tiếp với tầng liền kề.

Điều này giúp giảm sự phụ thuộc giữa các thành phần trong hệ thống.

---

### 4.1 Controller Layer

Controller là điểm tiếp nhận tất cả các HTTP Request từ phía Client.

Nhiệm vụ của Controller bao gồm:

- Nhận Request.
- Validate dữ liệu đầu vào.
- Gọi Service.
- Trả Response.

Controller **không xử lý nghiệp vụ**.

Ví dụ:

```
POST /api/transactions

↓

TransactionController

↓

TransactionService
```

Controller tuyệt đối không được:

- Truy cập Database.
- Viết SQL.
- Thực hiện tính toán nghiệp vụ.
- Gọi Repository trực tiếp.

---

### 4.2 Service Layer

Service là trung tâm của toàn bộ hệ thống.

Đây là nơi chứa tất cả Business Logic.

Ví dụ:

- Tạo giao dịch.
- Kiểm tra ngân sách.
- Tính Dashboard.
- Gửi Notification.
- Phân tích dữ liệu AI.

Service có thể gọi:

- Repository
- AI Service
- Notification Service
- Redis

Service không được xử lý HTTP Request.

---

### 4.3 Repository Layer

Repository chịu trách nhiệm làm việc với cơ sở dữ liệu.

Tầng này sử dụng **Spring Data JPA** để thao tác với MySQL.

Repository chỉ thực hiện các chức năng:

- CRUD
- Search
- Filter
- Pagination
- Custom Query

Repository không chứa nghiệp vụ.

Ví dụ:

```
TransactionRepository

↓

save()

findById()

findAll()

findByUserId()
```

---

### 4.4 Database Layer

Đây là nơi lưu trữ toàn bộ dữ liệu của hệ thống.

Các bảng chính bao gồm:

- User
- Category
- Transaction
- Budget
- Notification
- Chat Session
- Chat Message

Database là nguồn dữ liệu duy nhất của hệ thống.

Redis chỉ đóng vai trò Cache.

---

### 4.5 AI Layer

Đây là thành phần tạo nên điểm khác biệt của SmartSpend.

AI Layer chịu trách nhiệm:

- Đọc dữ liệu giao dịch.
- Tạo Prompt.
- Gửi yêu cầu tới OpenAI.
- Phân tích kết quả.
- Trả lời người dùng.

AI không truy cập trực tiếp Database.

Mọi dữ liệu đều phải đi qua Service.

Luồng xử lý:

```text
User Question
      │
      ▼
ChatController
      │
      ▼
AIService
      │
      ▼
TransactionService
      │
      ▼
Repository
      │
      ▼
MySQL

↓

Context Builder

↓

OpenAI API

↓

Response
```

---

## 5. Mối quan hệ giữa các tầng

Để đảm bảo hệ thống dễ bảo trì, SmartSpend áp dụng nguyên tắc phụ thuộc một chiều.

```text
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
Database
```

Điều này có nghĩa:

- Controller chỉ gọi Service.
- Service chỉ gọi Repository hoặc các Service khác khi cần thiết.
- Repository chỉ làm việc với Database.
- Database không phụ thuộc vào bất kỳ tầng nào.

Không được phép:

- Controller gọi Repository.
- Repository gọi Service.
- Entity gọi Repository.
- Entity chứa Business Logic.

Việc tuân thủ nguyên tắc này giúp mã nguồn rõ ràng, dễ kiểm thử và hạn chế sự phụ thuộc lẫn nhau giữa các thành phần.

---

# 6. Cấu trúc dự án

Thay vì tổ chức mã nguồn theo từng tầng (`controller`, `service`, `repository`...), SmartSpend được tổ chức theo **Feature Package Structure**.

Mỗi module sẽ chứa đầy đủ các thành phần liên quan đến chính nó, giúp giảm sự phụ thuộc giữa các module và dễ dàng mở rộng trong tương lai.

```
src/main/java
└── com.smartspend
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

Cách tổ chức này giúp mỗi module trở thành một đơn vị độc lập, dễ bảo trì và dễ phát triển.

---

# 7. Cấu trúc của một Module

Mỗi module đều có cấu trúc giống nhau.

Ví dụ module Transaction.

```
transaction
│
├── controller
├── service
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── mapper
├── specification
└── exception
```

Việc thống nhất cấu trúc giúp lập trình viên dễ dàng tìm kiếm và quản lý mã nguồn.

---

# 8. Vai trò của từng Package

## 8.1 auth

Chịu trách nhiệm xác thực và phân quyền người dùng.

Bao gồm:

- Đăng ký
- Đăng nhập
- Refresh Token
- JWT
- User Profile

Ví dụ:

```
auth

├── controller
├── service
├── repository
├── entity
├── dto
└── mapper
```

---

## 8.2 category

Quản lý danh mục thu và chi.

Ví dụ:

- Ăn uống
- Lương
- Giải trí
- Mua sắm
- Học tập

Chức năng:

- CRUD Category
- Category mặc định
- Category của người dùng

---

## 8.3 transaction

Đây là module quan trọng nhất của hệ thống.

Quản lý:

- Thu nhập
- Chi tiêu

Bao gồm:

- CRUD
- Search
- Filter
- Pagination
- Soft Delete

Đây cũng là nguồn dữ liệu chính để Dashboard và AI phân tích.

---

## 8.4 budget

Quản lý ngân sách.

Ví dụ:

Ngân sách ăn uống

2.000.000 VNĐ

Đã dùng

1.250.000 VNĐ

Còn lại

750.000 VNĐ

Module này chịu trách nhiệm:

- Tạo Budget
- Theo dõi tiến độ
- Kiểm tra vượt ngân sách

---

## 8.5 dashboard

Dashboard chỉ đọc dữ liệu.

Không tạo mới.

Không cập nhật.

Không xóa.

Dashboard tổng hợp dữ liệu từ nhiều module để hiển thị:

- Tổng thu
- Tổng chi
- Chênh lệch
- Chi tiêu theo danh mục
- Báo cáo theo tháng

---

## 8.6 notification

Quản lý thông báo.

Ví dụ:

- Vượt ngân sách
- Báo cáo cuối tháng
- Gợi ý từ AI

Module này không tự sinh dữ liệu.

Nó chỉ nhận dữ liệu từ các Service khác.

---

## 8.7 ai

Đây là module nổi bật nhất của SmartSpend.

Bao gồm:

- Prompt Builder
- Context Builder
- AI Service
- OpenAI Client

Module AI chỉ phân tích dữ liệu.

Không ghi trực tiếp vào Database.

---

## 8.8 common

Chứa các thành phần dùng chung.

Ví dụ:

```
common

├── exception
├── response
├── constant
├── util
└── validation
```

Các module khác đều có thể sử dụng.

---

## 8.9 config

Chứa toàn bộ cấu hình của hệ thống.

Ví dụ:

- Security
- Redis
- Swagger
- OpenAI
- CORS

---

## 8.10 security

Chứa các thành phần liên quan đến Spring Security.

Bao gồm:

- JWT Filter
- JWT Utility
- Authentication Entry Point
- UserDetailsService

---

# 9. Quy tắc phụ thuộc

SmartSpend chỉ cho phép phụ thuộc theo một chiều.

```
Controller

↓

Service

↓

Repository

↓

Database
```

Không được phép:

❌ Controller gọi Repository

❌ Repository gọi Service

❌ Repository gọi AI

❌ Entity gọi Repository

❌ Entity chứa Business Logic

Việc tuân thủ quy tắc này giúp hệ thống:

- Dễ kiểm thử
- Dễ mở rộng
- Hạn chế lỗi
- Giảm sự phụ thuộc giữa các thành phần

---

# 10. Quy tắc đặt tên

Toàn bộ dự án sử dụng tiếng Anh.

## Package

Viết thường.

Ví dụ:

```
transaction

category

notification
```

---

## Class

PascalCase.

Ví dụ:

```
TransactionService

CategoryController

BudgetRepository
```

---

## Method

camelCase.

Ví dụ:

```
createTransaction()

findByUserId()

calculateBudget()
```

---

## Biến

camelCase.

Ví dụ:

```
totalExpense

currentBudget

remainingAmount
```

---

## Hằng số

UPPER_CASE.

Ví dụ:

```
DEFAULT_PAGE_SIZE

JWT_SECRET

ACCESS_TOKEN_EXPIRE
```

---

# 11. Quy tắc phát triển

Để đảm bảo toàn bộ mã nguồn thống nhất, SmartSpend áp dụng các quy tắc sau.

### Controller

Chỉ nhận Request và trả Response.

Không xử lý nghiệp vụ.

---

### Service

Chỉ xử lý Business Logic.

Không làm việc trực tiếp với HTTP.

---

### Repository

Chỉ thao tác với Database.

Không chứa nghiệp vụ.

---

### Entity

Chỉ ánh xạ với Database.

Không chứa xử lý nghiệp vụ.

---

### DTO

Chỉ dùng để trao đổi dữ liệu.

Không ánh xạ trực tiếp với Database.

---

### Mapper

Chỉ chuyển đổi giữa DTO và Entity.

Không chứa Business Logic.

---

# 12. Tổng kết

Kiến trúc của SmartSpend được xây dựng theo hướng đơn giản, rõ ràng và dễ mở rộng.

Việc tổ chức theo **Feature Package Structure** giúp các module độc lập với nhau, giảm sự phụ thuộc và thuận tiện trong quá trình phát triển lâu dài.

Đây là kiến trúc phù hợp với một dự án Spring Boot hiện đại, đồng thời đủ chuyên nghiệp để sử dụng làm Portfolio hoặc đồ án tốt nghiệp.