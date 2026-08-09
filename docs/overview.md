# Stockholm Public Transport API Overview

This project implements a Spring Boot application that exposes routes for journey planning and next‑departure data. It is built on the *MCP* (Multi‑Channel Platform) framework which injects AI‑based functionality via an embedded AI server.

## Project layout

```
stockholm-public-transport/
├── src/main/java/com/jeltechnologies/mcp                # Core API & data sources
│   ├── RestClientConfigurations.java                   # Client configs (HTTP, timeouts, etc.)
│   ├── StringUtils.java                                 # Utility helpers
│   ├── StringCompareUtils.java                          # Comparator utilities
│   ├── TransportTimesApplication.java                  # Spring boot entry point
│   └── TestRunner.java                                  # Custom test runner (not used by default)
├── src/main/java/com/jeltechnologies/mcp/sl             # Service‑layer package – split in groups
│   ├── journeyplanner/                                 # Journey planning endpoints + data source
│   │   ├─ JourneyPlannerRestController.java            # REST endpoint /journey?from=..&to=..
│   │   ├─ JourneyPlannerMCPController.java             # MCP‑specific controller (used internally by AI server)
│   │   ├─ ResponseFromSL.java                           # DTO used for sending data back to the AI layer
│   │   ├─ JourneyPlannerDataSource.java                # Service that queries departure data and builds Trip objects
│   │   ├─ Trip.java, TripLeg.java                       # Domain objects representing a journey
│   └── departures/                                     # Next‑departure data endpoints + domain
│       ├─ NextDeparturesRestController.java            # REST endpoint /departure?site=..
│       ├─ NextDeparturesMCPController.java             # MCP‑specific controller for AI
│       ├─ SiteRepository.java                           # Repository for site lookup
│       ├─ Journey, DepartureRecord, Line, etc.         # Domain objects from the SL API
│       └─ ...
├── src/main/resources/application.yml    # Spring configuration (AI server, logging, port, etc.)
└── docker/                                   # Docker images & Compose files for local dev
```

## Key Endpoints

| HTTP | Path | Notes |
|------|------|-------|
| GET  | `/journey` | Query parameters `from`, `to`. Returns a JSON array of `Trip` objects. |
| GET  | `/departs?site=&line=` | Retrieves next‑departure information for a given site/line. |

The MCP controllers (e.g., `JourneyPlannerMCPController`) are used internally by the AI runtime to expose the same services under the `/api/v1/mcp/...` namespace.

## Running locally

### Docker Compose
The project ships with **docker-compose.yml** that builds a custom JAR and runs it in a container.
```bash
# build & run all containers
docker compose up --build -d
# stop
docker compose down
```
The application listens on port `18107` as defined in *application.yml*.

### Maven
Alternatively, use Maven (the wrapper is provided):
```bash
./mvnw clean package
java -jar stockholm-public-transport/target/*.jar --spring.profiles.active=local
```

## Tests
Unit tests are located under `src/test/java/...`. The main test class:
```
TransportTimesApplicationTests.java  # Basic context load & controller integration tests
```
Run with Maven: `./mvnw test`.

## AI Integration
The *ai* section in `application.yml` configures an embedded AI MCP server. It exposes SSE streams, resource/trigger notifications and the `/api/v1/sse`, `/api/v1/mcp/` endpoints.

## Known TODOs / Issues
- The `TestRunner` is unused; remove or wire it up for integration tests.
- Some controller methods throw generic `Exception`; consider better error handling.
- Swagger/OpenAPI documentation not auto‑generated yet – might be added in the future.

---

*This document was generated to aid onboarding and serve as a quick reference. Feel free to edit or expand it based on your particular use case.*