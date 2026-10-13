# Portfolio walkthrough

1. Start the default local demo and sign in as analyst.
2. Select ACME-2026-1001. Run evidence review, inspect policy IDs and the three tool results.
3. Confirm its amount, currency, reference and counterparty match the bank evidence.
4. Switch to approver, select the same invoice, add a review reason and approve.
5. Record the exact bank settlement; the invoice moves to PAID and a second journal appears.
6. Open the ledger; signed balances sum to zero. Inspect the audit actor and timestamps.
7. Review the Nova invoice: normal approver authorization is insufficient above the limit.
8. Review the overdue Acme invoice: the deterministic risk floor is MEDIUM.
9. Create an invoice as analyst and verify that same-account review is unavailable.
10. Optionally configure Ollama and rerun the evidence review; keep actual model results separate from the demo.

Suggested project statement: "Implemented a Java/Spring Boot invoice workflow with
idempotent commands, balanced double-entry journals, exact bank reconciliation,
role-gated human approvals, and a bounded read-only agent loop with auditable tool traces."

Only add performance, scale, model-quality or production claims after measuring them.
