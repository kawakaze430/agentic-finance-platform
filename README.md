# Agentic Finance Platform

A Java/Spring Boot portfolio project for invoice operations: exact decimal money,
human approval, double-entry bookkeeping, bank reconciliation, and an agent that
can retrieve evidence without receiving financial write tools.

The default demo uses fictional records and a deterministic planner. **It does
not run an LLM.** An optional Ollama mode implements native model tool calls.
Recording settlement updates bookkeeping; this application does not send money.

## Run locally

Requirements: JDK 17+, curl, unzip, and a SHA-512 utility. The bootstrap script
downloads and verifies Maven 3.9.11. On Windows, run the Maven commands below with
an installed Maven distribution instead of `./mvnw`.

```sh
./mvnw test
./mvnw spring-boot:run
```

Open **http://localhost:8080**. The default H2 file database persists in `data/`.
The fictional seed is idempotent across restarts.

| Demo account | Password | Allowed actions |
| --- | --- | --- |
| analyst | demo-analyst-password | Create invoices, import bank records, run agents |
| approver | demo-approver-password | Approve/reject and record exact settlement |
| admin | demo-admin-password | All operations, high-value approval, journal posting |

Use the analyst to select `ACME-2026-1001` and run an evidence review. Sign out,
sign in as the approver, provide a review reason, approve it, and record its exact
bank match. Inspect the general ledger and audit history. The high-value Nova
invoice requires an administrator. Invoice creators cannot review their own invoice.

```sh
# Alternative: Docker demo (loopback only)
docker compose -f compose.demo.yml up --build

# Test a running seeded demo; this records one fictional settlement
python3 scripts/smoke.py
```

## Implemented capabilities

| Area | Implementation |
| --- | --- |
| Backend | Java 17, Spring Boot 3.5.16, REST, validation, JPA, Flyway |
| Finance | Supported currencies, exact amounts, balanced immutable journals |
| Workflow | RECEIVED → APPROVED/REJECTED; APPROVED → PAID with exact evidence |
| Reliability | Idempotency snapshots, unique constraints, pessimistic review locks |
| Agent | Bounded tool loop, scoped allowlist, policy retrieval, stored traces |
| Guardrails | No financial agent tools, risk floor, known policy IDs, evidence requirement |
| Events | Transactional outbox; optional Kafka publisher with retryable delivery |
| Operations | Actuator health/readiness/liveness, Prometheus agent counters, request IDs |
| Interface | Responsive dashboard, role-aware actions, inspectable tool evidence |
| Deployment | Docker/Compose, Kubernetes template, pinned GitHub Actions |

All REST monetary responses are decimal **strings**, preserving paise/cents for
clients. POST commands accept decimal strings as well. Totals are calculated per
currency; no foreign-exchange conversion is performed. The numeric review limit
defaults to 250000 in each supported currency and is operator-configurable.

## Self-hosted model mode

Run Ollama locally, pull a tool-capable model, and explicitly select it:

```sh
ollama pull llama3.1:8b
AGENT_MODE=ollama OLLAMA_MODEL=llama3.1:8b ./mvnw spring-boot:run
```

The model must request `get_invoice` and `search_policy` before producing a final
JSON recommendation. Other available tools are `reconciliation_candidates` and
`ledger_summary`. Model failures remain FAILED runs; there is no silent fallback
to a demo result. Six tool calls and ten seconds per model request are the defaults.
Policy retrieval is lexical; vector embeddings and a vector database are not implemented.

## API examples

Every financial write requires an `Idempotency-Key` of 8–80 supported characters.
Retries with the same actor, key, command and payload return the stored response.
Changed payloads or commands return 409. Read-only agent runs do not require a key.

```sh
curl -u analyst:demo-analyst-password http://localhost:8080/api/invoices
curl -u analyst:demo-analyst-password http://localhost:8080/api/ledger/accounts
curl -u analyst:demo-analyst-password http://localhost:8080/api/policies
```

See [API contract](docs/openapi.yml), [architecture](docs/ARCHITECTURE.md),
[agent evaluation](docs/EVALUATION.md), [deployment](docs/DEPLOYMENT.md),
and [security boundaries](docs/SECURITY.md).

## PostgreSQL and events

Copy `.env.example` to `.env` and replace every password with a unique value of
at least 16 characters. The `prod` profile requires secrets and does not seed data.

```sh
docker compose up --build
# Add the optional local Kafka broker and enable outbox publication
docker compose -f compose.yml -f compose.kafka.yml up --build
```

The Kafka configuration is a local, single-broker example. Consumers must deduplicate
by eventId: delivery is at least once, including after a publish/commit crash window.

## Verification

```sh
./mvnw verify
node --check src/main/resources/static/app.js
```

GitHub Actions runs the 79 Java tests, JavaScript syntax check, Docker build,
container startup, and a live HTTP smoke covering agent evidence, human approval,
exact settlement and a balanced ledger. The workflow can also be run manually
from Actions. See [validation evidence and remaining limits](docs/VALIDATION.md)
for the successful build and what has not been tested with live integrations.

## Scope and next improvements

This is a functioning portfolio application with local integration tests, not a
certified banking system. The production profile is configuration, not a claim
of production readiness. Shared role accounts should be replaced with OIDC,
individual user identities, TLS, access policies and managed secret storage before
serving real users. There is no multi-tenancy, live bank integration, payment rail,
OCR, distributed saga, vector RAG, MCP server, or deployed cloud infrastructure.

[Roadmap](docs/ROADMAP.md) distinguishes follow-up work from implemented features.
