# Unblu Middleware Library - Quarkus Wrapper

This module provides Quarkus integration for the Unblu Middleware Library.

## Description

This module wraps the core library with Quarkus-specific configuration and components:
- Quarkus extensions and CDI integration
- RESTEasy Reactive endpoints
- Quarkus dependency injection setup
- Quarkus-specific annotations and adapters

## Usage

Add this dependency to your Quarkus project:

```gradle
dependencies {
    implementation 'com.unblu.middleware:unblu-middleware-lib-quarkus:VERSION'
}
```

Or with Maven:

```xml
<dependency>
    <groupId>com.unblu.middleware</groupId>
    <artifactId>unblu-middleware-lib-quarkus</artifactId>
    <version>VERSION</version>
</dependency>
```

## Requirements

- Quarkus 3.17.7 or higher
- Java 21 or higher
