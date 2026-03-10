# Microservice Template Specification

This document provides the exact template and code patterns that MUST be followed when creating any new microservice in the FoodAI platform.

## Service Creation Checklist

When creating a new microservice `{service-name}-service`, follow these steps in order:

### Step 1: Create Module Structure
```bash
{service-name}-service/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/foodai/{domain}/
│   │   │   ├── {ServiceName}Application.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── client/                 # Optional: WebClient for calling other services
│   │   │   ├── infrastructure/
│   │   │   │   ├── persistence/
│   │   │   │   ├── messaging/
│   │   │   │   └── client/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   ├── config/
│   │   │   └── exception/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-staging.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       └── db/migration/ (if using PostgreSQL)
│   └── test/
│       ├── java/com/foodai/{domain}/
│       │   ├── controller/
│       │   ├── service/
│       │   ├── domain/
│       │   ├── repository/
│       │   └── integration/
│       └── resources/
│           └── application-test.yml
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.foodai</groupId>
        <artifactId>foodai-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>{service-name}-service</artifactId>
    <name>{Service Name} Service</name>
    <description>{Service description}</description>

    <dependencies>
        <!-- Common Module -->
        <dependency>
            <groupId>com.foodai</groupId>
            <artifactId>common</artifactId>
        </dependency>

        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Database (choose based on service needs) -->
        <!-- MongoDB -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <!-- OR PostgreSQL -->
        <!--
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        -->

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Kafka -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- OpenAPI Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>

        <!-- Logstash Encoder -->
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mongodb</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Application Main Class Template

```java
package com.foodai.{domain};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
// OR for PostgreSQL: @EnableJpaAuditing
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for {Service Name} Service.
 *
 * <p>This service is responsible for {brief description}.
 *
 * @author FoodAI Team
 * @version 1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableMongoAuditing // OR @EnableJpaAuditing
@EnableKafka
public class {ServiceName}Application {

  public static void main(String[] args) {
    SpringApplication.run({ServiceName}Application.class, args);
  }
}
```

### Step 4: Domain Entity Template

```java
package com.foodai.{domain}.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Domain entity representing a {Entity Name}.
 *
 * <p>This is an aggregate root in the {Domain} bounded context.
 *
 * @author FoodAI Team
 */
@Document(collection = "{collection_name}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {EntityName} {

  @Id
  private String id;

  // Business fields
  private String name;
  // Add more fields as per domain model

  // Audit fields
  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  private boolean deleted;

  // Domain methods (business logic)
  /**
   * Validates if the entity is in a valid state.
   *
   * @throws IllegalStateException if entity is invalid
   */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Name cannot be empty");
    }
    // Add more validation logic
  }

  /**
   * Soft deletes the entity.
   */
  public void delete() {
    this.deleted = true;
  }
}
```

### Step 5: Repository Interface Template

```java
package com.foodai.{domain}.domain.repository;

import com.foodai.{domain}.domain.model.{EntityName};
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {EntityName} aggregate.
 *
 * <p>Provides data access operations for {EntityName} entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface {EntityName}Repository extends MongoRepository<{EntityName}, String> {

  /**
   * Finds a non-deleted entity by its ID.
   *
   * @param id the entity ID
   * @return Optional containing the entity if found and not deleted
   */
  Optional<{EntityName}> findByIdAndDeletedFalse(String id);

  /**
   * Finds all non-deleted entities with pagination.
   *
   * @param pageable pagination information
   * @return page of non-deleted entities
   */
  Page<{EntityName}> findByDeletedFalse(Pageable pageable);

  // Add custom query methods as needed
  // Example: Optional<User> findByEmailAndDeletedFalse(String email);
}
```

### Step 6: DTO Templates

**Request DTO:**
```java
package com.foodai.{domain}.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new {EntityName}.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new {EntityName}")
public class Create{EntityName}Request {

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Name of the {EntityName}", example = "John Doe")
  private String name;

  // Add more fields with validation annotations
}
```

**Response DTO:**
```java
package com.foodai.{domain}.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for {EntityName}.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "{EntityName} response")
public class {EntityName}Response {

  @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011")
  private String id;

  @Schema(description = "Name", example = "John Doe")
  private String name;

  @Schema(description = "Creation timestamp")
  private Instant createdAt;

  @Schema(description = "Last update timestamp")
  private Instant updatedAt;

  // Add more fields
}
```

### Step 7: Mapper Interface Template

```java
package com.foodai.{domain}.mapper;

import com.foodai.{domain}.domain.model.{EntityName};
import com.foodai.{domain}.dto.Create{EntityName}Request;
import com.foodai.{domain}.dto.{EntityName}Response;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for {EntityName} entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface {EntityName}Mapper {

  /**
   * Maps create request DTO to domain entity.
   *
   * @param request the create request
   * @return the domain entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", constant = "false")
  {EntityName} toEntity(Create{EntityName}Request request);

  /**
   * Maps domain entity to response DTO.
   *
   * @param entity the domain entity
   * @return the response DTO
   */
  {EntityName}Response toResponse({EntityName} entity);
}
```

### Step 8: Service Class Template

```java
package com.foodai.{domain}.service;

import com.foodai.{domain}.domain.model.{EntityName};
import com.foodai.{domain}.domain.repository.{EntityName}Repository;
import com.foodai.{domain}.dto.Create{EntityName}Request;
import com.foodai.{domain}.dto.{EntityName}Response;
import com.foodai.{domain}.mapper.{EntityName}Mapper;
import com.foodai.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Application service for {EntityName} operations.
 *
 * <p>Orchestrates domain operations and handles transactions.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class {EntityName}Service {

  private final {EntityName}Repository repository;
  private final {EntityName}Mapper mapper;
  // private final KafkaTemplate<String, Object> kafkaTemplate; // If events needed

  /**
   * Creates a new {EntityName}.
   *
   * @param request the create request
   * @return the created {EntityName} response
   */
  @Transactional
  public {EntityName}Response create(Create{EntityName}Request request) {
    log.info("Creating new {EntityName}", kv("request", request));

    {EntityName} entity = mapper.toEntity(request);
    entity.validate();

    {EntityName} saved = repository.save(entity);

    log.info("{EntityName} created successfully", kv("id", saved.getId()));

    // Publish event (if needed)
    // kafkaTemplate.send("{domain}.created", saved.getId(), saved);

    return mapper.toResponse(saved);
  }

  /**
   * Finds a {EntityName} by its ID.
   *
   * @param id the entity ID
   * @return the {EntityName} response
   * @throws NotFoundException if entity not found
   */
  public {EntityName}Response findById(String id) {
    log.debug("Finding {EntityName} by id", kv("id", id));

    {EntityName} entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new NotFoundException("{EntityName} not found with id: " + id));

    return mapper.toResponse(entity);
  }

  /**
   * Finds all {EntityName}s with pagination.
   *
   * @param pageable pagination information
   * @return page of {EntityName} responses
   */
  public Page<{EntityName}Response> findAll(Pageable pageable) {
    log.debug("Finding all {EntityName}s", kv("page", pageable.getPageNumber()),
        kv("size", pageable.getPageSize()));

    return repository.findByDeletedFalse(pageable)
        .map(mapper::toResponse);
  }

  /**
   * Updates an existing {EntityName}.
   *
   * @param id the entity ID
   * @param request the update request
   * @return the updated {EntityName} response
   * @throws NotFoundException if entity not found
   */
  @Transactional
  public {EntityName}Response update(String id, Create{EntityName}Request request) {
    log.info("Updating {EntityName}", kv("id", id), kv("request", request));

    {EntityName} entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new NotFoundException("{EntityName} not found with id: " + id));

    // Update fields
    entity.setName(request.getName());
    entity.validate();

    {EntityName} updated = repository.save(entity);

    log.info("{EntityName} updated successfully", kv("id", updated.getId()));

    // Publish event (if needed)
    // kafkaTemplate.send("{domain}.updated", updated.getId(), updated);

    return mapper.toResponse(updated);
  }

  /**
   * Deletes a {EntityName} (soft delete).
   *
   * @param id the entity ID
   * @throws NotFoundException if entity not found
   */
  @Transactional
  public void delete(String id) {
    log.info("Deleting {EntityName}", kv("id", id));

    {EntityName} entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new NotFoundException("{EntityName} not found with id: " + id));

    entity.delete();
    repository.save(entity);

    log.info("{EntityName} deleted successfully", kv("id", id));

    // Publish event (if needed)
    // kafkaTemplate.send("{domain}.deleted", id, null);
  }
}
```

### Step 9: Controller Template

```java
package com.foodai.{domain}.controller;

import com.foodai.{domain}.dto.Create{EntityName}Request;
import com.foodai.{domain}.dto.{EntityName}Response;
import com.foodai.{domain}.service.{EntityName}Service;
import com.foodai.common.dto.ApiResponse;
import com.foodai.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for {EntityName} operations.
 *
 * <p>Provides CRUD endpoints for managing {EntityName}s.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/{resource-plural}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "{EntityName} Management", description = "APIs for managing {EntityName}s")
public class {EntityName}Controller {

  private final {EntityName}Service service;

  /**
   * Creates a new {EntityName}.
   *
   * @param request the create request
   * @return the created {EntityName}
   */
  @PostMapping
  @Operation(summary = "Create {EntityName}", description = "Creates a new {EntityName}")
  @ApiResponses(value = {
      @SwaggerApiResponse(responseCode = "201", description = "{EntityName} created successfully"),
      @SwaggerApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<ApiResponse<{EntityName}Response>> create(
      @Valid @RequestBody Create{EntityName}Request request) {
    log.info("Received request to create {EntityName}");
    {EntityName}Response response = service.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(response));
  }

  /**
   * Gets a {EntityName} by ID.
   *
   * @param id the entity ID
   * @return the {EntityName}
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get {EntityName}", description = "Retrieves a {EntityName} by its ID")
  @ApiResponses(value = {
      @SwaggerApiResponse(responseCode = "200", description = "{EntityName} found"),
      @SwaggerApiResponse(responseCode = "404", description = "{EntityName} not found")
  })
  public ResponseEntity<ApiResponse<{EntityName}Response>> getById(
      @Parameter(description = "{EntityName} ID") @PathVariable String id) {
    log.info("Received request to get {EntityName} by id: {}", id);
    {EntityName}Response response = service.findById(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets all {EntityName}s with pagination.
   *
   * @param page page number (0-indexed)
   * @param size page size
   * @param sort sort field and direction (e.g., "createdAt,desc")
   * @return page of {EntityName}s
   */
  @GetMapping
  @Operation(summary = "List {EntityName}s", description = "Retrieves all {EntityName}s with pagination")
  public ResponseEntity<ApiResponse<PageResponse<{EntityName}Response>>> getAll(
      @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
      @Parameter(description = "Sort (field,direction)") @RequestParam(defaultValue = "createdAt,desc") String sort) {
    log.info("Received request to list {EntityName}s: page={}, size={}", page, size);

    String[] sortParams = sort.split(",");
    Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

    Page<{EntityName}Response> result = service.findAll(pageable);
    return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
  }

  /**
   * Updates an existing {EntityName}.
   *
   * @param id the entity ID
   * @param request the update request
   * @return the updated {EntityName}
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update {EntityName}", description = "Updates an existing {EntityName}")
  @ApiResponses(value = {
      @SwaggerApiResponse(responseCode = "200", description = "{EntityName} updated successfully"),
      @SwaggerApiResponse(responseCode = "404", description = "{EntityName} not found"),
      @SwaggerApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<ApiResponse<{EntityName}Response>> update(
      @Parameter(description = "{EntityName} ID") @PathVariable String id,
      @Valid @RequestBody Create{EntityName}Request request) {
    log.info("Received request to update {EntityName}: id={}", id);
    {EntityName}Response response = service.update(id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a {EntityName}.
   *
   * @param id the entity ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete {EntityName}", description = "Deletes a {EntityName} (soft delete)")
  @ApiResponses(value = {
      @SwaggerApiResponse(responseCode = "204", description = "{EntityName} deleted successfully"),
      @SwaggerApiResponse(responseCode = "404", description = "{EntityName} not found")
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "{EntityName} ID") @PathVariable String id) {
    log.info("Received request to delete {EntityName}: id={}", id);
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
```

### Step 10: Test Templates

**Service Test:**
```java
package com.foodai.{domain}.service;

import com.foodai.{domain}.domain.model.{EntityName};
import com.foodai.{domain}.domain.repository.{EntityName}Repository;
import com.foodai.{domain}.dto.Create{EntityName}Request;
import com.foodai.{domain}.dto.{EntityName}Response;
import com.foodai.{domain}.mapper.{EntityName}Mapper;
import com.foodai.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {EntityName}Service.
 */
@ExtendWith(MockitoExtension.class)
class {EntityName}ServiceTest {

  @Mock
  private {EntityName}Repository repository;

  @Mock
  private {EntityName}Mapper mapper;

  @InjectMocks
  private {EntityName}Service service;

  private Create{EntityName}Request createRequest;
  private {EntityName} entity;
  private {EntityName}Response response;

  @BeforeEach
  void setUp() {
    createRequest = Create{EntityName}Request.builder()
        .name("Test Name")
        .build();

    entity = {EntityName}.builder()
        .id("123")
        .name("Test Name")
        .deleted(false)
        .build();

    response = {EntityName}Response.builder()
        .id("123")
        .name("Test Name")
        .build();
  }

  @Test
  void shouldCreateSuccessfully_whenValidInput() {
    // Arrange
    when(mapper.toEntity(createRequest)).thenReturn(entity);
    when(repository.save(any({EntityName}.class))).thenReturn(entity);
    when(mapper.toResponse(entity)).thenReturn(response);

    // Act
    {EntityName}Response result = service.create(createRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("123");
    assertThat(result.getName()).isEqualTo("Test Name");

    verify(repository).save(any({EntityName}.class));
    verify(mapper).toEntity(createRequest);
    verify(mapper).toResponse(entity);
  }

  @Test
  void shouldReturnEntity_whenEntityExists() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("123")).thenReturn(Optional.of(entity));
    when(mapper.toResponse(entity)).thenReturn(response);

    // Act
    {EntityName}Response result = service.findById("123");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("123");

    verify(repository).findByIdAndDeletedFalse("123");
  }

  @Test
  void shouldThrowNotFoundException_whenEntityNotFound() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("999")).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> service.findById("999"))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("not found");

    verify(repository).findByIdAndDeletedFalse("999");
  }

  // Add more tests: update, delete, etc.
}
```

**Integration Test:**
```java
package com.foodai.{domain}.integration;

import com.foodai.{domain}.dto.Create{EntityName}Request;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {EntityName} API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class {EntityName}IntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

  @LocalServerPort
  private int port;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = "/api/v1/{resource-plural}";
  }

  @Test
  void shouldCreateAndRetrieve{EntityName}Successfully() {
    // Create
    Create{EntityName}Request request = Create{EntityName}Request.builder()
        .name("Test Name")
        .build();

    String id = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post()
        .then()
        .statusCode(201)
        .body("data.name", equalTo("Test Name"))
        .extract().path("data.id");

    // Retrieve
    given()
        .when()
        .get("/" + id)
        .then()
        .statusCode(200)
        .body("data.id", equalTo(id))
        .body("data.name", equalTo("Test Name"));
  }

  // Add more integration tests
}
```

## Configuration Files

### application.yml
```yaml
spring:
  application:
    name: {service-name}-service
  profiles:
    active: @spring.profiles.active@

server:
  port: 8080
  compression:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### application-dev.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/{service_name}_dev
  kafka:
    bootstrap-servers: localhost:9092
  redis:
    host: localhost
    port: 6379

logging:
  level:
    com.foodai: DEBUG
    org.springframework: INFO
```

## Complete Checklist

When Cursor generates code for a microservice, verify ALL of the following:

1. ✅ Module structure created correctly
2. ✅ pom.xml with all required dependencies
3. ✅ Application main class with correct annotations
4. ✅ Domain entities with validation logic
5. ✅ Repository interfaces
6. ✅ Service classes with business logic
7. ✅ DTOs with validation annotations
8. ✅ Mappers (MapStruct)
9. ✅ Controllers with OpenAPI documentation
10. ✅ Exception classes (if custom exceptions needed)
11. ✅ Configuration classes (MongoDB, Kafka, Redis)
12. ✅ application.yml files for all environments
13. ✅ Unit tests for ALL classes (80%+ coverage)
14. ✅ Integration tests with Testcontainers
15. ✅ ALL tests pass
16. ✅ Code coverage >= 80%
17. ✅ JavaDoc for all public classes and methods
18. ✅ README.md with setup instructions

## Cursor Code Generation Prompt Template

When asking Cursor to generate a microservice, use this template:

```
Create a complete {service-name}-service following the FoodAI microservice template:

Service: {service-name}-service
Domain: {domain}
Purpose: {brief description}
Database: {MongoDB/PostgreSQL}
Key Entities: {list entities}

Generate:
1. Complete module structure
2. All layers: Controller → Service → Domain → Repository
3. DTOs and Mappers
4. Configurations (MongoDB/PostgreSQL, Redis, Kafka)
5. Unit tests for each class
6. Integration tests with Testcontainers
7. Ensure 80%+ code coverage
8. Ensure ALL tests pass

Follow:
- Domain-Driven Design principles
- CRUD API standards from backend/.cursor/rules
- Error handling from common module
- Structured logging
- OpenAPI documentation

DO NOT proceed until:
- All tests are written
- All tests pass
- Code coverage >= 80%
```

This template ensures uniformity across all microservices while maintaining high quality and test coverage.


