# Thiết kế các module

# SmartSpend

### AI Personal Finance Manager

---

# 1. Giới thiệu

## 1.1 Mục đích

Tài liệu này mô tả thiết kế chi tiết các module của hệ thống **SmartSpend**.

Mỗi module được trình bày theo cùng một cấu trúc để giúp quá trình phát triển, kiểm thử và bảo trì mã nguồn được thống nhất.

Tài liệu này đóng vai trò cầu nối giữa:

- Business Rules.
- Database Design.
- API Design.
- Source Code.

---

## 1.2 Phạm vi

SmartSpend được chia thành các module chính sau:

1. Authentication.
2. Category.
3. Transaction.
4. Budget.
5. Dashboard.
6. Notification.
7. AI Assistant.
8. Export.

Dự án không bao gồm:

- Ví điện tử.
- Chuyển tiền.
- Thanh toán trực tuyến.
- Tài khoản ngân hàng.
- Quản lý đầu tư.

---

# 2. Nguyên tắc thiết kế module

Mỗi module được tổ chức theo **Feature Package Structure**.

Ví dụ:

```text
transaction
├── controller
├── service
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── mapper
└── specification
```

Luồng xử lý chung:

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

Quy tắc:

- Controller chỉ nhận request và trả response.
- Service chứa business logic.
- Repository chỉ truy cập dữ liệu.
- DTO dùng để trao đổi dữ liệu với client.
- Entity không được trả trực tiếp ra API.
- Mọi dữ liệu cá nhân phải được kiểm tra quyền sở hữu.
- `userId` được lấy từ `SecurityContext`, không nhận từ request body.

---

# 3. Authentication Module

## 3.1 Mục tiêu

Quản lý đăng ký, đăng nhập, xác thực JWT, Refresh Token và phiên đăng nhập của người dùng.

---

## 3.2 Chức năng

- Đăng ký tài khoản.
- Đăng nhập.
- Cấp Access Token.
- Cấp Refresh Token.
- Làm mới Access Token.
- Đăng xuất.
- Lấy thông tin người dùng hiện tại.
- Theo dõi phiên đăng nhập.

---

## 3.3 Thành phần chính

```text
auth
├── controller
│   └── AuthController
├── service
│   ├── AuthService
│   └── AuthServiceImpl
├── repository
│   ├── UserRepository
│   ├── RefreshTokenRepository
│   └── UserSessionRepository
├── entity
│   ├── User
│   ├── RefreshToken
│   └── UserSession
├── dto
│   ├── request
│   │   ├── RegisterRequest
│   │   ├── LoginRequest
│   │   └── RefreshTokenRequest
│   └── response
│       ├── AuthResponse
│       └── UserResponse
└── mapper
    └── UserMapper
```

---

## 3.4 Luồng đăng ký

```text
Client
   │
   ▼
AuthController
   │
   ▼
AuthService
   │
   ├── Kiểm tra email
   ├── Mã hóa mật khẩu
   ├── Tạo User
   ▼
UserRepository
   │
   ▼
MySQL
```

Các bước:

1. Validate email, mật khẩu và họ tên.
2. Kiểm tra email đã tồn tại hay chưa.
3. Mã hóa mật khẩu bằng BCrypt.
4. Tạo tài khoản mới.
5. Lưu người dùng.
6. Trả thông tin người dùng, không trả mật khẩu.

---

## 3.5 Luồng đăng nhập

```text
Client
   │
   ▼
AuthController
   │
   ▼
AuthService
   │
   ├── Xác thực email và mật khẩu
   ├── Tạo Access Token
   ├── Tạo Refresh Token
   ├── Lưu Refresh Token đã băm
   └── Lưu User Session
```

---

## 3.6 Phương thức Service dự kiến

```java
UserResponse register(RegisterRequest request);

AuthResponse login(
        LoginRequest request,
        String deviceInfo,
        String ipAddress
);

AuthResponse refreshToken(RefreshTokenRequest request);

void logout(String refreshToken);

UserResponse getCurrentUser(Long userId);
```

---

## 3.7 Business Rules liên quan

- AUTH-001 đến AUTH-007.
- SEC-001 đến SEC-004.

---

## 3.8 Validation

- Email đúng định dạng.
- Email không được trùng.
- Mật khẩu có độ dài tối thiểu.
- Không trả mật khẩu ra response.
- Refresh Token phải còn hạn và chưa bị thu hồi.

---

## 3.9 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `AUTH_EMAIL_ALREADY_EXISTS` | Email đã được đăng ký |
| `AUTH_INVALID_CREDENTIALS` | Email hoặc mật khẩu không đúng |
| `AUTH_REFRESH_TOKEN_INVALID` | Refresh Token không hợp lệ |
| `AUTH_REFRESH_TOKEN_EXPIRED` | Refresh Token đã hết hạn |
| `AUTH_UNAUTHORIZED` | Chưa đăng nhập hoặc Access Token không hợp lệ |

---

# 4. Category Module

## 4.1 Mục tiêu

Quản lý danh mục dùng để phân loại các khoản thu nhập và chi tiêu.

---

## 4.2 Chức năng

- Lấy danh sách danh mục.
- Tạo danh mục cá nhân.
- Cập nhật danh mục.
- Xóa danh mục chưa được sử dụng.
- Hiển thị danh mục mặc định của hệ thống.
- Lọc theo loại `INCOME` hoặc `EXPENSE`.

---

## 4.3 Thành phần chính

```text
category
├── controller
│   └── CategoryController
├── service
│   ├── CategoryService
│   └── CategoryServiceImpl
├── repository
│   └── CategoryRepository
├── entity
│   ├── Category
│   └── CategoryType
├── dto
│   ├── request
│   │   ├── CreateCategoryRequest
│   │   └── UpdateCategoryRequest
│   └── response
│       └── CategoryResponse
└── mapper
    └── CategoryMapper
```

---

## 4.4 Luồng tạo danh mục

```text
Client
   │
   ▼
CategoryController
   │
   ▼
CategoryService
   │
   ├── Lấy user hiện tại
   ├── Kiểm tra tên trùng
   ├── Gán isDefault = false
   ▼
CategoryRepository
   │
   ▼
MySQL
```

---

## 4.5 Phương thức Service dự kiến

```java
CategoryResponse create(
        Long userId,
        CreateCategoryRequest request
);

CategoryResponse update(
        Long userId,
        Long categoryId,
        UpdateCategoryRequest request
);

void delete(Long userId, Long categoryId);

CategoryResponse getById(Long userId, Long categoryId);

List<CategoryResponse> getAll(
        Long userId,
        CategoryType type
);
```

---

## 4.6 Business Rules liên quan

- CAT-001 đến CAT-006.

---

## 4.7 Validation

- Tên danh mục không được để trống.
- Loại danh mục phải là `INCOME` hoặc `EXPENSE`.
- Không được trùng tên trong cùng loại.
- Không được sửa hoặc xóa danh mục mặc định.
- Không được xóa danh mục đang được giao dịch sử dụng.

---

## 4.8 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `CATEGORY_NOT_FOUND` | Không tìm thấy danh mục |
| `CATEGORY_NAME_EXISTS` | Tên danh mục đã tồn tại |
| `CATEGORY_DEFAULT_READ_ONLY` | Không được sửa danh mục mặc định |
| `CATEGORY_IN_USE` | Danh mục đang được giao dịch sử dụng |
| `CATEGORY_ACCESS_DENIED` | Danh mục không thuộc người dùng hiện tại |

---

# 5. Transaction Module

## 5.1 Mục tiêu

Quản lý toàn bộ các khoản thu nhập và chi tiêu của người dùng.

Đây là module trung tâm của SmartSpend và là nguồn dữ liệu chính cho Dashboard, Budget, Export và AI Assistant.

---

## 5.2 Chức năng

- Tạo giao dịch.
- Xem chi tiết giao dịch.
- Cập nhật giao dịch.
- Xóa mềm giao dịch.
- Tìm kiếm theo ghi chú.
- Lọc theo loại.
- Lọc theo danh mục.
- Lọc theo khoảng thời gian.
- Phân trang và sắp xếp.

---

## 5.3 Thành phần chính

```text
transaction
├── controller
│   └── TransactionController
├── service
│   ├── TransactionService
│   └── TransactionServiceImpl
├── repository
│   └── TransactionRepository
├── entity
│   ├── Transaction
│   └── TransactionType
├── dto
│   ├── request
│   │   ├── CreateTransactionRequest
│   │   ├── UpdateTransactionRequest
│   │   └── TransactionFilterRequest
│   └── response
│       └── TransactionResponse
├── mapper
│   └── TransactionMapper
└── specification
    └── TransactionSpecification
```

---

## 5.4 Luồng tạo giao dịch

```text
Client
   │
   ▼
TransactionController
   │
   ▼
TransactionService
   │
   ├── Lấy user hiện tại
   ├── Kiểm tra Category
   ├── Kiểm tra quyền sở hữu
   ├── Kiểm tra type
   ├── Kiểm tra amount
   ├── Kiểm tra ngày giao dịch
   ▼
TransactionRepository
   │
   ▼
MySQL
   │
   ▼
BudgetService
   │
   └── Kiểm tra ngưỡng ngân sách
```

---

## 5.5 Phương thức Service dự kiến

```java
TransactionResponse create(
        Long userId,
        CreateTransactionRequest request
);

TransactionResponse update(
        Long userId,
        Long transactionId,
        UpdateTransactionRequest request
);

void softDelete(Long userId, Long transactionId);

TransactionResponse getById(
        Long userId,
        Long transactionId
);

PageResponse<TransactionResponse> search(
        Long userId,
        TransactionFilterRequest filter,
        Pageable pageable
);
```

---

## 5.6 Business Rules liên quan

- TRAN-001 đến TRAN-010.

---

## 5.7 Validation

- `amount > 0`.
- Ngày giao dịch không được ở tương lai.
- Loại giao dịch phải là `INCOME` hoặc `EXPENSE`.
- Loại giao dịch phải trùng loại danh mục.
- Danh mục phải thuộc người dùng hoặc là danh mục mặc định.
- Giao dịch đã xóa không được sửa.
- Người dùng không được truy cập giao dịch của người khác.

---

## 5.8 Tìm kiếm và lọc

Các tiêu chí hỗ trợ:

- Từ ngày.
- Đến ngày.
- Loại giao dịch.
- Danh mục.
- Từ khóa trong ghi chú.
- Số tiền tối thiểu.
- Số tiền tối đa.

Ví dụ:

```text
GET /api/transactions
    ?type=EXPENSE
    &categoryId=5
    &fromDate=2026-07-01
    &toDate=2026-07-31
    &page=0
    &size=20
```

---

## 5.9 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `TRANSACTION_NOT_FOUND` | Không tìm thấy giao dịch |
| `TRANSACTION_ACCESS_DENIED` | Giao dịch không thuộc người dùng |
| `TRANSACTION_INVALID_AMOUNT` | Số tiền không hợp lệ |
| `TRANSACTION_FUTURE_DATE` | Ngày giao dịch ở tương lai |
| `TRANSACTION_CATEGORY_TYPE_MISMATCH` | Loại giao dịch không khớp danh mục |
| `TRANSACTION_ALREADY_DELETED` | Giao dịch đã bị xóa |

---

# 6. Budget Module

## 6.1 Mục tiêu

Quản lý ngân sách chi tiêu theo từng danh mục và từng tháng.

---

## 6.2 Chức năng

- Tạo ngân sách.
- Cập nhật hạn mức.
- Xóa ngân sách.
- Lấy danh sách ngân sách theo tháng.
- Tính số tiền đã chi.
- Tính số tiền còn lại.
- Tính tỷ lệ sử dụng.
- Tạo cảnh báo ngân sách.

---

## 6.3 Thành phần chính

```text
budget
├── controller
│   └── BudgetController
├── service
│   ├── BudgetService
│   └── BudgetServiceImpl
├── repository
│   └── BudgetRepository
├── entity
│   └── Budget
├── dto
│   ├── request
│   │   ├── CreateBudgetRequest
│   │   └── UpdateBudgetRequest
│   └── response
│       └── BudgetResponse
└── mapper
    └── BudgetMapper
```

---

## 6.4 Luồng tạo ngân sách

```text
Client
   │
   ▼
BudgetController
   │
   ▼
BudgetService
   │
   ├── Kiểm tra Category
   ├── Category phải là EXPENSE
   ├── Kiểm tra ngân sách trùng tháng
   ├── Kiểm tra amount > 0
   ▼
BudgetRepository
   │
   ▼
MySQL
```

---

## 6.5 Luồng tính tiến độ

```text
Budget
   │
   ├── limitAmount
   │
   ▼
TransactionRepository
   │
   └── Tổng EXPENSE theo Category và tháng
   │
   ▼
BudgetService
   │
   ├── spentAmount
   ├── remainingAmount
   └── percentage
```

Công thức:

```text
Số tiền còn lại = Hạn mức - Số tiền đã chi

Tỷ lệ sử dụng = Số tiền đã chi / Hạn mức × 100
```

---

## 6.6 Phương thức Service dự kiến

```java
BudgetResponse create(
        Long userId,
        CreateBudgetRequest request
);

BudgetResponse update(
        Long userId,
        Long budgetId,
        UpdateBudgetRequest request
);

void delete(Long userId, Long budgetId);

BudgetResponse getById(Long userId, Long budgetId);

List<BudgetResponse> getByMonth(
        Long userId,
        int year,
        int month
);

void checkBudgetThreshold(
        Long userId,
        Long categoryId,
        LocalDate transactionDate
);
```

---

## 6.7 Ngưỡng cảnh báo

| Ngưỡng | Xử lý |
|---:|---|
| 80% | Tạo cảnh báo sắp vượt ngân sách |
| 100% | Tạo cảnh báo đã đạt hạn mức |
| Trên 100% | Tạo cảnh báo vượt ngân sách |

Hệ thống cần tránh tạo nhiều thông báo trùng lặp cho cùng một ngân sách và cùng một ngưỡng.

Trong phiên bản đầu, có thể xử lý bằng cách kiểm tra thông báo đã tồn tại trước khi tạo.

---

## 6.8 Business Rules liên quan

- BUD-001 đến BUD-007.

---

## 6.9 Validation

- Ngân sách chỉ dùng cho danh mục `EXPENSE`.
- Hạn mức phải lớn hơn 0.
- Một danh mục chỉ có một ngân sách trong cùng một tháng.
- Tháng và năm phải hợp lệ.
- Người dùng chỉ được thao tác với ngân sách của mình.

---

## 6.10 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `BUDGET_NOT_FOUND` | Không tìm thấy ngân sách |
| `BUDGET_ALREADY_EXISTS` | Ngân sách cùng danh mục và tháng đã tồn tại |
| `BUDGET_INVALID_AMOUNT` | Hạn mức không hợp lệ |
| `BUDGET_CATEGORY_NOT_EXPENSE` | Danh mục không phải loại chi tiêu |
| `BUDGET_ACCESS_DENIED` | Ngân sách không thuộc người dùng |

---

# 7. Dashboard Module

## 7.1 Mục tiêu

Tổng hợp dữ liệu tài chính của người dùng để hiển thị số liệu và biểu đồ.

Dashboard là module chỉ đọc, không thay đổi dữ liệu.

---

## 7.2 Chức năng

- Tổng thu.
- Tổng chi.
- Chênh lệch thu chi.
- Thống kê theo danh mục.
- Thống kê theo ngày hoặc tháng.
- Danh sách giao dịch gần đây.
- So sánh với kỳ trước.

---

## 7.3 Thành phần chính

```text
dashboard
├── controller
│   └── DashboardController
├── service
│   ├── DashboardService
│   └── DashboardServiceImpl
├── dto
│   └── response
│       ├── DashboardSummaryResponse
│       ├── CategoryStatisticResponse
│       ├── TrendResponse
│       └── PeriodComparisonResponse
└── projection
    ├── CategorySummaryProjection
    └── TrendProjection
```

Dashboard không nhất thiết có Entity hoặc Repository riêng.

Nó có thể sử dụng `TransactionRepository` để thực hiện các query tổng hợp.

---

## 7.4 Luồng xử lý

```text
Client
   │
   ▼
DashboardController
   │
   ▼
DashboardService
   │
   ├── TransactionRepository
   ├── BudgetRepository
   └── Redis Cache
   │
   ▼
Dashboard Response
```

---

## 7.5 Phương thức Service dự kiến

```java
DashboardSummaryResponse getSummary(
        Long userId,
        int year,
        int month
);

List<CategoryStatisticResponse> getExpenseByCategory(
        Long userId,
        LocalDate fromDate,
        LocalDate toDate
);

List<TrendResponse> getMonthlyTrend(
        Long userId,
        int year
);

PeriodComparisonResponse comparePeriods(
        Long userId,
        YearMonth currentPeriod,
        YearMonth previousPeriod
);
```

---

## 7.6 Cache

Redis có thể được sử dụng để cache Dashboard theo khóa:

```text
dashboard:{userId}:{year}:{month}
```

Cache phải được xóa khi:

- Tạo giao dịch.
- Cập nhật giao dịch.
- Xóa giao dịch.
- Thay đổi ngân sách nếu response có chứa dữ liệu ngân sách.

MySQL vẫn là nguồn dữ liệu chính.

---

## 7.7 Business Rules liên quan

- DASH-001 đến DASH-006.

---

## 7.8 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `DASHBOARD_INVALID_PERIOD` | Khoảng thời gian không hợp lệ |
| `DASHBOARD_DATA_UNAVAILABLE` | Không thể tổng hợp dữ liệu |

---

# 8. Notification Module

## 8.1 Mục tiêu

Quản lý các thông báo được tạo bởi hệ thống cho người dùng.

---

## 8.2 Chức năng

- Tạo thông báo.
- Lấy danh sách thông báo.
- Lấy số lượng thông báo chưa đọc.
- Đánh dấu một thông báo đã đọc.
- Đánh dấu tất cả đã đọc.
- Xóa thông báo.

---

## 8.3 Thành phần chính

```text
notification
├── controller
│   └── NotificationController
├── service
│   ├── NotificationService
│   └── NotificationServiceImpl
├── repository
│   └── NotificationRepository
├── entity
│   ├── Notification
│   └── NotificationType
├── dto
│   └── response
│       └── NotificationResponse
└── mapper
    └── NotificationMapper
```

---

## 8.4 Nguồn tạo thông báo

Thông báo có thể được tạo từ:

- Budget Service.
- AI Assistant.
- Hệ thống.

Ví dụ:

```text
BudgetService
   │
   ▼
NotificationService
   │
   ▼
NotificationRepository
```

---

## 8.5 Phương thức Service dự kiến

```java
NotificationResponse create(
        Long userId,
        NotificationType type,
        String title,
        String content
);

PageResponse<NotificationResponse> getAll(
        Long userId,
        Pageable pageable
);

long countUnread(Long userId);

void markAsRead(Long userId, Long notificationId);

void markAllAsRead(Long userId);

void delete(Long userId, Long notificationId);
```

---

## 8.6 Business Rules liên quan

- NOTI-001 đến NOTI-004.

---

## 8.7 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `NOTIFICATION_NOT_FOUND` | Không tìm thấy thông báo |
| `NOTIFICATION_ACCESS_DENIED` | Thông báo không thuộc người dùng |

---

# 9. AI Assistant Module

## 9.1 Mục tiêu

Phân tích dữ liệu thu chi và hỗ trợ người dùng hiểu rõ hơn về tình hình tài chính cá nhân.

AI Assistant chỉ đọc dữ liệu, không được thay đổi giao dịch, danh mục hoặc ngân sách.

---

## 9.2 Chức năng

- Tạo phiên hội thoại.
- Gửi câu hỏi.
- Phân tích dữ liệu tài chính.
- Trả lời bằng ngôn ngữ tự nhiên.
- Lưu lịch sử hội thoại.
- Xem lại các phiên chat.
- Xóa phiên chat.

---

## 9.3 Thành phần chính

```text
ai
├── controller
│   └── ChatController
├── service
│   ├── ChatService
│   ├── ChatServiceImpl
│   ├── AIService
│   └── AIServiceImpl
├── repository
│   ├── ChatSessionRepository
│   └── ChatMessageRepository
├── entity
│   ├── ChatSession
│   ├── ChatMessage
│   └── ChatRole
├── dto
│   ├── request
│   │   └── SendMessageRequest
│   └── response
│       ├── ChatSessionResponse
│       ├── ChatMessageResponse
│       └── ChatResponse
├── client
│   └── OpenAIClient
├── context
│   ├── FinancialContext
│   └── FinancialContextBuilder
├── prompt
│   └── FinancialPromptBuilder
└── parser
    └── AIResponseParser
```

---

## 9.4 Luồng xử lý câu hỏi

```text
User
   │
   ▼
ChatController
   │
   ▼
ChatService
   │
   ├── Lưu câu hỏi USER
   ├── Tải lịch sử hội thoại
   ▼
FinancialContextBuilder
   │
   ├── Tổng thu
   ├── Tổng chi
   ├── Chi theo danh mục
   ├── Ngân sách
   └── Xu hướng
   │
   ▼
FinancialPromptBuilder
   │
   ▼
OpenAIClient
   │
   ▼
AI Provider
   │
   ▼
AIResponseParser
   │
   ▼
Lưu câu trả lời ASSISTANT
   │
   ▼
Client
```

---

## 9.5 Phương thức Service dự kiến

```java
ChatSessionResponse createSession(Long userId);

ChatResponse sendMessage(
        Long userId,
        Long sessionId,
        SendMessageRequest request
);

PageResponse<ChatSessionResponse> getSessions(
        Long userId,
        Pageable pageable
);

List<ChatMessageResponse> getMessages(
        Long userId,
        Long sessionId
);

void deleteSession(Long userId, Long sessionId);
```

---

## 9.6 Context tài chính

Context gửi tới AI có thể bao gồm:

- Tổng thu trong kỳ.
- Tổng chi trong kỳ.
- Chênh lệch thu chi.
- Các danh mục chi tiêu lớn nhất.
- Ngân sách và tỷ lệ sử dụng.
- So sánh với kỳ trước.
- Một số giao dịch gần đây nếu cần.

Không gửi:

- Mật khẩu.
- Access Token.
- Refresh Token.
- API Key.
- Email nếu không cần thiết.

---

## 9.7 Business Rules liên quan

- AI-001 đến AI-006.

---

## 9.8 Error Handling

| Mã lỗi | Trường hợp |
|---|---|
| `CHAT_SESSION_NOT_FOUND` | Không tìm thấy phiên hội thoại |
| `CHAT_SESSION_ACCESS_DENIED` | Phiên chat không thuộc người dùng |
| `AI_PROVIDER_TIMEOUT` | AI Provider phản hồi quá chậm |
| `AI_PROVIDER_UNAVAILABLE` | Không thể kết nối AI Provider |
| `AI_RESPONSE_INVALID` | Phản hồi AI không hợp lệ |
| `AI_CONTEXT_EMPTY` | Chưa có đủ dữ liệu để phân tích |

Khi AI Provider lỗi, hệ thống phải trả thông báo dễ hiểu, không trả lỗi kỹ thuật trực tiếp cho người dùng.

---

# 10. Export Module

## 10.1 Mục tiêu

Cho phép người dùng xuất dữ liệu giao dịch để lưu trữ hoặc phân tích bên ngoài hệ thống.

---

## 10.2 Chức năng

- Xuất CSV.
- Xuất Excel.
- Lọc dữ liệu theo khoảng thời gian.
- Lọc theo loại giao dịch.
- Lọc theo danh mục.
- Chỉ xuất dữ liệu của người dùng hiện tại.

---

## 10.3 Thành phần chính

```text
export
├── controller
│   └── ExportController
├── service
│   ├── ExportService
│   └── ExportServiceImpl
├── dto
│   └── request
│       └── ExportTransactionRequest
└── generator
    ├── CsvReportGenerator
    └── ExcelReportGenerator
```

Phiên bản hiện tại không cần lưu lịch sử Export Job trong Database.

File có thể được tạo trực tiếp và trả về dưới dạng download response.

---

## 10.4 Luồng xử lý

```text
Client
   │
   ▼
ExportController
   │
   ▼
ExportService
   │
   ├── Validate bộ lọc
   ├── Truy vấn Transaction
   ├── Chọn Report Generator
   ▼
CSV hoặc Excel
   │
   ▼
HTTP Download Response
```

---

## 10.5 Phương thức Service dự kiến

```java
byte[] exportCsv(
        Long userId,
        ExportTransactionRequest request
);

byte[] exportExcel(
        Long userId,
        ExportTransactionRequest request
);
```

Có thể thay `byte[]` bằng một object chứa:

- Tên file.
- Content type.
- Dữ liệu file.

---

## 10.6 Business Rules liên quan

- EXP-001 đến EXP-004.

---

## 10.7 Validation

- Ngày bắt đầu không được sau ngày kết thúc.
- Chỉ xuất giao dịch chưa bị xóa.
- Danh mục lọc phải thuộc người dùng hoặc là danh mục mặc định.
- Định dạng chỉ được là CSV hoặc Excel.

---

## 10.8 Lỗi nghiệp vụ

| Mã lỗi | Trường hợp |
|---|---|
| `EXPORT_INVALID_DATE_RANGE` | Khoảng ngày không hợp lệ |
| `EXPORT_NO_DATA` | Không có dữ liệu phù hợp |
| `EXPORT_GENERATION_FAILED` | Không thể tạo file báo cáo |
| `EXPORT_UNSUPPORTED_FORMAT` | Định dạng không được hỗ trợ |

---

# 11. Phụ thuộc giữa các module

## 11.1 Sơ đồ phụ thuộc

```text
Authentication
      │
      └── Cung cấp thông tin user hiện tại
             │
             ├── Category
             ├── Transaction
             ├── Budget
             ├── Dashboard
             ├── Notification
             ├── AI Assistant
             └── Export

Transaction
      │
      ├── Budget
      ├── Dashboard
      ├── AI Assistant
      └── Export

Budget
      │
      ├── Notification
      ├── Dashboard
      └── AI Assistant
```

---

## 11.2 Quy tắc phụ thuộc

- Module không gọi Controller của module khác.
- Service có thể gọi Service của module khác nếu nghiệp vụ yêu cầu.
- Hạn chế Service phụ thuộc vòng tròn.
- Repository chỉ nên được sử dụng trong module sở hữu nó.
- Nếu module khác cần dữ liệu, ưu tiên gọi Service công khai của module sở hữu.
- Dashboard có thể dùng query tổng hợp từ `TransactionRepository` nếu được thống nhất rõ trong thiết kế.
- AI không được ghi trực tiếp vào dữ liệu nghiệp vụ.

---

# 12. Thứ tự phát triển module

Thứ tự khuyến nghị:

```text
1. Authentication
        │
        ▼
2. Category
        │
        ▼
3. Transaction
        │
        ▼
4. Budget
        │
        ▼
5. Dashboard
        │
        ▼
6. Notification
        │
        ▼
7. AI Assistant
        │
        ▼
8. Export
```

Lý do:

- Authentication là nền tảng bảo mật.
- Category cần có trước Transaction.
- Transaction là nguồn dữ liệu cho các module còn lại.
- Budget và Dashboard phụ thuộc Transaction.
- AI cần dữ liệu thực tế để phân tích.
- Export được phát triển sau cùng vì ít ảnh hưởng đến nghiệp vụ lõi.

---

# 13. Kiểm thử theo module

Mỗi module nên có:

- Unit Test cho Service.
- Repository Test cho query đặc biệt.
- Controller Integration Test cho API quan trọng.
- Test kiểm tra quyền sở hữu dữ liệu.
- Test validation.
- Test trường hợp lỗi.

Các module cần ưu tiên kiểm thử:

1. Authentication.
2. Transaction.
3. Budget.
4. AI Assistant.

---

# 14. Tổng kết

SmartSpend được chia thành tám module nghiệp vụ độc lập:

1. Authentication.
2. Category.
3. Transaction.
4. Budget.
5. Dashboard.
6. Notification.
7. AI Assistant.
8. Export.

Mỗi module được tổ chức theo Feature Package Structure và vẫn tuân thủ kiến trúc phân tầng bên trong:

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

Trong đó, `Transaction` là module trung tâm, cung cấp dữ liệu cho Budget, Dashboard, AI Assistant và Export.

Thiết kế module hiện tại đủ đơn giản để phù hợp với một dự án cá nhân, nhưng vẫn thể hiện được các kỹ năng quan trọng của một Java Backend Developer như:

- Thiết kế REST API.
- Spring Security và JWT.
- Spring Data JPA.
- Redis Cache.
- Quản lý nghiệp vụ theo module.
- Tích hợp AI.
- Kiểm soát quyền sở hữu dữ liệu.