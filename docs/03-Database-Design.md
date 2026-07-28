# Thiết kế cơ sở dữ liệu

# SmartSpend

### AI Personal Finance Manager

---

# 1. Giới thiệu

## 1.1 Mục đích

Tài liệu này mô tả thiết kế cơ sở dữ liệu của hệ thống **SmartSpend**.

Đây là tài liệu dùng làm cơ sở để:

- Thiết kế Entity.
- Xây dựng Migration bằng Flyway.
- Phát triển Repository.
- Thiết kế REST API.
- Đảm bảo dữ liệu luôn nhất quán.

Mọi thay đổi về cấu trúc dữ liệu đều phải được cập nhật trong tài liệu này trước khi triển khai vào mã nguồn.

---

## 1.2 Phạm vi

Phiên bản hiện tại của SmartSpend chỉ tập trung vào việc quản lý tài chính cá nhân.

Hệ thống **không** hỗ trợ:

- Ví điện tử
- Chuyển tiền
- Thanh toán trực tuyến
- Ngân hàng số
- Đầu tư tài chính

Do đó cơ sở dữ liệu được thiết kế đơn giản, dễ mở rộng và phù hợp với phạm vi của dự án.

---

# 2. Mục tiêu thiết kế

Cơ sở dữ liệu được thiết kế theo các mục tiêu sau:

- Chuẩn hóa dữ liệu.
- Giảm trùng lặp.
- Dễ mở rộng.
- Dễ bảo trì.
- Đảm bảo tính toàn vẹn dữ liệu.
- Phù hợp với Spring Data JPA.

---

# 3. Nguyên tắc thiết kế

## 3.1 Chuẩn hóa dữ liệu

Các bảng được thiết kế theo hướng chuẩn hóa để hạn chế dữ liệu trùng lặp.

Ví dụ:

- Danh mục được lưu riêng.
- Người dùng được lưu riêng.
- Giao dịch chỉ tham chiếu đến các bảng liên quan.

---

## 3.2 Mỗi bảng chỉ có một trách nhiệm

Mỗi bảng chỉ đại diện cho một thực thể trong hệ thống.

Ví dụ:

| Bảng | Vai trò |
|------|----------|
| users | Thông tin người dùng |
| categories | Danh mục |
| transactions | Giao dịch |
| budgets | Ngân sách |

Không tạo một bảng chứa nhiều loại dữ liệu khác nhau.

---

## 3.3 Sử dụng khóa chính

Mỗi bảng đều sử dụng:

```
BIGINT AUTO_INCREMENT
```

làm khóa chính.

Ví dụ

```
id
```

---

## 3.4 Sử dụng khóa ngoại

Quan hệ giữa các bảng được quản lý bằng Foreign Key.

Ví dụ

```
transaction

↓

user

↓

category
```

Điều này giúp đảm bảo dữ liệu luôn hợp lệ.

---

## 3.5 Soft Delete

Đối với dữ liệu nghiệp vụ quan trọng, hệ thống không xóa vật lý.

Thay vào đó sử dụng:

```
deleted_at
```

để đánh dấu dữ liệu đã bị xóa.

Các truy vấn mặc định sẽ bỏ qua dữ liệu này.

---

## 3.6 Audit Fields

Các bảng nghiệp vụ đều lưu thông tin:

- created_at
- updated_at

Một số bảng sẽ có thêm

- deleted_at

để phục vụ Soft Delete.

---

# 4. Quy ước đặt tên

## 4.1 Tên bảng

Tên bảng sử dụng:

- tiếng Anh
- chữ thường
- số nhiều

Ví dụ

```
users

transactions

categories

budgets
```

---

## 4.2 Tên cột

Tên cột sử dụng:

snake_case

Ví dụ

```
first_name

created_at

transaction_date
```

---

## 4.3 Khóa chính

Tất cả các bảng đều sử dụng

```
id
```

làm Primary Key.

---

## 4.4 Khóa ngoại

Tên khóa ngoại theo quy tắc:

```
<tên_bảng>_id
```

Ví dụ

```
user_id

category_id

budget_id
```

---

## 4.5 Timestamp

Toàn bộ Timestamp sử dụng

```
TIMESTAMP
```

Ví dụ

```
created_at

updated_at

deleted_at
```

---

# 5. Danh sách Entity

Phiên bản hiện tại của SmartSpend bao gồm 10 thực thể chính.

| STT | Entity | Vai trò |
|-----|---------|----------|
| 1 | User | Quản lý tài khoản người dùng |
| 2 | Category | Danh mục thu và chi |
| 3 | Transaction | Giao dịch tài chính |
| 4 | Budget | Ngân sách theo danh mục |
| 5 | Notification | Thông báo hệ thống |
| 6 | Chat Session | Phiên hội thoại AI |
| 7 | Chat Message | Nội dung hội thoại |
| 8 | Refresh Token | Làm mới JWT |
| 9 | User Session | Quản lý phiên đăng nhập |
---

# 6. Quan hệ giữa các Entity

## User

Một người dùng có thể:

- Có nhiều Category.
- Có nhiều Transaction.
- Có nhiều Budget.
- Có nhiều Notification.
- Có nhiều Chat Session.
- Có nhiều User Session.
- Có nhiều Refresh Token.

Quan hệ:

```
User

├── Category

├── Transaction

├── Budget

├── Notification

├── Chat Session

├── Refresh Token

└── User Session
```

---

## Category

Một Category

- Thuộc một User.
- Có nhiều Transaction.
- Có thể có nhiều Budget theo từng tháng.

---

## Transaction

Một Transaction

- Thuộc một User.
- Thuộc một Category.

Đây là bảng trung tâm của toàn bộ hệ thống.

Dashboard, Budget và AI đều đọc dữ liệu từ bảng này.

---

## Budget

Một Budget

- Thuộc một User.
- Thuộc một Category.
- Chỉ áp dụng cho một tháng.

---

## Notification

Một Notification

- Thuộc một User.

Được tạo từ:

- Budget
- AI
- Hệ thống

---

## Chat Session

Một Chat Session

- Thuộc một User.
- Có nhiều Chat Message.

---

## Chat Message

Một Chat Message

- Thuộc một Chat Session.

Bao gồm:

- Câu hỏi.
- Trả lời.

---

## Refresh Token

Một Refresh Token

- Thuộc một User.

Được sử dụng để cấp Access Token mới.

---

## User Session

Một User Session

- Thuộc một User.

Lưu thông tin:

- Thiết bị
- Địa chỉ IP
- Thời điểm đăng nhập

---

## Password Reset Token

Một Password Reset Token

- Thuộc một User.

Được sử dụng khi người dùng quên mật khẩu.

---

# 7. Luồng dữ liệu

Transaction là trung tâm của hệ thống.

Mọi chức năng phân tích đều được xây dựng từ dữ liệu giao dịch.

```
Transaction
      │
      ├────────► Dashboard
      │
      ├────────► Budget
      │
      ├────────► Notification
      │
      └────────► AI Assistant
```

Điều này giúp tránh việc lưu trữ dữ liệu trùng lặp.

Dashboard, Budget và AI chỉ đọc dữ liệu từ Transaction thay vì tạo thêm các bảng thống kê riêng.

---

# 8. Tổng quan mô hình dữ liệu

```text
User
 │
 ├────────────── Category
 │                  │
 │                  │
 │                  ▼
 │             Transaction
 │                  │
 │                  ├────────► Dashboard
 │                  ├────────► Budget
 │                  ├────────► Notification
 │                  └────────► AI
 │
 ├────────────── Budget
 │
 ├────────────── Notification
 │
 ├────────────── Chat Session
 │                  │
 │                  ▼
 │             Chat Message
 │
 ├────────────── Refresh Token
 │
 ├────────────── User Session
 │
 └────────────── Password Reset Token
```

Đây là mô hình dữ liệu tổng thể của SmartSpend.

Các phần tiếp theo sẽ mô tả chi tiết cấu trúc của từng bảng, bao gồm:

- Mục đích
- Thuộc tính
- Quan hệ
- Ràng buộc
- Chỉ mục
- Business Rule liên quan

---

# 9. Thiết kế chi tiết các bảng

## 9.1 Bảng Users

### Mục đích

Lưu trữ thông tin tài khoản của người dùng.

Đây là bảng trung tâm của toàn bộ hệ thống.

---

### Business Rules liên quan

- AUTH-001
- AUTH-002
- AUTH-003
- AUTH-004
- AUTH-006

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|------|-------|
| id | BIGINT | ❌ | Khóa chính |
| email | VARCHAR(255) | ❌ | Email đăng nhập |
| password | VARCHAR(255) | ❌ | Mật khẩu đã mã hóa |
| full_name | VARCHAR(100) | ❌ | Họ và tên |
| avatar_url | VARCHAR(500) | ✅ | Ảnh đại diện |
| created_at | TIMESTAMP | ❌ | Ngày tạo |
| updated_at | TIMESTAMP | ❌ | Ngày cập nhật |

---

### Quan hệ

```
User

├── Category

├── Transaction

├── Budget

├── Notification

├── Chat Session

├── Refresh Token

└── User Session
```

---

### Ràng buộc

- Email phải duy nhất.
- Password phải được mã hóa bằng BCrypt.
- Không lưu mật khẩu dạng văn bản thuần.

---

### Chỉ mục

```
PRIMARY KEY (id)

UNIQUE (email)
```

---

## 9.2 Bảng Categories

### Mục đích

Quản lý các danh mục thu nhập và chi tiêu.

Danh mục giúp phân loại giao dịch để phục vụ Dashboard, Budget và AI.

---

### Business Rules liên quan

- CAT-001
- CAT-002
- CAT-003
- CAT-004
- CAT-005
- CAT-006

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|------|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| name | VARCHAR(100) | ❌ | Tên danh mục |
| type | ENUM | ❌ | INCOME / EXPENSE |
| icon | VARCHAR(100) | ✅ | Biểu tượng |
| color | VARCHAR(20) | ✅ | Màu hiển thị |
| is_default | BOOLEAN | ❌ | Danh mục mặc định |
| created_at | TIMESTAMP | ❌ | Ngày tạo |
| updated_at | TIMESTAMP | ❌ | Ngày cập nhật |

---

### Quan hệ

```
User (1)

↓

Category (N)

↓

Transaction (N)

↓

Budget (N)
```

---

### Ràng buộc

- Chỉ có hai loại:

```
INCOME

EXPENSE
```

- Không được trùng tên trong cùng một loại.
- Không được xóa Category đang được Transaction sử dụng.

---

### Chỉ mục

```
INDEX(user_id)

INDEX(type)

UNIQUE(user_id, name, type)
```

---

## 9.3 Bảng Transactions

### Mục đích

Lưu toàn bộ các khoản thu và chi của người dùng.

Đây là bảng quan trọng nhất của hệ thống.

Dashboard, Budget và AI đều sử dụng dữ liệu từ bảng này.

---

### Business Rules liên quan

- TRAN-001 → TRAN-010

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|------|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| category_id | BIGINT | ❌ | Danh mục |
| type | ENUM | ❌ | INCOME / EXPENSE |
| amount | DECIMAL(15,2) | ❌ | Số tiền |
| note | TEXT | ✅ | Ghi chú |
| transaction_date | DATE | ❌ | Ngày phát sinh |
| created_at | TIMESTAMP | ❌ | Ngày tạo |
| updated_at | TIMESTAMP | ❌ | Ngày cập nhật |
| deleted_at | TIMESTAMP | ✅ | Soft Delete |

---

### Quan hệ

```
User (1)

↓

Transaction (N)

Category (1)

↓

Transaction (N)
```

---

### Ràng buộc

- amount > 0
- Không tạo Transaction trong tương lai.
- Category phải cùng loại với Transaction.
- Không xóa vật lý.

---

### Chỉ mục

```
INDEX(user_id)

INDEX(category_id)

INDEX(transaction_date)

INDEX(type)
```

---

## 9.4 Bảng Budgets

### Mục đích

Quản lý ngân sách theo từng danh mục trong từng tháng.

---

### Business Rules liên quan

- BUD-001 → BUD-007

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|------|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| category_id | BIGINT | ❌ | Danh mục |
| budget_month | DATE | ❌ | Tháng áp dụng |
| amount | DECIMAL(15,2) | ❌ | Ngân sách |
| created_at | TIMESTAMP | ❌ | Ngày tạo |
| updated_at | TIMESTAMP | ❌ | Ngày cập nhật |

---

### Quan hệ

```
User

↓

Budget

Category

↓

Budget
```

---

### Ràng buộc

- Chỉ áp dụng cho Category loại Expense.
- Một Category chỉ có một Budget trong cùng một tháng.
- amount > 0

---

### Chỉ mục

```
INDEX(user_id)

INDEX(category_id)

UNIQUE(category_id, budget_month)
```

---

## 9.5 Bảng Notifications

### Mục đích

Lưu các thông báo của hệ thống.

Ví dụ

- Vượt ngân sách
- Thông báo AI
- Báo cáo cuối tháng

---

### Business Rules liên quan

- NOTI-001 → NOTI-004

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|------|--------------|------|-------|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu |
| title | VARCHAR(200) | ❌ | Tiêu đề |
| content | TEXT | ❌ | Nội dung |
| type | ENUM | ❌ | BUDGET / AI / SYSTEM |
| is_read | BOOLEAN | ❌ | Đã đọc |
| created_at | TIMESTAMP | ❌ | Ngày tạo |

---

### Quan hệ

```
User

↓

Notification
```

---

### Chỉ mục

```
INDEX(user_id)

INDEX(is_read)

INDEX(type)
```

---

## 10. Kết luận phần 2

Năm bảng trên tạo thành phần lõi của SmartSpend.

Trong đó:

- **Users** quản lý tài khoản.
- **Categories** phân loại giao dịch.
- **Transactions** lưu dữ liệu tài chính.
- **Budgets** theo dõi ngân sách.
- **Notifications** gửi cảnh báo và thông báo.

Các bảng còn lại (Chat Session, Chat Message, Refresh Token và User Session) sẽ được thiết kế ở phần tiếp theo.

---

# 11. Thiết kế các bảng hỗ trợ

## 11.1 Bảng `chat_sessions`

### Mục đích

Lưu các phiên hội thoại giữa người dùng và AI Financial Assistant.

Mỗi phiên hội thoại đại diện cho một chủ đề trao đổi riêng, giúp người dùng dễ dàng xem lại lịch sử phân tích tài chính.

---

### Business Rules liên quan

- AI-001
- AI-002
- AI-003
- AI-005

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|---|---|---:|---|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Người sở hữu phiên hội thoại |
| title | VARCHAR(255) | ✅ | Tiêu đề phiên hội thoại |
| created_at | DATETIME(6) | ❌ | Thời điểm tạo |
| updated_at | DATETIME(6) | ❌ | Thời điểm cập nhật gần nhất |

---

### Quan hệ

```text
User (1)
   │
   └── Chat Session (N)

Chat Session (1)
   │
   └── Chat Message (N)
```

---

### Ràng buộc

- Mỗi phiên hội thoại phải thuộc về một người dùng.
- Người dùng chỉ được xem và thao tác với phiên hội thoại của chính mình.
- Tiêu đề có thể được tạo tự động từ tin nhắn đầu tiên.
- Không lưu dữ liệu tài chính nhạy cảm trực tiếp trong tiêu đề.

---

### Chỉ mục

```sql
PRIMARY KEY (id)

INDEX idx_chat_sessions_user_id (user_id)

INDEX idx_chat_sessions_user_updated (
    user_id,
    updated_at
)
```

Chỉ mục kết hợp `user_id` và `updated_at` hỗ trợ truy vấn danh sách phiên hội thoại gần đây của người dùng.

---

## 11.2 Bảng `chat_messages`

### Mục đích

Lưu từng tin nhắn trong một phiên hội thoại AI.

Tin nhắn có thể được gửi bởi người dùng hoặc được sinh ra bởi AI Assistant.

---

### Business Rules liên quan

- AI-001
- AI-002
- AI-003
- AI-004
- AI-005

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|---|---|---:|---|
| id | BIGINT | ❌ | Khóa chính |
| session_id | BIGINT | ❌ | Phiên hội thoại chứa tin nhắn |
| role | VARCHAR(20) | ❌ | Vai trò: `USER` hoặc `ASSISTANT` |
| content | TEXT | ❌ | Nội dung tin nhắn |
| raw_ai_response | JSON | ✅ | Phản hồi thô từ AI Provider, phục vụ debug |
| created_at | DATETIME(6) | ❌ | Thời điểm tạo tin nhắn |

---

### Quan hệ

```text
Chat Session (1)
      │
      └── Chat Message (N)
```

---

### Ràng buộc

- Mỗi tin nhắn phải thuộc về một phiên hội thoại.
- `role` chỉ nhận một trong hai giá trị:
  - `USER`
  - `ASSISTANT`
- Tin nhắn của người dùng không lưu `raw_ai_response`.
- Tin nhắn của AI có thể lưu phản hồi thô để hỗ trợ kiểm tra lỗi.
- Không lưu API Key, Access Token hoặc mật khẩu trong nội dung tin nhắn.

---

### Chỉ mục

```sql
PRIMARY KEY (id)

INDEX idx_chat_messages_session_id (session_id)

INDEX idx_chat_messages_session_created (
    session_id,
    created_at
)
```

Chỉ mục kết hợp hỗ trợ lấy lịch sử hội thoại theo đúng thứ tự thời gian.

---

## 11.3 Bảng `refresh_tokens`

### Mục đích

Lưu Refresh Token đã được băm để hỗ trợ cấp mới Access Token.

Hệ thống không lưu Refresh Token gốc nhằm hạn chế rủi ro khi cơ sở dữ liệu bị lộ.

---

### Business Rules liên quan

- AUTH-004
- AUTH-005
- AUTH-007
- SEC-002
- SEC-003

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|---|---|---:|---|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Người sở hữu token |
| token_hash | VARCHAR(255) | ❌ | Giá trị Refresh Token đã băm |
| expires_at | DATETIME(6) | ❌ | Thời điểm hết hạn |
| revoked_at | DATETIME(6) | ✅ | Thời điểm bị thu hồi |
| created_at | DATETIME(6) | ❌ | Thời điểm cấp token |

---

### Quan hệ

```text
User (1)
   │
   └── Refresh Token (N)
```

---

### Ràng buộc

- Mỗi Refresh Token phải thuộc về một người dùng.
- Token gốc chỉ được trả về cho Client tại thời điểm cấp.
- Database chỉ lưu `token_hash`.
- Token hết hạn hoặc đã bị thu hồi không được sử dụng.
- Khi thực hiện refresh, token cũ bị thu hồi và token mới được cấp.

---

### Chỉ mục

```sql
PRIMARY KEY (id)

UNIQUE KEY uk_refresh_tokens_token_hash (token_hash)

INDEX idx_refresh_tokens_user_id (user_id)

INDEX idx_refresh_tokens_expires_at (expires_at)
```

---

## 11.4 Bảng `user_sessions`

### Mục đích

Lưu thông tin các phiên đăng nhập của người dùng theo thiết bị và địa chỉ IP.

Bảng này hỗ trợ:

- Theo dõi thiết bị đăng nhập.
- Hiển thị lịch sử đăng nhập.
- Phát hiện hoạt động bất thường.
- Thu hồi phiên khi cần.

---

### Business Rules liên quan

- AUTH-001
- AUTH-004
- AUTH-006
- SEC-001
- SEC-004

---

### Cấu trúc bảng

| Cột | Kiểu dữ liệu | Null | Mô tả |
|---|---|---:|---|
| id | BIGINT | ❌ | Khóa chính |
| user_id | BIGINT | ❌ | Chủ sở hữu phiên |
| device_info | VARCHAR(255) | ✅ | Thông tin thiết bị hoặc trình duyệt |
| ip_address | VARCHAR(45) | ✅ | Địa chỉ IPv4 hoặc IPv6 |
| last_active_at | DATETIME(6) | ❌ | Lần hoạt động gần nhất |
| created_at | DATETIME(6) | ❌ | Thời điểm đăng nhập |

---

### Quan hệ

```text
User (1)
   │
   └── User Session (N)
```

---

### Ràng buộc

- Mỗi phiên đăng nhập phải thuộc về một người dùng.
- `ip_address` hỗ trợ tối đa 45 ký tự để lưu cả IPv4 và IPv6.
- Không lưu mật khẩu hoặc token trong bảng này.
- `last_active_at` được cập nhật khi người dùng thực hiện hoạt động hợp lệ.

---

### Chỉ mục

```sql
PRIMARY KEY (id)

INDEX idx_user_sessions_user_id (user_id)

INDEX idx_user_sessions_last_active (
    user_id,
    last_active_at
)
```

---

# 12. Quan hệ giữa các bảng

## 12.1 Quan hệ tổng thể

| Bảng cha | Quan hệ | Bảng con | Mô tả |
|---|---|---|---|
| `users` | 1 - N | `categories` | Một người dùng có nhiều danh mục |
| `users` | 1 - N | `transactions` | Một người dùng có nhiều giao dịch |
| `users` | 1 - N | `budgets` | Một người dùng có nhiều ngân sách |
| `users` | 1 - N | `notifications` | Một người dùng có nhiều thông báo |
| `users` | 1 - N | `chat_sessions` | Một người dùng có nhiều phiên chat |
| `users` | 1 - N | `refresh_tokens` | Một người dùng có nhiều Refresh Token |
| `users` | 1 - N | `user_sessions` | Một người dùng có nhiều phiên đăng nhập |
| `categories` | 1 - N | `transactions` | Một danh mục có nhiều giao dịch |
| `categories` | 1 - N | `budgets` | Một danh mục có nhiều ngân sách theo thời gian |
| `chat_sessions` | 1 - N | `chat_messages` | Một phiên chat có nhiều tin nhắn |

---

## 12.2 Sơ đồ quan hệ dạng văn bản

```text
users
│
├── categories
│      ├── transactions
│      └── budgets
│
├── transactions
│
├── budgets
│
├── notifications
│
├── chat_sessions
│      └── chat_messages
│
├── refresh_tokens
│
└── user_sessions
```

---

# 13. Thiết kế khóa ngoại

## 13.1 Nguyên tắc chung

Các khóa ngoại được sử dụng để đảm bảo dữ liệu tham chiếu luôn hợp lệ.

Ví dụ:

```sql
FOREIGN KEY (user_id)
REFERENCES users(id)
```

Hệ thống không sử dụng `ON DELETE CASCADE` cho các bảng nghiệp vụ quan trọng nếu việc xóa tự động có thể làm mất lịch sử dữ liệu.

---

## 13.2 Chính sách xóa

| Quan hệ | Chính sách đề xuất |
|---|---|
| User → Transaction | Không xóa cứng user |
| User → Category | Không xóa cứng user |
| User → Budget | Không xóa cứng user |
| User → Notification | Có thể xóa theo nghiệp vụ |
| Chat Session → Chat Message | Có thể dùng `ON DELETE CASCADE` |
| User → Refresh Token | Có thể xóa khi tài khoản bị xóa hoàn toàn |
| User → User Session | Có thể xóa khi tài khoản bị xóa hoàn toàn |

Với phiên bản hiện tại, tài khoản người dùng không nên bị xóa vật lý. Có thể bổ sung trạng thái tài khoản ở phiên bản sau.

---

# 14. Chiến lược Soft Delete

## 14.1 Bảng áp dụng

Trong phiên bản đầu tiên, Soft Delete được áp dụng cho bảng:

```text
transactions
```

Giao dịch là dữ liệu nghiệp vụ quan trọng, vì vậy khi người dùng xóa, hệ thống chỉ cập nhật:

```sql
deleted_at = CURRENT_TIMESTAMP
```

---

## 14.2 Quy tắc truy vấn

Mọi truy vấn nghiệp vụ mặc định phải lọc:

```sql
deleted_at IS NULL
```

Ví dụ:

```sql
SELECT *
FROM transactions
WHERE user_id = ?
  AND deleted_at IS NULL;
```

Các giao dịch đã xóa không được sử dụng trong:

- Dashboard.
- Tính tổng thu.
- Tính tổng chi.
- Tính tiến độ ngân sách.
- Phân tích AI.
- Xuất báo cáo.

---

## 14.3 Category không dùng Soft Delete

Danh mục không được xóa nếu đang được giao dịch sử dụng.

Với danh mục chưa được sử dụng, hệ thống có thể cho phép xóa vật lý.

Cách này giúp thiết kế đơn giản hơn nhưng vẫn bảo vệ tính toàn vẹn của dữ liệu lịch sử.

---

# 15. Audit Fields

## 15.1 Các trường dùng chung

Các bảng chính sử dụng:

```text
created_at
updated_at
```

Riêng bảng `transactions` có thêm:

```text
deleted_at
```

---

## 15.2 Quy tắc cập nhật

- `created_at` chỉ được thiết lập một lần khi tạo bản ghi.
- `updated_at` được cập nhật khi dữ liệu thay đổi.
- `deleted_at` chỉ được thiết lập khi thực hiện Soft Delete.
- Thời gian được lưu thống nhất trong Database.

---

## 15.3 Kiểu dữ liệu

Khuyến nghị sử dụng:

```sql
DATETIME(6)
```

thay cho `TIMESTAMP`.

Lý do:

- Hỗ trợ độ chính xác đến microsecond.
- Ít phụ thuộc vào giới hạn thời gian của `TIMESTAMP`.
- Phù hợp với `LocalDateTime` trong Java.

---

# 16. Chiến lược Index

## 16.1 Nguyên tắc

Chỉ mục được tạo dựa trên các truy vấn thực tế, không tạo index cho mọi cột.

Ưu tiên các cột:

- Khóa ngoại.
- Cột thường dùng để lọc.
- Cột thường dùng để sắp xếp.
- Tổ hợp cột thường xuất hiện cùng nhau trong điều kiện truy vấn.

---

## 16.2 Index đề xuất

### Bảng `transactions`

```sql
INDEX idx_transactions_user_date (
    user_id,
    transaction_date
)

INDEX idx_transactions_user_category_date (
    user_id,
    category_id,
    transaction_date
)

INDEX idx_transactions_user_type_date (
    user_id,
    type,
    transaction_date
)

INDEX idx_transactions_deleted_at (
    deleted_at
)
```

Đây là bảng có số lượng dữ liệu lớn nhất nên cần ưu tiên tối ưu.

---

### Bảng `budgets`

```sql
UNIQUE KEY uk_budgets_user_category_month (
    user_id,
    category_id,
    budget_month
)
```

Ràng buộc này đảm bảo một người dùng không tạo hai ngân sách cho cùng một danh mục trong cùng một tháng.

---

### Bảng `notifications`

```sql
INDEX idx_notifications_user_read_created (
    user_id,
    is_read,
    created_at
)
```

Hỗ trợ lấy danh sách thông báo chưa đọc mới nhất.

---

### Bảng `chat_messages`

```sql
INDEX idx_chat_messages_session_created (
    session_id,
    created_at
)
```

Hỗ trợ đọc lịch sử hội thoại theo thứ tự.

---

# 17. Ràng buộc dữ liệu

## 17.1 Số tiền

Mọi cột tiền tệ sử dụng:

```sql
DECIMAL(15,2)
```

Không sử dụng:

```text
FLOAT
DOUBLE
```

vì có thể gây sai số khi tính toán tài chính.

---

## 17.2 Giá trị giao dịch

```sql
amount > 0
```

Số tiền luôn lưu dưới dạng số dương.

Chiều thu hoặc chi được xác định bởi:

```text
transactions.type
```

Quy tắc:

```text
INCOME  → khoản thu
EXPENSE → khoản chi
```

SmartSpend không phải ví điện tử nên hệ thống không cập nhật số dư ví.

Chênh lệch tài chính được tính theo công thức:

```text
Chênh lệch = Tổng thu - Tổng chi
```

---

## 17.3 Loại giao dịch và danh mục

Cả `transactions` và `categories` đều lưu trường `type`.

Vai trò:

| Thành phần | Vai trò của `type` |
|---|---|
| Transaction | Xác định giao dịch là Thu hay Chi |
| Category | Xác định danh mục dùng cho Thu hay Chi |

Hai giá trị phải khớp nhau.

Ví dụ hợp lệ:

```text
Transaction type: EXPENSE
Category: Ăn uống
Category type: EXPENSE
```

Ví dụ không hợp lệ:

```text
Transaction type: INCOME
Category: Ăn uống
Category type: EXPENSE
```

Ràng buộc này được kiểm tra tại Service Layer vì Database không thể dễ dàng tạo `CHECK CONSTRAINT` tham chiếu sang bảng khác.

---

## 17.4 Ngày giao dịch

`transaction_date` không được lớn hơn ngày hiện tại.

Quy tắc này được kiểm tra ở:

- DTO Validation.
- Service Layer.

Không nên phụ thuộc hoàn toàn vào Database vì việc so sánh ngày hiện tại có thể liên quan đến múi giờ của ứng dụng.

---

# 18. Sơ đồ ERD

Bạn có thể giữ phần dưới đây trong tài liệu để sau này thay bằng hình ERD từ Draw.io.

```text
users
│
├── categories
│      ├── transactions
│      └── budgets
│
├── transactions
│
├── budgets
│
├── notifications
│
├── chat_sessions
│      └── chat_messages
│
├── refresh_tokens
│
└── user_sessions
```

Khi đã có ảnh ERD, đặt file tại:

```text
assets/diagrams/smartspend-erd.png
```

Sau đó nhúng vào tài liệu:

```markdown
![ERD SmartSpend](../assets/diagrams/smartspend-erd.png)
```

---

# 19. Danh sách bảng cuối cùng

| STT | Bảng | Vai trò |
|---:|---|---|
| 1 | `users` | Lưu tài khoản người dùng |
| 2 | `categories` | Lưu danh mục thu và chi |
| 3 | `transactions` | Lưu các khoản thu và chi |
| 4 | `budgets` | Lưu ngân sách theo tháng và danh mục |
| 5 | `notifications` | Lưu thông báo của người dùng |
| 6 | `chat_sessions` | Lưu phiên hội thoại AI |
| 7 | `chat_messages` | Lưu nội dung hội thoại |
| 8 | `refresh_tokens` | Lưu Refresh Token đã băm |
| 9 | `user_sessions` | Lưu thông tin phiên đăng nhập |

---

# 20. Tổng kết

Cơ sở dữ liệu của SmartSpend được thiết kế tập trung vào ba nhóm dữ liệu chính:

1. Dữ liệu người dùng và xác thực.
2. Dữ liệu quản lý thu nhập, chi tiêu và ngân sách.
3. Dữ liệu hỗ trợ AI và thông báo.

Bảng `transactions` là trung tâm của hệ thống.

Các module Dashboard, Budget, Export và AI đều sử dụng dữ liệu giao dịch để tính toán hoặc phân tích, nhưng không tạo thêm bảng tổng hợp không cần thiết.

Thiết kế hiện tại cố ý không bao gồm:

- Ví điện tử.
- Tài khoản ngân hàng.
- Số dư ví.
- Chuyển tiền.
- Thanh toán trực tuyến.

Điều này giúp SmartSpend giữ đúng phạm vi là một ứng dụng quản lý tài chính cá nhân thông minh, đồng thời giảm độ phức tạp để phù hợp với một dự án Portfolio dành cho sinh viên.