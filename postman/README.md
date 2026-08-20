# SmartSpend Postman

## Files

- `SmartSpend.postman_collection.json`: Collection chứa các request API.
- `SmartSpend.local.postman_environment.json`: Biến môi trường local.

## Import

1. Mở Postman.
2. Chọn **Import**.
3. Import cả hai file JSON.
4. Chọn environment **SmartSpend Local**.
5. Chạy Backend.
6. Mở request `Common / Health Check`.
7. Nhấn **Send**.

## Local Environment

```text
baseUrl=http://localhost:8080
accessToken=
refreshToken=
traceId=
```

## Health Check

```text
GET {{baseUrl}}/api/health
```
