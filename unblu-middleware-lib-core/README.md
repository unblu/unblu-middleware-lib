# Unblu Middleware Library - Core

This is the core module of the Unblu Middleware Library. It contains the framework-agnostic business logic and domain models.

## Description

The core module provides:
- Domain models and entities
- Business logic interfaces
- Core services (to be refactored to remove Spring dependencies)
- Unblu API client integration
- Common utilities

## Usage

This module is not meant to be used directly. Instead, use one of the framework-specific wrapper modules:
- `unblu-middleware-lib-spring` for Spring Boot applications
- `unblu-middleware-lib-quarkus` for Quarkus applications

## Dependencies

Currently, this module still has Spring dependencies that will be removed/abstracted in future refactoring.
