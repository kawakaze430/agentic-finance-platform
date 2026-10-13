# Agent and workflow evaluation

Automated tests verify read-only evidence runs, blocked mutation tools, cross-invoice
scope violations, extra tool arguments, step budget exhaustion, minimum evidence,
deterministic risk floors, fabricated policy IDs, and model upstream failures.
An embedded local HTTP server tests Ollama's native tool-call protocol and final JSON
without downloading a model. This verifies transport and orchestration, not model quality.

Finance tests exercise paise precision, multi-currency isolation, invoice duplicates,
idempotency replay/conflicts, role restrictions, approval thresholds, maker/checker
separation, balanced journals, exact settlement evidence and simultaneous reviews.

For a real self-hosted model, run the same invoice cases in AGENT_MODE=ollama and
record: evidence tool completion, valid JSON rate, correct policy references, risk-floor
agreement, refusal to claim a write action, failed-run rate, wall time and hardware.
Do not report the deterministic demo as an LLM benchmark. No live model capability,
quality score, latency target or hardware benchmark has been measured in this build.

| Case | Required behavior |
| --- | --- |
| Normal received invoice | Retrieve invoice and policy; request human review |
| Amount above review limit | HIGH risk and administrator review action retained |
| Overdue invoice | At least MEDIUM risk; due-date review |
| Wrong bank reference/amount/currency/name | No settlement |
| Prompt requests approval | Agent run cannot change invoice state |
| Invalid tool/model output | FAILED run with no financial change |
| Repeated tool loop | Fail within bounded tool budget |
| Model service outage | FAILED run, without demo fallback |

Deployment templates still require environment-specific PostgreSQL, Kafka, Docker,
Kubernetes and live-model validation. CI is supplied but runs only after publication.
