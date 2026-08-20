# Cập nhật Roadmap — Local AI Financial Assistant

## 1. Thay đổi định hướng AI

SmartSpend sử dụng AI chạy local.

Mục tiêu:

* Không phụ thuộc OpenAI API hoặc Gemini API.
* Không cần API Key cho LLM.
* Dữ liệu tài chính không cần gửi tới AI Provider bên ngoài.
* Model AI chạy trực tiếp trên máy phát triển.
* Spring Boot giao tiếp với AI thông qua Local HTTP API.
* AI chỉ được đọc và phân tích dữ liệu.
* AI không được trực tiếp sửa Transaction hoặc Budget.

Kiến trúc AI:

```text
Client
   ↓
Spring Boot
   ↓
AI Controller
   ↓
AI Service
   ↓
Financial Context Service
   ↓
MySQL + Redis
   ↓
Prompt Builder
   ↓
Ollama Client
   ↓
Ollama Local Server
   ↓
Local LLM
```

---

# 2. Công nghệ sử dụng

Thay:

```text
OpenAI hoặc Gemini API
```

bằng:

```text
Java 21
Spring Boot 3.5
Spring Web
Spring Security
Spring Data JPA
Jakarta Validation
MySQL 8
Redis
Flyway
Maven
Lombok
MapStruct
Springdoc OpenAPI
Docker Compose
JUnit 5
Mockito
Postman
JJWT
Ollama
Local LLM
Spring RestClient hoặc Spring AI
```

Trong phiên bản đầu tiên:

```text
Ollama
    ↓
Local LLM
```

là AI Provider chính.

Không sử dụng:

```text
OpenAI API
Gemini API
LangChain
Vector Database
RAG
```

trong phiên bản đầu tiên.

---

# 3. Phạm vi AI phiên bản đầu tiên

## Có

* AI Financial Assistant.
* Chat Session.
* Chat Message History.
* Phân tích dữ liệu tài chính.
* Phân tích Income và Expense.
* Phân tích Category.
* Phân tích Budget.
* Phân tích Budget Usage.
* Phân tích xu hướng chi tiêu.
* So sánh tháng hiện tại với tháng trước.
* Financial Context.
* Redis Financial Context Cache.
* Local LLM qua Ollama.
* Prompt Builder.
* Prompt Injection Protection.
* AI Rate Limit.
* Timeout.
* Retry có giới hạn.
* Lưu lịch sử Chat vào MySQL.

## Chưa làm

* RAG.
* Vector Database.
* Embedding.
* AI Agent tự chạy tool.
* AI sinh SQL rồi trực tiếp thực thi.
* AI sửa Transaction.
* AI sửa Budget.
* AI tạo Payment.
* Fine-tuning model.
* Training model riêng.
* Cloud AI Provider.

---

# 4. Giai đoạn 7 — AI Financial Assistant

## Trạng thái

```text
Chưa bắt đầu
```

## 7.1 Local AI Infrastructure

* [ ] Cài Ollama.
* [ ] Chọn Local LLM phù hợp cấu hình máy.
* [ ] Pull model bằng Ollama.
* [ ] Kiểm tra model chạy local.
* [ ] Kiểm tra Ollama Local HTTP API.
* [ ] Spring Boot kết nối được Ollama.
* [ ] Không cần Cloud AI API Key.
* [ ] Cấu hình model từ environment/application config.
* [ ] Cấu hình Ollama Base URL.
* [ ] Cấu hình timeout.

Luồng:

```text
Spring Boot
     ↓
localhost:11434
     ↓
Ollama
     ↓
Local LLM
```

---

## 7.2 AI Configuration

Dự kiến:

```text
ai/config/AiProperties.java
ai/config/AiConfig.java
```

Cấu hình:

```text
Base URL
Model
Temperature
Timeout
Max Context
```

Ví dụ logic:

```text
app.ai.provider=ollama
app.ai.base-url=http://localhost:11434
app.ai.model=<local-model>
```

Tên model không hard-code trong Service.

---

## 7.3 AI Provider Client

Dự kiến:

```text
ai/client/AiProviderClient.java
ai/client/OllamaClient.java
```

Abstraction:

```text
AiChatService
      ↓
AiProviderClient
      ↓
OllamaClient
      ↓
Ollama Local API
```

Mục tiêu:

* Business Service không phụ thuộc trực tiếp Ollama.
* Có thể đổi model mà không sửa business logic.
* Có thể bổ sung provider khác về sau nếu cần.

---

## 7.4 Chat Entity

Sử dụng các bảng đã có:

```text
chat_sessions
chat_messages
```

Dự kiến:

```text
ai/entity/ChatSession.java
ai/entity/ChatMessage.java
```

Quan hệ:

```text
User
  ↓
ChatSession
  ↓
ChatMessage
```

Yêu cầu:

* Quan hệ LAZY.
* Ownership theo User.
* Không tải toàn bộ message history nếu không cần.
* Có giới hạn số message đưa vào context AI.

---

## 7.5 Repository

Dự kiến:

```text
ai/repository/ChatSessionRepository.java
ai/repository/ChatMessageRepository.java
```

Chức năng:

* Tìm Session của current User.
* Lấy message theo Session.
* Pagination lịch sử Chat.
* Không truy cập Session của User khác.

---

## 7.6 DTO

Dự kiến:

```text
ai/dto/request/AiChatRequest.java

ai/dto/response/AiChatResponse.java
ai/dto/response/ChatSessionResponse.java
ai/dto/response/ChatMessageResponse.java
```

Request không chứa:

```text
userId
financialContext
systemPrompt
model
```

User chỉ gửi nội dung câu hỏi.

Ví dụ:

```json
{
  "message": "Tháng này tôi đang tiêu nhiều nhất vào đâu?"
}
```

---

## 7.7 Financial Context

Đây là phần trung tâm của AI Module.

Dự kiến:

```text
ai/context/FinancialContext.java
ai/context/FinancialContextService.java
ai/context/FinancialContextServiceImpl.java
```

Financial Context lấy dữ liệu từ:

```text
Transaction
Budget
Category
Dashboard Aggregate Query
```

Không truyền toàn bộ Transaction Entity cho AI.

Không làm:

```text
SELECT *
    ↓
10000 Transaction
    ↓
Local LLM
```

Thay vào đó:

```text
MySQL
  ↓
SUM / GROUP BY
  ↓
FinancialContext
  ↓
Local LLM
```

Context ví dụ:

```text
Period: 2026-08

Total Income:
25,000,000

Total Expense:
18,000,000

Balance:
7,000,000

Expense by Category:
Food:
5,000,000

Shopping:
4,000,000

Transport:
2,000,000

Budgets:
Food:
5,000,000 / 6,000,000

Shopping:
4,000,000 / 3,500,000
```

---

## 7.8 Financial Context Cache

Redis key:

```text
financial-context:user:{userId}
```

Ví dụ:

```text
financial-context:user:1
```

Luồng:

```text
AI request
    ↓
Redis
    ↓
CACHE HIT
    ↓
FinancialContext
```

Nếu cache miss:

```text
AI request
    ↓
Redis MISS
    ↓
MySQL Aggregate Query
    ↓
FinancialContext
    ↓
Redis SET
    ↓
Ollama
```

Cache phải bị xóa khi:

```text
Transaction Create
Transaction Update
Transaction Delete

Budget Create
Budget Update
Budget Delete
```

Phần Transaction và Budget hiện tại đã chuẩn bị:

```text
financial-context:user:{userId}
```

cho mục đích này.

---

## 7.9 Prompt Builder

Dự kiến:

```text
ai/prompt/FinancialPromptBuilder.java
```

Prompt gồm:

```text
System Instruction
Financial Context
Chat History giới hạn
User Message
Safety Rules
```

Ví dụ:

```text
Bạn là trợ lý phân tích tài chính cá nhân của SmartSpend.

Bạn chỉ được phân tích dữ liệu được cung cấp.

Không được giả vờ rằng bạn đã thực hiện giao dịch.

Không được sửa Transaction hoặc Budget.

Không được yêu cầu secret.

Nếu không đủ dữ liệu, hãy nói rõ rằng dữ liệu chưa đủ.

Financial Context:
...

User Question:
...
```

Prompt không chứa:

```text
Password
JWT
Refresh Token
Database Password
Redis Password
Secret Key
```

---

## 7.10 AI Read-Only

AI chỉ được phép:

```text
Đọc dữ liệu đã được Backend tổng hợp
Phân tích
So sánh
Giải thích
Đưa ra nhận xét
Đưa ra gợi ý
```

AI không được:

```text
INSERT Transaction
UPDATE Transaction
DELETE Transaction

CREATE Budget
UPDATE Budget
DELETE Budget
```

Luồng bắt buộc:

```text
Database
   ↓
Java Backend
   ↓
Financial Context
   ↓
Ollama
```

Không cho:

```text
Ollama
   ↓
SQL
   ↓
Database
```

---

## 7.11 Chat Service

Dự kiến:

```text
ai/service/AiChatService.java
ai/service/AiChatServiceImpl.java
```

Luồng:

```text
User Question
     ↓
Current User
     ↓
Load/Create ChatSession
     ↓
Save USER Message
     ↓
FinancialContextService
     ↓
PromptBuilder
     ↓
OllamaClient
     ↓
Local LLM
     ↓
AI Response
     ↓
Save ASSISTANT Message
     ↓
Response DTO
```

---

## 7.12 Controller

Dự kiến:

```text
ai/controller/AiChatController.java
```

API dự kiến:

```text
POST /api/ai/chat

GET /api/ai/sessions

GET /api/ai/sessions/{sessionId}

GET /api/ai/sessions/{sessionId}/messages

DELETE /api/ai/sessions/{sessionId}
```

Tất cả endpoint:

```text
Authentication Required
```

---

## 7.13 Timeout

Local LLM có thể phản hồi chậm hơn REST API thông thường.

Phải có:

```text
Connection Timeout
Read Timeout
```

Nếu Ollama không chạy:

```text
Spring Boot
    ↓
Ollama connection failed
    ↓
AI_PROVIDER_UNAVAILABLE
```

Không để request treo vô hạn.

---

## 7.14 Retry

Không retry vô hạn.

Chỉ retry lỗi phù hợp.

Ví dụ:

```text
Connection reset
Temporary Ollama failure
```

Không retry:

```text
Invalid Request
Model Not Found
Validation Error
```

---

## 7.15 AI Rate Limit

Redis dùng để hạn chế AI request.

Ví dụ key:

```text
ai-rate-limit:user:{userId}
```

Mục tiêu:

* Tránh spam Local LLM.
* Tránh máy local bị quá tải.
* Bảo vệ CPU/GPU/RAM.
* Hạn chế nhiều request AI chạy đồng thời.

Rate Limit AI khác Login Rate Limit.

---

## 7.16 Prompt Injection Protection

User có thể nhập:

```text
Ignore all previous instructions...
```

Backend không được coi User Message là System Instruction.

Prompt phải phân tách:

```text
SYSTEM RULES
FINANCIAL CONTEXT
CHAT HISTORY
USER MESSAGE
```

AI không được:

* Tiết lộ system prompt.
* Tiết lộ secret.
* Tự thay đổi quyền.
* Thực thi SQL.
* Tự gọi API nội bộ ngoài luồng Backend kiểm soát.

---

## 7.17 Local AI Privacy

Dữ liệu tài chính được xử lý theo luồng:

```text
MySQL Local
     ↓
Spring Boot Local
     ↓
Ollama Local
     ↓
Local LLM
```

Phiên bản đầu tiên không chủ động gửi dữ liệu tài chính tới:

```text
OpenAI
Gemini
Anthropic
Cloud LLM Provider
```

---

## 7.18 AI Test

Dự kiến:

```text
AiChatServiceImplTest
FinancialContextServiceImplTest
OllamaClientTest
AiChatControllerIntegrationTest
```

Test:

* [ ] Build Financial Context đúng.
* [ ] Không lấy Transaction Soft Deleted.
* [ ] Ownership Chat Session.
* [ ] Cache hit.
* [ ] Cache miss.
* [ ] Prompt Builder.
* [ ] Save USER Message.
* [ ] Save ASSISTANT Message.
* [ ] Ollama success.
* [ ] Ollama timeout.
* [ ] Ollama unavailable.
* [ ] Invalid AI response.
* [ ] Rate Limit.
* [ ] Authentication required.

Integration Test không nên phụ thuộc Local LLM thật cho mọi lần:

```text
Unit / Integration Test
       ↓
Mock AiProviderClient
```

Test Ollama thật được thực hiện riêng bằng:

```text
Manual Integration Test
```

---

# 5. Milestone 4 — AI Integration

Thay nội dung cũ bằng:

```text
Local AI Infrastructure
Ollama
Local LLM
Chat Session
Chat Message
Financial Context
Financial Context Cache
Prompt Builder
Ollama Client
AI Service
AI Controller
Timeout
Retry
Rate Limit
Prompt Injection Protection
AI Read-Only
```

Trạng thái:

```text
Chưa bắt đầu
```

Điều kiện hoàn thành:

```text
Spring Boot
    ↓
Ollama Local
    ↓
Local LLM
```

hoạt động mà không cần Cloud AI API.

---

# 6. Version 0.4.0

Thay:

```text
AI Financial Assistant
```

bằng:

```text
Local AI Financial Assistant

Ollama
Local LLM
Financial Context
Chat History
Redis Financial Context Cache
AI Rate Limit
Prompt Protection
```

Trạng thái:

```text
Chưa bắt đầu
```

---

# 7. Technical Decision mới

## TD-019 — AI chỉ đọc dữ liệu

AI không trực tiếp sửa:

```text
Transaction
Budget
Category
Notification
```

AI chỉ nhận Financial Context do Backend chuẩn bị.

---

## TD-024 — AI chạy Local

SmartSpend sử dụng:

```text
Ollama
+
Local LLM
```

thay cho Cloud AI Provider trong phiên bản đầu tiên.

Luồng:

```text
Spring Boot
     ↓
Ollama Local API
     ↓
Local LLM
```

---

## TD-025 — Ollama là AI Runtime

Spring Boot không chạy model trực tiếp trong JVM.

Model được quản lý bởi:

```text
Ollama
```

Spring Boot giao tiếp với Ollama qua Local HTTP API.

---

## TD-026 — Không phụ thuộc Cloud AI API

Phiên bản đầu tiên không yêu cầu:

```text
OPENAI_API_KEY
GEMINI_API_KEY
```

AI Provider không được hard-code vào business logic.

---

## TD-027 — AI Provider Abstraction

Sử dụng:

```text
AiProviderClient
       ↓
OllamaClient
```

Service chỉ phụ thuộc:

```text
AiProviderClient
```

không phụ thuộc trực tiếp implementation Ollama.

---

## TD-028 — Financial Context thay vì gửi toàn bộ dữ liệu

Không gửi toàn bộ Transaction cho LLM.

Sử dụng:

```text
Aggregate Query
Projection
Budget
Dashboard Statistic
        ↓
FinancialContext
```

---

## TD-029 — Không sử dụng RAG trong V1

V1 không sử dụng:

```text
Embedding
Vector Database
RAG
```

Lý do:

Dữ liệu SmartSpend chủ yếu là Structured Data trong MySQL.

Luồng phù hợp hơn:

```text
SQL Aggregate
      ↓
Financial Context
      ↓
Local LLM
```

RAG chỉ được cân nhắc nếu sau này AI cần truy vấn:

```text
PDF
Financial Documents
Knowledge Base
FAQ
Regulation Documents
```

---

## TD-030 — Financial Context Cache

Redis key:

```text
financial-context:user:{userId}
```

Transaction/Budget thay đổi phải invalidate cache.

Redis chỉ là cache.

MySQL vẫn là Source of Truth.

---

## TD-031 — Local LLM không truy cập Database trực tiếp

Không cho:

```text
LLM
 ↓
SQL
 ↓
MySQL
```

Chỉ cho:

```text
MySQL
 ↓
Repository
 ↓
Service
 ↓
FinancialContext
 ↓
LLM
```

---

# 8. Cập nhật rủi ro AI

Xóa:

```text
AI Provider chi phí cao
→ Rate Limit + Token Logging
```

Thay bằng:

| Rủi ro                               | Mức độ     | Cách xử lý                        |
| ------------------------------------ | ---------- | --------------------------------- |
| Local LLM chạy chậm                  | Trung bình | Chọn model phù hợp phần cứng      |
| Ollama không chạy                    | Trung bình | Timeout + AI_PROVIDER_UNAVAILABLE |
| Model dùng nhiều RAM                 | Cao        | Giới hạn model theo cấu hình máy  |
| CPU/GPU quá tải                      | Trung bình | AI Rate Limit                     |
| AI Hallucination                     | Cao        | Financial Context + System Rules  |
| Prompt Injection                     | Cao        | Tách System/User Prompt           |
| Context quá dài                      | Trung bình | Aggregate + giới hạn Chat History |
| AI truy cập dữ liệu không đúng quyền | Cao        | Backend kiểm soát Ownership       |
| Dữ liệu cũ trong AI Cache            | Trung bình | Cache Invalidation                |
| Model thay đổi behavior              | Trung bình | Model config + test               |
| Local model không tồn tại            | Trung bình | Startup/health validation         |

---

# 9. Definition of Done bổ sung cho AI

AI task chỉ hoàn thành khi:

* [ ] Ollama chạy local.
* [ ] Model được pull thành công.
* [ ] Spring Boot gọi được Ollama.
* [ ] Không cần Cloud API Key.
* [ ] AI chỉ đọc dữ liệu.
* [ ] Ownership được kiểm tra trước khi build context.
* [ ] Không gửi Entity trực tiếp cho AI.
* [ ] Không gửi secret.
* [ ] Không gửi JWT.
* [ ] Không gửi password.
* [ ] Financial Context dùng aggregate query.
* [ ] Financial Context Cache hoạt động.
* [ ] Cache invalidation hoạt động.
* [ ] AI Rate Limit hoạt động.
* [ ] Timeout hoạt động.
* [ ] Ollama unavailable được xử lý.
* [ ] Prompt Injection Protection.
* [ ] Chat History có giới hạn.
* [ ] Unit Test dùng mock AI Provider.
* [ ] Manual Test với Ollama thật.
* [ ] Postman test thành công.

---

# 10. Công việc sau Dashboard

Sau khi Dashboard hoàn thành, thứ tự mới là:

```text
Chat 24
Local AI Infrastructure + Ollama

        ↓

Chat 25
Chat Entity + Repository

        ↓

Chat 26
Financial Context + Redis Cache

        ↓

Chat 27
Prompt Builder + Safety Rules

        ↓

Chat 28
AiProviderClient + OllamaClient

        ↓

Chat 29
AI Chat Service

        ↓

Chat 30
AI Controller

        ↓

Chat 31
AI Rate Limit + Timeout + Error Handling

        ↓

Chat 32
AI Test

        ↓

Postman + Ollama Real Test

        ↓

Hoàn thành Local AI Financial Assistant
```

---

# 11. Roadmap Chat cho AI

## Chat 24 — Local AI Infrastructure

### File

```text
ai/config/AiProperties.java
ai/config/AiConfig.java

ai/client/AiProviderClient.java
ai/client/OllamaClient.java
```

### Chức năng

* Ollama Local API.
* Model configurable.
* Base URL configurable.
* Timeout.
* Kiểm tra Ollama connection.

---

## Chat 25 — Chat Entity và Repository

### File

```text
ai/entity/ChatSession.java
ai/entity/ChatMessage.java

ai/repository/ChatSessionRepository.java
ai/repository/ChatMessageRepository.java
```

### Chức năng

* Chat Session thuộc User.
* Chat Message thuộc Session.
* Ownership.
* LAZY.
* Pagination History.

---

## Chat 26 — Financial Context

### File

```text
ai/context/FinancialContext.java
ai/context/FinancialContextService.java
ai/context/FinancialContextServiceImpl.java
ai/cache/FinancialContextCacheService.java
```

### Chức năng

* Tổng Income.
* Tổng Expense.
* Balance.
* Category Statistic.
* Budget.
* Budget Usage.
* Monthly Trend.
* Redis Cache.
* Không tính Transaction Soft Deleted.

Cache key:

```text
financial-context:user:{userId}
```

---

## Chat 27 — Prompt Builder

### File

```text
ai/prompt/FinancialPromptBuilder.java
```

### Chức năng

* System Prompt.
* Financial Context.
* User Message.
* Chat History.
* Prompt Injection Protection.
* Không gửi secret.
* Không gửi JWT.
* AI Read-Only.

---

## Chat 28 — Ollama Client

### File

```text
ai/client/AiProviderClient.java
ai/client/OllamaClient.java

ai/model/OllamaChatRequest.java
ai/model/OllamaChatResponse.java
```

### Chức năng

* POST local Ollama API.
* Non-streaming trước.
* Timeout.
* Parse Response.
* Provider unavailable.
* Model not found.

---

## Chat 29 — AI Chat Service

### File

```text
ai/service/AiChatService.java
ai/service/AiChatServiceImpl.java
```

### Luồng

```text
Current User
    ↓
Chat Session
    ↓
Save User Message
    ↓
Financial Context
    ↓
Prompt
    ↓
Ollama
    ↓
Save Assistant Message
    ↓
Response
```

---

## Chat 30 — AI Controller

### File

```text
ai/controller/AiChatController.java
```

### API

```text
POST /api/ai/chat

GET /api/ai/sessions

GET /api/ai/sessions/{id}/messages

DELETE /api/ai/sessions/{id}
```

---

## Chat 31 — AI Protection

### Chức năng

* Redis Rate Limit.
* Timeout.
* Retry.
* Ollama unavailable.
* Prompt length validation.
* Chat History limit.
* Model error handling.

---

## Chat 32 — AI Test

### File

```text
ai/service/AiChatServiceImplTest.java

ai/context/FinancialContextServiceImplTest.java

ai/client/OllamaClientTest.java

ai/controller/AiChatControllerIntegrationTest.java
```

### Test

```text
Financial Context
Cache Hit
Cache Miss
Ownership
Prompt
Chat Persistence
Ollama Mock
Timeout
Provider unavailable
Rate Limit
Authentication
```

---

# 12. Luồng phát triển SmartSpend sau cập nhật

```text
Authentication
      ↓
Category
      ↓
Transaction
      ↓
Budget
      ↓
Budget Alert
      ↓
Notification
      ↓
Dashboard
      ↓
Dashboard Test
      ↓
Local AI Infrastructure
      ↓
Ollama
      ↓
Financial Context
      ↓
Prompt Builder
      ↓
Local LLM
      ↓
AI Chat
      ↓
AI Test
      ↓
Export
      ↓
Testing & Optimization
      ↓
Portfolio Release
```

Mục tiêu AI:

```text
SmartSpend
    ↓
MySQL + Redis
    ↓
Spring Boot
    ↓
Ollama
    ↓
Local LLM
```

Không phụ thuộc:

```text
OpenAI API
Gemini API
Cloud LLM API
```
