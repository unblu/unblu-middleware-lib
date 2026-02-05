# Multi-Module Restructure - Verification Checklist

## ✅ Completed Steps

### 1. Project Structure
- [x] Created `unblu-middleware-lib-core` module directory
- [x] Created `unblu-middleware-lib-spring` module directory  
- [x] Created `unblu-middleware-lib-quarkus` module directory
- [x] Created src/main/java, src/test/java, src/main/resources, src/test/resources for all modules

### 2. Source Code Migration
- [x] Moved all Java source from `src/main/java` to `unblu-middleware-lib-core/src/main/java`
- [x] Moved all test source from `src/test/java` to `unblu-middleware-lib-core/src/test/java`
- [x] Moved test resources from `src/test/resources` to `unblu-middleware-lib-core/src/test/resources`
- [x] Verified source code structure (bots, common, externalmessenger, outboundrequests, webhooks packages)
- [x] Copied lombok.config to core module

### 3. Build Configuration
- [x] Updated root `build.gradle` to multi-module configuration
- [x] Updated `settings.gradle` with module includes
- [x] Created `unblu-middleware-lib-core/build.gradle` with all dependencies
- [x] Created `unblu-middleware-lib-spring/build.gradle` with Spring Boot plugin
- [x] Created `unblu-middleware-lib-quarkus/build.gradle` with Quarkus dependencies
- [x] Removed duplicate plugin applications

### 4. Documentation
- [x] Created `MIGRATION_GUIDE.md` with detailed next steps
- [x] Created `unblu-middleware-lib-core/README.md`
- [x] Created `unblu-middleware-lib-spring/README.md`
- [x] Created `unblu-middleware-lib-quarkus/README.md`
- [x] Created `RESTRUCTURE_SUMMARY.md` with quick overview
- [x] Updated main `Readme.adoc` with multi-module information

### 5. Gradle Upgrade
- [x] Upgraded Gradle from 8.13 to 8.14 (required for Spring Boot 4.0.1)
- [x] Updated gradle-wrapper.properties
- [x] Updated build.gradle wrapper configuration

## 🔄 Next Steps (TODO)

### Priority 1: Verify Build
```bash
cd C:\Users\Jan\repositories\unblu-middleware-lib-github
.\gradlew projects                    # List all modules
.\gradlew clean build -x test        # Build without tests
.\gradlew :unblu-middleware-lib-core:build  # Build core only
```

### Priority 2: Fix Spring Boot 4 Test Issues
The core module tests currently fail with Spring Boot 4 due to:
- Missing `@AutoConfigureWebTestClient` annotations
- Qualifier issues with `@MockitoBean`
- `@Captor` initialization problems

**Files to fix:**
- ✅ `BotsControllerTest.java` - Added @AutoConfigureWebTestClient
- ✅ `WebhookControllerTest.java` - Added @AutoConfigureWebTestClient
- [ ] `WebhookRequestHandlerServiceTest.java` - Needs @AutoConfigureWebTestClient
- [ ] `WebhookContextTests.java` - Needs @AutoConfigureWebTestClient
- [ ] `ProcessingContinuesOnErrorTest.java` - Needs @AutoConfigureWebTestClient
- [ ] `OutboundContextTests.java` - Needs @AutoConfigureWebTestClient
- [ ] `DialogBotServiceOnEventTest.java` - Needs @AutoConfigureWebTestClient
- [ ] `DialogBotServiceOfferTest.java` - Needs @AutoConfigureWebTestClient
- [ ] `DialogBotServiceDifferentDialogsTest.java` - Needs @AutoConfigureWebTestClient
- [ ] `WebhookRegistrationServiceSelfHealingTest.java` - Fix @Captor initialization
- [ ] `WebhookRegistrationServiceAutoConfigTest.java` - Fix null pointer issues

### Priority 3: Refactor Core Module
Remove Spring-specific annotations from core:

**Phase 1: Identify Spring Dependencies**
```bash
# Search for Spring annotations
grep -r "@Component" unblu-middleware-lib-core/src/main/java
grep -r "@Service" unblu-middleware-lib-core/src/main/java
grep -r "@Configuration" unblu-middleware-lib-core/src/main/java
grep -r "@RestController" unblu-middleware-lib-core/src/main/java
grep -r "@Bean" unblu-middleware-lib-core/src/main/java
```

**Phase 2: Create Abstractions**
- Define DI-agnostic interfaces
- Replace Spring controllers with service interfaces
- Abstract configuration into properties classes

**Phase 3: Move Framework Code**
- Move controllers to spring module
- Move @Configuration classes to spring module
- Move @Bean methods to spring module

### Priority 4: Implement Spring Module
Create Spring Boot auto-configuration:

**Structure:**
```
unblu-middleware-lib-spring/src/main/
├── java/com/unblu/middleware/spring/
│   ├── autoconfigure/
│   │   ├── UnbluMiddlewareAutoConfiguration.java
│   │   ├── UnbluWebhooksAutoConfiguration.java
│   │   ├── UnbluBotsAutoConfiguration.java
│   │   └── UnbluExternalMessengerAutoConfiguration.java
│   ├── controllers/
│   │   ├── WebhookController.java
│   │   └── OutboundRequestsController.java
│   └── config/
│       └── WebFluxConfiguration.java
└── resources/
    └── META-INF/spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

**Tasks:**
- [ ] Create auto-configuration classes
- [ ] Move controllers from core
- [ ] Create Spring Boot starter metadata
- [ ] Add configuration properties binding
- [ ] Create integration tests

### Priority 5: Implement Quarkus Module
Create Quarkus extension:

**Structure:**
```
unblu-middleware-lib-quarkus/src/main/
├── java/com/unblu/middleware/quarkus/
│   ├── resources/
│   │   ├── WebhookResource.java
│   │   └── OutboundRequestsResource.java
│   ├── producers/
│   │   └── MiddlewareProducers.java
│   └── config/
│       └── MiddlewareConfig.java
└── resources/
    └── application.properties
```

**Tasks:**
- [ ] Create JAX-RS resources (equivalent to controllers)
- [ ] Create CDI producers (equivalent to @Bean)
- [ ] Add configuration mappings
- [ ] Create health checks
- [ ] Create Quarkus tests with @QuarkusTest

### Priority 6: Update Tests
- [ ] Keep unit tests in core (make them framework-agnostic)
- [ ] Move Spring integration tests to spring module
- [ ] Create Quarkus integration tests in quarkus module
- [ ] Ensure all tests pass

### Priority 7: Publishing
- [ ] Test publishing to local Maven: `.\gradlew publishToMavenLocal`
- [ ] Update version numbers if needed
- [ ] Test consuming the artifacts from another project
- [ ] Update CI/CD pipelines if any

## 🧪 Testing Commands

```bash
# Clean everything
.\gradlew clean

# List all projects/modules
.\gradlew projects

# Build all modules (skip tests for now)
.\gradlew build -x test

# Build specific module
.\gradlew :unblu-middleware-lib-core:build -x test
.\gradlew :unblu-middleware-lib-spring:build -x test
.\gradlew :unblu-middleware-lib-quarkus:build -x test

# Run tests for specific module
.\gradlew :unblu-middleware-lib-core:test

# Publish to local Maven repository
.\gradlew publishToMavenLocal

# Check dependencies
.\gradlew :unblu-middleware-lib-core:dependencies
.\gradlew :unblu-middleware-lib-spring:dependencies
```

## ⚠️ Known Issues

1. **Spring Boot 4 Test Failures**: Tests need `@AutoConfigureWebTestClient` annotation
2. **Core Module Has Spring Dependencies**: Need to refactor out Spring-specific code
3. **Empty Wrapper Modules**: Spring and Quarkus modules need implementation
4. **No Integration Tests**: Wrapper modules need their own tests

## 📊 Module Statistics

### Core Module
- **Lines of Code**: ~15,000+ (estimated)
- **Packages**: 5 main packages (bots, common, externalmessenger, outboundrequests, webhooks)
- **Test Files**: 16 test classes
- **Dependencies**: Spring Boot, Reactor, Unblu API client, Lombok, etc.

### Spring Module
- **Lines of Code**: 0 (to be implemented)
- **Dependencies**: Spring Boot, core module

### Quarkus Module
- **Lines of Code**: 0 (to be implemented)
- **Dependencies**: Quarkus BOM, core module

## 📚 Reference Documentation

- See `MIGRATION_GUIDE.md` for detailed migration steps
- See `RESTRUCTURE_SUMMARY.md` for quick overview
- See individual module README files for module-specific information
- See main `Readme.adoc` for usage examples
