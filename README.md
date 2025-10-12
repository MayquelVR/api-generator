# API Generator - Dynamic Collection Management System

## Arquitectura Hexagonal (Ports & Adapters)

Este proyecto implementa una arquitectura hexagonal que separa claramente las responsabilidades y hace que el código sea independiente de frameworks y tecnologías específicas.

### Principios Fundamentales

#### 🎯 **Núcleo del Dominio (Core)**
El corazón de la aplicación contiene la lógica de negocio pura, sin dependencias externas:
- **Modelos de Dominio**: Entidades que representan los conceptos del negocio (usuarios, colecciones, documentos)
- **Casos de Uso**: Operaciones de negocio (crear colección, validar esquema, gestionar documentos)
- **Reglas de Negocio**: Validaciones, restricciones y lógica específica del dominio

#### 🔌 **Puertos (Ports)**
Interfaces que definen los contratos de comunicación:
- **Puertos de Entrada (Input Ports)**: Definen qué puede hacer la aplicación desde el exterior
- **Puertos de Salida (Output Ports)**: Definen qué necesita la aplicación del mundo exterior (persistencia, notificaciones, tokens, etc.)

#### 🔄 **Adaptadores (Adapters)**
Implementaciones concretas que conectan el dominio con el mundo exterior:

**Adaptadores de Entrada (Driving/Primary)**:
- REST Controllers: Exponen APIs HTTP para consumo externo
- Mappers: Convierten DTOs externos a modelos de dominio

**Adaptadores de Salida (Driven/Secondary)**:
- Repositorios: Conectan con bases de datos (PostgreSQL/JPA)
- Servicios externos: Email, generación de tokens, cifrado de contraseñas

### Flujo de Dependencias

```
┌─────────────────────────────────────────┐
│          Mundo Exterior                 │
│  (HTTP Clients, Database, Email, etc.)  │
└─────────────────────────────────────────┘
              ↓        ↑
    ┌─────────────────────────┐
    │   Adaptadores (I/O)     │
    │  ├─ REST Controllers    │ ← Entrada
    │  ├─ JPA Repositories    │ ← Salida
    │  ├─ Email Service       │ ← Salida
    │  └─ Security Services   │ ← Salida
    └─────────────────────────┘
              ↓        ↑
    ┌─────────────────────────┐
    │    Puertos (Interfaces) │
    │  ├─ Use Cases (Input)   │
    │  └─ Repositories (Out)  │
    └─────────────────────────┘
              ↓        ↑
    ┌─────────────────────────┐
    │    DOMINIO (Core)       │
    │  ├─ Modelos             │
    │  ├─ Servicios           │
    │  ├─ Validaciones        │
    │  └─ Excepciones         │
    └─────────────────────────┘
```

**Regla de oro**: Las dependencias siempre apuntan hacia el dominio, nunca al revés.

### Módulos Principales

#### 📦 **Módulo de Autenticación**
Gestiona el ciclo de vida completo de los usuarios:
- Registro y verificación de cuentas
- Autenticación con JWT (tokens de acceso y refresh)
- Recuperación de contraseñas
- Gestión de tokens temporales

**Características**:
- UUIDs versión 7 para identificación única ordenada temporalmente
- Tokens de verificación y reset con expiración
- Hashing seguro de contraseñas
- Validación de credenciales

#### 📚 **Módulo de Colecciones**
Sistema dinámico de gestión de colecciones y documentos:
- Definición flexible de esquemas con tipos de datos variados
- Validación automática de documentos contra esquemas
- Soporte para estructuras anidadas (objetos y arrays)
- CRUD completo de colecciones y documentos

**Características**:
- Esquemas almacenados como JSONB para máxima flexibilidad
- Validación en tiempo real de tipos y restricciones
- Búsquedas optimizadas por usuario y nombre de colección
- UUIDs versión 7 para colecciones y documentos

### Ventajas de la Arquitectura Hexagonal

#### ✅ **Testabilidad**
- El dominio puede ser probado sin frameworks
- Los puertos permiten crear mocks fácilmente
- Tests unitarios rápidos y aislados

#### ✅ **Independencia de Frameworks**
- Spring Boot puede ser reemplazado sin afectar el dominio
- La base de datos puede cambiar sin tocar la lógica de negocio
- Los adaptadores se pueden intercambiar según necesidades

#### ✅ **Mantenibilidad**
- Separación clara de responsabilidades
- Cada capa tiene un propósito específico
- Cambios en infraestructura no afectan al negocio

#### ✅ **Escalabilidad**
- Módulos desacoplados pueden evolucionar independientemente
- Fácil agregar nuevos casos de uso
- Adaptadores múltiples para el mismo puerto (ej: diferentes DBs)

### Decisiones Arquitectónicas

#### 🔐 **Desacoplamiento entre Módulos**
Los módulos `auth` y `collection` son independientes a nivel de persistencia:
- No hay foreign keys en la base de datos entre módulos
- Cada módulo gestiona su propia integridad
- Comunicación solo a través de interfaces del dominio

#### 🆔 **UUIDs Versión 7**
Todos los identificadores principales usan UUID v7:
- Ordenación temporal automática
- Mejor rendimiento en índices
- Distribución sin colisiones
- Compatibilidad con sistemas distribuidos

#### 📊 **Esquemas Dinámicos**
Las colecciones usan esquemas JSONB:
- Máxima flexibilidad para definir estructuras
- Validación en tiempo de ejecución
- Soporte para anidación ilimitada
- Sin migraciones de esquema para nuevos tipos de datos

#### 🎯 **Validación en Capas**
- **Capa Web**: Validaciones de formato (DTO validations)
- **Capa Dominio**: Reglas de negocio complejas
- **Capa Persistencia**: Constraints de base de datos

### Patrones Aplicados

- **Dependency Inversion**: Las abstracciones no dependen de detalles
- **Single Responsibility**: Cada clase tiene una única razón para cambiar
- **Interface Segregation**: Interfaces específicas y cohesivas
- **Builder Pattern**: Construcción fluida de objetos complejos
- **Repository Pattern**: Abstracción de la capa de persistencia
- **DTO Pattern**: Separación entre modelos de dominio y transporte

### Stack Tecnológico

#### Backend
- **Framework**: Spring Boot 4.0
- **Lenguaje**: Java 21
- **Base de Datos**: PostgreSQL (con soporte JSONB)
- **ORM**: Hibernate/JPA
- **Seguridad**: Spring Security + JWT
- **Validación**: Jakarta Validation
- **UUID v7**: uuid-creator library

#### Infraestructura
- **Contenedores**: Docker Compose
- **Build Tool**: Maven
- **API Style**: RESTful


