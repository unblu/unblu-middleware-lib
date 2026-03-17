# Unblu Middleware Lib - Quarkus Deployment

This module contains deployment-time build steps shared by all Quarkus feature modules.

## Purpose

It wires build-time integration for runtime modules and dynamic Unblu API bean registration.

## Usage

You usually **do not** add this module directly.

Depend on one or more Quarkus feature runtime modules instead, for example:

- `unblu-middleware-lib-quarkus-webhooks`
- `unblu-middleware-lib-quarkus-outboundrequests`
- `unblu-middleware-lib-quarkus-dialog-bot`
- `unblu-middleware-lib-quarkus-conversation-observing-bot`
- `unblu-middleware-lib-quarkus-external-messenger`

Each feature runtime module points to this deployment module internally through Quarkus extension metadata.

## For Extension Developers

If you are integrating these features in another Quarkus extension, you may depend on:

```xml
<dependency>
  <groupId>com.unblu.middleware</groupId>
  <artifactId>unblu-middleware-lib-quarkus-deployment</artifactId>
  <version>${unblu.middleware.version}</version>
</dependency>
```
