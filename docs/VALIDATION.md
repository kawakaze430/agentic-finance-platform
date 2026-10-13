# Validation performed

Verified on 13 October 2026 against the prepared source:

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
- Docker, PostgreSQL, Kafka and Kubernetes deployment templates were not executed.
- Maven packaging completed, but a subsequent packaged-JAR launch encountered
  an empty/truncated nested ZIP in this environment. The source/classpath runtime
  passed; packaged-container validation remains for a normal JDK/Docker runner.
- GitHub CI has not run because a remote repository has not yet been created.

The source archive includes the complete Git history, not runtime databases,
credentials, dependency caches, generated binaries, or browser screenshots.
