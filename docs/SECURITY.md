# Security boundaries

Only authenticated requests can read business data. The public surface consists
of static dashboard assets and health probes. Metrics and outbox state require ADMIN.
Analysts cannot approve or settle. Approvers cannot create invoices, run agents or
post arbitrary journals. An ADMIN still cannot decide an invoice it created.

The local demo publishes three synthetic account passwords so anyone can run the
example. The prod profile requires explicit passwords of at least 16 characters
and rejects demo-prefixed passwords. Configure independent account passwords,
individual identities/OIDC and TLS before using actual records. Basic auth is
for this controlled portfolio example; there are no sessions or credential cookies.
JSON POST APIs disable CSRF, do not enable cross-origin access, and do not mutate
state through GET requests. The dashboard keeps credentials in page memory.

The agent has exactly four read tools. There is no payment, approval, journal, shell,
filesystem or arbitrary HTTP tool. Input arguments must match a fixed schema.
Business text and retrieved policy passages are treated as untrusted data in the
system prompt. The backend enforces authorization and state transitions regardless
of model prose; prompts alone do not provide those protections.

Model requests contain business evidence. Ollama defaults to localhost. If an
operator changes OLLAMA_BASE_URL, that operator controls the destination and must
decide whether it is appropriate for their data. No model provider API key is required.

The application does not process card data, connect bank accounts or send payments.
Audit records are append-only through exposed APIs, but a database administrator
can modify them. They are not cryptographically tamper-proof. Secrets, runtime
databases and build output are excluded from Git and Docker build contexts.

Future work: OIDC, per-user and per-tenant authorization, rate limits, audit exports,
managed secrets, signed images, a deployment-specific CSP review and load testing.
