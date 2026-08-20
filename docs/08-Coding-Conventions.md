# Quy ước lập trình

## SmartSpend

### AI Personal Finance Manager

---

# 1. Giới thiệu

## 1.1 Mục đích

Tài liệu này định nghĩa các quy ước lập trình được sử dụng trong dự án **SmartSpend**.

Mục tiêu:

* Code dễ đọc.
* Code dễ bảo trì.
* Code dễ kiểm thử.
* Cấu trúc nhất quán giữa các module.
* Hạn chế lỗi do cách viết code không đồng nhất.
* Giữ đúng kiến trúc đã thống nhất.
* Hạn chế phụ thuộc giữa các module.
* Đảm bảo các business rule được xử lý đúng tầng.

Mọi mã nguồn mới nên tuân thủ tài liệu này.

---

# 2. Công nghệ và phiên bản

| Thành phần         | Công nghệ               |
| ------------------ | ----------------------- |
| Ngôn ngữ           | Java 21                 |
| Framework          | Spring Boot 3.5         |
| Web                | Spring Web MVC          |
| Security           | Spring Security         |
| ORM                | Spring Data JPA         |
| Validation         | Jakarta Bean Validation |
| Database           | MySQL 8                 |
| Cache / Rate Limit | Redis                   |
| Migration          | Flyway                  |
| JWT                | JJWT                    |
| Build Tool         | Maven                   |
| Mapping            | MapStruct               |
| Logging            | SLF4J + Logback         |
| API Docs           | Springdoc OpenAPI       |
| Test               | JUnit 5 + Mockito       |
| API Testing        | Postman                 |

---

# 3. Nguyên tắc chung

| Nguyên tắc            | Mô tả                                                             |
| --------------------- | ----------------------------------------------------------------- |
| Consistency           | Cách viết nhất quán quan trọng hơn viết ngắn                      |
| Readability           | Ưu tiên code dễ đọc                                               |
| Single Responsibility | Mỗi class có trách nhiệm rõ ràng                                  |
| Fail Fast             | Kiểm tra lỗi càng sớm càng tốt                                    |
| DTO Boundary          | Không nhận hoặc trả Entity trực tiếp qua API                      |
| Explicit Code         | Tránh logic ngầm khó theo dõi                                     |
| Ownership First       | Mọi dữ liệu cá nhân phải kiểm tra quyền sở hữu                    |
| Security Context      | Không nhận `userId` từ Request nếu có thể lấy từ Security Context |
| No Sensitive Logging  | Không log password, token hoặc API Key                            |
| BigDecimal for Money  | Tiền phải dùng `BigDecimal`                                       |
| Flyway First          | Schema do Flyway quản lý                                          |
| Soft Delete Aware     | Query Transaction phải loại dữ liệu đã xóa                        |

---

# 4. Cấu trúc package

SmartSpend sử dụng **Package by Feature**.

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

Ví dụ module Transaction:

```text
transaction/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── mapper/
├── repository/
└── service/
```

`TransactionSpecifications` hiện được đặt trong:

```text
transaction/repository/
```

Không tạo package riêng chỉ để chứa một file nếu chưa thực sự cần.

## Quy tắc

* Package viết thường.
* Không dùng `_` trong tên package.
* Không đặt business logic trong `config`.
* Không đặt business logic trong `util`.
* Controller module này không gọi Controller module khác.
* Hạn chế gọi Repository module khác nếu Service của module đó có thể cung cấp nghiệp vụ cần thiết.
* Không tạo package rỗng.

---

# 5. Quy ước đặt tên

## 5.1 Class

PascalCase.

```java
TransactionController
TransactionService
TransactionServiceImpl
TransactionRepository
CreateTransactionRequest
TransactionResponse
```

---

## 5.2 Method

camelCase.

```java
create()
update()
delete()
getById()
getAll()
generateAccessToken()
findAvailableCategories()
```

Tên method phải thể hiện hành động hoặc ý nghĩa.

Không nên:

```java
process()
handle()
execute()
doSomething()
```

trừ khi ngữ cảnh thực sự phù hợp.

---

## 5.3 Biến

camelCase.

```java
currentUser
transactionDate
passwordHash
refreshToken
categoryId
totalExpense
```

Tránh:

```java
u
tx
amt
obj
data1
```

---

## 5.4 Hằng số

UPPER_SNAKE_CASE.

```java
BEARER_PREFIX
LOGIN_ATTEMPT_PREFIX
REFRESH_TOKEN_BYTES
TRACE_ID_KEY
```

---

## 5.5 Boolean

Ưu tiên tên thể hiện trạng thái.

```java
isRead
isDeleted
isActive
isRevoked
isEnabled
isSystemDefault
```

Method kiểm tra có thể dùng:

```java
isOwnedBy()
isActive()
isExpired()
isRevoked()
```

---

# 6. Entity

## 6.1 Quy tắc chung

* Tên Entity số ít.
* Tên bảng số nhiều.
* Không trả Entity trực tiếp qua API.
* Quan hệ `@ManyToOne` dùng `FetchType.LAZY`.
* Enum dùng `EnumType.STRING`.
* Tiền dùng `BigDecimal`.
* Không dùng `double` hoặc `float` cho tiền.
* Không đặt Jakarta Validation dành cho HTTP Request lên Entity nếu không cần.
* Entity phải map đúng tên cột Flyway.
* Không dùng `@Data`.
* Không dùng `@ToString` toàn Entity có quan hệ JPA.
* `equals()` và `hashCode()` phải được kiểm soát rõ ràng.
* Constructor không tham số dành cho JPA nên dùng `protected`.

---

## 6.2 Ví dụ Entity

```java
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 20
    )
    private TransactionType type;

    @Column(
            name = "amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "merchant",
            length = 255
    )
    private String merchant;

    @Column(
            name = "payment_method",
            length = 20
    )
    private String paymentMethod;

    @Column(
            name = "note",
            length = 500
    )
    private String note;

    @Column(
            name = "transaction_date",
            nullable = false
    )
    private LocalDate transactionDate;

    @CreatedDate
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Transaction() {
    }
}
```

---

## 6.3 Không dùng `@Data`

Không dùng:

```java
@Data
@Entity
public class Transaction {
}
```

Lý do:

* Sinh `equals()`.
* Sinh `hashCode()`.
* Sinh `toString()`.
* Dễ truy cập quan hệ LAZY ngoài ý muốn.
* Có thể tạo vòng lặp.
* Có thể log dữ liệu không cần thiết.

Entity hiện tại ưu tiên getter/setter rõ ràng hoặc Lombok có chọn lọc nếu thực sự cần.

---

## 6.4 Enum

```java
public enum TransactionType {
    INCOME,
    EXPENSE
}
```

Map:

```java
@Enumerated(EnumType.STRING)
```

Không dùng:

```java
EnumType.ORDINAL
```

---

# 7. JPA Relationship

## 7.1 Many-to-One

Mặc định:

```java
@ManyToOne(fetch = FetchType.LAZY)
```

Không dùng `EAGER` nếu không có lý do rõ ràng.

Ví dụ:

```java
@ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
)
@JoinColumn(
        name = "category_id",
        nullable = false
)
private Category category;
```

---

## 7.2 Quan hệ Category mặc định

Category mặc định dùng chung:

```text
user_id = NULL
is_default = true
```

Do đó:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

không được khai báo:

```java
nullable = false
```

---

# 8. Auditing

Các Entity cần audit dùng:

```java
@EntityListeners(AuditingEntityListener.class)
```

và:

```java
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime updatedAt;
```

`JpaAuditingConfig` chịu trách nhiệm bật:

```java
@EnableJpaAuditing
```

Không tự set `createdAt` hoặc `updatedAt` trong Service nếu Spring Auditing đã xử lý.

---

# 9. DTO

## 9.1 Phân loại

```text
dto/
├── request/
└── response/
```

Ví dụ:

```text
CreateCategoryRequest
UpdateCategoryRequest
CategoryResponse

RegisterRequest
LoginRequest
AuthResponse
```

---

## 9.2 Request DTO

Ưu tiên Java 21 `record`.

```java
public record CreateTransactionRequest(

        @NotNull(
                message = "Category is required"
        )
        Long categoryId,

        @NotNull(
                message = "Transaction type is required"
        )
        TransactionType type,

        @NotNull(
                message = "Amount is required"
        )
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than 0"
        )
        BigDecimal amount,

        @Size(
                max = 500,
                message = "Note must not exceed 500 characters"
        )
        String note,

        @NotNull(
                message = "Transaction date is required"
        )
        @PastOrPresent(
                message = "Transaction date must not be in the future"
        )
        LocalDate transactionDate

) {
}
```

---

## 9.3 Response DTO

```java
public record TransactionResponse(

        Long id,

        Long categoryId,

        String categoryName,

        TransactionType type,

        BigDecimal amount,

        String merchant,

        String paymentMethod,

        String note,

        LocalDate transactionDate,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
```

---

## 9.4 Quy tắc DTO

* Request không có `userId`.
* Request không có `isDefault` nếu client không được quyền thiết lập.
* Không trả `passwordHash`.
* Không trả `tokenHash`.
* Không trả Entity lồng nhau.
* Request và Response là class khác nhau.
* Validation nằm tại Request.
* Response chỉ chứa dữ liệu client cần.

---

# 10. Mapper

MapStruct dùng cho mapping cơ bản.

```java
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toResponse(
            Transaction transaction
    );
}
```

Mapping phức tạp có thể khai báo rõ:

```java
@Mapping(
        target = "categoryId",
        source = "category.id"
)
@Mapping(
        target = "categoryName",
        source = "category.name"
)
TransactionResponse toResponse(
        Transaction transaction
);
```

## Quy tắc

Mapper:

* Không query database.
* Không gọi Repository.
* Không kiểm tra ownership.
* Không chứa business rule.
* Không mã hóa password.
* Không sinh JWT.
* Chỉ chịu trách nhiệm chuyển đổi dữ liệu.

Ví dụ Authentication:

```text
AuthService
→ PasswordEncoder

UserMapper
→ map DTO ↔ Entity
```

---

# 11. Controller

## 11.1 Trách nhiệm

Controller chỉ:

* Nhận HTTP Request.
* Deserialize JSON.
* Validate Request DTO.
* Đọc thông tin HTTP khi cần.
* Gọi Service.
* Trả HTTP Response.

Controller không:

* Gọi Repository.
* Viết SQL.
* Chứa business rule.
* Kiểm tra ownership thủ công.
* Hash password.
* Sinh JWT.
* Tính toán nghiệp vụ phức tạp.

---

## 11.2 Constructor Injection

Dùng constructor injection.

```java
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService =
                categoryService;
    }
}
```

Không dùng:

```java
@Autowired
private CategoryService categoryService;
```

---

## 11.3 Ví dụ

```java
@PostMapping
public ResponseEntity<CategoryResponse> create(
        @Valid
        @RequestBody
        CreateCategoryRequest request
) {
    CategoryResponse response =
            categoryService.create(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}
```

---

# 12. Security Context

SmartSpend không nhận `userId` từ Request.

Không nên:

```java
public record CreateTransactionRequest(
        Long userId,
        Long categoryId
) {
}
```

Không nên để client gửi:

```json
{
  "userId": 10
}
```

User hiện tại lấy từ Spring Security:

```java
Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();
```

Sau đó:

```java
UserPrincipal principal =
        (UserPrincipal)
                authentication.getPrincipal();

Long userId = principal.getId();
```

Service chịu trách nhiệm lấy current user khi cần.

---

# 13. Service

## 13.1 Trách nhiệm

Service xử lý:

* Business Rule.
* Ownership.
* Transaction boundary.
* Repository coordination.
* Password hashing.
* JWT coordination.
* Refresh Token rotation.
* Redis rate limit.
* Cache invalidation.
* External service coordination.

---

## 13.2 Interface

Ví dụ:

```java
public interface CategoryService {

    List<CategoryResponse> getAll(
            CategoryType type
    );

    CategoryResponse getById(
            Long categoryId
    );

    CategoryResponse create(
            CreateCategoryRequest request
    );

    CategoryResponse update(
            Long categoryId,
            UpdateCategoryRequest request
    );

    void delete(
            Long categoryId
    );
}
```

Không truyền `userId` từ Controller nếu Service có thể lấy từ Security Context.

---

## 13.3 Implementation

```java
@Service
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            CategoryMapper categoryMapper
    ) {
        this.categoryRepository =
                categoryRepository;

        this.userRepository =
                userRepository;

        this.categoryMapper =
                categoryMapper;
    }
}
```

---

## 13.4 Transaction Boundary

Method chỉ đọc:

```java
@Transactional(readOnly = true)
```

Method ghi:

```java
@Transactional
```

Ví dụ:

```java
@Override
@Transactional
public CategoryResponse create(
        CreateCategoryRequest request
) {
}
```

---

## 13.5 Quy tắc Service

* Không trả Entity ra Controller.
* Không trả `Optional`.
* Không dùng `null` làm business error.
* Dùng `AppException`.
* Dùng `ErrorCode`.
* Ownership được kiểm tra trước update/delete.
* Không giữ DB transaction lâu khi gọi API ngoài.

---

# 14. Repository

Repository dùng Spring Data JPA.

```java
public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {
}
```

Repository chịu trách nhiệm:

* CRUD.
* Query.
* Pagination.
* Projection.
* Specification.

Repository không chịu trách nhiệm:

* Business Rule.
* HTTP.
* Security response.
* Mapping API DTO.

---

## 14.1 Query Method

```java
Optional<Transaction>
findByIdAndUserIdAndDeletedAtIsNull(
        Long transactionId,
        Long userId
);
```

Tên method phải rõ ràng.

Nếu quá dài:

* `@Query`.
* Specification.
* Custom Repository.

---

## 14.2 Query Ownership

Dữ liệu cá nhân phải lọc theo User.

Không dùng:

```java
findById(id)
```

nếu nghiệp vụ yêu cầu ownership.

Ưu tiên:

```java
findByIdAndUserIdAndDeletedAtIsNull(
        id,
        userId
)
```

---

# 15. Specification

Transaction có nhiều filter nên sử dụng Specification.

```java
public final class TransactionSpecifications {

    private TransactionSpecifications() {
    }
}
```

Mỗi Specification đại diện cho một điều kiện:

```text
belongsToUser
notDeleted
hasType
hasCategory
transactionDateFrom
transactionDateTo
containsKeyword
```

Ví dụ:

```java
public static Specification<Transaction>
hasType(TransactionType type) {

    if (type == null) {
        return null;
    }

    return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(
                    root.get("type"),
                    type
            );
}
```

Sau đó ghép:

```java
Specification<Transaction> specification =
        TransactionSpecifications.filter(
                userId,
                type,
                categoryId,
                fromDate,
                toDate,
                keyword
        );
```

và:

```java
transactionRepository.findAll(
        specification,
        pageable
);
```

## Quy tắc

* Specification chỉ xây query.
* Không chứa business rule.
* Luôn có điều kiện ownership.
* Transaction query phải có `notDeleted()`.
* Filter `null` không thêm điều kiện.

---

# 16. Soft Delete

Transaction không xóa vật lý.

Entity:

```java
@Column(name = "deleted_at")
private LocalDateTime deletedAt;
```

Method:

```java
public void softDelete() {
    this.deletedAt =
            LocalDateTime.now();
}
```

Service:

```java
transaction.softDelete();

transactionRepository.save(
        transaction
);
```

Không dùng:

```java
transactionRepository.delete(transaction);
```

cho Transaction.

Query phải loại:

```text
deleted_at IS NOT NULL
```

bằng điều kiện:

```text
deleted_at IS NULL
```

---

# 17. Validation

Validation chia 2 loại.

## 17.1 Validation định dạng

Đặt tại Request DTO.

Ví dụ:

```java
@NotBlank
@Email
String email
```

hoặc:

```java
@DecimalMin("0.01")
BigDecimal amount
```

---

## 17.2 Validation nghiệp vụ

Đặt tại Service.

Ví dụ:

* Email đã tồn tại.
* Category tồn tại.
* Category thuộc user.
* Category là mặc định.
* Transaction Type khớp Category Type.
* Budget trùng tháng.
* Refresh Token còn hiệu lực.
* Transaction có quyền truy cập.

Không cố đưa business validation vào DTO.

---

# 18. Exception Handling

## 18.1 AppException

Business error:

```java
throw new AppException(
        ErrorCode.CATEGORY_NOT_FOUND
);
```

Không dùng:

```java
throw new RuntimeException(
        "Category not found"
);
```

---

## 18.2 ErrorCode

Mỗi lỗi quan trọng có mã riêng.

Ví dụ:

```java
CATEGORY_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "CATEGORY_NOT_FOUND",
        "Không tìm thấy danh mục"
),

CATEGORY_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "CATEGORY_ALREADY_EXISTS",
        "Danh mục đã tồn tại"
),

AUTH_INVALID_CREDENTIALS(
        HttpStatus.UNAUTHORIZED,
        "AUTH_INVALID_CREDENTIALS",
        "Email hoặc mật khẩu không chính xác"
);
```

---

## 18.3 Quy tắc

* Không trả stack trace.
* Không trả SQL error.
* Không dùng một ErrorCode chung cho mọi nghiệp vụ.
* Validation error chỉ rõ field.
* HTTP Status phải phù hợp.

---

# 19. Authentication

## 19.1 Password

Password chỉ tồn tại dạng plaintext ở Request ngắn hạn.

Service:

```java
String passwordHash =
        passwordEncoder.encode(
                request.password()
        );
```

Database chỉ lưu:

```text
password_hash
```

Không log password.

---

## 19.2 Refresh Token

Raw Refresh Token:

```text
Client
```

Database chỉ lưu:

```text
SHA-256(rawRefreshToken)
```

Không lưu raw token.

---

## 19.3 JWT

Access Token:

```text
Bearer JWT
```

JWT chứa tối thiểu:

```text
sub
email
role
iat
exp
```

Không chứa:

```text
password
passwordHash
refreshToken
```

---

## 19.4 Stateless

Security sử dụng:

```java
SessionCreationPolicy.STATELESS
```

Không dùng HTTP Session để lưu Authentication.

---

# 20. Redis

Redis là thành phần hỗ trợ.

Dùng cho:

```text
Rate Limit
Cache
TTL Data
```

Không dùng Redis làm nguồn dữ liệu chính.

Ví dụ Login Rate Limit:

```text
auth:login-attempt:<email>
```

Nếu Redis lỗi, nghiệp vụ có thể áp dụng fallback tùy trường hợp.

Không để Redis lỗi làm crash toàn bộ ứng dụng nếu Redis chỉ là lớp bảo vệ bổ sung.

---

# 21. API Response

SmartSpend hướng tới response thống nhất bằng `ApiResponse`.

Không trả:

```java
Map<String, Object>
```

thủ công ở nhiều Controller.

Không trả Entity.

Response lỗi phải có:

```text
success
message
errorCode
timestamp
traceId
```

Response thành công nên có:

```text
success
message
data
timestamp
traceId
```

---

# 22. Trace ID

Mỗi Request có Trace ID.

Header:

```text
X-Trace-Id
```

Trace ID được đưa vào MDC.

Không tự tạo Trace ID riêng ở từng Controller.

---

# 23. Logging

Dùng SLF4J.

```java
private static final Logger log =
        LoggerFactory.getLogger(
                AuthServiceImpl.class
        );
```

Hoặc Lombok `@Slf4j` nếu module thống nhất sử dụng.

Nên log:

* Login thất bại.
* Login rate limit.
* Business event quan trọng.
* Redis lỗi.
* AI Provider lỗi.
* Unexpected system error.

Không log:

* Password.
* Access Token.
* Refresh Token.
* Token Hash.
* API Key.
* Secret.
* Prompt chứa dữ liệu nhạy cảm.

---

## 23.1 Structured Logging

Không:

```java
log.info(
        "User " + userId + " logged in"
);
```

Nên:

```java
log.info(
        "User logged in. userId={}",
        userId
);
```

---

# 24. Tiền tệ

Dùng:

```java
BigDecimal
```

Không:

```java
double
float
```

So sánh:

```java
if (
        amount.compareTo(
                BigDecimal.ZERO
        ) <= 0
) {
}
```

Không:

```java
amount > 0
```

---

# 25. Ngày giờ

| Dữ liệu                  | Java            |
| ------------------------ | --------------- |
| Transaction Date         | `LocalDate`     |
| Created / Updated        | `LocalDateTime` |
| Deleted At               | `LocalDateTime` |
| Refresh Token expiration | `LocalDateTime` |
| JWT internal timestamp   | `Instant`       |

Không lưu ngày bằng String.

---

# 26. Optional

Repository có thể trả:

```java
Optional<Category>
```

Service xử lý ngay:

```java
Category category =
        categoryRepository
                .findAccessibleById(
                        categoryId,
                        userId
                )
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );
```

Service không trả Optional ra Controller.

---

# 27. Null

* Không trả `null` cho Collection.
* Dùng danh sách rỗng.
* Không dùng `Optional` làm field Entity.
* Optional filter có thể dùng `null` trong Specification để bỏ điều kiện.
* Business error không biểu diễn bằng `null`.

---

# 28. Import

Không dùng wildcard.

Không:

```java
import java.util.*;
```

Nên:

```java
import java.util.List;
import java.util.Optional;
```

Xóa import thừa.

---

# 29. Comment

Comment giải thích **vì sao**, không giải thích điều hiển nhiên.

Không:

```java
// Save user
userRepository.save(user);
```

Có thể:

```java
// Redis rate limiting is auxiliary;
// authentication remains available if Redis is temporarily unavailable.
```

JavaDoc dùng cho:

* Public API phức tạp.
* Business rule khó hiểu.
* Utility phức tạp.
* External client.

---

# 30. Configuration

Không hard-code secret.

Không:

```java
String secret = "abc123";
```

Dùng:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
```

Production secret lấy từ environment.

Test có thể dùng secret giả cố định trong:

```text
application-test.yml
```

Không commit:

```text
.env
JWT secret thật
Database password thật
AI API key
```

---

# 31. Flyway Migration

Tên:

```text
V1__create_initial_schema.sql
V2__insert_default_categories.sql
V3__description.sql
```

Quy tắc:

* Không sửa migration đã được dùng chung.
* Thay đổi schema → migration mới.
* Migration name mô tả nội dung.
* Hibernate chỉ validate schema.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

---

# 32. Test

## 32.1 Unit Test

Test Service bằng Mockito.

```java
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
}
```

Không khởi động Spring nếu không cần.

---

## 32.2 Controller Integration Test

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {
}
```

Controller test có thể mock Service:

```java
@MockitoBean
private CategoryService categoryService;
```

---

## 32.3 Test Profile

Integration Test phải dùng:

```java
@ActiveProfiles("test")
```

Không vô tình dùng dev profile.

---

## 32.4 Given – When – Then

```java
@Test
void create_shouldSavePersonalCategory() {

    // Given

    // When

    // Then
}
```

---

## 32.5 Naming

Project hiện ưu tiên:

```text
method_shouldExpectedResult_whenCondition
```

Ví dụ:

```java
register_shouldCreateUser_whenEmailDoesNotExist()

delete_shouldRejectDefaultCategory()

getAll_shouldFilterByType()
```

Giữ cùng phong cách trong module.

---

# 33. Test Data

Entity tạo thủ công trong Unit Test thường chưa có ID vì chưa đi qua database.

Nếu cần ID:

```java
ReflectionTestUtils.setField(
        user,
        "id",
        1L
);
```

Không sửa Entity chỉ để phục vụ Unit Test.

---

# 34. Mockito

Mockito dùng strict stubbing mặc định.

Không thêm stub mà code không sử dụng.

Nếu gặp:

```text
UnnecessaryStubbingException
```

ưu tiên sửa test data hoặc logic stub.

Không dùng `lenient()` chỉ để che lỗi nếu chưa xác định nguyên nhân.

---

# 35. Postman

Mỗi API sau khi code xong cần:

1. Chạy Backend.
2. Test bằng Postman.
3. Kiểm tra HTTP status.
4. Kiểm tra JSON.
5. Kiểm tra Authentication.
6. Kiểm tra Trace ID.
7. Lưu request vào Collection.

---

# 36. Git Commit Convention

```text
<type>(<scope>): <description>
```

Ví dụ:

```text
feat(auth): add JWT authentication

feat(category): add category CRUD

feat(transaction): add transaction specification

fix(config): fix RedisTemplate bean

test(category): add category service tests

docs(roadmap): update transaction progress
```

Type:

| Type       | Ý nghĩa   |
| ---------- | --------- |
| `feat`     | Tính năng |
| `fix`      | Sửa lỗi   |
| `refactor` | Cải tiến  |
| `test`     | Test      |
| `docs`     | Tài liệu  |
| `chore`    | Cấu hình  |
| `perf`     | Hiệu năng |

---

# 37. Checklist trước Pull Request

## Architecture

* [ ] File đúng module.
* [ ] Controller không gọi Repository.
* [ ] Business logic nằm ở Service.
* [ ] Không trả Entity.
* [ ] Mapper không chứa business logic.

## Security

* [ ] `userId` lấy từ Security Context.
* [ ] Ownership đã được kiểm tra.
* [ ] Không log dữ liệu nhạy cảm.
* [ ] Không hard-code secret.
* [ ] Endpoint cần auth không bị `permitAll`.

## Database

* [ ] Entity khớp Flyway.
* [ ] Money dùng `BigDecimal`.
* [ ] Enum dùng STRING.
* [ ] Quan hệ dùng LAZY.
* [ ] Transaction query lọc Soft Delete.
* [ ] Query dữ liệu cá nhân lọc User.

## API

* [ ] DTO có validation.
* [ ] Không nhận `userId`.
* [ ] HTTP Status phù hợp.
* [ ] ErrorCode phù hợp.
* [ ] Swagger được cập nhật.
* [ ] Postman đã test.

## Test

* [ ] Unit Test.
* [ ] Integration Test khi cần.
* [ ] Ownership test.
* [ ] Business Rule test.
* [ ] Test profile đúng.
* [ ] `mvn clean test` thành công.

## Code

* [ ] Không wildcard import.
* [ ] Không import thừa.
* [ ] Không dead code.
* [ ] Không tên biến khó hiểu.
* [ ] Không duplicate logic rõ ràng.
* [ ] Code format thống nhất.

---

# 38. Definition of Done cho code

Một nhóm code chỉ được xem là hoàn thành khi:

```text
Compile
↓
Unit Test
↓
Integration Test nếu cần
↓
Run Backend
↓
Postman
↓
Review
↓
Update Documentation
↓
Git Commit
```

Lệnh kiểm tra cuối:

```powershell
.\mvnw.cmd clean test
```

phải trả:

```text
BUILD SUCCESS
```

---

# 39. Tổng kết

Các nguyên tắc quan trọng nhất của SmartSpend:

1. Package by Feature.
2. Controller chỉ xử lý HTTP.
3. Service chứa business logic.
4. Repository chỉ truy cập dữ liệu.
5. DTO dùng `record`.
6. Không trả Entity qua API.
7. Không nhận `userId` từ Request.
8. Lấy current user từ Security Context.
9. Kiểm tra ownership.
10. Quan hệ JPA dùng LAZY.
11. Không dùng `@Data` cho Entity.
12. Money dùng `BigDecimal`.
13. Enum dùng STRING.
14. Transaction dùng Soft Delete.
15. Transaction filter dùng Specification.
16. Password chỉ lưu hash.
17. Refresh Token chỉ lưu hash.
18. JWT Stateless.
19. Redis chỉ là lớp hỗ trợ.
20. Flyway quản lý schema.
21. Test dùng profile `test`.
22. Không hard-code secret.
23. Không log dữ liệu nhạy cảm.
24. `mvn clean test` phải thành công trước khi hoàn thành task.

Tất cả mã nguồn mới phải bám theo tài liệu này để giữ dự án SmartSpend nhất quán trong suốt quá trình phát triển.
