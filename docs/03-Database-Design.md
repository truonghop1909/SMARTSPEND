# Database Design

## SmartSpend

### AI Personal Finance Manager

---

# 1. Mục đích

Tài liệu này mô tả thiết kế cơ sở dữ liệu của hệ thống SmartSpend.

Đây là tài liệu dùng làm cơ sở để:

- Thiết kế Entity.
- Xây dựng Flyway Migration.
- Phát triển Repository.
- Thiết kế API.
- Kiểm tra tính toàn vẹn dữ liệu.

Mọi thay đổi về cấu trúc cơ sở dữ liệu phải được cập nhật trong tài liệu này trước khi triển khai.

---

# 2. Quy ước Database

## 2.1 Database Engine

Hệ quản trị cơ sở dữ liệu:

```
MySQL 8.x
```

Storage Engine:

```
InnoDB
```

Character Set:

```
utf8mb4
```

Collation:

```
utf8mb4_unicode_ci
```

---

## 2.2 Quy ước đặt tên

### Tên bảng

- Tiếng Anh.
- Chữ thường.
- Dạng số nhiều.

Ví dụ:

```
users
categories
transactions
budgets
notifications
```

---

### Tên cột

Sử dụng:

```
snake_case
```

Ví dụ:

```
created_at

updated_at

transaction_date
```

---

### Primary Key

Tất cả các bảng sử dụng:

```
id BIGINT AUTO_INCREMENT
```

---

### Foreign Key

Tên khóa ngoại theo quy tắc:

```
<tên_bảng>_id
```

Ví dụ:

```
user_id

category_id

session_id
```

---

## 2.3 Kiểu dữ liệu

### ID

```
BIGINT
```

---

### Chuỗi

```
VARCHAR
```

---

### Văn bản dài

```
TEXT
```

---

### Tiền tệ

```
DECIMAL(15,2)
```

Không sử dụng:

- FLOAT
- DOUBLE

để tránh sai số.

---

### Thời gian

Toàn bộ thời gian sử dụng:

```
DATETIME(6)
```

Các trường chuẩn:

```
created_at

updated_at

deleted_at
```

---

### Boolean

```
BOOLEAN
```

---

### JSON

Chỉ sử dụng khi thật sự cần.

Hiện tại:

```
chat_messages.raw_ai_response
```

---

## 2.4 Soft Delete

Chỉ áp dụng cho:

```
transactions
```

Bảng sử dụng trường:

```
deleted_at
```

Các bảng còn lại sử dụng xóa vật lý.

---

# 3. Danh sách bảng

Phiên bản đầu của SmartSpend gồm 9 bảng.

| STT | Bảng | Vai trò |
|----:|------|----------|
| 1 | users | Quản lý tài khoản |
| 2 | categories | Danh mục thu và chi |
| 3 | transactions | Giao dịch tài chính |
| 4 | budgets | Ngân sách |
| 5 | notifications | Thông báo |
| 6 | chat_sessions | Phiên hội thoại AI |
| 7 | chat_messages | Nội dung hội thoại |
| 8 | refresh_tokens | Refresh Token |
| 9 | user_sessions | Phiên đăng nhập |

---

# 4. Quan hệ tổng thể

```
users
│
├── categories
│      │
│      ├── transactions
│      │
│      └── budgets
│
├── transactions
│
├── budgets
│
├── notifications
│
├── chat_sessions
│      │
│      └── chat_messages
│
├── refresh_tokens
│
└── user_sessions
```

---

# 5. Quy tắc thiết kế

## User

Một User có thể có nhiều:

- Category
- Transaction
- Budget
- Notification
- Chat Session
- Refresh Token
- User Session

---

## Category

Một Category:

- Thuộc một User hoặc là Category mặc định của hệ thống.
- Có nhiều Transaction.
- Có nhiều Budget theo từng tháng.

---

## Transaction

Một Transaction:

- Thuộc một User.
- Thuộc một Category.

Đây là bảng trung tâm của hệ thống.

---

## Budget

Một Budget:

- Thuộc một User.
- Thuộc một Category.
- Chỉ áp dụng cho một tháng.

---

## Notification

Một Notification:

- Thuộc một User.

---

## Chat Session

Một Chat Session:

- Thuộc một User.
- Có nhiều Chat Message.

---

## Chat Message

Một Chat Message:

- Thuộc một Chat Session.

---

## Refresh Token

Một Refresh Token:

- Thuộc một User.

---

## User Session

Một User Session:

- Thuộc một User.

---

# 6. Nguyên tắc Migration

Flyway được sử dụng để quản lý toàn bộ thay đổi Database.

Quy tắc đặt tên:

```
V1__create_initial_schema.sql

V2__insert_default_categories.sql

V3__...

V4__...
```

Không sửa Migration đã chạy trên môi trường Production.

Mọi thay đổi cấu trúc phải tạo Migration mới.

---

# 7. Thiết kế chi tiết các bảng

## 7.1 Bảng `users`

### Mục đích

Lưu thông tin tài khoản người dùng.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| email | VARCHAR(255) | ❌ | Email đăng nhập |
| password_hash | VARCHAR(255) | ❌ | Mật khẩu đã mã hóa |
| full_name | VARCHAR(100) | ❌ | Họ và tên |
| avatar_url | VARCHAR(500) | ✅ | Ảnh đại diện |
| role | VARCHAR(20) | ❌ | USER, ADMIN |
| status | VARCHAR(20) | ❌ | ACTIVE, INACTIVE, LOCKED |
| auth_provider | VARCHAR(20) | ❌ | LOCAL, GOOGLE |
| created_at | DATETIME(6) | ❌ | Ngày tạo |
| updated_at | DATETIME(6) | ❌ | Ngày cập nhật |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Unique

```sql
UNIQUE KEY uk_users_email (email)
```

---

### Index

```sql
UNIQUE (email)
```

---

### Quan hệ

```text
users (1)

├── categories (N)

├── transactions (N)

├── budgets (N)

├── notifications (N)

├── chat_sessions (N)

├── refresh_tokens (N)

└── user_sessions (N)
```

---

## 7.2 Bảng `categories`

### Mục đích

Lưu danh mục thu nhập và chi tiêu.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ✅ | Chủ sở hữu |
| name | VARCHAR(100) | ❌ | Tên danh mục |
| type | VARCHAR(20) | ❌ | INCOME, EXPENSE |
| icon | VARCHAR(100) | ✅ | Icon |
| color | VARCHAR(20) | ✅ | Màu |
| is_default | BOOLEAN | ❌ | Danh mục mặc định |
| created_at | DATETIME(6) | ❌ | Ngày tạo |
| updated_at | DATETIME(6) | ❌ | Ngày cập nhật |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)
```

---

### Unique

```sql
UNIQUE KEY uk_categories_user_name_type
(
    user_id,
    name,
    type
)
```

---

### Index

```sql
INDEX idx_categories_user_id
(user_id)

INDEX idx_categories_type
(type)

INDEX idx_categories_default
(is_default)
```

---

### Quan hệ

```text
users (1)

↓

categories (N)

↓

transactions (N)

↓

budgets (N)
```

---

## 7.3 Bảng `transactions`

### Mục đích

Lưu toàn bộ giao dịch thu và chi.

Đây là bảng trung tâm của hệ thống.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| category_id | BIGINT | ❌ | Danh mục |
| type | VARCHAR(20) | ❌ | INCOME, EXPENSE |
| amount | DECIMAL(15,2) | ❌ | Số tiền |
| merchant | VARCHAR(255) | ✅ | Nơi giao dịch |
| payment_method | VARCHAR(20) | ✅ | CASH, BANK, EWALLET |
| note | VARCHAR(500) | ✅ | Ghi chú |
| transaction_date | DATE | ❌ | Ngày giao dịch |
| created_at | DATETIME(6) | ❌ | Ngày tạo |
| updated_at | DATETIME(6) | ❌ | Ngày cập nhật |
| deleted_at | DATETIME(6) | ✅ | Soft Delete |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)

FOREIGN KEY (category_id)
REFERENCES categories(id)
```

---

### Index

```sql
INDEX idx_transactions_user_date
(
    user_id,
    transaction_date
)

INDEX idx_transactions_user_category
(
    user_id,
    category_id
)

INDEX idx_transactions_deleted
(
    deleted_at
)

INDEX idx_transactions_type
(
    type
)
```

---

### Quan hệ

```text
users (1)

↓

transactions (N)

categories (1)

↓

transactions (N)
```

---

### Ghi chú

- Chỉ áp dụng Soft Delete cho bảng này.
- Dashboard, Budget và AI đều đọc dữ liệu từ bảng `transactions`.
- Không lưu số dư tài khoản trong Database.

## 7.4 Bảng `budgets`

### Mục đích

Lưu ngân sách của người dùng theo từng danh mục và từng tháng.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| category_id | BIGINT | ❌ | Danh mục |
| budget_year | INT | ❌ | Năm áp dụng |
| budget_month | TINYINT | ❌ | Tháng áp dụng |
| amount | DECIMAL(15,2) | ❌ | Ngân sách |
| created_at | DATETIME(6) | ❌ | Ngày tạo |
| updated_at | DATETIME(6) | ❌ | Ngày cập nhật |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)

FOREIGN KEY (category_id)
REFERENCES categories(id)
```

---

### Unique

```sql
UNIQUE KEY uk_budgets_user_category_month
(
    user_id,
    category_id,
    budget_year,
    budget_month
)
```

---

### Index

```sql
INDEX idx_budgets_user_month
(
    user_id,
    budget_year,
    budget_month
)

INDEX idx_budgets_category
(
    category_id
)
```

---

### Quan hệ

```text
users (1)

↓

budgets (N)

categories (1)

↓

budgets (N)
```

---

## 7.5 Bảng `notifications`

### Mục đích

Lưu các thông báo của hệ thống.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| title | VARCHAR(200) | ❌ | Tiêu đề |
| content | TEXT | ❌ | Nội dung |
| type | VARCHAR(20) | ❌ | BUDGET, AI, SYSTEM |
| action_url | VARCHAR(255) | ✅ | Liên kết điều hướng |
| is_read | BOOLEAN | ❌ | Đã đọc |
| created_at | DATETIME(6) | ❌ | Ngày tạo |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)
```

---

### Index

```sql
INDEX idx_notifications_user_read
(
    user_id,
    is_read
)

INDEX idx_notifications_created
(
    created_at
)
```

---

### Quan hệ

```text
users (1)

↓

notifications (N)
```

---

## 7.6 Bảng `chat_sessions`

### Mục đích

Lưu các phiên hội thoại giữa người dùng và AI.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| title | VARCHAR(255) | ✅ | Tiêu đề |
| last_message_at | DATETIME(6) | ✅ | Tin nhắn cuối |
| created_at | DATETIME(6) | ❌ | Ngày tạo |
| updated_at | DATETIME(6) | ❌ | Ngày cập nhật |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)
```

---

### Index

```sql
INDEX idx_chat_sessions_user
(
    user_id
)

INDEX idx_chat_sessions_last_message
(
    user_id,
    last_message_at
)
```

---

### Quan hệ

```text
users (1)

↓

chat_sessions (N)

↓

chat_messages (N)
```

---

## 7.7 Bảng `chat_messages`

### Mục đích

Lưu từng tin nhắn trong phiên hội thoại AI.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| session_id | BIGINT | ❌ | Phiên hội thoại |
| role | VARCHAR(20) | ❌ | USER, ASSISTANT |
| content | TEXT | ❌ | Nội dung |
| raw_ai_response | JSON | ✅ | Dữ liệu AI trả về |
| created_at | DATETIME(6) | ❌ | Ngày tạo |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (session_id)
REFERENCES chat_sessions(id)
ON DELETE CASCADE
```

---

### Index

```sql
INDEX idx_chat_messages_session
(
    session_id,
    created_at
)
```

---

### Quan hệ

```text
chat_sessions (1)

↓

chat_messages (N)
```

---

## 7.8 Bảng `refresh_tokens`

### Mục đích

Lưu Refresh Token phục vụ xác thực JWT.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| token_hash | VARCHAR(255) | ❌ | Token đã băm |
| device_name | VARCHAR(255) | ✅ | Thiết bị |
| ip_address | VARCHAR(45) | ✅ | Địa chỉ IP |
| expires_at | DATETIME(6) | ❌ | Hết hạn |
| revoked_at | DATETIME(6) | ✅ | Thu hồi |
| created_at | DATETIME(6) | ❌ | Ngày tạo |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)
ON DELETE CASCADE
```

---

### Unique

```sql
UNIQUE KEY uk_refresh_tokens_hash
(
    token_hash
)
```

---

### Index

```sql
INDEX idx_refresh_tokens_user
(
    user_id
)

INDEX idx_refresh_tokens_expired
(
    expires_at
)
```

---

### Quan hệ

```text
users (1)

↓

refresh_tokens (N)
```

---

## 7.9 Bảng `user_sessions`

### Mục đích

Lưu thông tin các phiên đăng nhập của người dùng.

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|:---:|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| device_name | VARCHAR(255) | ✅ | Thiết bị |
| device_type | VARCHAR(50) | ✅ | MOBILE, TABLET, DESKTOP |
| os | VARCHAR(100) | ✅ | Hệ điều hành |
| browser | VARCHAR(100) | ✅ | Trình duyệt |
| ip_address | VARCHAR(45) | ✅ | Địa chỉ IP |
| last_active_at | DATETIME(6) | ❌ | Hoạt động cuối |
| created_at | DATETIME(6) | ❌ | Ngày đăng nhập |

---

### Primary Key

```sql
PRIMARY KEY (id)
```

---

### Foreign Key

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)
ON DELETE CASCADE
```

---

### Index

```sql
INDEX idx_user_sessions_user
(
    user_id
)

INDEX idx_user_sessions_last_active
(
    user_id,
    last_active_at
)
```

---

### Quan hệ

```text
users (1)

↓

user_sessions (N)
```

# 8. Quan hệ giữa các bảng

## 8.1 Quan hệ tổng thể

| Bảng cha | Quan hệ | Bảng con | Mô tả |
|-----------|----------|-----------|------|
| users | 1 - N | categories | Một User có nhiều Category |
| users | 1 - N | transactions | Một User có nhiều Transaction |
| users | 1 - N | budgets | Một User có nhiều Budget |
| users | 1 - N | notifications | Một User có nhiều Notification |
| users | 1 - N | chat_sessions | Một User có nhiều Chat Session |
| users | 1 - N | refresh_tokens | Một User có nhiều Refresh Token |
| users | 1 - N | user_sessions | Một User có nhiều User Session |
| categories | 1 - N | transactions | Một Category có nhiều Transaction |
| categories | 1 - N | budgets | Một Category có nhiều Budget |
| chat_sessions | 1 - N | chat_messages | Một Chat Session có nhiều Chat Message |

---

## 8.2 ERD

```text
users
│
├── categories
│   ├── transactions
│   └── budgets
│
├── transactions
├── budgets
├── notifications
│
├── chat_sessions
│   └── chat_messages
│
├── refresh_tokens
└── user_sessions
```

---

# 9. Index

## users

```sql
UNIQUE KEY uk_users_email (email)
```

---

## categories

```sql
INDEX idx_categories_user_id (user_id)

INDEX idx_categories_type (type)

INDEX idx_categories_default (is_default)

UNIQUE KEY uk_categories_user_name_type
(
    user_id,
    name,
    type
)
```

---

## transactions

```sql
INDEX idx_transactions_user_date
(
    user_id,
    transaction_date
)

INDEX idx_transactions_user_category
(
    user_id,
    category_id
)

INDEX idx_transactions_deleted
(
    deleted_at
)

INDEX idx_transactions_type
(
    type
)
```

---

## budgets

```sql
UNIQUE KEY uk_budgets_user_category_month
(
    user_id,
    category_id,
    budget_year,
    budget_month
)

INDEX idx_budgets_user_month
(
    user_id,
    budget_year,
    budget_month
)
```

---

## notifications

```sql
INDEX idx_notifications_user_read
(
    user_id,
    is_read
)
```

---

## chat_sessions

```sql
INDEX idx_chat_sessions_user
(
    user_id
)

INDEX idx_chat_sessions_last_message
(
    user_id,
    last_message_at
)
```

---

## chat_messages

```sql
INDEX idx_chat_messages_session
(
    session_id,
    created_at
)
```

---

## refresh_tokens

```sql
UNIQUE KEY uk_refresh_tokens_hash
(
    token_hash
)

INDEX idx_refresh_tokens_user
(
    user_id
)

INDEX idx_refresh_tokens_expires
(
    expires_at
)
```

---

## user_sessions

```sql
INDEX idx_user_sessions_user
(
    user_id
)

INDEX idx_user_sessions_last_active
(
    user_id,
    last_active_at
)
```

---

# 10. Soft Delete

Chỉ bảng `transactions` sử dụng Soft Delete.

```sql
UPDATE transactions
SET deleted_at = CURRENT_TIMESTAMP(6)
WHERE id = ?;
```

Mọi truy vấn nghiệp vụ phải bổ sung điều kiện:

```sql
deleted_at IS NULL
```

---

# 11. Flyway Migration

## V1

```text
V1__create_initial_schema.sql
```

Tạo toàn bộ schema:

- users
- categories
- transactions
- budgets
- notifications
- chat_sessions
- chat_messages
- refresh_tokens
- user_sessions

---

## V2

```text
V2__insert_default_categories.sql
```

Seed dữ liệu danh mục mặc định.

---

# 12. Tổng kết

Database SmartSpend sử dụng:

- MySQL 8
- InnoDB
- utf8mb4
- DATETIME(6)
- DECIMAL(15,2)
- BIGINT AUTO_INCREMENT

Toàn bộ thay đổi schema được quản lý bằng Flyway Migration.