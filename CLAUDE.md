# CLAUDE.md - AI Assistant Guide for Ecoembes Recycling Management System

## Project Overview

This is a **multi-service recycling plant management system** for Ecoembes, implementing a microservices architecture with three independent services communicating via different protocols (REST and TCP Sockets). The system manages container tracking, truck dispatching, and recycling plant capacity coordination.

**Project Type**: Educational/Academic Software Design Project
**Architecture**: Microservices with Service Gateway Pattern
**Technology Stack**: Java 21, Spring Boot 3.5.6, H2 Database, Gradle 8.14
**Communication**: REST (HTTP) + Custom TCP Socket Protocol

---

## Repository Structure

```
Ecoembes_Dise-o-del-software/
├── Ecoembes/          # Central orchestration service (REST API)
│   ├── src/main/java/es/deusto/sd/auctions/
│   │   ├── entity/       # JPA entities (Contenedor, Camion, Estado, Personal)
│   │   ├── dao/          # Spring Data JPA repositories
│   │   ├── service/      # Business logic (EcoembesService, AuthService)
│   │   ├── facade/       # REST controllers (EcoembesController, AuthController)
│   │   ├── Gateway/      # External service integration (PlantaGateway interface)
│   │   ├── dto/          # Data transfer objects
│   │   └── factory/      # Factory pattern for plant gateway creation
│   ├── data/             # H2 database files
│   └── build.gradle
│
├── plasSb/            # PlasSb recycling plant service (REST)
│   ├── src/main/java/org/example/
│   │   ├── entity/       # Capacidad entity
│   │   ├── service/      # PlasSbService
│   │   ├── facade/       # PlasSbController
│   │   └── dao/          # CapacidadRepository
│   ├── data/             # H2 database files
│   └── build.gradle
│
├── contSocket/        # ContSocket recycling plant service (TCP Socket)
│   ├── src/main/java/org/example/
│   │   ├── ContSocketServer.java    # Multi-threaded TCP server
│   │   ├── ClientHandler.java       # Per-connection handler
│   │   └── CapacidadService.java    # In-memory capacity service
│   ├── config/           # Separate test configuration module
│   └── build.gradle
│
└── README.md
```

---

## Architecture

### Layered Architecture (Ecoembes & plasSb)

```
┌─────────────────────────────────────┐
│   Presentation Layer (Facade)      │  @RestController
│   - EcoembesController             │  REST endpoints
│   - AuthController                 │
│   - PlasSbController                │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│   Service Layer                     │  Business Logic
│   - EcoembesService                 │
│   - AuthService                     │
│   - PlasSbService                   │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│   Data Access Layer (DAO)           │  Spring Data JPA
│   - ContenedorRepository            │
│   - EstadoRepository                │
│   - CamionRepository                │
│   - CapacidadRepository             │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│   Persistence Layer                 │  H2 Database
│   - ecoembes.mv.db                  │
│   - plassbdb.mv.db                  │
└─────────────────────────────────────┘
```

### Service Gateway Pattern

**Ecoembes** uses the **Gateway Pattern** to abstract communication with external recycling plants:

- **PlantaGateway** (interface) - Defines contract for plant communication
- **PlasSbGateway** - REST/HTTP client using `RestTemplate`
- **ConSocketGateway** - TCP Socket client using `Socket`, `PrintWriter`, `BufferedReader`
- **PlantsFactory** - Singleton factory creating appropriate gateway based on `Tipo` enum

### Multi-Protocol Communication

```
┌─────────────┐
│  Ecoembes   │ (Central Orchestrator)
└──────┬──────┘
       │
       ├─────────REST/HTTP─────────→ plasSb (Port 8085)
       │                              GET /plasSb/capacidad?fecha=dd-MM-yyyy
       │
       └─────────TCP Socket────────→ contSocket (Port 8090)
                                      Protocol: "CAPACIDAD|dd-MM-yyyy"
```

### Service Ports

- **Ecoembes**: 8083 (Spring Boot REST)
- **plasSb**: 8085 (Spring Boot REST)
- **contSocket**: 8090 (Custom TCP Socket Server)

---

## Technology Stack

### Core Technologies
- **Java**: Version 21 (required, configured in toolchain)
- **Spring Boot**: 3.5.6
- **Spring Data JPA**: ORM and repository abstraction
- **H2 Database**: File-based relational database
- **Gradle**: 8.14 with wrapper scripts

### Key Dependencies
- `spring-boot-starter-web` - REST/MVC framework
- `spring-boot-starter-data-jpa` - JPA/Hibernate integration
- `com.h2database:h2` - H2 JDBC driver
- `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13` - OpenAPI/Swagger documentation
- `jakarta.persistence-api` - JPA standard
- `jakarta.validation-api` - Bean validation

### Build System
- Gradle 8.14 (`gradle/wrapper/gradle-wrapper.properties`)
- Maven Central Repository
- Separate `build.gradle` per module

---

## Naming Conventions

### Package Structure
- **Ecoembes**: `es.deusto.sd.auctions`
- **plasSb/contSocket**: `org.example`

### Class Naming
- **Controllers**: Suffix with `Controller` (e.g., `EcoembesController`)
- **Services**: Suffix with `Service` (e.g., `EcoembesService`)
- **Repositories**: Suffix with `Repository` (e.g., `ContenedorRepository`)
- **Entities**: Plain nouns (e.g., `Contenedor`, `Camion`, `Estado`)
- **DTOs**: Suffix with `DTO` (e.g., `ContenedorDTO`, `CamionRequestDTO`)
- **Gateways**: Suffix with `Gateway` (e.g., `PlasSbGateway`)

### Method Naming
- Use **camelCase** with action verbs
- Examples: `get_contenedores()`, `crear_camion()`, `consultarCapacidad()`
- Repository queries: `findByContenedorAndFechaBetween()`, `findByFecha()`

### Database Conventions
- **Column names**: `snake_case` (e.g., `id_contenedor`, `camion_id`)
- **Primary keys**: `id` (Long, auto-increment)
- **Foreign keys**: `{entity}_id` (e.g., `contenedor_id`, `estado_id`)

---

## Database Schema

### Ecoembes Database (`data/ecoembes.mv.db`)

```sql
CONTENEDOR
  id: Long (PK, auto-increment)
  estado_id: Long (FK → ESTADO)

ESTADO
  id: Long (PK, auto-increment)
  estado: VARCHAR(30) -- ENUM {Verde, Naranja, Rojo}
  fecha: TIMESTAMP
  cantidad: DOUBLE -- Normalized 0.0-1.0 (fill percentage)
  contenedor_id: Long (FK → CONTENEDOR)

CAMION
  id: Long (PK, auto-increment)
  planta: VARCHAR -- Target plant name
  fecha: TIMESTAMP -- Dispatch date

CAMION_CONTENEDOR (join table)
  camion_id: Long (FK)
  contenedor_id: Long (FK)

PERSONAL (not yet fully persisted)
  email: VARCHAR (PK)
  contrasena: VARCHAR
  activeToken: VARCHAR
```

### plasSb Database (`data/plassbdb.mv.db`)

```sql
CAPACIDAD
  fecha: DATE (PK)
  peso: DOUBLE -- Available capacity in kg
```

### JPA Relationships
- **Contenedor** 1:1 (ManyToOne, EAGER) → Estado
- **Estado** *:1 (ManyToOne, EAGER) → Contenedor
- **Camion** *:* (ManyToMany, LAZY) → Contenedor (join table: `camion_contenedor`)

---

## REST API Endpoints

### Ecoembes (Port 8083)

**Base Path**: `/ecoembes`

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/contenedores` | Get all containers | `?token=xxx` |
| GET | `/plantas` | Get available plants | `?token=xxx` |
| GET | `/plantas/{id}/consultar` | Query plant capacity | `?fecha=dd-MM-yyyy&token=xxx` |
| POST | `/plantas/{id}/notificar` | Notify plant of dispatch | Body: `{numContenedores, numEnvases}`, `?token=xxx` |
| GET | `/camiones` | Get all trucks | `?token=xxx` |
| POST | `/camiones_nuevo` | Create new truck | Body: `CamionRequestDTO`, `?token=xxx` |

**Authentication**: `/auth/login` (POST) - Body: `{email, contrasena}` → Returns token

### plasSb (Port 8085)

**Base Path**: `/plasSb`

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/capacidad` | Get capacity for date | `?fecha=dd-MM-yyyy` |

### contSocket (Port 8090)

**Protocol**: Custom TCP text-based protocol

| Command | Format | Response |
|---------|--------|----------|
| Query capacity | `CAPACIDAD\|dd-MM-yyyy` | `{peso}` (double) |
| Notify dispatch | `NOTIFICAR\|{numCont}\|{numEnv}` | `OK` or error message |

---

## Date Format Standard

**Universal Format**: `dd-MM-yyyy`

```java
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
sdf.setLenient(false); // Strict validation
Date fecha = sdf.parse(dateString);
```

**Examples**: `25-12-2024`, `01-01-2025`

**Important**: Always use strict validation (`setLenient(false)`) to reject invalid dates like `32-13-2024`.

---

## Container Status System

Containers use a **color-coded status indicator** based on fill level:

| Estado | Fill Range | Meaning |
|--------|-----------|---------|
| **Verde** (Green) | 0% - 80% | Normal operation |
| **Naranja** (Orange) | 80% - 100% | Nearly full |
| **Rojo** (Red) | 100% | Full, needs pickup |

**Stored as**: Enum string in `ESTADO.estado` column
**Field**: `cantidad` (DOUBLE) - Normalized 0.0 to 1.0 representing fill percentage

---

## Authentication System

**Current Status**: In development (partially implemented)

### Token Generation
```java
// AuthService.java
String token = Long.toHexString(System.currentTimeMillis());
```

### Token Storage
- **In-memory** HashMap in `AuthService`
- **Not persistent** across service restarts
- Key: `token` (String), Value: `email` (String)

### Validation
```java
// Currently bypassed in controllers (commented out)
if (!authService.valido(token)) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}
```

**Note**: Token validation is currently disabled in controllers. When implementing features, preserve this authentication logic for future activation.

---

## Development Workflows

### Starting the Services

**Option 1: Gradle Wrapper (Recommended)**
```bash
# Start Ecoembes
cd Ecoembes
./gradlew bootRun

# Start plasSb
cd plasSb
./gradlew bootRun

# Start contSocket
cd contSocket
./gradlew run
```

**Option 2: Build JAR and Run**
```bash
./gradlew build
java -jar build/libs/{module-name}.jar
```

### Service Startup Order

1. **plasSb** (Port 8085) - Start first
2. **contSocket** (Port 8090) - Start second
3. **Ecoembes** (Port 8083) - Start last (depends on others)

**Reason**: Ecoembes orchestrates calls to plasSb and contSocket, so they must be running first.

### Testing Connectivity

**Test plasSb REST endpoint:**
```bash
curl "http://localhost:8085/plasSb/capacidad?fecha=08-12-2024"
```

**Test contSocket TCP server:**
```bash
telnet localhost 8090
CAPACIDAD|08-12-2024
# Should return capacity as double
```

**Test Ecoembes orchestration:**
```bash
# Login first
curl -X POST http://localhost:8083/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","contrasena":"password"}'

# Query plant (with token from login)
curl "http://localhost:8083/ecoembes/plantas/1/consultar?fecha=08-12-2024&token=YOUR_TOKEN"
```

---

## Code Organization Principles

### Layered Separation of Concerns

**DO:**
- Place REST endpoints in `facade/` package with `@RestController`
- Implement business logic in `service/` package
- Use `dao/` for Spring Data JPA repositories only
- Keep entities in `entity/` package with JPA annotations
- Create DTOs for API contracts in `dto/` package
- Put integration logic in `Gateway/` package

**DON'T:**
- Mix business logic in controllers
- Put JPA queries in service layer (use repositories)
- Expose entities directly in REST responses (use DTOs)
- Hardcode external URLs (use configuration properties)

### Entity vs DTO Pattern

**Entities** (`entity/` package):
- Annotated with `@Entity`, `@Table`, `@Id`, `@Column`
- Represent database structure
- Used internally by services and repositories
- May contain bidirectional relationships

**DTOs** (`dto/` package):
- Plain POJOs or records
- Annotated with validation (`@NotNull`, `@NotEmpty`, `@Size`, `@Positive`)
- Represent API contracts
- Used in controller parameters and responses
- No JPA annotations

**Conversion**: Services handle Entity ↔ DTO mapping (typically manual mapping code).

### Gateway Pattern Implementation

When adding new external service integration:

1. **Define method in PlantaGateway interface**
2. **Implement in concrete gateway** (e.g., `PlasSbGateway`)
3. **Update PlantsFactory** to instantiate new gateway type
4. **Use gateway in EcoembesService**, not direct HTTP/Socket calls

**Example:**
```java
// Gateway/PlantaGateway.java
public interface PlantaGateway {
    double consultarCapacidadDisponible(Date fecha);
    void notificarEnvio(int numContenedores, int numEnvases);
    Tipo getTipo();
    String getNombre();
}

// Gateway/PlasSbGateway.java
public class PlasSbGateway implements PlantaGateway {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public double consultarCapacidadDisponible(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String url = baseUrl + "/plasSb/capacidad?fecha=" + sdf.format(fecha);
        return restTemplate.getForObject(url, Double.class);
    }
}
```

---

## Common Tasks and Patterns

### Adding a New Entity

1. **Create entity class** in `entity/` package:
```java
@Entity
@Table(name = "mi_entidad")
public class MiEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campo_ejemplo")
    private String campoEjemplo;

    // Getters, setters, constructors
}
```

2. **Create repository** in `dao/` package:
```java
public interface MiEntidadRepository extends JpaRepository<MiEntidad, Long> {
    // Custom query methods (Spring auto-generates implementations)
    List<MiEntidad> findByCampoEjemplo(String campo);
}
```

3. **Create DTO** in `dto/` package:
```java
public class MiEntidadDTO {
    @NotNull
    private String campoEjemplo;

    // Getters, setters, constructors
}
```

4. **Add service methods** in appropriate service class:
```java
@Service
public class EcoembesService {
    @Autowired
    private MiEntidadRepository miEntidadRepository;

    public MiEntidadDTO crearEntidad(MiEntidadDTO dto) {
        MiEntidad entity = new MiEntidad();
        entity.setCampoEjemplo(dto.getCampoEjemplo());
        MiEntidad saved = miEntidadRepository.save(entity);
        return convertToDTO(saved);
    }
}
```

5. **Add controller endpoint** in `facade/`:
```java
@RestController
@RequestMapping("/ecoembes")
public class EcoembesController {
    @Autowired
    private EcoembesService service;

    @PostMapping("/mi-entidad")
    public ResponseEntity<MiEntidadDTO> crear(@RequestBody MiEntidadDTO dto) {
        return ResponseEntity.ok(service.crearEntidad(dto));
    }
}
```

### Adding a New REST Endpoint

1. **Define method in service layer**
2. **Add controller method** with appropriate annotations:
   - `@GetMapping`, `@PostMapping`, etc.
   - `@RequestParam` for query parameters
   - `@RequestBody` for JSON payloads
   - `@PathVariable` for URL path segments
3. **Handle errors with try-catch**:
```java
@GetMapping("/ejemplo")
public ResponseEntity<?> ejemplo(@RequestParam String token) {
    try {
        // Uncomment when auth is active
        // if (!authService.valido(token)) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // }

        ResultDTO result = service.performAction();
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(e.getMessage());
    }
}
```

### Working with Dates

**Always use the standard format:**
```java
private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
static {
    DATE_FORMAT.setLenient(false); // Strict validation
}

// Parsing
try {
    Date fecha = DATE_FORMAT.parse(fechaString);
} catch (ParseException e) {
    throw new IllegalArgumentException("Invalid date format. Use dd-MM-yyyy");
}

// Formatting
String fechaString = DATE_FORMAT.format(fecha);
```

### Adding Custom Repository Queries

Spring Data JPA auto-generates queries from method names:

```java
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    // SELECT * FROM estado WHERE contenedor_id = ? AND fecha BETWEEN ? AND ?
    List<Estado> findByContenedorAndFechaBetween(
        Contenedor contenedor,
        Date inicio,
        Date fin
    );

    // SELECT * FROM estado WHERE estado = ?
    List<Estado> findByEstado(String estado);

    // SELECT * FROM estado WHERE cantidad > ?
    List<Estado> findByCantidadGreaterThan(Double cantidad);
}
```

**Reference**: [Spring Data JPA Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

---

## Testing Strategy

### Test Framework
- **JUnit 5** (Jupiter) configured in `contSocket/config/build.gradle`
- Test platform launcher included

### Running Tests
```bash
./gradlew test
```

### Test Structure (to be implemented)
```
src/
├── main/java/
└── test/java/
    ├── entity/     # Entity tests (validation, relationships)
    ├── dao/        # Repository tests (queries, CRUD)
    ├── service/    # Service tests (business logic, mocks)
    └── facade/     # Controller tests (MockMvc, REST endpoints)
```

### Recommended Test Patterns

**Repository Tests**: Use in-memory H2
```java
@DataJpaTest
class ContenedorRepositoryTest {
    @Autowired
    private ContenedorRepository repository;

    @Test
    void testFindById() {
        // Test implementation
    }
}
```

**Service Tests**: Mock repositories
```java
@ExtendWith(MockitoExtension.class)
class EcoembesServiceTest {
    @Mock
    private ContenedorRepository contenedorRepository;

    @InjectMocks
    private EcoembesService service;

    @Test
    void testGetContenedores() {
        // Test implementation
    }
}
```

**Controller Tests**: Use MockMvc
```java
@WebMvcTest(EcoembesController.class)
class EcoembesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EcoembesService service;

    @Test
    void testGetContenedores() throws Exception {
        mockMvc.perform(get("/ecoembes/contenedores?token=abc"))
            .andExpect(status().isOk());
    }
}
```

---

## Configuration Files

### application.properties (per module)

**Ecoembes** (`Ecoembes/src/main/resources/application.properties`):
```properties
server.port=8083

# H2 Database
spring.datasource.url=jdbc:h2:file:./data/ecoembes
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# H2 Console
spring.h2.console.enabled=true
```

**plasSb** (`plasSb/src/main/resources/application.properties`):
```properties
server.port=8085
spring.datasource.url=jdbc:h2:file:./data/plassbdb
# ... (similar to Ecoembes)
```

### build.gradle (per module)

Common structure:
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.6'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'org.example' // or 'es.deusto.sd'
version = '1.0-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13'
    runtimeOnly 'com.h2database:h2'
}
```

---

## Git Workflow

### Branch Strategy

**Current Branch**: `claude/claude-md-mix052vzt5s9k6lx-01Ay3699MBEAYCZvxXy4XoGT`

**Important**: All development should occur on branches with the format:
- Start with `claude/`
- End with session ID matching the task
- Example: `claude/feature-name-{sessionId}`

### Commit Message Style

Based on recent commit history:
```
Limpieza de entidades y repos
Conexiones y persistencia de ecoembes
Conexiones a servicios externos funcionales
Conexión funcional con plassb
```

**Pattern**: Brief, descriptive, Spanish language, present tense or noun phrases

### Pushing Changes

**Always use**:
```bash
git add .
git commit -m "Descriptive message in Spanish"
git push -u origin claude/branch-name-{sessionId}
```

**Critical**: Branch must start with `claude/` and end with matching session ID, otherwise push fails with 403.

### Retry Logic for Network Issues

If push/pull/fetch fails due to network errors, retry up to 4 times with exponential backoff:
- 1st retry: Wait 2 seconds
- 2nd retry: Wait 4 seconds
- 3rd retry: Wait 8 seconds
- 4th retry: Wait 16 seconds

---

## API Documentation

### OpenAPI/Swagger

**Enabled**: SpringDoc OpenAPI 2.8.13

**Access** (when services running):
- **Ecoembes**: http://localhost:8083/swagger-ui.html
- **plasSb**: http://localhost:8085/swagger-ui.html

**OpenAPI JSON**:
- http://localhost:8083/v3/api-docs
- http://localhost:8085/v3/api-docs

### H2 Database Console

**Access** (when services running):
- **Ecoembes**: http://localhost:8083/h2-console
- **plasSb**: http://localhost:8085/h2-console

**Connection Settings**:
- **JDBC URL**: `jdbc:h2:file:./data/ecoembes` (or `plassbdb`)
- **Username**: `sa`
- **Password**: (empty)

---

## Error Handling Conventions

### Controller Error Responses

**Pattern**:
```java
try {
    // Business logic
    return ResponseEntity.ok(result);
} catch (SpecificException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Error: " + e.getMessage());
} catch (Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
}
```

**Common Status Codes**:
- `200 OK` - Success with body
- `204 NO_CONTENT` - Success without body
- `400 BAD_REQUEST` - Invalid input/validation error
- `401 UNAUTHORIZED` - Missing or invalid token
- `404 NOT_FOUND` - Resource not found
- `500 INTERNAL_SERVER_ERROR` - Unexpected error

### Validation

Use Jakarta Bean Validation annotations on DTOs:
```java
public class CamionRequestDTO {
    @NotNull(message = "Plant name is required")
    @NotEmpty
    private String planta;

    @NotNull(message = "Date is required")
    private String fecha;

    @NotNull(message = "Container IDs are required")
    @Size(min = 1, message = "At least one container required")
    private List<@Positive Long> contenedorIds;
}
```

---

## Performance Considerations

### Database Connection Pooling
- Spring Boot auto-configures HikariCP (default connection pool)
- File-based H2 suitable for development, not production

### Lazy vs Eager Loading
- **Camion ↔ Contenedor**: LAZY (many-to-many relationships)
- **Contenedor ↔ Estado**: EAGER (always needed together)

**Recommendation**: Use LAZY by default, EAGER only when data is always needed.

### Socket Server Concurrency
- **contSocket** uses thread-per-connection pattern
- Each client spawns new Thread (`new Thread(new ClientHandler(socket)).start()`)
- Socket timeout: 5 seconds (`socket.setSoTimeout(5000)`)

**Limitation**: Not suitable for high concurrency (hundreds of connections). Consider thread pool executor for production.

### REST Client Timeouts
- **RestTemplate** in gateways has no explicit timeout configured
- Consider adding timeout configuration:
```java
RestTemplate restTemplate = new RestTemplateBuilder()
    .setConnectTimeout(Duration.ofSeconds(5))
    .setReadTimeout(Duration.ofSeconds(10))
    .build();
```

---

## Security Considerations

### Current Security Status

⚠️ **This is a development/educational project with minimal security**:

- **Authentication**: Token-based, but validation is disabled in controllers
- **Authorization**: Not implemented (no role-based access control)
- **Passwords**: Stored in plain text (not hashed)
- **Tokens**: Predictable (hex timestamp), not cryptographically secure
- **Database**: No encryption, credentials visible in properties
- **Network**: HTTP only (no HTTPS/TLS)

### Production Checklist (if deploying)

- [ ] Hash passwords with BCrypt
- [ ] Use JWT tokens with signatures
- [ ] Enable Spring Security
- [ ] Configure HTTPS/TLS
- [ ] Externalize database credentials
- [ ] Enable CORS only for trusted origins
- [ ] Add rate limiting
- [ ] Implement SQL injection protection (Spring Data JPA helps, but validate input)
- [ ] Sanitize user inputs for XSS

---

## Troubleshooting

### Service Won't Start

**Issue**: Port already in use
```
Port 8083 is already in use
```

**Solution**:
```bash
# Find process using port
lsof -i :8083
# or
netstat -tulpn | grep 8083

# Kill process
kill -9 <PID>
```

### H2 Database Locked

**Issue**: `Database may be already in use` error

**Solution**:
1. Stop all running services
2. Check for `.lock.db` files in `data/` directories
3. Delete `.lock.db` files if stale
4. Restart services

### Gateway Connection Failures

**Issue**: `Connection refused` when Ecoembes calls plasSb or contSocket

**Solution**:
1. Verify plasSb and contSocket are running
2. Check ports: `netstat -tulpn | grep 8085` and `grep 8090`
3. Verify URLs in gateway implementations match running services
4. Check firewall rules (if applicable)

### Date Parsing Errors

**Issue**: `ParseException` when parsing dates

**Solution**:
- Verify date format is exactly `dd-MM-yyyy`
- Check for extra whitespace
- Ensure month is 01-12, day is valid for month

---

## Best Practices for AI Assistants

### When Adding Features

1. **Read existing code first** - Never propose changes without reading relevant files
2. **Follow existing patterns** - Match the layered architecture and naming conventions
3. **Use DTOs for API contracts** - Never expose entities directly
4. **Validate inputs** - Use Jakarta validation annotations
5. **Handle errors gracefully** - Return appropriate HTTP status codes
6. **Preserve authentication logic** - Even though disabled, keep the commented code intact
7. **Test manually** - Start services and test with curl/browser

### When Refactoring

1. **Don't over-engineer** - Keep solutions simple
2. **Don't add unnecessary abstractions** - Three similar lines are better than premature abstraction
3. **Don't add features beyond the request** - Only make changes that are directly requested
4. **Avoid backwards-compatibility hacks** - Delete unused code completely
5. **Don't add comments to unchanged code** - Only document new complex logic

### When Debugging

1. **Check service startup order** - plasSb and contSocket before Ecoembes
2. **Review console logs** - SQL logging is enabled (`spring.jpa.show-sql=true`)
3. **Use H2 console** - Inspect database state at http://localhost:8083/h2-console
4. **Test gateways independently** - Use curl for plasSb, telnet for contSocket
5. **Verify date formats** - Ensure `dd-MM-yyyy` everywhere

### When Committing

1. **Write clear commit messages** - Brief, descriptive, Spanish language
2. **Test before committing** - Start all services and verify functionality
3. **Push to correct branch** - Must start with `claude/` and end with session ID
4. **Don't commit secrets** - Check for credentials, tokens, API keys
5. **Use retry logic** - Exponential backoff for network failures

---

## External Resources

### Documentation
- **Spring Boot**: https://docs.spring.io/spring-boot/docs/3.5.x/reference/html/
- **Spring Data JPA**: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
- **H2 Database**: http://www.h2database.com/html/main.html
- **Jakarta Persistence**: https://jakarta.ee/specifications/persistence/3.1/
- **OpenAPI/Swagger**: https://springdoc.org/

### Tools
- **Gradle**: https://docs.gradle.org/8.14/userguide/userguide.html
- **Java 21**: https://docs.oracle.com/en/java/javase/21/

---

## Project Status

### Completed
✅ Core entity models (Contenedor, Camion, Estado, Personal)
✅ Repository layer with Spring Data JPA
✅ Service layer business logic
✅ REST controllers for Ecoembes and plasSb
✅ TCP socket server for contSocket
✅ Gateway pattern for multi-protocol integration
✅ Factory pattern for gateway creation
✅ H2 database persistence
✅ OpenAPI/Swagger documentation
✅ Multi-threaded socket handling

### In Progress
🔄 Authentication system (token validation disabled)
🔄 Test coverage (infrastructure ready, tests not written)

### Future Considerations
⏸️ Production security hardening
⏸️ HTTPS/TLS configuration
⏸️ Production database migration (from H2 to PostgreSQL/MySQL)
⏸️ Connection pool tuning
⏸️ API rate limiting
⏸️ Monitoring and logging framework

---

## Quick Reference

### Starting Services (Development)
```bash
# Terminal 1 - plasSb
cd plasSb && ./gradlew bootRun

# Terminal 2 - contSocket
cd contSocket && ./gradlew run

# Terminal 3 - Ecoembes
cd Ecoembes && ./gradlew bootRun
```

### Testing Endpoints
```bash
# Login
curl -X POST http://localhost:8083/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ecoembes.com","contrasena":"admin123"}'

# Get containers
curl "http://localhost:8083/ecoembes/contenedores?token=YOUR_TOKEN"

# Query plant capacity
curl "http://localhost:8083/ecoembes/plantas/1/consultar?fecha=08-12-2024&token=YOUR_TOKEN"

# Create truck
curl -X POST "http://localhost:8083/ecoembes/camiones_nuevo?token=YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"planta":"PlasSb","fecha":"08-12-2024","contenedorIds":[1,2,3]}'
```

### Common Gradle Commands
```bash
./gradlew build          # Build project
./gradlew bootRun        # Run Spring Boot app
./gradlew test           # Run tests
./gradlew clean          # Clean build artifacts
./gradlew dependencies   # Show dependency tree
```

---

## Contact and Support

This is an educational project for Software Design coursework at Universidad de Deusto.

**Repository**: https://github.com/lucasEcheverria/Ecoembes_Dise-o-del-software

For questions about this codebase, consult:
- Project README
- Code comments
- Spring Boot documentation
- Course instructors

---

*Last Updated: 2024-12-08*
*Generated for AI Assistant Context*
