# Thiết kế REST API

# SmartSpend

### AI Personal Finance Manager

---

# 1. Giới thiệu

## 1.1 Mục đích

Tài liệu này mô tả thiết kế REST API của hệ thống **SmartSpend**.

Tài liệu được sử dụng làm cơ sở để:

- Phát triển Controller.
- Thiết kế Request DTO và Response DTO.
- Cấu hình Swagger/OpenAPI.
- Kiểm thử API.
- Tích hợp Frontend với Backend.
- Thống nhất cách trả dữ liệu và xử lý lỗi.

Mọi API trong hệ thống phải tuân theo các quy ước được định nghĩa trong tài liệu này.

---

## 1.2 Phạm vi

Tài liệu bao gồm API của các module:

1. Authentication.
2. Category.
3. Transaction.
4. Budget.
5. Dashboard.
6. Notification.
7. AI Assistant.
8. Export.

SmartSpend là ứng dụng quản lý tài chính cá nhân, không cung cấp API cho:

- Ví điện tử.
- Chuyển tiền.
- Thanh toán trực tuyến.
- Liên kết tài khoản ngân hàng.
- Giao dịch đầu tư.

---

# 2. Quy ước chung

## 2.1 Base URL

Trong môi trường phát triển:

```text
http://localhost:8080/api
```

Ví dụ:

```text
http://localhost:8080/api/auth/login
```

Trong môi trường Production, Base URL sẽ được thay đổi theo tên miền triển khai.

---

## 2.2 Định dạng dữ liệu

Request và Response sử dụng định dạng:

```text
application/json
```

Đối với API xuất báo cáo, Response có thể sử dụng:

```text
text/csv
```

hoặc:

```text
application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
```

---

## 2.3 Quy ước đặt tên Endpoint

Endpoint sử dụng:

- Chữ thường.
- Danh từ số nhiều.
- Dấu gạch ngang nếu cần phân tách nhiều từ.
- Không dùng động từ trong URL khi có thể biểu diễn bằng HTTP Method.

Ví dụ đúng:

```text
GET /api/transactions

POST /api/categories

PATCH /api/notifications/{id}/read
```

Ví dụ không khuyến nghị:

```text
GET /api/getTransactions

POST /api/createCategory

POST /api/deleteTransaction
```

---

## 2.4 HTTP Method

| Method | Mục đích |
|---|---|
| `GET` | Lấy dữ liệu |
| `POST` | Tạo mới dữ liệu hoặc thực hiện nghiệp vụ |
| `PUT` | Thay thế toàn bộ tài nguyên |
| `PATCH` | Cập nhật một phần tài nguyên |
| `DELETE` | Xóa hoặc xóa mềm tài nguyên |

SmartSpend ưu tiên sử dụng `PATCH` cho các thao tác cập nhật một phần.

---

## 2.5 Authentication

Toàn bộ API, ngoại trừ một số API thuộc Authentication, đều yêu cầu Access Token hợp lệ.

Header:

```http
Authorization: Bearer <access_token>
```

Ví dụ:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Các API không yêu cầu đăng nhập:

```text
POST /api/auth/register

POST /api/auth/login

POST /api/auth/refresh
```

---

## 2.6 Quy tắc lấy `userId`

`userId` luôn được lấy từ `SecurityContext`.

Không nhận `userId` từ:

- Request body.
- Query parameter.
- Path variable.

Ví dụ Request không hợp lệ:

```json
{
  "userId": 10,
  "amount": 150000
}
```

Client không có quyền tự chỉ định chủ sở hữu dữ liệu.

---

# 3. Response Format

## 3.1 Response thành công

Tất cả API trả dữ liệu theo cấu trúc chung:

```json
{
  "success": true,
  "message": "Thành công",
  "data": {},
  "timestamp": "2026-07-28T20:30:00"
}
```

Ý nghĩa:

| Trường | Kiểu | Mô tả |
|---|---|---|
| `success` | BOOLEAN | Trạng thái xử lý |
| `message` | STRING | Thông báo cho Client |
| `data` | OBJECT | Dữ liệu trả về |
| `timestamp` | DATETIME | Thời điểm phản hồi |

---

## 3.2 Response không có dữ liệu

Ví dụ API đánh dấu thông báo đã đọc:

```json
{
  "success": true,
  "message": "Đánh dấu thông báo đã đọc thành công",
  "data": null,
  "timestamp": "2026-07-28T20:30:00"
}
```

---

## 3.3 Response lỗi

```json
{
  "success": false,
  "message": "Không tìm thấy giao dịch",
  "errorCode": "TRANSACTION_NOT_FOUND",
  "errors": null,
  "timestamp": "2026-07-28T20:30:00"
}
```

Ý nghĩa:

| Trường | Kiểu | Mô tả |
|---|---|---|
| `success` | BOOLEAN | Luôn là `false` |
| `message` | STRING | Mô tả lỗi |
| `errorCode` | STRING | Mã lỗi của hệ thống |
| `errors` | OBJECT | Chi tiết lỗi Validation |
| `timestamp` | DATETIME | Thời điểm xảy ra lỗi |

---

## 3.4 Response lỗi Validation

```json
{
  "success": false,
  "message": "Dữ liệu đầu vào không hợp lệ",
  "errorCode": "VALIDATION_ERROR",
  "errors": {
    "email": "Email không đúng định dạng",
    "password": "Mật khẩu phải có ít nhất 8 ký tự"
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

---

# 4. Phân trang

Các API danh sách sử dụng các query parameter:

| Parameter | Mặc định | Mô tả |
|---|---:|---|
| `page` | `0` | Trang hiện tại, bắt đầu từ 0 |
| `size` | `20` | Số phần tử mỗi trang |
| `sort` | Tùy API | Cột và hướng sắp xếp |

Ví dụ:

```text
GET /api/transactions?page=0&size=20&sort=transactionDate,desc
```

---

## 4.1 Response phân trang

```json
{
  "success": true,
  "message": "Lấy danh sách thành công",
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "last": true
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

---

# 5. HTTP Status Code

| Status | Ý nghĩa | Trường hợp |
|---:|---|---|
| `200 OK` | Xử lý thành công | GET, PATCH và nghiệp vụ thông thường |
| `201 Created` | Tạo tài nguyên thành công | POST tạo mới |
| `204 No Content` | Thành công nhưng không trả body | Có thể dùng cho DELETE |
| `400 Bad Request` | Request không hợp lệ | Validation hoặc tham số sai |
| `401 Unauthorized` | Chưa xác thực | Thiếu hoặc sai Access Token |
| `403 Forbidden` | Không có quyền | Truy cập dữ liệu người khác |
| `404 Not Found` | Không tìm thấy tài nguyên | ID không tồn tại |
| `409 Conflict` | Xung đột nghiệp vụ | Email trùng, ngân sách trùng |
| `429 Too Many Requests` | Vượt giới hạn request | Rate limit |
| `500 Internal Server Error` | Lỗi hệ thống | Lỗi ngoài dự kiến |
| `502 Bad Gateway` | Dịch vụ ngoài lỗi | AI Provider không phản hồi hợp lệ |
| `503 Service Unavailable` | Dịch vụ tạm thời không khả dụng | AI Provider hoặc Redis lỗi |

---

# 6. Authentication API

## 6.1 Danh sách Endpoint

| Method | Endpoint | Mô tả | Authentication |
|---|---|---|---|
| `POST` | `/api/auth/register` | Đăng ký tài khoản | Không |
| `POST` | `/api/auth/login` | Đăng nhập | Không |
| `POST` | `/api/auth/refresh` | Cấp cặp token mới | Không |
| `POST` | `/api/auth/logout` | Đăng xuất | Có |
| `GET` | `/api/auth/me` | Lấy thông tin người dùng hiện tại | Có |

---

## 6.2 Đăng ký tài khoản

### Endpoint

```http
POST /api/auth/register
```

### Mục đích

Tạo tài khoản người dùng mới.

### Authentication

Không yêu cầu.

### Request Body

```json
{
  "email": "nguyenvana@example.com",
  "password": "Password@123",
  "fullName": "Nguyễn Văn A"
}
```

### Validation

| Trường | Quy tắc |
|---|---|
| `email` | Bắt buộc, đúng định dạng email, tối đa 255 ký tự |
| `password` | Bắt buộc, tối thiểu 8 ký tự |
| `fullName` | Bắt buộc, tối đa 100 ký tự |

Có thể áp dụng quy tắc mật khẩu:

- Có ít nhất một chữ hoa.
- Có ít nhất một chữ thường.
- Có ít nhất một chữ số.
- Có ít nhất một ký tự đặc biệt.

### Response `201 Created`

```json
{
  "success": true,
  "message": "Đăng ký tài khoản thành công",
  "data": {
    "id": 1,
    "email": "nguyenvana@example.com",
    "fullName": "Nguyễn Văn A",
    "avatarUrl": null,
    "createdAt": "2026-07-28T20:30:00"
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `400` | `VALIDATION_ERROR` | Dữ liệu đầu vào không hợp lệ |
| `409` | `AUTH_EMAIL_ALREADY_EXISTS` | Email đã được đăng ký |

### Business Rules liên quan

- AUTH-002.
- AUTH-003.
- AUTH-007.

---

## 6.3 Đăng nhập

### Endpoint

```http
POST /api/auth/login
```

### Mục đích

Xác thực email và mật khẩu, sau đó cấp Access Token và Refresh Token.

### Authentication

Không yêu cầu.

### Request Body

```json
{
  "email": "nguyenvana@example.com",
  "password": "Password@123"
}
```

### Header tùy chọn

Backend có thể lấy thông tin thiết bị từ:

```http
User-Agent: Mozilla/5.0 ...
```

Địa chỉ IP được lấy từ request, không nhận từ body.

### Response `200 OK`

```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "tokenType": "Bearer",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "a55f9ca4-...",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "nguyenvana@example.com",
      "fullName": "Nguyễn Văn A",
      "avatarUrl": null
    }
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `400` | `VALIDATION_ERROR` | Thiếu email hoặc mật khẩu |
| `401` | `AUTH_INVALID_CREDENTIALS` | Email hoặc mật khẩu không đúng |
| `429` | `AUTH_RATE_LIMIT_EXCEEDED` | Đăng nhập sai quá nhiều lần |

### Rate Limit

Có thể giới hạn:

```text
5 lần đăng nhập thất bại trong 1 phút
```

Redis key đề xuất:

```text
login-attempt:{email}
```

### Business Rules liên quan

- AUTH-001.
- AUTH-003.
- AUTH-007.
- SEC-003.

---

## 6.4 Làm mới Token

### Endpoint

```http
POST /api/auth/refresh
```

### Mục đích

Cấp Access Token và Refresh Token mới bằng Refresh Token hiện tại.

Hệ thống sử dụng Refresh Token Rotation:

1. Xác thực token hiện tại.
2. Thu hồi token cũ.
3. Tạo token mới.
4. Trả cặp token mới cho Client.

### Authentication

Không yêu cầu Access Token.

### Request Body

```json
{
  "refreshToken": "a55f9ca4-..."
}
```

### Response `200 OK`

```json
{
  "success": true,
  "message": "Làm mới token thành công",
  "data": {
    "tokenType": "Bearer",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "b66e8db5-...",
    "expiresIn": 3600
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `401` | `AUTH_REFRESH_TOKEN_INVALID` | Token không hợp lệ |
| `401` | `AUTH_REFRESH_TOKEN_EXPIRED` | Token đã hết hạn |
| `401` | `AUTH_REFRESH_TOKEN_REVOKED` | Token đã bị thu hồi |

### Business Rules liên quan

- AUTH-004.
- AUTH-005.

---

## 6.5 Đăng xuất

### Endpoint

```http
POST /api/auth/logout
```

### Mục đích

Thu hồi Refresh Token hiện tại.

### Authentication

Yêu cầu Access Token hợp lệ.

### Request Body

```json
{
  "refreshToken": "b66e8db5-..."
}
```

### Response `200 OK`

```json
{
  "success": true,
  "message": "Đăng xuất thành công",
  "data": null,
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `401` | `AUTH_UNAUTHORIZED` | Access Token không hợp lệ |
| `401` | `AUTH_REFRESH_TOKEN_INVALID` | Refresh Token không hợp lệ |

---

## 6.6 Lấy thông tin người dùng hiện tại

### Endpoint

```http
GET /api/auth/me
```

### Mục đích

Lấy thông tin tài khoản của người dùng đang đăng nhập.

### Authentication

Yêu cầu Access Token hợp lệ.

### Response `200 OK`

```json
{
  "success": true,
  "message": "Lấy thông tin người dùng thành công",
  "data": {
    "id": 1,
    "email": "nguyenvana@example.com",
    "fullName": "Nguyễn Văn A",
    "avatarUrl": null,
    "createdAt": "2026-07-28T20:30:00",
    "updatedAt": "2026-07-28T20:30:00"
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `401` | `AUTH_UNAUTHORIZED` | Chưa xác thực |
| `404` | `USER_NOT_FOUND` | Không tìm thấy người dùng |

---

# 7. Category API

## 7.1 Danh sách Endpoint

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/categories` | Lấy danh sách danh mục |
| `GET` | `/api/categories/{id}` | Lấy chi tiết danh mục |
| `POST` | `/api/categories` | Tạo danh mục |
| `PATCH` | `/api/categories/{id}` | Cập nhật danh mục |
| `DELETE` | `/api/categories/{id}` | Xóa danh mục |

Tất cả Category API đều yêu cầu Access Token hợp lệ.

---

## 7.2 Lấy danh sách danh mục

### Endpoint

```http
GET /api/categories
```

### Query Parameter

| Parameter | Bắt buộc | Mô tả |
|---|---:|---|
| `type` | Không | `INCOME` hoặc `EXPENSE` |

Ví dụ:

```text
GET /api/categories?type=EXPENSE
```

### Mục đích

Lấy danh mục mặc định của hệ thống và danh mục riêng của người dùng hiện tại.

### Response `200 OK`

```json
{
  "success": true,
  "message": "Lấy danh sách danh mục thành công",
  "data": [
    {
      "id": 1,
      "name": "Ăn uống",
      "type": "EXPENSE",
      "icon": "utensils",
      "color": "#F59E0B",
      "default": true,
      "editable": false
    },
    {
      "id": 15,
      "name": "Cà phê",
      "type": "EXPENSE",
      "icon": "coffee",
      "color": "#92400E",
      "default": false,
      "editable": true
    }
  ],
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `400` | `CATEGORY_INVALID_TYPE` | Giá trị `type` không hợp lệ |
| `401` | `AUTH_UNAUTHORIZED` | Chưa đăng nhập |

---

## 7.3 Lấy chi tiết danh mục

### Endpoint

```http
GET /api/categories/{id}
```

### Path Variable

| Tên | Kiểu | Mô tả |
|---|---|---|
| `id` | LONG | ID danh mục |

### Response `200 OK`

```json
{
  "success": true,
  "message": "Lấy thông tin danh mục thành công",
  "data": {
    "id": 15,
    "name": "Cà phê",
    "type": "EXPENSE",
    "icon": "coffee",
    "color": "#92400E",
    "default": false,
    "editable": true,
    "createdAt": "2026-07-20T08:00:00",
    "updatedAt": "2026-07-20T08:00:00"
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Quyền truy cập

Người dùng chỉ được xem:

- Danh mục mặc định.
- Danh mục do chính mình tạo.

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `403` | `CATEGORY_ACCESS_DENIED` | Danh mục thuộc người khác |
| `404` | `CATEGORY_NOT_FOUND` | Không tìm thấy danh mục |

---

## 7.4 Tạo danh mục

### Endpoint

```http
POST /api/categories
```

### Request Body

```json
{
  "name": "Cà phê",
  "type": "EXPENSE",
  "icon": "coffee",
  "color": "#92400E"
}
```

### Validation

| Trường | Quy tắc |
|---|---|
| `name` | Bắt buộc, tối đa 100 ký tự |
| `type` | Bắt buộc, chỉ nhận `INCOME` hoặc `EXPENSE` |
| `icon` | Không bắt buộc, tối đa 100 ký tự |
| `color` | Không bắt buộc, đúng định dạng màu Hex |

Client không được truyền:

```text
userId
isDefault
```

Backend tự gán:

```text
userId = người dùng hiện tại

isDefault = false
```

### Response `201 Created`

```json
{
  "success": true,
  "message": "Tạo danh mục thành công",
  "data": {
    "id": 15,
    "name": "Cà phê",
    "type": "EXPENSE",
    "icon": "coffee",
    "color": "#92400E",
    "default": false,
    "editable": true,
    "createdAt": "2026-07-28T20:30:00"
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `400` | `VALIDATION_ERROR` | Dữ liệu không hợp lệ |
| `409` | `CATEGORY_NAME_EXISTS` | Tên đã tồn tại trong cùng loại |

### Business Rules liên quan

- CAT-001.
- CAT-002.
- CAT-005.

---

## 7.5 Cập nhật danh mục

### Endpoint

```http
PATCH /api/categories/{id}
```

### Request Body

```json
{
  "name": "Cà phê và đồ uống",
  "icon": "cup-soda",
  "color": "#78350F"
}
```

Trong phiên bản đầu, không nên cho thay đổi `type` của danh mục đã tạo.

Lý do:

- Danh mục có thể đang được giao dịch sử dụng.
- Thay đổi loại có thể làm sai dữ liệu lịch sử.
- Có thể làm giao dịch và danh mục không còn cùng loại.

### Response `200 OK`

```json
{
  "success": true,
  "message": "Cập nhật danh mục thành công",
  "data": {
    "id": 15,
    "name": "Cà phê và đồ uống",
    "type": "EXPENSE",
    "icon": "cup-soda",
    "color": "#78350F",
    "default": false,
    "editable": true,
    "updatedAt": "2026-07-28T20:35:00"
  },
  "timestamp": "2026-07-28T20:35:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `403` | `CATEGORY_ACCESS_DENIED` | Danh mục thuộc người khác |
| `403` | `CATEGORY_DEFAULT_READ_ONLY` | Danh mục mặc định |
| `404` | `CATEGORY_NOT_FOUND` | Không tìm thấy danh mục |
| `409` | `CATEGORY_NAME_EXISTS` | Tên danh mục bị trùng |

---

## 7.6 Xóa danh mục

### Endpoint

```http
DELETE /api/categories/{id}
```

### Mục đích

Xóa danh mục cá nhân chưa được giao dịch hoặc ngân sách sử dụng.

### Response `200 OK`

```json
{
  "success": true,
  "message": "Xóa danh mục thành công",
  "data": null,
  "timestamp": "2026-07-28T20:30:00"
}
```

### Lỗi có thể xảy ra

| Status | Error Code | Trường hợp |
|---:|---|---|
| `403` | `CATEGORY_ACCESS_DENIED` | Danh mục thuộc người khác |
| `403` | `CATEGORY_DEFAULT_READ_ONLY` | Danh mục mặc định |
| `404` | `CATEGORY_NOT_FOUND` | Không tìm thấy danh mục |
| `409` | `CATEGORY_IN_USE` | Danh mục đang được sử dụng |

### Business Rules liên quan

- CAT-004.
- CAT-006.

---

# 8. Kết luận phần 1

Phần này đã định nghĩa:

- Quy ước chung của toàn bộ REST API.
- Response format.
- Phân trang.
- HTTP Status Code.
- Authentication API.
- Category API.

Phần tiếp theo sẽ mô tả:

1. Transaction API.
2. Budget API.
3. Dashboard API.

# 9. Transaction API

## 9.1 Danh sách Endpoint

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/transactions` | Lấy danh sách giao dịch |
| `GET` | `/api/transactions/{id}` | Lấy chi tiết giao dịch |
| `POST` | `/api/transactions` | Tạo giao dịch |
| `PATCH` | `/api/transactions/{id}` | Cập nhật giao dịch |
| `DELETE` | `/api/transactions/{id}` | Xóa giao dịch (Soft Delete) |

Tất cả Transaction API đều yêu cầu Access Token hợp lệ.

---

## 9.2 Lấy danh sách giao dịch

### Endpoint

```http
GET /api/transactions
```

### Query Parameters

| Parameter | Bắt buộc | Mô tả |
|---|---:|---|
| page | Không | Trang hiện tại |
| size | Không | Số phần tử mỗi trang |
| type | Không | INCOME hoặc EXPENSE |
| categoryId | Không | ID danh mục |
| fromDate | Không | Ngày bắt đầu |
| toDate | Không | Ngày kết thúc |
| keyword | Không | Tìm kiếm theo ghi chú |
| minAmount | Không | Số tiền tối thiểu |
| maxAmount | Không | Số tiền tối đa |
| sort | Không | Mặc định `transactionDate,desc` |

Ví dụ

```http
GET /api/transactions?page=0&size=20&type=EXPENSE&categoryId=5
```

### Response

```json
{
  "success": true,
  "message": "Lấy danh sách giao dịch thành công",
  "data": {
    "content": [
      {
        "id": 1,
        "categoryId": 5,
        "categoryName": "Ăn uống",
        "type": "EXPENSE",
        "amount": 120000,
        "note": "Ăn trưa",
        "transactionDate": "2026-07-28"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

---

## 9.3 Lấy chi tiết giao dịch

### Endpoint

```http
GET /api/transactions/{id}
```

### Response

```json
{
  "success": true,
  "message": "Lấy giao dịch thành công",
  "data": {
    "id": 1,
    "categoryId": 5,
    "categoryName": "Ăn uống",
    "type": "EXPENSE",
    "amount": 120000,
    "note": "Ăn trưa",
    "transactionDate": "2026-07-28",
    "createdAt": "2026-07-28T10:00:00",
    "updatedAt": "2026-07-28T10:00:00"
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

---

## 9.4 Tạo giao dịch

### Endpoint

```http
POST /api/transactions
```

### Request

```json
{
  "categoryId": 5,
  "type": "EXPENSE",
  "amount": 120000,
  "note": "Ăn trưa",
  "transactionDate": "2026-07-28"
}
```

### Validation

| Trường | Quy tắc |
|---|---|
| categoryId | Bắt buộc |
| type | INCOME hoặc EXPENSE |
| amount | > 0 |
| transactionDate | Không lớn hơn ngày hiện tại |
| note | Tối đa 500 ký tự |

### Response

```json
{
  "success": true,
  "message": "Tạo giao dịch thành công",
  "data": {
    "id": 100,
    "categoryName": "Ăn uống",
    "type": "EXPENSE",
    "amount": 120000
  },
  "timestamp": "2026-07-28T20:30:00"
}
```

### Business Rules

- TRAN-001
- TRAN-002
- TRAN-003
- TRAN-004
- TRAN-005
- TRAN-006

---

## 9.5 Cập nhật giao dịch

### Endpoint

```http
PATCH /api/transactions/{id}
```

### Request

```json
{
  "categoryId": 8,
  "amount": 150000,
  "note": "Ăn tối",
  "transactionDate": "2026-07-28"
}
```

Không cho phép thay đổi chủ sở hữu giao dịch.

---

## 9.6 Xóa giao dịch

### Endpoint

```http
DELETE /api/transactions/{id}
```

### Mục đích

Thực hiện Soft Delete.

Database chỉ cập nhật:

```sql
deleted_at = NOW()
```

Không xóa vật lý.

---

## 9.7 Các lỗi

| Status | Error |
|---:|---|
| 400 | VALIDATION_ERROR |
| 401 | AUTH_UNAUTHORIZED |
| 403 | TRANSACTION_ACCESS_DENIED |
| 404 | TRANSACTION_NOT_FOUND |
| 409 | TRANSACTION_CATEGORY_TYPE_MISMATCH |

---

# 10. Budget API

## 10.1 Danh sách Endpoint

| Method | Endpoint |
|---|---|
| GET | /api/budgets |
| GET | /api/budgets/{id} |
| POST | /api/budgets |
| PATCH | /api/budgets/{id} |
| DELETE | /api/budgets/{id} |

---

## 10.2 Tạo ngân sách

### Endpoint

```http
POST /api/budgets
```

### Request

```json
{
  "categoryId": 5,
  "budgetMonth": "2026-07",
  "amount": 5000000
}
```

### Validation

- category phải là EXPENSE
- amount > 0
- Không được trùng tháng

### Response

```json
{
  "success": true,
  "message": "Tạo ngân sách thành công",
  "data": {
    "id": 1,
    "categoryName": "Ăn uống",
    "amount": 5000000,
    "budgetMonth": "2026-07"
  }
}
```

---

## 10.3 Danh sách ngân sách

```http
GET /api/budgets?year=2026&month=7
```

Response sẽ bao gồm

```json
{
  "categoryName": "Ăn uống",
  "budget": 5000000,
  "spent": 3250000,
  "remaining": 1750000,
  "percentage": 65
}
```

---

## 10.4 Các lỗi

| Status | Error |
|---:|---|
|404|BUDGET_NOT_FOUND|
|409|BUDGET_ALREADY_EXISTS|
|400|BUDGET_INVALID_AMOUNT|

---

# 11. Dashboard API

Dashboard chỉ đọc dữ liệu.

Không cập nhật.

---

## 11.1 Danh sách Endpoint

| Method | Endpoint |
|---|---|
| GET | /api/dashboard/summary |
| GET | /api/dashboard/category |
| GET | /api/dashboard/trend |
| GET | /api/dashboard/comparison |

---

## 11.2 Dashboard Summary

```http
GET /api/dashboard/summary?year=2026&month=7
```

### Response

```json
{
  "success": true,
  "data": {
    "income": 25000000,
    "expense": 18000000,
    "balance": 7000000,
    "transactionCount": 56
  }
}
```

---

## 11.3 Dashboard Category

```http
GET /api/dashboard/category
```

Response

```json
[
    {
        "category":"Ăn uống",
        "amount":4500000,
        "percentage":35
    },
    {
        "category":"Di chuyển",
        "amount":2100000,
        "percentage":17
    }
]
```

---

## 11.4 Dashboard Trend

```http
GET /api/dashboard/trend?year=2026
```

Response

```json
[
    {
        "month":1,
        "income":22000000,
        "expense":18000000
    },
    {
        "month":2,
        "income":25000000,
        "expense":17000000
    }
]
```

---

## 11.5 Dashboard Comparison

```http
GET /api/dashboard/comparison
```

Response

```json
{
    "currentMonthExpense":18000000,
    "lastMonthExpense":16000000,
    "changePercentage":12.5
}
```

---

## 11.6 Các lỗi

|Status|Error|
|---:|---|
|400|DASHBOARD_INVALID_PERIOD|
|500|DASHBOARD_DATA_UNAVAILABLE|

---

# 12. Kết luận phần 2

Phần này đã mô tả:

- Transaction API
- Budget API
- Dashboard API

Đây là các API cốt lõi của SmartSpend.

Phần tiếp theo sẽ trình bày:

- Notification API
- AI Chat API
- Export API
- Error Code chuẩn của toàn hệ thống
- Quy ước Swagger/OpenAPI

# 13. Notification API

## 13.1 Danh sách Endpoint

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/notifications` | Lấy danh sách thông báo |
| GET | `/api/notifications/unread-count` | Lấy số thông báo chưa đọc |
| PATCH | `/api/notifications/{id}/read` | Đánh dấu đã đọc |
| PATCH | `/api/notifications/read-all` | Đánh dấu tất cả đã đọc |
| DELETE | `/api/notifications/{id}` | Xóa thông báo |

Tất cả Notification API đều yêu cầu Access Token hợp lệ.

---

## 13.2 Lấy danh sách thông báo

### Endpoint

```http
GET /api/notifications?page=0&size=20
```

### Response

```json
{
  "success": true,
  "message": "Lấy danh sách thông báo thành công",
  "data": {
    "content": [
      {
        "id": 10,
        "title": "Đã vượt ngân sách",
        "content": "Bạn đã vượt ngân sách Ăn uống.",
        "type": "BUDGET",
        "isRead": false,
        "createdAt": "2026-07-28T09:20:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "6f43e2a15a"
}
```

---

## 13.3 Đánh dấu đã đọc

### Endpoint

```http
PATCH /api/notifications/{id}/read
```

### Response

```json
{
  "success": true,
  "message": "Đánh dấu đã đọc",
  "data": null,
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "6f43e2a15a"
}
```

---

## 13.4 Đánh dấu tất cả đã đọc

### Endpoint

```http
PATCH /api/notifications/read-all
```

---

## 13.5 Xóa thông báo

### Endpoint

```http
DELETE /api/notifications/{id}
```

---

## 13.6 Các lỗi

| Status | Error |
|---:|---|
|401|AUTH_UNAUTHORIZED|
|403|NOTIFICATION_ACCESS_DENIED|
|404|NOTIFICATION_NOT_FOUND|

---

# 14. AI Chat API

## 14.1 Danh sách Endpoint

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/chat/sessions` | Danh sách phiên chat |
| POST | `/api/chat/sessions` | Tạo phiên chat |
| DELETE | `/api/chat/sessions/{id}` | Xóa phiên chat |
| GET | `/api/chat/sessions/{id}/messages` | Lấy lịch sử hội thoại |
| POST | `/api/chat/sessions/{id}/messages` | Gửi câu hỏi |

---

## 14.2 Tạo phiên chat

### Endpoint

```http
POST /api/chat/sessions
```

### Request

Không cần Request Body.

Backend tự tạo phiên mới.

### Response

```json
{
  "success": true,
  "message": "Tạo phiên hội thoại thành công",
  "data": {
    "sessionId": 15,
    "title": "Phiên hội thoại mới"
  },
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "ad72cb71"
}
```

---

## 14.3 Gửi câu hỏi

### Endpoint

```http
POST /api/chat/sessions/{sessionId}/messages
```

### Request

```json
{
  "message": "Tháng này tôi tiêu nhiều nhất vào đâu?"
}
```

---

### Response

```json
{
  "success": true,
  "message": "AI đã trả lời",
  "data": {
    "question": "Tháng này tôi tiêu nhiều nhất vào đâu?",
    "answer": "Trong tháng 7 bạn đã chi nhiều nhất cho danh mục Ăn uống với tổng số tiền 4.500.000 VNĐ."
  },
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "ab34dc89"
}
```

---

## 14.4 Lấy lịch sử hội thoại

### Endpoint

```http
GET /api/chat/sessions/{sessionId}/messages
```

### Response

```json
{
  "success": true,
  "data": [
    {
      "role": "USER",
      "content": "Tôi tiêu nhiều nhất vào đâu?"
    },
    {
      "role": "ASSISTANT",
      "content": "Bạn chi nhiều nhất cho Ăn uống."
    }
  ],
  "timestamp": "2026-07-28T20:30:00",
  "traceId": "19cb2ef8"
}
```

---

## 14.5 Xóa phiên chat

### Endpoint

```http
DELETE /api/chat/sessions/{sessionId}
```

---

## 14.6 Các lỗi

| Status | Error |
|---:|---|
|401|AUTH_UNAUTHORIZED|
|403|CHAT_SESSION_ACCESS_DENIED|
|404|CHAT_SESSION_NOT_FOUND|
|502|AI_PROVIDER_UNAVAILABLE|
|503|AI_PROVIDER_TIMEOUT|

---

# 15. Export API

## 15.1 Danh sách Endpoint

| Method | Endpoint |
|---|---|
| GET | `/api/export/csv` |
| GET | `/api/export/excel` |

---

## 15.2 Xuất CSV

### Endpoint

```http
GET /api/export/csv
```

### Query Parameters

```text
fromDate

toDate

type

categoryId
```

Ví dụ

```http
GET /api/export/csv?fromDate=2026-07-01&toDate=2026-07-31
```

### Response

```http
Content-Type: text/csv

Content-Disposition:
attachment;
filename="transactions.csv"
```

---

## 15.3 Xuất Excel

### Endpoint

```http
GET /api/export/excel
```

Response

```http
Content-Type:
application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
```

---

## 15.4 Các lỗi

| Status | Error |
|---:|---|
|400|EXPORT_INVALID_DATE_RANGE|
|404|EXPORT_NO_DATA|
|500|EXPORT_GENERATION_FAILED|

---

# 16. Error Codes

## 16.1 Authentication

| Error Code | Ý nghĩa |
|---|---|
| AUTH_UNAUTHORIZED | Chưa đăng nhập |
| AUTH_INVALID_CREDENTIALS | Sai email hoặc mật khẩu |
| AUTH_EMAIL_ALREADY_EXISTS | Email đã tồn tại |
| AUTH_REFRESH_TOKEN_INVALID | Refresh Token không hợp lệ |
| AUTH_REFRESH_TOKEN_EXPIRED | Refresh Token hết hạn |

---

## 16.2 Category

| Error Code | Ý nghĩa |
|---|---|
| CATEGORY_NOT_FOUND | Không tìm thấy Category |
| CATEGORY_IN_USE | Category đang được sử dụng |
| CATEGORY_NAME_EXISTS | Trùng tên |
| CATEGORY_DEFAULT_READ_ONLY | Category mặc định |

---

## 16.3 Transaction

| Error Code | Ý nghĩa |
|---|---|
| TRANSACTION_NOT_FOUND | Không tìm thấy giao dịch |
| TRANSACTION_INVALID_AMOUNT | Số tiền không hợp lệ |
| TRANSACTION_FUTURE_DATE | Ngày giao dịch không hợp lệ |
| TRANSACTION_CATEGORY_TYPE_MISMATCH | Không cùng loại Category |

---

## 16.4 Budget

| Error Code | Ý nghĩa |
|---|---|
| BUDGET_NOT_FOUND | Không tìm thấy Budget |
| BUDGET_ALREADY_EXISTS | Đã tồn tại Budget |
| BUDGET_INVALID_AMOUNT | Hạn mức không hợp lệ |

---

## 16.5 AI

| Error Code | Ý nghĩa |
|---|---|
| AI_PROVIDER_TIMEOUT | AI phản hồi quá chậm |
| AI_PROVIDER_UNAVAILABLE | Không kết nối AI |
| AI_CONTEXT_EMPTY | Không đủ dữ liệu phân tích |

---

## 16.6 Notification

| Error Code | Ý nghĩa |
|---|---|
| NOTIFICATION_NOT_FOUND | Không tìm thấy thông báo |

---

## 16.7 Export

| Error Code | Ý nghĩa |
|---|---|
| EXPORT_NO_DATA | Không có dữ liệu |
| EXPORT_GENERATION_FAILED | Không tạo được file |

---

# 17. Swagger / OpenAPI

## 17.1 URL

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON

```text
http://localhost:8080/v3/api-docs
```

---

## 17.2 Yêu cầu

Mỗi API cần có:

- Summary
- Description
- Request Example
- Response Example
- HTTP Status
- Security Requirement

Ví dụ

```java
@Operation(
    summary = "Tạo giao dịch",
    description = "Tạo mới một khoản thu hoặc chi"
)
```

---

## 17.3 Security Scheme

```text
Bearer JWT
```

Swagger cần hỗ trợ nút **Authorize** để nhập Access Token.

---

# 18. Versioning

Phiên bản đầu tiên:

```text
/api/...
```

Nếu sau này có thay đổi lớn:

```text
/api/v2/...
```

Không nên thêm version ngay khi chưa cần.

---

# 19. API Naming Convention

Quy tắc đặt tên:

✅ Đúng

```text
GET /transactions

POST /transactions

PATCH /transactions/{id}

DELETE /transactions/{id}
```

❌ Không nên

```text
/getTransaction

/createTransaction

/deleteTransaction
```

Ưu tiên sử dụng HTTP Method thay cho động từ trong URL.

---

# 20. Tổng kết

Hệ thống SmartSpend hiện có khoảng **30–35 REST API**, được chia theo 8 module:

| Module | Số API dự kiến |
|---|---:|
| Authentication | 5 |
| Category | 5 |
| Transaction | 5 |
| Budget | 5 |
| Dashboard | 4 |
| Notification | 5 |
| AI Assistant | 5 |
| Export | 2 |

Tất cả API tuân thủ các nguyên tắc:

- Thiết kế RESTful.
- JWT Authentication.
- Response thống nhất.
- Mã lỗi chuẩn hóa.
- Kiểm tra quyền sở hữu dữ liệu.
- Hỗ trợ Swagger/OpenAPI.
- Có khả năng mở rộng trong các phiên bản tiếp theo.

Tài liệu API Design là cơ sở để triển khai Controller, Service, Swagger và viết Integration Test cho toàn bộ hệ thống.