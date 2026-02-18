# Unblu Middleware Library - Quarkus Runtime

This module provides the Quarkus runtime components for the Unblu Middleware Library.

## Description

This is the **runtime** module that clients should depend on. It provides:
- RESTEasy Reactive endpoints for webhooks and outbound requests
- Quarkus CDI integration
- Runtime utilities for request/response handling
- Configuration templates

The deployment-time components (auto-configuration, bootstrapping, etc.) are in the separate `unblu-middleware-lib-quarkus-deployment` module, which is automatically pulled in by Quarkus during the build.

## Architecture

This module follows the Quarkus extension pattern:
- **Runtime Module** (this module): Contains runtime components that are included in the final application
- **Deployment Module** (`unblu-middleware-lib-quarkus-deployment`): Contains build-time components for auto-configuration

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

The deployment module will be automatically included by Quarkus during the build process.

## Requirements

- Quarkus 3.31.2 or higher
- Java 21 or higher
