# Kế hoạch tạo mã nguồn bằng ChatGPT

## SmartSpend

### AI Personal Finance Manager

---

## 1. Mục đích

Tài liệu này định nghĩa cách sử dụng ChatGPT để hỗ trợ tạo mã nguồn cho dự án **SmartSpend**.

Mục tiêu là:

* Mỗi phiên chat chỉ xử lý một phần rõ ràng.
* Code sinh ra phải thống nhất với tài liệu thiết kế.
* Hạn chế việc ChatGPT tự thêm chức năng ngoài phạm vi.
* Dễ kiểm tra và sửa lỗi.
* Không sinh toàn bộ project trong một lần.
* Đảm bảo mỗi module hoạt động trước khi chuyển sang module tiếp theo.
* Với AI Module, người phát triển phải hiểu khái niệm trước khi triển khai code.
* AI chạy local và không phụ thuộc Cloud AI Provider trong phiên bản đầu tiên.

---

## 2. Nguyên tắc làm việc với ChatGPT

### 2.1 Mỗi phiên chat chỉ xử lý một nhiệm vụ

Ví dụ:

```text
Chat 01: Khởi tạo project

Chat 02: Cấu hình Docker

Chat 03: Common Response và Exception

Chat 04: Flyway

Chat 05: Authentication Entity và Repository
```

Không yêu cầu:

```text
Hãy tạo toàn bộ project SmartSpend.
```

Vì dễ dẫn đến:

* Thiếu file.
* Sai package.
* Mâu thuẫn giữa module.
* Code không chạy.
* Khó kiểm tra lỗi.
* Tự thêm thiết kế không cần thiết.

---

### 2.2 Chỉ tạo code sau khi đọc tài liệu liên quan

Trước mỗi phiên chat cần tuân thủ:

```text
README.md
docs/01-Architecture.md
docs/02-Business-Rules.md
docs/03-Database-Design.md
docs/04-Module-Design.md
docs/05-API-Design.md
docs/08-Coding-Conventions.md
docs/09-Development-Roadmap.md
docs/12-Project-Structure.md
```

Đối với AI Module cần thêm:

```text
docs/06-AI-Module.md
```

AI Module phải tuân thủ thêm quyết định:

```text
Ollama
Local LLM
AI Read-Only
Financial Context
Không dùng Cloud AI API trong V1
Không dùng RAG trong V1
```

---

### 2.3 Không tự ý thay đổi thiết kế

ChatGPT không được:

* Thêm bảng mới.
* Thêm module mới.
* Đổi tên package.
* Đổi endpoint.
* Đổi response contract.
* Thêm Wallet.
* Thêm Transfer.
* Thêm Payment.
* Thêm Microservices.
* Thêm Kafka hoặc RabbitMQ.
* Thay đổi Business Rules.
* Tự chuyển AI sang OpenAI/Gemini.
* Tự thêm RAG.
* Tự thêm Vector Database.
* Tự thêm AI Agent.
* Cho Local LLM truy cập Database trực tiếp.

Nếu phát hiện vấn đề:

1. Nêu vấn đề.
2. Đưa tối đa hai phương án.
3. Không tự thiết kế lại project.

---

### 2.4 Mỗi lần chỉ tạo một nhóm file nhỏ

Mỗi phiên nên tạo tối đa:

```text
3 đến 8 file
```

AI Module cũng phải chia nhỏ:

```text
Infrastructure
Entity
Financial Context
Prompt
Ollama Client
Service
Controller
Protection
Test
```

Không tạo toàn bộ AI Module trong một lần.

---

### 2.5 Mỗi response phải có cấu trúc rõ ràng

ChatGPT phải trả:

1. Danh sách file.
2. Đường dẫn.
3. Nội dung đầy đủ.
4. Vai trò từng file.
5. Luồng chạy.
6. Cách kiểm tra.
7. Lệnh test.
8. Các file cần sửa do dependency thay đổi.

---

# 3. Prompt nền dùng cho mọi phiên code

```text
Bạn là Senior Java Backend Developer và Technical Mentor.

Tôi đang phát triển:

SmartSpend — AI Personal Finance Manager

Công nghệ:

- Java 21
- Spring Boot 3.5
- Spring Security
- JWT
- Spring Data JPA
- MySQL 8
- Redis
- Flyway
- Maven
- MapStruct
- Swagger/OpenAPI
- Docker
- Ollama
- Local LLM

SmartSpend sử dụng AI chạy local.

Không sử dụng OpenAI API hoặc Gemini API trong phiên bản đầu tiên.

AI Runtime:

Spring Boot
    ↓
Ollama Local HTTP API
    ↓
Local LLM

AI chỉ được đọc và phân tích dữ liệu.

AI không được trực tiếp:
- tạo Transaction
- sửa Transaction
- xóa Transaction
- tạo Budget
- sửa Budget
- xóa Budget
- thực thi SQL

Dữ liệu AI phải đi qua:

MySQL
    ↓
Repository
    ↓
Service
    ↓
Financial Context
    ↓
Prompt
    ↓
Ollama

Yêu cầu bắt buộc:

1. Tuân thủ tài liệu thiết kế.
2. Không tự thêm chức năng ngoài phạm vi.
3. Không thêm Wallet, Transfer, Payment hoặc Bank Account.
4. Package by Feature.
5. Controller → Service → Repository.
6. Không trả Entity qua API.
7. Tiền dùng BigDecimal.
8. userId lấy từ SecurityContext.
9. Kiểm tra Ownership.
10. Constructor Injection.
11. Không Field Injection.
12. Không dùng @Data cho Entity.
13. Enum dùng EnumType.STRING.
14. AppException + ErrorCode.
15. ApiResponse/PageResponse thống nhất.
16. Không hard-code secret.
17. Không log password/JWT/token.
18. Java 21 + Spring Boot 3.5.
19. Không dùng API deprecated.
20. Chỉ tạo đúng nhóm file được yêu cầu.
21. AI chạy local bằng Ollama.
22. Không tự thêm OpenAI/Gemini.
23. Không thêm RAG nếu chưa được yêu cầu.
24. Không cho LLM truy cập Database trực tiếp.
25. AI Provider phải có abstraction.
26. Không gửi secret vào prompt.
27. Không gửi JWT vào prompt.
28. Không gửi toàn bộ Transaction cho LLM.
29. Financial Context ưu tiên Aggregate Query.
30. Unit Test không phụ thuộc Ollama thật.

Cách trả lời:

- Ghi rõ đường dẫn.
- Viết toàn bộ code.
- Không dùng ...
- Không pseudo-code.
- Không bỏ import.
- Không tạo file ngoài danh sách nếu chưa cần.
- Nếu phải sửa file cũ vì constructor/dependency mới, phải nói rõ.
- Sau code đưa lệnh compile/test.
```

---

# 4. Kế hoạch các phiên chat

## Chat 01 → Chat 23

Giữ nguyên các phần:

```text
Project Setup
Docker
Common
Flyway
Authentication
Category
Transaction
Budget
Notification
Dashboard
Dashboard Test
```

Theo trạng thái hiện tại của project.

---

# 5. AI Assistant — Kiến thức trước khi code

Trước Chat 24, người phát triển cần hiểu các khái niệm sau ở mức đủ dùng cho AI Application Backend:

```text
AI
Machine Learning
Deep Learning
Neural Network
Transformer
Attention
LLM
Token
Tokenizer
Context Window
Inference
Model Parameters
Temperature
Sampling
Hallucination
Grounding
Prompt Engineering
System Prompt
User Prompt
Structured Output
Ollama
Local LLM
Quantization
RAM / VRAM
Prompt Injection
Rate Limit
Timeout
```

Không yêu cầu phải học trước:

```text
PyTorch nâng cao
TensorFlow
Training Model
Fine-tuning
LoRA
CUDA Programming
RAG
Vector Database
AI Agent
```

Các phần này có thể học sau.

---

# 6. AI Assistant

## Chat 24 — Local AI Infrastructure

### Mục tiêu

Thiết lập nền tảng để Spring Boot giao tiếp với Local LLM.

### Cần hiểu trước

```text
Inference
LLM
Ollama
Local Model
HTTP API
Model Parameters
Temperature
Context Window
```

### File

```text
ai/config/AiProperties.java

ai/config/AiConfig.java

ai/client/AiProviderClient.java
```

### Cấu hình dự kiến

```text
app.ai.provider=ollama
app.ai.base-url=http://localhost:11434
app.ai.model=<model>
app.ai.timeout=<duration>
```

### Yêu cầu

* Ollama chạy local.
* Model configurable.
* Base URL configurable.
* Không API Key.
* Không hard-code model trong Service.
* Business logic không phụ thuộc trực tiếp Ollama.

### Kiểm tra

```text
ollama list
```

và gọi Ollama local thành công.

---

## Chat 25 — Chat Entity và Repository

### Cần hiểu trước

```text
Chat Session
Chat History
Context
Context Window
Ownership
```

### File

```text
ai/entity/ChatSession.java

ai/entity/ChatMessage.java

ai/entity/ChatRole.java

ai/repository/ChatSessionRepository.java

ai/repository/ChatMessageRepository.java
```

### Yêu cầu

* Quan hệ LAZY.
* Session thuộc User.
* Message thuộc Session.
* Ownership.
* Pagination.
* Không load toàn bộ Chat History.
* Giới hạn History đưa cho LLM.

---

## Chat 26 — AI DTO và Financial Context

### Cần hiểu trước

```text
Grounding
Hallucination
Structured Data
Context
Context Window
```

### File

```text
ai/dto/request/SendMessageRequest.java

ai/dto/response/ChatSessionResponse.java

ai/dto/response/ChatMessageResponse.java

ai/dto/response/ChatResponse.java

ai/context/FinancialContext.java

ai/context/CategorySpendingContext.java

ai/context/BudgetContext.java

ai/context/PeriodComparisonContext.java

ai/context/FinancialContextService.java

ai/context/FinancialContextServiceImpl.java

ai/cache/FinancialContextCacheService.java
```

### Financial Context lấy từ

```text
Transaction Aggregate Query
Budget
Category Statistic
Monthly Trend
Period Comparison
```

### Không làm

```text
SELECT *
→ List<Transaction>
→ LLM
```

### Làm

```text
SUM / GROUP BY
      ↓
FinancialContext
      ↓
LLM
```

### Cache key

```text
financial-context:user:{userId}
```

### Yêu cầu

* Không tính Transaction đã Soft Delete.
* Không gửi Entity.
* Không gửi dữ liệu User khác.
* Redis chỉ là cache.
* MySQL là Source of Truth.

---

## Chat 27 — Prompt Builder và AI Safety

### Cần hiểu trước

```text
Prompt Engineering
System Prompt
User Prompt
Prompt Template
Prompt Injection
Hallucination
Grounding
```

### File

```text
ai/prompt/SystemPromptProvider.java

ai/prompt/FinancialPromptBuilder.java

ai/model/ConversationMessage.java

ai/model/AiRequest.java

ai/model/AiResult.java
```

### Prompt structure

```text
SYSTEM RULES

FINANCIAL CONTEXT

CHAT HISTORY

USER MESSAGE
```

### Yêu cầu

* System Prompt cố định.
* User message không được biến thành System Instruction.
* Financial Context có cấu trúc.
* Giới hạn lịch sử.
* Không gửi secret.
* Không gửi password.
* Không gửi JWT.
* Không gửi Refresh Token.
* AI chỉ đọc dữ liệu.
* Nếu thiếu dữ liệu phải nói không đủ dữ liệu.
* Không tự bịa số liệu.

---

## Chat 28 — Ollama Client

### Cần hiểu trước

```text
Inference
HTTP Client
Ollama REST API
Request/Response
Timeout
Structured Output
```

### File

```text
ai/client/AiProviderClient.java

ai/client/OllamaClient.java

ai/model/OllamaChatRequest.java

ai/model/OllamaChatResponse.java

ai/parser/AiResponseParser.java
```

### Kiến trúc

```text
AiChatService
      ↓
AiProviderClient
      ↓
OllamaClient
      ↓
localhost:11434
      ↓
Ollama
      ↓
Local LLM
```

### Yêu cầu

* Local HTTP.
* Non-streaming trong V1.
* Timeout.
* Parse response.
* Model not found.
* Ollama unavailable.
* Không retry vô hạn.
* Không gọi Ollama thật trong Unit Test.

---

## Chat 29 — AI Chat Service

### Cần hiểu trước

```text
AI Orchestration
Inference
Context
Prompt
Conversation History
```

### File

```text
ai/service/AiService.java

ai/service/AiServiceImpl.java

ai/service/ChatService.java

ai/service/ChatServiceImpl.java
```

### Luồng

```text
Current User
     ↓
Load/Create ChatSession
     ↓
Save USER Message
     ↓
FinancialContextService
     ↓
FinancialPromptBuilder
     ↓
AiProviderClient
     ↓
Ollama
     ↓
Local LLM
     ↓
Save ASSISTANT Message
     ↓
Response
```

### Transaction boundary

Không giữ Database Transaction mở trong lúc Local LLM đang inference.

Không làm:

```text
@Transactional
    ↓
INSERT user message
    ↓
gọi Ollama 20 giây
    ↓
INSERT assistant message
```

theo cách giữ connection DB suốt thời gian inference.

---

## Chat 30 — AI Controller

### File

```text
ai/controller/ChatController.java
```

### API

```text
POST /api/ai/chat

GET /api/ai/sessions

GET /api/ai/sessions/{id}/messages

DELETE /api/ai/sessions/{id}
```

### Yêu cầu

* Authentication Required.
* Không nhận userId.
* Ownership.
* Không cho client chọn System Prompt.
* Không cho client gửi Financial Context trực tiếp.
* Không cho client chọn model tùy ý trong V1.

---

## Chat 31 — AI Rate Limit, Timeout và Protection

### Cần hiểu trước

```text
Prompt Injection
Resource Exhaustion
Rate Limiting
Timeout
Retry
Context Limit
```

### Chức năng

* Redis AI Rate Limit.
* Giới hạn request/user.
* Giới hạn prompt length.
* Giới hạn Chat History.
* Timeout Ollama.
* Provider unavailable.
* Model not found.
* Retry có giới hạn.
* Không retry validation error.
* Không retry model-not-found.
* Không để request treo vô hạn.

### Redis key

```text
ai-rate-limit:user:{userId}
```

---

## Chat 32 — AI Test

### File

```text
ai/context/FinancialContextServiceImplTest.java

ai/prompt/FinancialPromptBuilderTest.java

ai/service/ChatServiceImplTest.java

ai/client/OllamaClientTest.java

ai/controller/ChatControllerIntegrationTest.java
```

### Test

```text
Financial Context đúng
Soft Deleted Transaction không được tính
Ownership
Cache Hit
Cache Miss
Prompt đúng cấu trúc
Không lộ secret
Chat History limit
Save USER Message
Save ASSISTANT Message
Provider Success
Provider Timeout
Provider Unavailable
Model Not Found
Rate Limit
Authentication
```

### Quy tắc quan trọng

Unit/Integration Test:

```text
Mock AiProviderClient
```

Không yêu cầu Ollama thật chạy mỗi lần:

```text
mvn clean test
```

Ollama thật chỉ dùng trong:

```text
Manual Integration Test
Postman Test
```

---

## Chat 33 — AI Local End-to-End Test

### Mục tiêu

Test toàn bộ:

```text
Postman
   ↓
Spring Boot
   ↓
JWT
   ↓
FinancialContext
   ↓
Redis
   ↓
Prompt
   ↓
Ollama
   ↓
Local LLM
   ↓
Chat History
```

### Test

* Ollama đang chạy.
* Model đã pull.
* Spring Boot kết nối được.
* User hỏi được AI.
* AI nhận đúng Financial Context.
* Chat được lưu DB.
* AI không thấy dữ liệu User khác.
* Financial Context Cache hoạt động.
* Transaction thay đổi → cache bị invalidate.
* Budget thay đổi → cache bị invalidate.
* Ollama tắt → API trả lỗi phù hợp.

---

# 7. Export Module

Sau khi AI hoàn thành:

## Chat 34 — CSV và Excel Export

### File

```text
export/dto/request/ExportTransactionRequest.java

export/model/ExportedFile.java

export/generator/CsvReportGenerator.java

export/generator/ExcelReportGenerator.java

export/service/ExportService.java

export/service/ExportServiceImpl.java

export/controller/ExportController.java
```

### Yêu cầu

* CSV.
* Excel.
* Filter.
* Ownership.
* Không export Transaction đã xóa.
* Trả file trực tiếp.

---

# 8. Hoàn thiện

## Chat 35 — Test tổng thể

Kiểm tra:

```text
Authentication
Category
Transaction
Budget
Notification
Dashboard
Local AI
Export
```

Lệnh:

```bash
mvn clean test
```

Ollama thật không phải dependency bắt buộc của test suite.

---

## Chat 36 — Docker hóa Backend

### File

```text
Dockerfile
docker-compose.yml
```

### Yêu cầu

* Multi-stage build.
* Backend.
* MySQL.
* Redis.
* Ollama được đánh giá riêng trước khi đưa vào Docker Compose.
* Không bắt buộc Docker hóa Local LLM ở V1 nếu host Ollama thuận tiện hơn.

---

## Chat 37 — Postman Collection

### Nội dung

```text
Authentication
Category
Transaction
Budget
Notification
Dashboard
AI
Export
```

AI cần có:

```text
POST /api/ai/chat
GET sessions
GET messages
DELETE session
```

---

## Chat 38 — README và Demo

Thêm:

```text
Local AI Architecture
Ollama Installation
Model Setup
AI Screenshot
AI Demo
Financial Context Flow
AI Privacy
AI Read-Only Rule
```

---

# 9. Prompt mẫu cho AI Module

```text
Tôi đang xây dựng AI Financial Assistant của SmartSpend.

AI phải chạy local bằng Ollama.

Không sử dụng:
- OpenAI API
- Gemini API
- Cloud LLM
- RAG
- Vector Database
- AI Agent

Kiến trúc:

Spring Boot
    ↓
FinancialContextService
    ↓
PromptBuilder
    ↓
AiProviderClient
    ↓
OllamaClient
    ↓
Ollama Local
    ↓
Local LLM

Yêu cầu:

1. AI chỉ đọc dữ liệu.
2. Không cho LLM truy cập Database trực tiếp.
3. Không gửi toàn bộ Transaction cho LLM.
4. Dùng Aggregate Query để tạo Financial Context.
5. Không gửi secret/JWT/password/token.
6. userId lấy từ SecurityContext.
7. Ownership được kiểm tra ở Backend.
8. Ollama URL và model phải configurable.
9. Không hard-code model trong Service.
10. Có timeout.
11. Unit Test mock AiProviderClient.
12. Không cần Ollama thật để mvn test.
13. Không tự thêm dependency mới nếu chưa cần.
14. Chỉ tạo đúng file tôi yêu cầu.
```

---

# 10. Prompt review code — bổ sung AI

Khi review AI code cần kiểm tra thêm:

```text
14. AI có gọi Database trực tiếp không?
15. Có gửi Entity cho LLM không?
16. Có gửi secret/JWT/token không?
17. Financial Context có lấy đúng current user không?
18. Có Prompt Injection Protection không?
19. OllamaClient có timeout không?
20. AI Service có giữ DB Transaction trong lúc inference không?
21. Có Rate Limit không?
22. Chat History có giới hạn không?
23. Unit Test có phụ thuộc Ollama thật không?
24. AI có quyền sửa dữ liệu nghiệp vụ không?
25. Provider có được abstraction qua interface không?
```

---

# 11. Prompt sửa lỗi AI

```text
SmartSpend Local AI đang gặp lỗi.

Stack:

Spring Boot
Ollama
Local LLM
MySQL
Redis

Tôi sẽ gửi:
- lỗi
- log
- file liên quan
- cấu hình Ollama nếu cần

Hãy:

1. Phân biệt lỗi Spring Boot, HTTP Client, Ollama hay Model.
2. Xác định nguyên nhân gốc.
3. Không đoán nếu log chưa đủ.
4. Không chuyển sang OpenAI/Gemini để né lỗi.
5. Không thay kiến trúc Local AI.
6. Chỉ sửa file liên quan.
7. Không thêm dependency nếu chưa cần.
8. Viết đầy đủ file cần sửa.
9. Đưa lệnh kiểm tra Ollama.
10. Đưa lệnh/test Spring Boot sau khi sửa.
```

---

# 12. Quy trình sau mỗi phiên AI

Ngoài quy trình code thông thường:

```text
1. Hiểu khái niệm AI liên quan.
2. Xác định vai trò class.
3. Copy code.
4. Compile.
5. Unit Test.
6. Kiểm tra Ollama nếu phiên có liên quan.
7. Test API.
8. Kiểm tra Database.
9. Kiểm tra Redis.
10. Kiểm tra prompt không lộ dữ liệu nhạy cảm.
11. Git diff.
12. Commit.
13. Update Development Log.
14. Update Project Structure.
15. Update Roadmap.
```

---

# 13. Quy tắc commit AI

Ví dụ:

```text
feat(ai): add local ollama configuration

feat(ai): add chat entities and repositories

feat(ai): add financial context builder

feat(ai): add financial context redis cache

feat(ai): add financial prompt builder

feat(ai): add ollama client

feat(ai): implement local ai chat service

feat(ai): add ai rate limiting

test(ai): add financial context tests

test(ai): add ollama client tests

docs(ai): document local llm architecture
```

Không dùng commit:

```text
feat(ai): integrate openai
```

trừ khi tương lai project đổi yêu cầu.

---

# 14. Definition of Done cho AI Session

Một phiên AI chỉ hoàn thành khi:

* [ ] Code compile.
* [ ] Test pass.
* [ ] Không import lỗi.
* [ ] Không deprecated API.
* [ ] Không vi phạm Architecture.
* [ ] Không vi phạm Business Rules.
* [ ] Không Cloud API dependency ngoài yêu cầu.
* [ ] Ollama config không hard-code.
* [ ] Model configurable.
* [ ] AI Read-Only.
* [ ] Không gửi Entity.
* [ ] Không gửi secret.
* [ ] Không gửi JWT.
* [ ] Ownership đúng.
* [ ] Financial Context đúng.
* [ ] Soft Deleted Transaction bị loại bỏ.
* [ ] Timeout có cấu hình.
* [ ] AI error có ErrorCode.
* [ ] Test không phụ thuộc Ollama thật.
* [ ] Manual Ollama test nếu cần.
* [ ] Git diff được review.

---

# 15. Thứ tự ưu tiên khi thiếu thời gian

Thứ tự mới:

```text
1. Project Setup
2. Common
3. Flyway
4. Authentication
5. Category
6. Transaction
7. Budget
8. Notification
9. Dashboard
10. Local AI Infrastructure
11. Financial Context
12. Prompt Builder
13. Ollama Client
14. AI Chat cơ bản
15. AI Test
16. Docker
17. Test tổng thể
```

Có thể làm sau:

```text
RAG
Embedding
Vector Database
AI Agent
Tool Calling
Streaming Response
Fine-tuning
Cloud Deployment
Advanced Retry
Circuit Breaker
```

---

# 16. Kiến thức AI học song song với code

Thứ tự học:

```text
AI
 ↓
Machine Learning
 ↓
Deep Learning
 ↓
Neural Network
 ↓
Transformer
 ↓
Attention
 ↓
LLM
 ↓
Token / Tokenizer
 ↓
Context Window
 ↓
Inference
 ↓
Temperature / Sampling
 ↓
Prompt Engineering
 ↓
Hallucination
 ↓
Grounding
 ↓
Structured Output
 ↓
Ollama
 ↓
Local LLM
 ↓
Quantization / RAM / VRAM
 ↓
AI Application Architecture
 ↓
Prompt Injection
 ↓
AI Security
```

Sau khi hoàn thành SmartSpend V1 mới học tiếp:

```text
Embedding
 ↓
Vector Search
 ↓
Vector Database
 ↓
RAG
 ↓
Tool Calling
 ↓
AI Agent
```

---

# 17. Tổng kết

AI Architecture của SmartSpend được chốt:

```text
User
 ↓
Spring Security
 ↓
AI Controller
 ↓
AI Service
 ↓
Financial Context
 ↓
MySQL / Redis
 ↓
Prompt Builder
 ↓
AiProviderClient
 ↓
OllamaClient
 ↓
Ollama Local
 ↓
Local LLM
```

Nguyên tắc:

```text
Java Backend kiểm soát dữ liệu.

LLM chỉ phân tích.
```

Không:

```text
LLM
 ↓
SQL
 ↓
Database
```

Không:

```text
SmartSpend
 ↓
OpenAI/Gemini Cloud
```

trong phiên bản đầu tiên.

Mục tiêu cuối cùng:

```text
SmartSpend
    +
Spring Boot
    +
MySQL
    +
Redis
    +
Ollama
    +
Local LLM
```

tạo thành một project Java Backend có AI chạy local, có dữ liệu thật, caching, security, testing và kiến trúc rõ ràng.
