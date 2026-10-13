# Follow-up roadmap

The source and README describe the implemented version. These are proposed extensions:

1. Replace shared role accounts with OIDC and individual maker/checker identities.
2. Add PostgreSQL Testcontainers checks and database upgrade/rollback rehearsals.
3. Add vector retrieval with evaluated document chunking and citation provenance.
4. Build an MCP adapter for existing read tools, preserving the same authorization.
5. Add configurable per-currency review limits and approval escalation.
6. Add CSV/statement import with format validation, deduplication and import reports.
7. Add per-tenant keys, row-level authorization and tenant-specific ledgers.
8. Add distributed outbox consumer deduplication, dead-letter monitoring and replay.
9. Add real model evaluation sets, prompt versioning and operator-approved benchmarks.
10. Add rate limits, audit exports, storage retention, backups and performance testing.

Each follow-up should ship a working increment with behavioral verification and a
focused pull request. The current history already contains more than 100 meaningful
changes; future commits should continue to represent real implementation work.
