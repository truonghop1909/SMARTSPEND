# Business Rules

## 1. Mục đích

Tài liệu mô tả các quy tắc nghiệp vụ của hệ thống SmartSpend.

Mọi chức năng phải tuân theo các quy tắc trong tài liệu này.

---

# 2. Authentication

## BR-AUTH-001

Người dùng đăng ký bằng Email.

Email phải là duy nhất.

---

## BR-AUTH-002

Người dùng đăng nhập bằng:

- Email
- Password

---

## BR-AUTH-003

Một Email chỉ thuộc một tài khoản.

---

## BR-AUTH-004

Người dùng chỉ được truy cập dữ liệu của chính mình.

---

## BR-AUTH-005

Tài khoản có các trạng thái:

- Active
- Inactive
- Locked

---

# 3. Category

## BR-CATEGORY-001

Mỗi Category thuộc về một User.

---

## BR-CATEGORY-002

Category có hai loại:

- Income
- Expense

---

## BR-CATEGORY-003

Tên Category không được trùng trong cùng loại.

---

## BR-CATEGORY-004

Không được xóa Category đang có Transaction.

---

# 4. Transaction

## BR-TRANSACTION-001

Mỗi Transaction thuộc:

- một User
- một Category

---

## BR-TRANSACTION-002

Transaction có hai loại:

- Income
- Expense

---

## BR-TRANSACTION-003

Số tiền phải lớn hơn 0.

---

## BR-TRANSACTION-004

Transaction phải có ngày giao dịch.

---

## BR-TRANSACTION-005

Transaction chỉ được sửa bởi chủ sở hữu.

---

# 5. Budget

## BR-BUDGET-001

Budget được tạo theo:

- Category
- Tháng
- Năm

---

## BR-BUDGET-002

Một Category chỉ có một Budget trong một tháng.

---

## BR-BUDGET-003

Budget chỉ áp dụng cho Expense.

---

## BR-BUDGET-004

Budget được cập nhật khi Transaction thay đổi.

---

# 6. Dashboard

## BR-DASHBOARD-001

Dashboard chỉ hiển thị dữ liệu của User hiện tại.

---

## BR-DASHBOARD-002

Dashboard thống kê từ Transaction.

Không lưu dữ liệu riêng.

---

# 7. Notification

## BR-NOTIFICATION-001

Hệ thống gửi cảnh báo khi Budget đạt 80%.

---

## BR-NOTIFICATION-002

Hệ thống gửi cảnh báo khi Budget vượt 100%.

---

# 8. AI Assistant

## BR-AI-001

AI chỉ phân tích dữ liệu của User hiện tại.

---

## BR-AI-002

AI không được tự sửa dữ liệu.

---

# 9. Export

## BR-EXPORT-001

Người dùng chỉ được Export dữ liệu của chính mình.

---

## BR-EXPORT-002

Hỗ trợ:

- CSV
- Excel

---

# 10. Tổng kết

Mọi module trong SmartSpend phải tuân theo các Business Rules trên.

Nếu thay đổi Business Rule phải cập nhật:

- Database Design
- Module Design
- API Design