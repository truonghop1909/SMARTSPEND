# Development Log

## Session 01
Date: 2026-07-30

---

## Goal

Hoàn thành môi trường phát triển.

---

## Completed

- Tạo Spring Boot project
- Java 21
- Maven Wrapper
- Docker Desktop
- Docker Compose
- MySQL
- Redis
- Flyway
- Build thành công

---

## Fixed Issues

### JAVA_HOME

Maven dùng JDK 20.

Fix:
- đổi JAVA_HOME sang JDK21.

---

### Docker

Docker daemon chưa chạy.

Fix:
- khởi động Docker Desktop.

---

### MySQL

Spring Boot dùng root không đúng password.

Fix:
- tạo user smartspend
- cấu hình datasource dùng smartspend
- đồng bộ docker-compose và application-dev.yml

---

## Files Created

docker-compose.yml

application.yml

application-dev.yml

.env.example

---

## Files Modified

pom.xml

application-dev.yml

docker-compose.yml

---

## Decisions

- Không dùng root cho Backend.
- Flyway quản lý schema.
- Docker Compose quản lý MySQL và Redis.
- Package theo Feature.

---

## Next Session

- Hoàn thiện common
- OpenAPI Config
- Security Config

## Session-002.1 — ApiResponse

**Trạng thái:** Hoàn thành

### Mục tiêu

Tạo cấu trúc response thống nhất cho toàn bộ REST API.

### File đã tạo

```text
src/main/java/com/smartspend/common/response/ApiResponse.java

## Session-002.2 — Exception Handling

**Ngày:** 2026-07-30
**Trạng thái:** Hoàn thành

### Mục tiêu

Hoàn thiện hệ thống mã lỗi và xử lý exception tập trung cho SmartSpend.

### Đã hoàn thành

* [x] Hoàn thiện `ErrorCode`.
* [x] Hoàn thiện `GlobalExceptionHandler`.
* [x] Xử lý lỗi nghiệp vụ bằng `AppException`.
* [x] Xử lý validation DTO.
* [x] Xử lý constraint violation.
* [x] Xử lý request body không hợp lệ.
* [x] Xử lý thiếu request parameter.
* [x] Xử lý sai kiểu parameter.
* [x] Xử lý HTTP method không hỗ trợ.
* [x] Xử lý đường dẫn không tồn tại.
* [x] Xử lý Access Denied.
* [x] Xử lý lỗi hệ thống.
* [x] Xóa Markdown backticks bị copy vào file Java.
* [x] Build project thành công.

### File đã hoàn thiện

```text
src/main/java/com/smartspend/common/exception/ErrorCode.java
src/main/java/com/smartspend/common/exception/GlobalExceptionHandler.java
```

### Quyết định kỹ thuật

* Mọi exception của REST API được xử lý tập trung bằng `@RestControllerAdvice`.
* Lỗi nghiệp vụ dùng `AppException` và `ErrorCode`.
* Business exception được log ở mức `WARN`.
* System exception được log ở mức `ERROR`.
* Không trả stack trace hoặc thông tin kỹ thuật cho client.
* Validation error hỗ trợ trả lỗi theo từng field.
* HTTP status được lấy từ `ErrorCode`.

### Kiểm tra

```powershell
.\mvnw.cmd clean test
```

Kết quả:

```text
BUILD SUCCESS
```

### Công việc tiếp theo

* [ ] Tạo Health Check API.
* [ ] Kiểm tra response lỗi thực tế.
* [ ] Kiểm tra Swagger UI.
* [ ] Kiểm tra `X-Trace-Id`.
* [ ] Tạo `RedisConfig`.
* [ ] Tạo `JpaAuditingConfig`.
