# Multi-Module Migration Guide

## Overview

The project has been restructured into a multi-module Gradle project with three modules:

1. **unblu-middleware-lib-core** - Framework-agnostic core library (contains all existing code)
2. **unblu-middleware-lib-spring** - Spring Boot wrapper module
3. **unblu-middleware-lib-quarkus** - Quarkus wrapper module

## Project Structure

```
unblu-middleware-lib/
├── build.gradle                          (root build file)
├── settings.gradle                       (defines modules)
├── gradle.properties                     (shared properties)
├── unblu-middleware-lib-core/
│   ├── build.gradle
│   ├── README.md
│   └── src/
│       ├── main/java/                    (all existing source code)
│       └── test/java/                    (all existing tests)
├── unblu-middleware-lib-spring/
│   ├── build.gradle
│   ├── README.md
│   └── src/
│       └── main/java/                    (Spring-specific wrappers - to be created)
└── unblu-middleware-lib-quarkus/
    ├── build.gradle
    ├── README.md
    └── src/
        └── main/java/                    (Quarkus-specific wrappers - to be created)
```

## Changes Made

### 1. Module Structure
- Created three sub-modules with proper Gradle configuration
- Moved all existing source code to `unblu-middleware-lib-core/src/`
- Created empty structure for Spring and Quarkus wrapper modules

### 2. Build Configuration

#### Root `build.gradle`
- Removed direct dependencies
- Configured `allprojects` and `subprojects` blocks
- Publishing configuration moved to `subprojects`

#### Core Module (`unblu-middleware-lib-core/build.gradle`)
- Contains all original dependencies
- Uses `java-library` plugin
- Dependencies marked as `api` to be exposed to consuming modules
- **Note:** Still has Spring dependencies that need to be refactored

#### Spring Module (`unblu-middleware-lib-spring/build.gradle`)
- Depends on core module via `project(':unblu-middleware-lib-core')`
- Includes Spring Boot plugin and dependencies
- Will contain Spring-specific controllers, configurations, and auto-configuration

#### Quarkus Module (`unblu-middleware-lib-quarkus/build.gradle`)
- Depends on core module via `project(':unblu-middleware-lib-core')`
- Includes Quarkus BOM and dependencies
- Will contain Quarkus-specific resources, extensions, and CDI beans

### 3. Settings File
Updated `settings.gradle` to include all three modules:
```groovy
rootProject.name = 'unblu-middleware-lib'

include 'unblu-middleware-lib-core'
include 'unblu-middleware-lib-spring'
include 'unblu-middleware-lib-quarkus'
```

## Next Steps

### 1. Refactor Core Module (Priority: High)
The core module currently contains Spring-specific code that needs to be abstracted:

**Framework-specific code to identify and abstract:**
- `@Component`, `@Service`, `@Configuration` annotations
- `@Autowired`, `@RequiredArgsConstructor` for DI
- Spring WebFlux controllers (`@RestController`, `@RequestMapping`)
- Spring configuration classes (`@Bean` methods)
- `WebTestClient` and Spring test annotations

**Refactoring approach:**
- Create framework-agnostic interfaces for dependency injection
- Move controllers/endpoints to wrapper modules
- Abstract reactive types (Mono/Flux) behind interfaces if needed
- Keep domain models, business logic, and Unblu API client in core

### 2. Implement Spring Wrapper (Priority: High)
Create Spring Boot auto-configuration in `unblu-middleware-lib-spring`:

**Tasks:**
- Move Spring controllers from core to this module
- Create `@Configuration` classes with `@Bean` definitions
- Create `spring.factories` or `AutoConfiguration.imports` for auto-configuration
- Add Spring-specific adapters/bridges for core interfaces
- Create Spring Boot starter dependencies

**Example structure:**
```
unblu-middleware-lib-spring/src/main/java/
└── com/unblu/middleware/spring/
    ├── autoconfigure/
    │   ├── UnbluMiddlewareAutoConfiguration.java
    │   └── UnbluMiddlewareProperties.java
    ├── controllers/
    │   ├── WebhookController.java
    │   └── OutboundRequestsController.java
    └── config/
        └── WebFluxConfiguration.java
```

### 3. Implement Quarkus Wrapper (Priority: Medium)
Create Quarkus extension in `unblu-middleware-lib-quarkus`:

**Tasks:**
- Create Quarkus REST endpoints using `@Path` annotations
- Implement CDI producers (`@Produces`) for core services
- Create Quarkus-specific configuration using `@ConfigProperty`
- Add health checks and metrics endpoints
- Create Quarkus extension metadata if needed

**Example structure:**
```
unblu-middleware-lib-quarkus/src/main/java/
└── com/unblu/middleware/quarkus/
    ├── resources/
    │   ├── WebhookResource.java
    │   └── OutboundRequestsResource.java
    ├── producers/
    │   └── MiddlewareProducers.java
    └── config/
        └── MiddlewareConfig.java
```

### 4. Update Tests
- Keep framework-agnostic tests in core module
- Create Spring-specific integration tests in spring module
- Create Quarkus-specific tests in quarkus module using `@QuarkusTest`

### 5. Update Documentation
- Update main README.adoc with multi-module information
- Add usage examples for both Spring and Quarkus
- Document migration path for existing users

## Building the Project

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :unblu-middleware-lib-core:build
./gradlew :unblu-middleware-lib-spring:build
./gradlew :unblu-middleware-lib-quarkus:build

# Run tests
./gradlew test

# Publish to local Maven
./gradlew publishToMavenLocal
```

## Usage in Consumer Projects

### For Spring Boot Projects
```gradle
dependencies {
    implementation 'com.unblu.middleware:unblu-middleware-lib-spring:1.11.1-SNAPSHOT'
}
```

### For Quarkus Projects
```gradle
dependencies {
    implementation 'com.unblu.middleware:unblu-middleware-lib-quarkus:1.11.1-SNAPSHOT'
}
```

## Breaking Changes

**Current users will need to update their dependency:**
- Old: `com.unblu.middleware:unblu-middleware-lib:VERSION`
- New: `com.unblu.middleware:unblu-middleware-lib-spring:VERSION` (for Spring Boot users)

## Gradle Version

The project now requires **Gradle 8.14** to support Spring Boot 4.0.1.

## Questions or Issues?

If you encounter any issues during the migration, please consult:
- Module-specific README files
- Gradle build files for dependency information
- Main project documentation
