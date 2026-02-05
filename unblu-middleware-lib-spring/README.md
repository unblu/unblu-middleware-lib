# Unblu Middleware Library - Spring Boot Wrapper

This module provides Spring Boot integration for the Unblu Middleware Library.

## Description

This module wraps the core library with Spring Boot-specific configuration and components:
- Spring Boot auto-configuration
- Spring WebFlux controllers and routes
- Spring dependency injection setup
- Spring-specific annotations and adapters

## Usage

Add this dependency to your Spring Boot project:

```gradle
dependencies {
    implementation 'com.unblu.middleware:unblu-middleware-lib-spring:VERSION'
}
```

Or with Maven:

```xml
<dependency>
    <groupId>com.unblu.middleware</groupId>
    <artifactId>unblu-middleware-lib-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

## Requirements

- Spring Boot 4.0.1 or higher
- Java 21 or higher
