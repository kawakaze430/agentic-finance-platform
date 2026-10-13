# Validation performed

Verified on 13 October 2026 against the prepared source:

The public repository is
[kawakaze430/agentic-finance-platform](https://github.com/kawakaze430/agentic-finance-platform).
[GitHub Actions run 38446563496](https://github.com/kawakaze430/agentic-finance-platform/actions/runs/38446563496)
passed on source revision `88893c9e64e944e5a5e877c8190c89999d5746aa`:
Java 17 compilation and packaging, 79 tests, JavaScript syntax, Docker image
build, container startup and health, and the live invoice workflow smoke.
The smoke verified agent evidence, human approval, exact settlement, and a
balanced ledger using fictional records. Subsequent validation-note edits do
not change the tested application or workflow.

- 79 automated tests passed, with zero failures, errors, or skipped tests.
- Tests cover exact money, currencies, idempotency, authorization, double-entry
  journals, concurrent approval, settlement evidence, agent boundaries, model
  protocol/failures, and independent request transaction boundaries.
- A live HTTP smoke passed against a running classpath application with fictional
  data: agent evidence, human approval, exact settlement, and a balanced ledger.
- JavaScript syntax passed. HTML IDs are unique and JavaScript element references
  match the markup. YAML files parsed and OpenAPI schema references resolved.
- Every prepared commit's author and committer use the connected owner's
  GitHub-linked noreply identity. The history has no empty commits.

Limits of this validation:

- Browser rendering was not reviewed: the environment had no usable Chromium
  binary and its browser download returned a non-archive response.
- No real Ollama model was downloaded or benchmarked. Ollama protocol tests used
  an embedded HTTP stub; the live application smoke used the labeled demo mode.
- PostgreSQL, Kafka and Kubernetes deployment templates were not executed.
- The local workspace encountered a truncated nested ZIP when launching a
  packaged JAR. GitHub CI subsequently rebuilt the JAR in the Docker image and
  successfully started it and ran the live HTTP smoke on a standard runner.

Runtime databases, credentials, dependency caches, generated binaries, and
browser screenshots are excluded from version control.
