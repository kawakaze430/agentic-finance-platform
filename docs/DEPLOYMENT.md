# Deployment guide

## Local example

Run `./mvnw spring-boot:run`, or `docker compose -f compose.demo.yml up --build`.
Local sample data persists in data/ or the named Docker volume. Production never
loads fictional seed records. A demo reset deletes the local data directory or
Docker volume; do that only when those records are disposable.

## PostgreSQL

Copy .env.example to .env, replace passwords, and run `docker compose up --build`.
This configuration binds the API to 127.0.0.1 and does not expose the database.
Flyway owns schema changes; Hibernate validates the resulting schema.

## Optional Kafka

Run `docker compose -f compose.yml -f compose.kafka.yml up --build`.
The outbox publisher retries events if the broker is not ready. The local broker
has one replica and no TLS/authentication, so it is not a production broker design.
Inspect GET /api/outbox as admin; attempts increase after failed delivery.

## Kubernetes

Build/push the image to your registry and update deploy/kubernetes.yml with its
digest. Supply a reachable PostgreSQL host in finance-config and create a
finance-secrets Secret holding DATABASE_PASSWORD, FINANCE_ANALYST_PASSWORD,
FINANCE_APPROVER_PASSWORD and FINANCE_ADMIN_PASSWORD. Keep secrets out of Git.
Apply the manifest and inspect startup/readiness probes before routing traffic.
It supplies a ClusterIP service; ingress, TLS and cloud deployment are not supplied.

Liveness checks process state. Readiness includes database health. Prometheus
metrics require administrator authentication. Supply collector credentials securely.

## GitHub history

The source archive preserves actual Git commits. Authenticate GitHub CLI with
`gh auth login`; never paste an access token into a chat, README or script. The
optional scripts/publish.sh checks the account, verifies the build, creates a new
public repository, and pushes the prepared history. If a repository already exists,
inspect its contents and configure the remote manually; the helper refuses to overwrite it.

Unmerged feature-branch commits may not count on the contribution graph until they
reach the default branch. A squash merge condenses history. Use a normal merge
when preserving the individual project commits is intentional.

References: [Spring Boot requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html),
[Ollama tool calling](https://docs.ollama.com/capabilities/tool-calling),
[GitHub commit attribution](https://docs.github.com/en/account-and-profile/how-tos/email-preferences/setting-your-commit-email-address),
[GitHub contributions](https://docs.github.com/en/account-and-profile/reference/profile-contributions-reference).
