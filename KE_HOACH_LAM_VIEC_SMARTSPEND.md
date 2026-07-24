# Ke hoach lam viec du an SmartSpend

Tai lieu nay dung de thong nhat cach lam viec giua cac thanh vien trong du an SmartSpend. Muc tieu la lam tung moc nho, moi moc deu co code chay duoc, test duoc va commit ro rang.

## 1. Nguyen tac lam viec

Moi vong lam viec nen di theo trinh tu:

1. Chot muc tieu ngan gon.
2. Doc code hien tai truoc khi sua.
3. Sua mot pham vi nho, tranh gom qua nhieu module.
4. Chay build/test.
5. Kiem tra `git status`.
6. Commit voi message ro nghia.
7. Push len branch chung.

Khong push truc tiep len `main` neu co branch rieng. Nen dung branch theo tinh nang, vi du:

```powershell
feature/wallet
feature/auth
feature/budget
```

Commit message nen theo dang:

```text
feat(wallet): add wallet response dto
fix(transaction): rollback balance update on failure
refactor(entity): organize domain packages
```

## 2. Hien trang du an

Du an hien co:

- Backend Spring Boot.
- MySQL local.
- Entity theo ERD SmartSpend.
- Entity da duoc tach theo package domain:
  - `entity.auth`
  - `entity.wallet`
  - `entity.transaction`
  - `entity.budget`
  - `entity.receipt`
  - `entity.recurring`
  - `entity.notification`
  - `entity.export`
- Common response/exception co ban:
  - `ApiResponse`
  - `PageResponse`
  - `ErrorCode`
  - `AppException`
  - `GlobalExceptionHandler`
- `WalletController` bat dau tra ve `ApiResponse<WalletResponse>` thay vi tra truc tiep entity.

## 3. Nguyen tac ky thuat bat buoc

- Khong tra entity JPA truc tiep ra API.
- API tra response thong nhat bang `ApiResponse<T>`.
- Tien dung `BigDecimal`, cot database dung `DECIMAL(15,2)`.
- Khong dung `double` hoac `float` cho tien.
- Enum luu bang `EnumType.STRING`.
- `userId` khong lay tu request body. Sau khi co auth, phai lay tu `SecurityContext`.
- Service layer la noi xu ly nghiep vu.
- Cac thao tac tien va so du phai nam trong `@Transactional`.
- AI chi ho tro trich xuat/goi y, khong tu ghi giao dich tai chinh truc tiep.
- Khong commit secret nhu password DB, JWT secret, API key.

Format response thanh cong:

```json
{
  "success": true,
  "data": {},
  "message": "OK",
  "errorCode": null
}
```

Format response loi:

```json
{
  "success": false,
  "data": null,
  "message": "Wallet not found",
  "errorCode": "WALLET_NOT_FOUND"
}
```

## 4. Giai doan 0: Nen tang du an

Muc tieu: tao nen mong chung de cac module sau khong bi lech convention.

Da lam:

- Tao entity theo ERD.
- Tach entity theo domain package.
- Tao common response/exception co ban.
- Tao `WalletResponse`.
- Sua wallet API mau de tra `ApiResponse`.

Can lam tiep:

1. Them Swagger/OpenAPI.
2. Chuyen config tu `application.properties` sang `application.yml`.
3. Tao `application-dev.yml.example`.
4. Them Docker Compose cho MySQL va Redis.
5. Them Flyway baseline.
6. Giam phu thuoc vao `spring.jpa.hibernate.ddl-auto=update`.

Muc tieu ket thuc giai doan 0:

- Backend chay duoc.
- Swagger UI mo duoc.
- Co format response/exception chuan.
- Co config local mau cho ca nhom.
- Co nen tang migration database.

## 5. Giai doan 1: Wallet lam module mau

Muc tieu: hoan thien Wallet nhu mot module mau de cac module sau copy pattern.

Can lam:

1. Tao DTO:
   - `CreateWalletRequest`
   - `UpdateWalletRequest`
   - `WalletResponse`
2. Tao API:
   - `GET /api/wallets/{id}`
   - `GET /api/wallets`
   - `POST /api/wallets`
   - `PATCH /api/wallets/{id}`
   - `DELETE /api/wallets/{id}` hoac archive
3. Hoan thien service CRUD.
4. Validate input bang Jakarta Bean Validation.
5. Khong tra entity truc tiep.
6. Viet test service/controller co ban.

## 6. Giai doan 2: Category

Muc tieu: quan ly danh muc thu/chi.

Can lam:

1. `CategoryRepository`.
2. `CategoryService`.
3. `CategoryController`.
4. DTO request/response.
5. CRUD category.
6. Category mac dinh cua he thong.
7. Filter theo `INCOME` hoac `EXPENSE`.

## 7. Giai doan 3: Auth

Muc tieu: co dang ky, dang nhap, JWT va lay user tu token.

Can lam:

1. Them/cau hinh Spring Security.
2. `UserRepository`.
3. DTO:
   - `RegisterRequest`
   - `LoginRequest`
   - `AuthResponse`
4. Hash password bang `BCryptPasswordEncoder`.
5. Tao JWT service.
6. Login tra access token.
7. Refresh token rotation.
8. Logout/revoke refresh token.
9. `SecurityConfig`.
10. `UserPrincipal`.

Sau giai doan nay, cac API ca nhan nhu wallet/transaction/budget phai lay user tu token, khong nhan `userId` tu body.

## 8. Giai doan 4: Transaction

Muc tieu: xay dung loi tai chinh cua he thong.

Can lam:

1. Tao giao dich thu/chi.
2. Cap nhat balance cua wallet trong cung `@Transactional`.
3. Ghi `WalletBalanceLog`.
4. Ghi `TransactionAudit`.
5. Sua giao dich.
6. Soft delete giao dich.
7. Validate amount > 0.
8. Kiem tra wallet/category thuoc dung user.

Day la giai doan quan trong nhat. Can test ky vi lien quan truc tiep den tien va so du.

## 9. Giai doan 5: Transfer

Muc tieu: chuyen tien giua hai vi.

Can lam:

1. Tao `TransferRequest`.
2. Tao `TransferGroup`.
3. Tao 2 transaction lien ket.
4. Tru tien vi nguon.
5. Cong tien vi dich.
6. Rollback toan bo neu co loi.
7. Chan chuyen tien cung mot vi.
8. Chan amount <= 0.
9. Them idempotency key bang Redis sau khi co Redis.

## 10. Giai doan 6: Budget

Muc tieu: ngan sach theo thang/category.

Can lam:

1. CRUD budget.
2. Tinh `spentAmount` tu transaction.
3. Canh bao moc 80% va 100%.
4. Ghi `BudgetAlertLog`.
5. API xem tien do ngan sach.

## 11. Giai doan 7: Dashboard/Report

Muc tieu: thong ke tong quan tai chinh.

Can lam:

1. Tong thu/chi theo thang.
2. Chi theo category.
3. Bien dong so du.
4. API dashboard.
5. Cache Redis sau khi logic on dinh.
6. Evict cache sau khi transaction thay doi.

## 12. Giai doan 8: Receipt va AI

Muc tieu: upload hoa don va dung AI doc thong tin.

Can lam:

1. Upload file hoa don.
2. Luu `Receipt`.
3. Xu ly AI bang `@Async`.
4. Goi AI API bang `WebClient`.
5. Luu raw result vao `ReceiptExtractionResult`.
6. Luu line items vao `ReceiptItem`.
7. Cho user xac nhan thu cong.
8. Chi tao transaction sau khi user xac nhan.

Nguyen tac: AI khong tu ghi giao dich tai chinh truc tiep.

## 13. Giai doan 9: Recurring va Notification

Muc tieu: giao dich dinh ky va thong bao.

Can lam:

1. CRUD recurring transaction.
2. `@Scheduled` quet `nextRunDate`.
3. Tao transaction tu dong.
4. Tao notification.
5. API doc notification.
6. API mark read/unread.

## 14. Giai doan 10: Export

Muc tieu: xuat du lieu.

Can lam:

1. Export CSV truoc.
2. Export Excel sau bang Apache POI.
3. Tao `ExportJob`.
4. Tao `ExportFile`.
5. Cap nhat status job.
6. Don file het han neu co storage.

## 15. Phan cong goi y cho nhom 2 nguoi

Nguoi 1:

- Auth.
- Wallet.
- Transaction.
- Transfer.
- Logic so du.

Nguoi 2:

- Category.
- Budget.
- Dashboard/Report.
- Receipt/AI.
- Recurring.
- Notification.
- Export.

Phan can lam chung/review chung:

- `common`.
- `SecurityConfig`.
- `ApiResponse`.
- `ErrorCode`.
- Flyway baseline.
- Swagger convention.
- Entity contract, dac biet `User`, `Wallet`, `Transaction`.

## 16. Quy trinh Git khuyen dung

Neu dang o root repo:

```powershell
cd D:\HocBai\LapTrinh\SMARTSPEND
git status
git add -A backend/src/main/java
git commit -m "feat(common): add api response and exception handling"
git push origin hop
```

Neu dang o folder backend:

```powershell
cd D:\HocBai\LapTrinh\SMARTSPEND\backend
git status
git add -A src/main/java
git commit -m "feat(wallet): add wallet crud"
git push origin hop
```

Luu y: Git repo nam o folder `SMARTSPEND`. Neu dang o `backend`, khong them tien to `backend/` vao path nua.

## 17. Lenh test

Chay test voi JDK 21:

```powershell
cd D:\HocBai\LapTrinh\SMARTSPEND\backend
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
& 'C:\Users\admin\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd' test
```

Neu muon clean build:

```powershell
& 'C:\Users\admin\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd' clean test
```

## 18. Buoc tiep theo nen lam

Thu tu tiep theo nen la:

1. Commit phan common response/exception hien tai.
2. Them Swagger/OpenAPI.
3. Chuyen config sang `application.yml`.
4. Tao Docker Compose MySQL + Redis.
5. Them Flyway baseline.
6. Hoan thien Wallet CRUD.

