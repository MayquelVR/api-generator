# Arquitectura Hexagonal - API Generator

## 📐 ¿Qué es Arquitectura Hexagonal?

La **Arquitectura Hexagonal** (también conocida como **Ports and Adapters**) es un patrón arquitectónico que separa la lógica de negocio del código de infraestructura.

### Beneficios:
- ✅ **Independencia de frameworks**: El dominio no depende de Spring, JPA, etc.
- ✅ **Testeable**: Puedes probar la lógica de negocio sin base de datos ni HTTP
- ✅ **Flexible**: Puedes cambiar adaptadores sin tocar el dominio
- ✅ **Mantenible**: Separación clara de responsabilidades

---

## 🏗️ Estructura del Proyecto

```
apiDomain/
├── domain/                          # ❤️ NÚCLEO - Lógica de negocio pura
│   ├── model/
│   │   └── ApiEntity.java          # Entidad de dominio (sin anotaciones JPA)
│   ├── port/
│   │   ├── in/                     # Puertos de entrada (Casos de uso)
│   │   │   ├── CreateApiUseCase.java
│   │   │   └── InvokeApiUseCase.java
│   │   └── out/                    # Puertos de salida (Interfaces)
│   │       ├── ApiRepositoryPort.java
│   │       └── UserRepositoryPort.java
│   ├── service/
│   │   └── ApiDomainService.java   # Implementa los casos de uso
│   └── exception/                   # Excepciones del dominio
│       ├── ApiNotFoundException.java
│       ├── UserNotFoundException.java
│       ├── InvalidHttpMethodException.java
│       └── InvalidRequestFormatException.java
│
├── adapter/                         # 🔌 ADAPTADORES
│   ├── in/                         # Adaptadores de entrada
│   │   └── web/
│   │       ├── ApiRestController.java  # Controlador REST
│   │       └── mapper/
│   │           └── ApiDtoMapper.java   # Convierte DTOs a dominio
│   └── out/                        # Adaptadores de salida
│       └── persistence/
│           ├── ApiRepositoryAdapter.java   # Implementa ApiRepositoryPort
│           └── UserRepositoryAdapter.java  # Implementa UserRepositoryPort
│
├── config/
│   └── ApiHexagonalConfig.java     # Configuración de Spring
│
├── dto/                            # DTOs (Request/Response)
│   └── ApiCreateReq.java
│
├── model/                          # Entidades JPA (infraestructura)
│   └── API.java
│
└── repository/                     # Repositorios JPA (infraestructura)
    └── ApiRepository.java
```

---

## 🔄 Flujo de Datos

### Crear una API (POST /apiDomain):

```
1. [Cliente HTTP] 
   ↓ (envía ApiCreateReq)
2. [ApiRestController] (Adaptador de entrada)
   ↓ (usa ApiDtoMapper para convertir a ApiEntity)
3. [CreateApiUseCase] (Puerto de entrada)
   ↓ (implementado por)
4. [ApiDomainService] (Lógica de negocio)
   ↓ (valida y usa)
5. [ApiRepositoryPort] (Puerto de salida - interfaz)
   ↓ (implementado por)
6. [ApiRepositoryAdapter] (Adaptador de salida)
   ↓ (convierte ApiEntity a API JPA y usa)
7. [ApiRepository] (Spring Data JPA)
   ↓
8. [Base de Datos PostgreSQL]
```

### Invocar una API (GET /{username}/{route}):

```
1. [Cliente HTTP]
   ↓
2. [ApiRestController]
   ↓
3. [InvokeApiUseCase]
   ↓
4. [ApiDomainService]
   ├── Busca API en ApiRepositoryPort
   ├── Valida método HTTP
   ├── Valida formato de request
   └── Devuelve responseFormat
   ↓
5. [Cliente recibe respuesta JSON]
```

---

## 🎯 Capas y Responsabilidades

### 1️⃣ **DOMINIO (Core Business Logic)**

#### `ApiEntity` (modelo de dominio)
- Entidad pura sin anotaciones de infraestructura
- Representa una API en términos de negocio

#### `CreateApiUseCase` y `InvokeApiUseCase` (puertos de entrada)
- Definen QUÉ puede hacer el sistema
- Son interfaces que exponen la funcionalidad

#### `ApiRepositoryPort` y `UserRepositoryPort` (puertos de salida)
- Definen QUÉ necesita el dominio del exterior
- Son interfaces que el dominio NO implementa

#### `ApiDomainService`
- ❤️ **CORAZÓN DE LA APLICACIÓN**
- Contiene toda la lógica de negocio
- Implementa los casos de uso (puertos de entrada)
- Usa los puertos de salida (sin saber cómo funcionan)
- Validaciones:
  - Usuario existe
  - Datos de API válidos
  - Método HTTP correcto
  - Formato de request válido

---

### 2️⃣ **ADAPTADORES DE ENTRADA (Drivers)**

#### `ApiRestController`
- Recibe peticiones HTTP
- Convierte DTOs a entidades de dominio
- Delega a los casos de uso
- Maneja respuestas HTTP

#### `ApiDtoMapper`
- Convierte ApiCreateReq (DTO) a ApiEntity (dominio)
- Separa el contrato HTTP del modelo de dominio

---

### 3️⃣ **ADAPTADORES DE SALIDA (Driven)**

#### `ApiRepositoryAdapter`
- Implementa `ApiRepositoryPort`
- Convierte entre ApiEntity (dominio) y API (JPA)
- Usa ApiRepository (Spring Data JPA)
- **Traduce** entre el mundo del dominio y el mundo de la persistencia

#### `UserRepositoryAdapter`
- Implementa `UserRepositoryPort`
- Verifica si usuarios existen
- Usa UserRepository (Spring Data JPA)

---

### 4️⃣ **CONFIGURACIÓN**

#### `ApiHexagonalConfig`
- **Conecta todo** usando Dependency Injection
- Crea el bean de ApiDomainService
- Expone los casos de uso como beans

---

## 🔧 Diferencias con el Código Anterior

### ❌ Antes (Arquitectura tradicional en capas):
```java
@Service
public class ApiService {
    private final ApiRepository apiRepository;  // ❌ Depende de JPA
    private final UserRepository userRepository; // ❌ Depende de infraestructura
    
    public void createApi(ApiCreateReq req, String username) {
        User user = userRepository.findByUsername(username)...  // ❌ JPA leak
        API apiDomain = API.builder()...  // ❌ Usa entidad JPA directamente
        apiRepository.save(apiDomain);
    }
}
```

**Problemas:**
- El servicio depende directamente de JPA
- No puedes testear sin base de datos
- Difícil cambiar la persistencia

---

### ✅ Ahora (Arquitectura Hexagonal):

```java
// DOMINIO (sin dependencias externas)
public class ApiDomainService implements CreateApiUseCase, InvokeApiUseCase {
    private final ApiRepositoryPort apiRepositoryPort;  // ✅ Interfaz del dominio
    private final UserRepositoryPort userRepositoryPort; // ✅ Interfaz del dominio
    
    public void createApi(ApiEntity apiEntity) {  // ✅ Usa entidad de dominio
        if (!userRepositoryPort.existsByUsername(apiEntity.getUsername())) {
            throw new UserNotFoundException(...);
        }
        validateApiData(apiEntity);  // ✅ Lógica de negocio pura
        apiRepositoryPort.save(apiEntity);  // ✅ Usa puerto (interfaz)
    }
}

// ADAPTADOR (implementa el puerto)
@Component
public class ApiRepositoryAdapter implements ApiRepositoryPort {
    private final ApiRepository apiRepository;  // Spring Data JPA
    
    public void save(ApiEntity apiEntity) {
        API jpaEntity = convertToJpaEntity(apiEntity);  // ✅ Conversión
        apiRepository.save(jpaEntity);
    }
}
```

**Ventajas:**
- ✅ El dominio no sabe nada de JPA
- ✅ Puedes testear con implementaciones mock
- ✅ Puedes cambiar de PostgreSQL a MongoDB sin tocar el dominio

---

## 🧪 Testing

### Test unitario del dominio (sin Spring, sin BD):

```java
@Test
void testCreateApi() {
    // Mocks de los puertos
    ApiRepositoryPort mockApiRepo = mock(ApiRepositoryPort.class);
    UserRepositoryPort mockUserRepo = mock(UserRepositoryPort.class);
    
    // Servicio de dominio (lógica pura)
    ApiDomainService service = new ApiDomainService(mockApiRepo, mockUserRepo);
    
    // Simular que el usuario existe
    when(mockUserRepo.existsByUsername("john")).thenReturn(true);
    
    // Ejecutar caso de uso
    ApiEntity apiDomain = ApiEntity.builder()
        .name("Test API")
        .route("test")
        .method("GET")
        .username("john")
        .build();
        
    service.createApi(apiDomain);
    
    // Verificar
    verify(mockApiRepo).save(apiDomain);
}
```

---

## 📦 Dependencias

### Dominio → **NADA** (0 dependencias)
- No depende de Spring
- No depende de JPA
- No depende de HTTP
- **Java puro**

### Adaptadores → Infraestructura
- Dependen de Spring, JPA, etc.
- Implementan los puertos del dominio

---

## 🚀 Cómo Usar

El uso desde el cliente NO cambia:

```bash
# Crear API
POST /apiDomain
Authorization: Bearer <JWT_TOKEN>
{
  "name": "Users API",
  "route": "users",
  "method": "GET",
  "responseFormat": "{\"users\": []}"
}

# Invocar API
GET /apiDomain/john/users
```

---

## 🎓 Principios Aplicados

1. **Dependency Inversion**: El dominio define interfaces, la infraestructura las implementa
2. **Single Responsibility**: Cada clase tiene una única razón para cambiar
3. **Open/Closed**: Puedes agregar nuevos adaptadores sin cambiar el dominio
4. **Interface Segregation**: Puertos pequeños y específicos
5. **Separation of Concerns**: Dominio, aplicación e infraestructura separados

---

## 🔄 Migración desde el Código Anterior

### Pasos realizados:

1. ✅ Creada capa de dominio con entidades puras
2. ✅ Definidos puertos de entrada (casos de uso)
3. ✅ Definidos puertos de salida (interfaces de repositorio)
4. ✅ Implementado servicio de dominio con lógica de negocio
5. ✅ Creados adaptadores de persistencia
6. ✅ Creado adaptador web (controlador)
7. ✅ Configuración de Spring para conectar todo
8. ✅ Excepciones del dominio
9. ✅ Manejador global de excepciones actualizado

### Código viejo (mantener por compatibilidad):
- `apiDomain/service/ApiService.java` → Puede eliminarse
- `apiDomain/controller/ApiController.java` → Reemplazado por `ApiRestController.java`

---

## 📚 Más Información

- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

¡Tu aplicación ahora sigue Arquitectura Hexagonal! 🎉

