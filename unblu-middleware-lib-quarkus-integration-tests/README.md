# Unblu Middleware Library - Quarkus Integration Tests

This module contains integration tests for the Quarkus implementation of the Unblu Middleware Library.

## Purpose

This module provides comprehensive integration tests that verify the runtime and deployment modules work correctly together in a real Quarkus application context. Integration tests validate:

- CDI bean injection and lifecycle
- API client configuration and bootstrapping
- Auto-configuration and auto-registration
- Self-healing mechanisms
- Route handling (webhooks, outbound requests)
- Error handling

## Structure

This is a test-only module with no main sources. It depends on the deployment module, which transitively includes the runtime module.

## Running Tests

### Run all integration tests:
```bash
./gradlew :unblu-middleware-lib-quarkus-integration-tests:test
```

### Run specific test:
```bash
./gradlew :unblu-middleware-lib-quarkus-integration-tests:test --tests "com.unblu.middleware.integration.*"
```

## Test Configuration

Test configuration is provided in `src/test/resources/application.properties`. Tests use mock or test data to avoid dependencies on external services.

## Why a Separate Module?

Integration tests are kept in a separate module for several reasons:

1. **Clean separation**: Runtime and deployment modules remain focused on production code
2. **Faster builds**: Main modules can be built and tested quickly without heavy integration tests
3. **Test dependencies**: Integration tests may require additional dependencies not needed in production
4. **CI/CD flexibility**: Integration tests can be run separately in CI pipelines
5. **Quarkus best practices**: Follows Quarkus extension development patterns

## Requirements

- Quarkus 3.31.2 or higher
- Java 21 or higher
- JUnit 5

