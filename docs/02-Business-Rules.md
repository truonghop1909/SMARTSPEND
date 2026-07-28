# Quy tắc nghiệp vụ

# SmartSpend

### AI Personal Finance Manager

---

# 1. Giới thiệu

## 1.1 Mục đích

Tài liệu này mô tả các quy tắc nghiệp vụ (Business Rules) của hệ thống SmartSpend.

Business Rule là những quy định mà hệ thống bắt buộc phải tuân thủ trong quá trình xử lý dữ liệu và nghiệp vụ.

Các tài liệu như Database Design, API Design, Module Design và Source Code đều phải được xây dựng dựa trên những quy tắc này.

---

## 1.2 Phạm vi

Các Business Rule được chia theo từng miền nghiệp vụ (Domain) thay vì theo bảng dữ liệu.

Điều này giúp tài liệu dễ bảo trì và phù hợp với cách tổ chức mã nguồn theo Feature.

---

# 2. Quy tắc xác thực (Authentication)

| Mã | Quy tắc |
|-----|----------|
| AUTH-001 | Người dùng phải đăng nhập trước khi sử dụng hệ thống. |
| AUTH-002 | Email của mỗi người dùng là duy nhất. |
| AUTH-003 | Mật khẩu phải được mã hóa trước khi lưu. |
| AUTH-004 | Mọi API (trừ Login và Register) đều yêu cầu Access Token hợp lệ. |
| AUTH-005 | Refresh Token chỉ được sử dụng để cấp Access Token mới. |
| AUTH-006 | Người dùng chỉ được truy cập dữ liệu của chính mình. |
| AUTH-007 | Hệ thống không lưu mật khẩu dưới dạng văn bản thuần (Plain Text). |

---

# 3. Quy tắc quản lý danh mục (Category)

| Mã | Quy tắc |
|-----|----------|
| CAT-001 | Danh mục được chia thành hai loại: Thu nhập (Income) và Chi tiêu (Expense). |
| CAT-002 | Người dùng có thể tạo danh mục riêng. |
| CAT-003 | Hệ thống cung cấp một số danh mục mặc định. |
| CAT-004 | Danh mục mặc định không được chỉnh sửa hoặc xóa. |
| CAT-005 | Không được tạo hai danh mục cùng tên trong cùng một loại. |
| CAT-006 | Không được xóa danh mục đang được sử dụng bởi giao dịch. |

---

# 4. Quy tắc quản lý giao dịch (Transaction)

| Mã | Quy tắc |
|-----|----------|
| TRAN-001 | Mỗi giao dịch thuộc về một người dùng. |
| TRAN-002 | Mỗi giao dịch phải được phân loại vào một danh mục. |
| TRAN-003 | Giao dịch chỉ có hai loại: Thu nhập hoặc Chi tiêu. |
| TRAN-004 | Loại giao dịch phải trùng với loại của danh mục. |
| TRAN-005 | Giá trị giao dịch phải lớn hơn 0. |
| TRAN-006 | Mỗi giao dịch phải có ngày phát sinh. |
| TRAN-007 | Không được tạo giao dịch trong tương lai. |
| TRAN-008 | Người dùng chỉ được chỉnh sửa giao dịch của chính mình. |
| TRAN-009 | Xóa giao dịch sử dụng Soft Delete. |
| TRAN-010 | Các giao dịch đã xóa không được sử dụng để thống kê. |

---

# 5. Quy tắc quản lý ngân sách (Budget)

| Mã | Quy tắc |
|-----|----------|
| BUD-001 | Ngân sách chỉ áp dụng cho danh mục Chi tiêu. |
| BUD-002 | Một ngân sách chỉ áp dụng cho một tháng. |
| BUD-003 | Một danh mục chỉ có một ngân sách trong cùng một tháng. |
| BUD-004 | Giá trị ngân sách phải lớn hơn 0. |
| BUD-005 | Tiến độ ngân sách được tính từ tổng các khoản Chi tiêu. |
| BUD-006 | Thu nhập không được tính vào ngân sách. |
| BUD-007 | Khi chi tiêu vượt ngân sách, hệ thống phải tạo cảnh báo. |

---

# 6. Quy tắc Dashboard

| Mã | Quy tắc |
|-----|----------|
| DASH-001 | Dashboard chỉ đọc dữ liệu, không cập nhật dữ liệu. |
| DASH-002 | Dashboard chỉ hiển thị dữ liệu của người dùng hiện tại. |
| DASH-003 | Tổng thu được tính từ các giao dịch Thu nhập. |
| DASH-004 | Tổng chi được tính từ các giao dịch Chi tiêu. |
| DASH-005 | Chênh lệch tài chính = Tổng thu - Tổng chi. |
| DASH-006 | Dashboard không tính các giao dịch đã bị xóa. |

---

# 7. Quy tắc AI Financial Assistant

| Mã | Quy tắc |
|-----|----------|
| AI-001 | AI chỉ được phân tích dữ liệu của người dùng hiện tại. |
| AI-002 | AI không được phép chỉnh sửa dữ liệu trong hệ thống. |
| AI-003 | AI chỉ trả lời dựa trên dữ liệu hiện có của người dùng. |
| AI-004 | Nếu dữ liệu không đủ để phân tích, AI phải thông báo rõ cho người dùng. |
| AI-005 | Lịch sử hội thoại được lưu để cải thiện trải nghiệm người dùng. |
| AI-006 | API Key của AI không được lưu trong cơ sở dữ liệu hoặc trả về cho Client. |

---

# 8. Quy tắc thông báo (Notification)

| Mã | Quy tắc |
|-----|----------|
| NOTI-001 | Mỗi thông báo thuộc về một người dùng. |
| NOTI-002 | Hệ thống tạo thông báo khi ngân sách vượt ngưỡng cảnh báo. |
| NOTI-003 | Người dùng có thể đánh dấu thông báo đã đọc. |
| NOTI-004 | Người dùng không được xem thông báo của người khác. |

---

# 9. Quy tắc xuất báo cáo (Export)

| Mã | Quy tắc |
|-----|----------|
| EXP-001 | Chỉ xuất dữ liệu của người dùng hiện tại. |
| EXP-002 | Chỉ xuất các giao dịch chưa bị xóa. |
| EXP-003 | Hỗ trợ xuất CSV và Excel. |
| EXP-004 | Khoảng thời gian xuất báo cáo phải hợp lệ. |

---

# 10. Quy tắc bảo mật (Security)

| Mã | Quy tắc |
|-----|----------|
| SEC-001 | Mọi dữ liệu phải được kiểm tra quyền sở hữu trước khi xử lý. |
| SEC-002 | Không trả về mật khẩu trong bất kỳ API nào. |
| SEC-003 | Không ghi log các thông tin nhạy cảm như mật khẩu, Access Token hoặc API Key. |
| SEC-004 | Chỉ người dùng đã xác thực mới được truy cập API được bảo vệ. |

---

# 11. Tổng kết

Business Rules là nền tảng của toàn bộ hệ thống SmartSpend.

Tất cả các tài liệu thiết kế, cơ sở dữ liệu, API và mã nguồn đều phải tuân thủ các quy tắc được định nghĩa trong tài liệu này.

Khi phát sinh yêu cầu mới, Business Rule cần được cập nhật trước, sau đó mới tiến hành điều chỉnh Database, API và Source Code để đảm bảo tính nhất quán của hệ thống.