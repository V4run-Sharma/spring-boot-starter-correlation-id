# Spring Boot Correlation ID Starter

A lightweight Spring Boot starter for automatic correlation ID generation, propagation, and MDC-based logging to enable end-to-end request tracing in distributed systems.

This starter provides a simple, log-centric approach to request correlation and complements distributed tracing systems such as OpenTelemetry, Zipkin, and Jaeger.

1. [Features](#features)
2. [Why Correlation IDs?](#why-correlation-ids)
3. [Installation](#installation)
4. [Default Behavior](#default-behavior)
5. [Configuration](#configuration)
6. [Logging Configuration (Recommended)](#logging-configuration-recommended)
7. [RestTemplate Propagation](#resttemplate-propagation)
8. [WebClient Propagation](#webclient-propagation)
9. [Async MDC Propagation](#async-mdc-propagation)
10. [How This Works (High Level)](#how-this-works-high-level)
11. [Compatibility](#compatibility)
12. [Relationship to Distributed Tracing](#relationship-to-distributed-tracing)
13. [License](#license)
14. [Contributions](#contributions)

---

### Features

- Automatic correlation ID generation for incoming HTTP requests
- MDC integration for structured log correlation
- Adds correlation ID to HTTP response headers
- Outgoing propagation for:
    - RestTemplate
    - WebClient
- Async MDC propagation for:
    - @Async
    - Default Spring Boot task executors
- Zero-code integration via Spring Boot auto-configuration
- Optional dependencies (does not force web/reactive stacks)

---

### Why Correlation IDs?

In microservices and distributed systems, a single user request often spans multiple services and threads. Correlation IDs allow you to:

- Trace a single request across multiple services
- Correlate logs across systems
- Debug production issues faster
- Provide support teams with a single request identifier

This starter standardizes correlation ID handling so teams don’t have to reimplement it in every service.

---

### Installation

Add the dependency to your Spring Boot project:

```xml
<dependency>
  <groupId>io.github.v4run-sharma</groupId>
  <artifactId>spring-boot-starter-correlation-id</artifactId>
  <version>0.1.0</version>
</dependency>
```

---

### Quick Start

1. Add the dependency
2. Update your logging pattern
3. Run your application

That’s it — correlation IDs will be automatically generated and propagated.

---

### Default Behavior

Out of the box, the starter will:

- Read `X-Correlation-Id` from incoming requests
- Generate a new correlation ID if missing
- Store the correlation ID in SLF4J MDC
- Add the correlation ID to the HTTP response
- Propagate the correlation ID to downstream HTTP calls
- Propagate MDC context to async threads
No additional configuration is required.

---

### Configuration

All properties are optional.

```.properties
correlation-id.header-name=X-Correlation-Id
correlation-id.generate-if-missing=true
correlation-id.mdc-key=correlationId
```

| Property                             | Default            | Description                      |
|--------------------------------------|--------------------|----------------------------------|
| `correlation-id.header-name`         | `X-Correlation-Id` | HTTP header used for correlation |
| `correlation-id.generate-if-missing` | `true`             | Generate a new ID if missing     |
| `correlation-id.mdc-key`             | `correlationId`    | MDC key used in logs             |


---

### Logging Configuration (Recommended)

To include correlation ID in logs, update your logging pattern.

**Logback example**
```xml
<pattern>
  %d{HH:mm:ss.SSS} [%X{correlationId}] %-5level %logger - %msg%n
</pattern>
```

**Example Log Output**

```markdown
12:01:45.123 [7f3a2c91-9b2d-4e8a-a0f4-1c3f8a9d21aa] INFO  OrderService - Processing order
```

---

### RestTemplate Propagation

All Spring-managed RestTemplate instances automatically propagate the correlation ID.

**Example**

```java
@Autowired
private RestTemplate restTemplate;

public void callDownstream() {
    restTemplate.getForObject(
        "http://downstream-service/api",
        String.class
    );
}
```

**The outgoing request will automatically include:**
>X-Correlation-Id: <current-correlation-id>

No manual interceptor registration required.

---

### WebClient Propagation

For reactive applications, the starter provides a pre-configured WebClient.Builder.

Example

```java
@Autowired
private WebClient.Builder webClientBuilder;

public Mono<String> callDownstream() {
    return webClientBuilder.build()
            .get()
            .uri("http://downstream-service/api")
            .retrieve()
            .bodyToMono(String.class);
}
```

The correlation ID will be automatically added to outgoing requests.

>**Note**: If you define your own WebClient.Builder, ensure you add the filter manually.

---

### Async MDC Propagation

MDC is thread-local by default and normally breaks in async execution.

> Note: Async propagation applies to Spring Boot’s default task executor. 
> If you define custom executors, you must set the TaskDecorator manually.

This starter automatically propagates MDC context for:
- @Async methods
- Default Spring Boot task executors

**Example:**

```java
@Async
public void processAsync() {
    log.info("Running async task");
}
```

The correlation ID will be preserved in async logs.

**Custom Executors**

If you define a custom ThreadPoolTaskExecutor, you should manually set the TaskDecorator:

>`executor.setTaskDecorator(new MdcTaskDecorator());`

---

### How This Works (High Level)

```
Incoming HTTP Request
        |
        v
Servlet Filter
        |
        +--> Correlation ID read/generated
        +--> Stored in MDC
        |
        v
Application Logic
        |
        +--> RestTemplate (auto-propagated)
        +--> WebClient (auto-propagated)
        +--> @Async (MDC propagated)
        |
        v
Outgoing HTTP Requests
```

---

### Compatibility

- Spring Boot 3.x
- Java 17+
- Servlet-based applications
- Optional support for WebFlux

---

### Relationship to Distributed Tracing

This starter is not a replacement for OpenTelemetry, Zipkin, or Jaeger.

It is designed to:
- Complement distributed tracing
- Provide lightweight log-based tracing
- Support teams without full observability stacks

---

### Known Limitations

- MDC propagation applies only to Spring-managed executors by default
- Custom WebClient.Builder instances must manually register the filter
- This starter does not provide full distributed tracing (use OpenTelemetry for spans)

---

### License
Apache License 2.0

---

### Contributions
Pull requests and issues are welcome.