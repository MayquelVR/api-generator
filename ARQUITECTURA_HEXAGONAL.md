# Hexagonal Architecture - API Generator

## 📐 What is Hexagonal Architecture?

**Hexagonal Architecture** (also known as **Ports and Adapters**) is an architectural pattern that separates business logic from infrastructure code.

### Benefits:
- ✅ **Framework Independence**: Domain doesn't depend on Spring, JPA, etc.
- ✅ **Testable**: Test business logic without database or HTTP
- ✅ **Flexible**: Change adapters without touching the domain
- ✅ **Maintainable**: Clear separation of concerns

---

## 🏗️ Project Structure

```
com.viewdatatools.apigenarator/
├── ApigenaratorApplication.java     # 🚀 Spring Boot Main Application
│
├── api/                             # 📡 API MODULE - Dynamic API Management
│   ├── domain/                      # ❤️ CORE - Pure Business Logic
│   │   ├── model/                   # Domain entities (no JPA annotations)
│   │   ├── port/
│   │   │   ├── in/                  # Input ports (Use Cases)
│   │   │   └── out/                 # Output ports (Interfaces)
│   │   ├── service/                 # Use case implementations
│   │   └── exception/               # Domain exceptions
│   │
│   ├── adapter/                     # 🔌 ADAPTERS
│   │   ├── in/                      # Input adapters
│   │   │   └── web/                 # REST Controllers
│   │   │       └── mapper/          # DTO to Domain mappers
│   │   └── out/                     # Output adapters
│   │       └── persistence/         # JPA Repositories & Entities
│   │           └── entity/          # JPA entities
│   │
│   └── dto/                         # DTOs (Request/Response)
│
├── auth/                            # 🔐 AUTH MODULE - Authentication & Authorization
│   ├── domain/                      # ❤️ CORE - Pure Business Logic
│   │   ├── model/                   # Domain entities (no JPA annotations)
│   │   ├── port/
│   │   │   ├── in/                  # Input ports (Use Cases)
│   │   │   └── out/                 # Output ports (Interfaces)
│   │   ├── service/                 # Use case implementations
│   │   └── exception/               # Domain exceptions
│   │
│   ├── adapter/                     # 🔌 ADAPTERS
│   │   ├── in/                      # Input adapters
│   │   │   └── web/                 # REST Controllers
│   │   └── out/                     # Output adapters
│   │       ├── persistence/         # JPA Repositories & Entities
│   │       │   └── entity/          # JPA entities
│   │       └── mail/                # Email service adapter
│   │
│   └── dto/                         # DTOs (Request/Response)
│
├── config/                          # ⚙️ GLOBAL CONFIGURATION
│   └── SecurityConfig.java          # Spring Security configuration
│
├── security/                        # 🔒 SECURITY UTILITIES
│   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   └── JwtUtil.java                 # JWT utilities
│
└── exception/                       # 🚨 GLOBAL EXCEPTION HANDLING
    └── GlobalExceptionHandler.java  # HTTP exception handler
```

---

## 🔄 Data Flow Example

### Creating an API (POST /api/apis):

```
[HTTP Client] 
   ↓ (sends ApiCreateRequest + JWT Token)
[JwtAuthenticationFilter] (Validates JWT)
   ↓
[ApiRestController] (Input adapter)
   ↓ (uses mapper to convert to ApiDomain)
[CreateApiUseCase] (Input port)
   ↓ (implemented by)
[CreateApiService] (Business logic)
   ↓ (validates user via UserRepositoryPort)
   ↓ (validates data and uses ApiRepositoryPort)
[ApiRepositoryAdapter] (Output adapter)
   ↓ (converts ApiDomain to JPA Entity)
[ApiRepository] (Spring Data JPA)
   ↓
[PostgreSQL Database]
```

---

## 🎯 Layers & Responsibilities

### 1️⃣ **DOMAIN (Core Business Logic)**

**Location**: `{module}/domain/`

- **Models**: Pure domain entities without infrastructure annotations
- **Input Ports**: Interfaces defining what the system can do (Use Cases)
- **Output Ports**: Interfaces defining what the domain needs from outside
- **Services**: Implement use cases and contain business logic
- **Exceptions**: Domain-specific exceptions

**Key Principle**: Zero external dependencies (only Java + Lombok)

---

### 2️⃣ **INPUT ADAPTERS (Drivers)**

**Location**: `{module}/adapter/in/`

- **REST Controllers**: Expose HTTP endpoints
- **Mappers**: Convert DTOs to domain models
- **DTOs**: Request/Response data transfer objects

**Responsibility**: Receive external requests and delegate to domain

---

### 3️⃣ **OUTPUT ADAPTERS (Driven)**

**Location**: `{module}/adapter/out/`

- **Persistence Adapters**: Implement repository ports using JPA
- **External Service Adapters**: Implement ports for email, APIs, etc.
- **JPA Entities**: Database entities with annotations
- **JPA Repositories**: Spring Data repositories

**Responsibility**: Implement domain ports using infrastructure

---

### 4️⃣ **GLOBAL CONFIGURATION**

**Location**: `config/`, `security/`, `exception/`

- **SecurityConfig**: Spring Security setup (CORS, JWT, authentication)
- **JwtAuthenticationFilter**: JWT validation for all requests
- **GlobalExceptionHandler**: Convert domain exceptions to HTTP responses

**Responsibility**: Wire all components together

---

## 🔧 Traditional vs Hexagonal Architecture

### ❌ Traditional Layered Architecture:
```java
@Service
public class ApiService {
    private final ApiRepository apiRepository;  // ❌ Direct JPA dependency
    
    public void createApi(ApiCreateRequest req) {
        API api = API.builder()...  // ❌ Uses JPA entity directly
        apiRepository.save(api);
    }
}
```

**Problems**: Domain depends on infrastructure, hard to test, difficult to change

---

### ✅ Hexagonal Architecture:

```java
// DOMAIN (no external dependencies)
public class CreateApiService implements CreateApiUseCase {
    private final ApiRepositoryPort apiRepositoryPort;  // ✅ Domain interface
    
    @Override
    public ApiDomain createApi(ApiDomain apiDomain) {  // ✅ Domain entity
        validateApiData(apiDomain);  // ✅ Pure business logic
        return apiRepositoryPort.save(apiDomain);
    }
}

// ADAPTER (implements the port)
@Component
public class ApiRepositoryAdapter implements ApiRepositoryPort {
    private final ApiRepository apiRepository;  // Spring Data JPA
    
    @Override
    public ApiDomain save(ApiDomain apiDomain) {
        Api jpaEntity = convertToJpaEntity(apiDomain);
        Api saved = apiRepository.save(jpaEntity);
        return convertToDomain(saved);
    }
}
```

**Benefits**: Domain is pure, easy to test, easy to change infrastructure

---

## 📦 Layer Dependencies

```
Domain → NOTHING (0 external dependencies)
  ↑
  |
Adapters → Infrastructure (Spring, JPA, Security, etc.)
  ↑
  |
Configuration → Everything (wires components together)
```

---

## 🚀 API Endpoints

### API Module

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/apis` | Create new API | JWT |
| GET | `/api/apis` | List user's APIs | JWT |
| GET | `/api/apis/{username}/{route}` | Invoke specific API | No |

### Auth Module

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login | No |
| POST | `/api/auth/verify` | Verify account | No |
| POST | `/api/auth/forgot-password` | Request password reset | No |
| POST | `/api/auth/reset-password` | Reset password | No |

---

## 🔐 Security

### JWT Authentication
1. User registers and verifies email
2. User logs in with credentials
3. System generates JWT token
4. Client sends JWT in `Authorization: Bearer {token}` header
5. `JwtAuthenticationFilter` validates token on each request
6. If valid, grants access to resource

### CORS Configuration
- Allowed origins: Configurable via `application.properties`
- Allowed headers: `Authorization`, `Content-Type`, `Accept`
- Allowed methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

### Password Encryption
- BCrypt with cost factor 10
- Passwords never stored in plain text

---

## 🎓 SOLID Principles Applied

1. **Single Responsibility Principle (SRP)**: Each class has one reason to change
2. **Open/Closed Principle (OCP)**: Add new adapters without changing domain
3. **Liskov Substitution Principle (LSP)**: Adapters can be substituted
4. **Interface Segregation Principle (ISP)**: Small, specific ports
5. **Dependency Inversion Principle (DIP)**: Domain defines interfaces, infrastructure implements them

---

## 🔄 Key Advantages

- ✅ **Testability**: Test business logic without infrastructure
- ✅ **Maintainability**: Clear code organization
- ✅ **Flexibility**: Easy to change databases, external services
- ✅ **Scalability**: Modular structure, can split into microservices

---

## 📚 Additional Resources

- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)

---

## 🎉 Summary

This **API Generator** application follows **complete Hexagonal Architecture**:
- ✅ Pure domain with zero external dependencies
- ✅ Clear ports (input and output)
- ✅ Input adapters (REST controllers)
- ✅ Output adapters (persistence, email, security)
- ✅ Total separation between business logic and infrastructure
- ✅ Robust JWT authentication system
- ✅ User management with email verification
- ✅ Dynamic user-configurable APIs

**The project is well-structured and follows software architecture best practices!** 🚀
