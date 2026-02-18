# Unblu Middleware Lib - Quarkus Deployment

This module contains the Quarkus deployment-time components for the Unblu Middleware Library.

## Purpose

This module provides auto-configuration and bootstrapping functionality for Quarkus applications. It includes:

- **Bootstrap Components**: Configuration producers, API client setup, Unblu pinger
- **Automation**: Auto-registration, auto-subscription, and self-healing capabilities
- **Error Handling**: Fatal startup error handlers
- **Dependency Injection**: CDI producers for Unblu APIs and configuration beans

## Usage

This module is typically a transitive dependency when you include the runtime module in your Quarkus application. The deployment module is automatically used during build time by Quarkus.

### For End Users

End users should depend on the **runtime** module `unblu-middleware-lib-quarkus`:

```xml
<dependency>
    <groupId>com.unblu.middleware</groupId>
    <artifactId>unblu-middleware-lib-quarkus</artifactId>
    <version>${unblu.middleware.version}</version>
</dependency>
```

### For Extension Developers

If you're developing Quarkus extensions that integrate with Unblu middleware, you may need to depend on this deployment module:

```xml
<dependency>
    <groupId>com.unblu.middleware</groupId>
    <artifactId>unblu-middleware-lib-quarkus-deployment</artifactId>
    <version>${unblu.middleware.version}</version>
</dependency>
```

## Components

### Bootstrap

- **ConfigurationProducer**: Produces configuration beans from Quarkus config
- **UnbluApis**: Produces all Unblu API client beans
- **UnbluPinger**: Verifies connectivity to Unblu server on startup
- **DtoBinder**: Utility for binding configuration to POJOs

### Automation

- **AutoRegister**: Handles automatic registration of components
- **AutoSubscribe**: Handles automatic subscription setup
- **SelfHealingBootstrap**: Periodic self-healing for components

### Error Handling

- **QuarkusFatalStartupErrorHandler**: Handles fatal errors by shutting down Quarkus
- **RequestQueueErrorHandlerBootstrap**: Provides error handlers for request queues

