# Quy ước lập trình

## SmartSpend

### AI Personal Finance Manager

---

## 1. Giới thiệu

### 1.1 Mục đích

Tài liệu này định nghĩa các quy ước lập trình được sử dụng trong dự án **SmartSpend**.

Mục tiêu là giúp mã nguồn:

- Dễ đọc.
- Dễ bảo trì.
- Dễ kiểm thử.
- Nhất quán giữa các module.
- Hạn chế lỗi do cách viết code không đồng nhất.

Mọi mã nguồn được thêm vào dự án nên tuân thủ các quy tắc trong tài liệu này.

---

## 2. Công nghệ và phiên bản

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA |
| Validation | Jakarta Bean Validation |
| Database | MySQL 8 |
| Cache | Redis |
| Migration | Flyway |
| Build Tool | Maven |
| Mapping | MapStruct |
| Logging | SLF4J + Logback |
| API Docs | Swagger/OpenAPI |

---

## 3. Nguyên tắc chung

| Nguyên tắc | Mô tả |
|---|---|
| Consistency | Cách viết nhất quán quan trọng hơn cách viết ngắn |
| Readability | Ưu tiên code dễ đọc |
| Single Responsibility | Mỗi class chỉ đảm nhiệm một nhiệm vụ |
| Fail Fast | Kiểm tra dữ liệu đầu vào càng sớm càng tốt |
| DTO Boundary | Không nhận hoặc trả Entity trực tiếp qua API |
| Explicit Code | Tránh logic ngầm và giá trị khó hiểu |
| No Sensitive Logging | Không ghi log mật khẩu, token hoặc API Key |
| BigDecimal for Money | Mọi dữ liệu tiền tệ phải dùng `BigDecimal` |

---

## 4. Cấu trúc package

SmartSpend sử dụng cách tổ chức **Package by Feature**.

```text
com.smartspend
├── auth
├── category
├── transaction
├── budget
├── dashboard
├── notification
├── ai
├── export
├── common
├── config
└── security
```

Mỗi module chứa các tầng riêng:

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

### Quy tắc

- Tên package viết thường.
- Không dùng dấu gạch dưới.
- Không đặt business logic trong `config` hoặc `util`.
- Không để module truy cập trực tiếp Controller của module khác.
- Hạn chế Repository được sử dụng bên ngoài module sở hữu.

---

## 5. Quy ước đặt tên

### 5.1 Class

Sử dụng PascalCase.

```java
TransactionController
TransactionService
TransactionRepository
CreateTransactionRequest
TransactionResponse
```

---

### 5.2 Method

Sử dụng camelCase.

```java
createTransaction()
getTransactionById()
findTransactions()
calculateMonthlyExpense()
```

Tên method phải thể hiện rõ hành động.

Không nên:

```java
process()
handle()
doSomething()
executeData()
```

---

### 5.3 Biến

Sử dụng camelCase.

```java
currentUser
totalIncome
transactionDate
remainingBudget
```

Không dùng tên quá ngắn, trừ biến lặp đơn giản.

Không nên:

```java
u
tx
amt
obj
data1
```

---

### 5.4 Hằng số

Sử dụng chữ hoa và dấu gạch dưới.

```java
DEFAULT_PAGE_SIZE
MAX_LOGIN_ATTEMPTS
ACCESS_TOKEN_EXPIRE_SECONDS
```

---

### 5.5 Boolean

Tên biến Boolean nên bắt đầu bằng:

- `is`
- `has`
- `can`
- `should`

Ví dụ:

```java
isRead
isDefault
hasPermission
canDelete
```

---

## 6. Entity

### 6.1 Quy tắc chung

- Tên Entity là danh từ số ít.
- Tên bảng là danh từ số nhiều.
- Không trả Entity trực tiếp ra API.
- Quan hệ `@ManyToOne` mặc định dùng `FetchType.LAZY`.
- Enum lưu bằng `EnumType.STRING`.
- Tiền tệ dùng `BigDecimal`.
- Không dùng `double` hoặc `float` cho tiền.
- Không đặt validation của API trực tiếp lên Entity nếu không cần thiết.

---

### 6.2 Ví dụ Entity

```java
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String note;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
```

---

### 6.3 Không dùng `@Data` bừa bãi

Không khuyến nghị:

```java
@Data
@Entity
public class Transaction {
}
```

Lý do:

- `@Data` tự sinh `equals()` và `hashCode()`.
- Có thể gây lỗi với quan hệ JPA.
- Có thể tạo vòng lặp trong `toString()`.
- Có thể vô tình log dữ liệu không cần thiết.

Ưu tiên:

```java
@Getter
@Setter
@NoArgsConstructor
```

---

### 6.4 Enum

```java
public enum TransactionType {
    INCOME,
    EXPENSE
}
```

Entity phải map enum bằng:

```java
@Enumerated(EnumType.STRING)
```

Không dùng:

```java
EnumType.ORDINAL
```

vì thứ tự enum có thể thay đổi.

---

## 7. DTO

### 7.1 Phân loại DTO

DTO được chia thành:

```text
request
response
```

Ví dụ:

```text
CreateTransactionRequest
UpdateTransactionRequest
TransactionResponse
```

Không dùng một DTO chung cho mọi nghiệp vụ.

---

### 7.2 Request DTO

Ưu tiên sử dụng `record` với Java 21.

```java
public record CreateTransactionRequest(

        @NotNull(message = "Danh mục không được để trống")
        Long categoryId,

        @NotNull(message = "Loại giao dịch không được để trống")
        TransactionType type,

        @NotNull(message = "Số tiền không được để trống")
        @DecimalMin(
                value = "0.01",
                message = "Số tiền phải lớn hơn 0"
        )
        BigDecimal amount,

        @Size(
                max = 500,
                message = "Ghi chú không được vượt quá 500 ký tự"
        )
        String note,

        @NotNull(message = "Ngày giao dịch không được để trống")
        @PastOrPresent(
                message = "Ngày giao dịch không được ở tương lai"
        )
        LocalDate transactionDate
) {
}
```

---

### 7.3 Response DTO

```java
public record TransactionResponse(
        Long id,
        Long categoryId,
        String categoryName,
        TransactionType type,
        BigDecimal amount,
        String note,
        LocalDate transactionDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
```

---

### 7.4 Quy tắc DTO

- Không có `userId` trong Request DTO.
- Không trả `passwordHash`.
- Không trả `tokenHash`.
- Không trả Entity lồng nhau.
- Chỉ trả dữ liệu Client thực sự cần.
- Request và Response không dùng chung một class.
- Validation đặt tại Request DTO.

---

## 8. Controller

### 8.1 Trách nhiệm

Controller chỉ:

- Nhận HTTP Request.
- Validate DTO.
- Lấy người dùng hiện tại.
- Gọi Service.
- Trả HTTP Response.

Controller không:

- Chứa business logic.
- Gọi Repository.
- Thực hiện query Database.
- Tính toán số liệu.
- Tự xử lý exception bằng `try-catch` nếu đã có Global Handler.

---

### 8.2 Ví dụ Controller

```java
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        TransactionResponse response =
                transactionService.create(principal.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Tạo giao dịch thành công",
                        response
                ));
    }
}
```

---

### 8.3 Quy tắc Controller

- Dùng `@RestController`.
- Đặt base path bằng `@RequestMapping`.
- Dùng constructor injection.
- Dùng `@Valid` cho Request DTO.
- Dùng `ResponseEntity` khi cần kiểm soát HTTP Status.
- Không trả trực tiếp `Map<String, Object>`.
- Không viết response thủ công lặp lại ở nhiều Controller.

---

## 9. Service

### 9.1 Trách nhiệm

Service xử lý:

- Business Rules.
- Kiểm tra quyền sở hữu.
- Gọi Repository.
- Transaction boundary.
- Cache.
- Gọi dịch vụ ngoài.
- Phối hợp nhiều module.

---

### 9.2 Interface

```java
public interface TransactionService {

    TransactionResponse create(
            Long userId,
            CreateTransactionRequest request
    );

    TransactionResponse getById(
            Long userId,
            Long transactionId
    );

    void softDelete(
            Long userId,
            Long transactionId
    );
}
```

---

### 9.3 Implementation

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse create(
            Long userId,
            CreateTransactionRequest request
    ) {
        Category category = categoryRepository
                .findAccessibleCategory(userId, request.categoryId())
                .orElseThrow(() -> new AppException(
                        ErrorCode.CATEGORY_NOT_FOUND
                ));

        validateCategoryType(category, request.type());

        Transaction transaction =
                transactionMapper.toEntity(request);

        transaction.setCategory(category);

        Transaction saved = transactionRepository.save(transaction);

        return transactionMapper.toResponse(saved);
    }
}
```

---

### 9.4 Quy tắc Service

- Class triển khai có hậu tố `Impl` nếu dùng interface.
- Dùng `@Transactional(readOnly = true)` ở cấp class khi phần lớn method chỉ đọc.
- Method ghi dữ liệu dùng `@Transactional`.
- Không trả Entity ra Controller.
- Không trả `Optional` từ Service ra Controller.
- Không bắt `Exception` chung rồi bỏ qua lỗi.
- Không sử dụng `null` để biểu diễn lỗi nghiệp vụ.
- Dùng `AppException` và `ErrorCode`.

---

## 10. Repository

### 10.1 Quy tắc chung

Repository sử dụng Spring Data JPA.

```java
public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
                JpaSpecificationExecutor<Transaction> {
}
```

Repository chỉ xử lý:

- CRUD.
- Query.
- Pagination.
- Projection.
- Specification.

Không xử lý business logic.

---

### 10.2 Tên Query Method

```java
Optional<Transaction> findByIdAndUserIdAndDeletedAtIsNull(
        Long id,
        Long userId
);
```

Tên method phải rõ ràng.

Nếu tên method quá dài, sử dụng:

- `@Query`
- Specification
- Custom Repository

---

### 10.3 Query tổng hợp

```java
@Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.user.id = :userId
      and t.type = :type
      and t.transactionDate between :fromDate and :toDate
      and t.deletedAt is null
""")
BigDecimal calculateTotalAmount(
        Long userId,
        TransactionType type,
        LocalDate fromDate,
        LocalDate toDate
);
```

---

### 10.4 Quy tắc Repository

- Không dùng native query khi JPQL có thể đáp ứng.
- Native query chỉ dùng cho truy vấn tổng hợp phức tạp hoặc tối ưu đặc biệt.
- Query luôn lọc theo `userId` với dữ liệu cá nhân.
- Query Transaction phải loại dữ liệu đã Soft Delete.
- Không gọi Repository từ Controller.
- Không dùng `findAll()` cho dữ liệu cá nhân mà thiếu điều kiện người dùng.

---

## 11. Mapper

Khuyến nghị sử dụng MapStruct.

```java
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toEntity(CreateTransactionRequest request);

    @Mapping(
            target = "categoryId",
            source = "category.id"
    )
    @Mapping(
            target = "categoryName",
            source = "category.name"
    )
    TransactionResponse toResponse(Transaction transaction);
}
```

### Quy tắc Mapper

- Mapper chỉ chuyển đổi dữ liệu.
- Không gọi Repository.
- Không chứa business logic.
- Không tự kiểm tra quyền.
- Không thực hiện truy vấn Database.
- Mapping đặc biệt phải được khai báo rõ ràng.

---

## 12. Validation

Validation được chia thành hai nhóm.

### 12.1 Validation định dạng

Thực hiện ở DTO:

- Bắt buộc.
- Độ dài.
- Email.
- Số tiền dương.
- Ngày không ở tương lai.

Ví dụ:

```java
@NotBlank
@Email
private String email;
```

---

### 12.2 Validation nghiệp vụ

Thực hiện ở Service:

- Email đã tồn tại.
- Category thuộc người dùng.
- Category đúng loại.
- Budget bị trùng.
- Transaction đã bị xóa.
- Người dùng có quyền truy cập.

Không cố đưa toàn bộ validation nghiệp vụ vào DTO.

---

## 13. Exception Handling

### 13.1 AppException

```java
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

---

### 13.2 ErrorCode

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    TRANSACTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TRANSACTION_NOT_FOUND",
            "Không tìm thấy giao dịch"
    ),

    CATEGORY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CATEGORY_NOT_FOUND",
            "Không tìm thấy danh mục"
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

---

### 13.3 GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(
            AppException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiErrorResponse response = ApiErrorResponse.of(
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}
```

---

### 13.4 Quy tắc Exception

- Không trả stack trace cho Client.
- Không trả message kỹ thuật của Database.
- Không dùng một mã lỗi chung cho mọi trường hợp.
- Không dùng `RuntimeException` trực tiếp cho lỗi nghiệp vụ.
- Mọi lỗi nghiệp vụ phải có `ErrorCode`.
- Validation error phải chỉ rõ field bị lỗi.

---

## 14. API Response

### 14.1 Response thành công

```java
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp,
        String traceId
) {
}
```

Ví dụ:

```json
{
  "success": true,
  "message": "Tạo giao dịch thành công",
  "data": {},
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "2d8f91a1"
}
```

---

### 14.2 Response lỗi

```json
{
  "success": false,
  "message": "Không tìm thấy giao dịch",
  "errorCode": "TRANSACTION_NOT_FOUND",
  "errors": null,
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "2d8f91a1"
}
```

---

## 15. Logging

Sử dụng:

```java
@Slf4j
```

Ví dụ:

```java
log.info(
        "Created transaction. userId={}, transactionId={}",
        userId,
        transaction.getId()
);
```

### Nên ghi log

- Đăng nhập thành công hoặc thất bại.
- Tạo giao dịch quan trọng.
- Vượt ngân sách.
- Gọi AI Provider.
- AI Provider bị lỗi.
- Exception ngoài dự kiến.

### Không ghi log

- Mật khẩu.
- Access Token.
- Refresh Token.
- API Key.
- Toàn bộ Prompt chứa dữ liệu nhạy cảm.
- Thông tin cá nhân không cần thiết.

---

### 15.1 Không nối chuỗi thủ công

Không nên:

```java
log.info("User " + userId + " created transaction");
```

Nên:

```java
log.info(
        "User created transaction. userId={}",
        userId
);
```

---

## 16. Transaction

### 16.1 Nghiệp vụ ghi dữ liệu

Method ghi dữ liệu sử dụng:

```java
@Transactional
```

Ví dụ:

```java
@Transactional
public TransactionResponse create(...) {
}
```

---

### 16.2 Nghiệp vụ chỉ đọc

```java
@Transactional(readOnly = true)
```

---

### 16.3 Không đặt Transaction ở Controller

Không nên:

```java
@PostMapping
@Transactional
public ResponseEntity<?> create() {
}
```

Transaction boundary phải nằm tại Service.

---

### 16.4 Gọi API bên ngoài

Không nên giữ Database Transaction mở quá lâu trong khi chờ AI Provider.

Nên tách:

1. Đọc dữ liệu.
2. Tạo context.
3. Gọi AI.
4. Mở transaction ngắn để lưu kết quả.

---

## 17. Xử lý tiền tệ

Mọi dữ liệu tiền sử dụng:

```java
BigDecimal
```

Ví dụ:

```java
BigDecimal totalExpense =
        transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
```

So sánh:

```java
if (amount.compareTo(BigDecimal.ZERO) <= 0) {
    throw new AppException(
            ErrorCode.TRANSACTION_INVALID_AMOUNT
    );
}
```

Không dùng:

```java
amount > 0
```

với `BigDecimal`.

---

## 18. Ngày và giờ

| Dữ liệu | Kiểu Java |
|---|---|
| Ngày giao dịch | `LocalDate` |
| Tháng ngân sách | `YearMonth` tại DTO/Service |
| Thời điểm tạo | `LocalDateTime` |
| Thời điểm hết hạn token | `LocalDateTime` hoặc `Instant` |

Khuyến nghị:

- Dùng `LocalDate` cho ngày thu chi.
- Dùng `Instant` cho token nếu hệ thống cần chuẩn hóa UTC.
- Không dùng `java.util.Date`.
- Không tự lưu ngày dưới dạng `String`.

---

## 19. Optional

Repository có thể trả:

```java
Optional<Transaction>
```

Service không nên trả `Optional` ra Controller.

Nên xử lý ngay:

```java
Transaction transaction = repository
        .findByIdAndUserIdAndDeletedAtIsNull(id, userId)
        .orElseThrow(() -> new AppException(
                ErrorCode.TRANSACTION_NOT_FOUND
        ));
```

---

## 20. Null

- Hạn chế trả `null`.
- Collection rỗng phải trả `List.of()` hoặc danh sách rỗng.
- Không trả `null` thay cho danh sách.
- Dùng `Optional` tại tầng Repository khi phù hợp.
- Không lạm dụng `Optional` làm field trong Entity hoặc DTO.

---

## 21. Comment

Chỉ comment khi cần giải thích **lý do**, không comment điều code đã thể hiện rõ.

Không nên:

```java
// Lấy user theo id
User user = userRepository.findById(id);
```

Nên:

```java
// Refresh token is rotated to prevent reuse after it has been exchanged.
revokeCurrentToken(token);
```

JavaDoc nên dùng cho:

- Public service interface.
- Utility phức tạp.
- Thuật toán hoặc business rule khó hiểu.
- Client tích hợp bên ngoài.

---

## 22. Import

Không dùng wildcard import.

Không nên:

```java
import java.util.*;
```

Nên:

```java
import java.util.List;
import java.util.Optional;
```

Xóa import không sử dụng trước khi commit.

---

## 23. Dependency Injection

Ưu tiên Constructor Injection.

```java
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl {

    private final BudgetRepository budgetRepository;
}
```

Không dùng Field Injection:

```java
@Autowired
private BudgetRepository budgetRepository;
```

---

## 24. Configuration

Không hard-code cấu hình.

Không nên:

```java
String apiKey = "sk-...";
```

Nên dùng biến môi trường:

```yaml
ai:
  api-key: ${AI_API_KEY}
```

Map cấu hình bằng:

```java
@ConfigurationProperties(prefix = "ai")
```

Không commit:

- API Key.
- JWT Secret.
- Database Password.
- File `.env` thật.

---

## 25. Flyway Migration

Tên file:

```text
V1__create_initial_schema.sql
V2__insert_default_categories.sql
V3__add_transaction_indexes.sql
```

Quy tắc:

- Không sửa migration đã chạy trên môi trường chung.
- Khi thay đổi schema, tạo migration mới.
- Tên migration phải thể hiện rõ nội dung.
- Không dùng Hibernate tự động sửa schema trong Production.

Production:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

---

## 26. Kiểm thử

### 26.1 Tên method test

```java
createTransaction_shouldReturnResponse_whenRequestIsValid()
```

Hoặc:

```java
shouldCreateTransactionWhenRequestIsValid()
```

Chọn một cách và dùng thống nhất.

---

### 26.2 Cấu trúc Given – When – Then

```java
@Test
void shouldCreateTransactionWhenRequestIsValid() {
    // Given
    CreateTransactionRequest request = createValidRequest();

    // When
    TransactionResponse response =
            transactionService.create(USER_ID, request);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.amount()).isEqualByComparingTo("150000");
}
```

---

### 26.3 Nội dung cần kiểm thử

- Luồng thành công.
- Validation.
- Không tìm thấy dữ liệu.
- Truy cập dữ liệu người khác.
- Business Rule.
- Soft Delete.
- Query tổng hợp.
- AI Provider lỗi.
- Refresh Token hết hạn.

---

## 27. Git Commit Convention

Cấu trúc:

```text
<type>(<scope>): <description>
```

Ví dụ:

```text
feat(transaction): add create transaction API

fix(budget): prevent duplicate monthly budget

refactor(auth): simplify refresh token rotation

test(category): add category service tests

docs(api): update transaction endpoints
```

Các loại thường dùng:

| Type | Ý nghĩa |
|---|---|
| `feat` | Tính năng mới |
| `fix` | Sửa lỗi |
| `refactor` | Cải tiến code |
| `test` | Thêm hoặc sửa test |
| `docs` | Cập nhật tài liệu |
| `chore` | Cấu hình, dependency |
| `perf` | Cải thiện hiệu năng |

---

## 28. Checklist trước khi tạo Pull Request

### Kiến trúc

- [ ] Code nằm đúng module.
- [ ] Controller không gọi Repository.
- [ ] Business logic nằm tại Service.
- [ ] Không trả Entity trực tiếp.

### Bảo mật

- [ ] `userId` lấy từ SecurityContext.
- [ ] Đã kiểm tra quyền sở hữu dữ liệu.
- [ ] Không log thông tin nhạy cảm.
- [ ] Không hard-code secret.

### Dữ liệu

- [ ] Tiền sử dụng `BigDecimal`.
- [ ] Enum lưu bằng `STRING`.
- [ ] Query Transaction lọc Soft Delete.
- [ ] Query dữ liệu cá nhân có điều kiện `userId`.

### API

- [ ] Request DTO có validation.
- [ ] HTTP Status phù hợp.
- [ ] Response theo format chung.
- [ ] Error Code rõ ràng.
- [ ] Swagger được cập nhật.

### Kiểm thử

- [ ] Có test cho luồng thành công.
- [ ] Có test cho trường hợp lỗi.
- [ ] Có test quyền sở hữu nếu cần.
- [ ] Tất cả test đều chạy thành công.

### Mã nguồn

- [ ] Không có import thừa.
- [ ] Không có code bị comment nhưng không sử dụng.
- [ ] Không có tên biến khó hiểu.
- [ ] Không có logic lặp lại không cần thiết.
- [ ] Code đã được format.

---

## 29. Tổng kết

SmartSpend sử dụng các quy ước lập trình nhằm đảm bảo mã nguồn rõ ràng, nhất quán và dễ bảo trì.

Những nguyên tắc quan trọng nhất gồm:

1. Tổ chức package theo Feature.
2. Controller chỉ xử lý HTTP.
3. Service chứa Business Logic.
4. Repository chỉ truy cập dữ liệu.
5. Không trả Entity qua API.
6. Dùng `BigDecimal` cho tiền.
7. Lấy `userId` từ SecurityContext.
8. Kiểm tra quyền sở hữu dữ liệu.
9. Chuẩn hóa Exception và Response.
10. Không lưu hoặc ghi log dữ liệu nhạy cảm.

Tất cả mã nguồn mới cần tuân thủ tài liệu này trước khi được tích hợp vào dự án.