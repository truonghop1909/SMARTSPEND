# SmartSpend Team Rules

Tai lieu nay thong nhat quy uoc ky thuat de hai nguoi co the chia viec ma it xung dot.

## Current direction

SmartSpend se lam theo huong modular monolith truoc:

- Van la mot Spring Boot app.
- Chia code theo module/domain ro rang.
- Moi nguoi so huu mot nhom module rieng.
- Khi du lon moi tinh den tach microservice that.

## Shared technical decisions

- Java: JDK 21.
- Spring Boot: can thong nhat mot version trong `backend/pom.xml` va docs.
- Database: MySQL 8.
- Migration: Flyway.
- API docs: Swagger/OpenAPI.
- Money type: `BigDecimal`, khong dung `double` hoac `float`.
- Default currency: `VND`.

## Module ownership

Nguoi 1 nen uu tien:

```text
auth
security
user
refresh token
session
CurrentUserProvider
```

Nguoi 2 nen uu tien:

```text
wallet
category
transaction
balance log
basic finance flow
```

File/module cua nguoi nao thi nguoi do chiu trach nhiem chinh. Neu can sua vung cua nguoi kia, bao truoc de tranh conflict.

## Package convention

Code nen chia theo domain:

```text
controller/<domain>
service/<domain>
service/impl
repository/<domain>
dto/<domain>
entity/<domain>
config
security
common
exception
```

Vi du:

```text
dto/wallet/WalletCreateRequest.java
dto/wallet/WalletUpdateRequest.java
dto/wallet/WalletResponse.java
repository/wallet/WalletRepository.java
controller/wallet/WalletController.java
```

## DTO rule

Khong tra JPA Entity truc tiep ra API.

Controller nen nhan request DTO va tra response DTO:

```text
WalletCreateRequest
WalletUpdateRequest
WalletResponse
```

Entity chi nen nam trong service/repository layer.

## API response rule

Tat ca API nen tra cung format qua `ApiResponse<T>`.

Thanh cong:

```json
{
  "success": true,
  "data": {},
  "message": "OK",
  "errorCode": null
}
```

Loi nghiep vu nen dung `AppException` va `ErrorCode`.

Khong nen throw truc tiep:

```java
throw new RuntimeException("Wallet not found");
```

Nen dung:

```java
throw new AppException(ErrorCode.WALLET_NOT_FOUND);
```

## REST endpoint convention

Dung REST style:

```text
GET    /api/wallets
GET    /api/wallets/{id}
POST   /api/wallets
PATCH  /api/wallets/{id}
DELETE /api/wallets/{id}
```

Quy uoc chung:

- Dung `PATCH` cho cap nhat mot phan.
- Dung `page`, `size`, `sort` cho pagination.
- Date format: `yyyy-MM-dd`.
- Xoa du lieu tai chinh nen uu tien soft delete/archive neu hop ly.

## User ownership rule

Du lieu tai chinh bat buoc gan voi user.

Khong query chi bang id:

```java
walletRepository.findById(walletId);
```

Nen query theo id va user:

```java
walletRepository.findByIdAndUserId(walletId, userId);
```

Sau khi co auth, `userId` phai lay tu `SecurityContext` thong qua mot interface chung, vi du:

```java
public interface CurrentUserProvider {
    Long getCurrentUserId();
}
```

Khong nhan `userId` tu request body cho API nghiep vu.

## Transaction rule

Service method doc:

```java
@Transactional(readOnly = true)
```

Service method ghi:

```java
@Transactional
```

Cac thao tac lien quan tien va so du phai nam trong cung mot transaction:

- Tao transaction.
- Cap nhat wallet balance.
- Ghi wallet balance log.
- Ghi audit neu co.

## Validation rule

Request DTO nen dung Bean Validation:

```java
@NotBlank
@NotNull
@Positive
@Size(max = 100)
```

Controller method nhan request body nen co `@Valid`.

## Config and secret rule

Duoc commit:

```text
application.yml
application-dev.yml.example
.env.example
```

Khong commit:

```text
application-dev.yml
.env
*.log
```

Secret nhu database password, JWT secret, OpenAI key phai de trong environment variable hoac file local bi ignore.

## Test rule

Toi thieu nen co:

- Service test cho business logic.
- Controller test cho API response.
- Test loi: not found, access denied, validation failed.

Truoc khi merge:

```bash
cd backend
mvn test
```

Neu test fail do moi truong local, ghi ro ly do trong pull request.

## Suggested work order

Thu tu nen lam gan nhat:

1. Thong nhat JDK 21 va Spring Boot version.
2. Tao `CurrentUserProvider`.
3. Hoan thien Auth/Security.
4. Hoan thien Wallet CRUD.
5. Hoan thien Category CRUD.
6. Lam Transaction va balance update.
7. Lam Budget/Dashboard.
8. Lam AI/Receipt/Notification/Export sau.
