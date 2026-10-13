CREATE TABLE vendor (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, name VARCHAR(120) NOT NULL, email VARCHAR(200) NOT NULL, currency VARCHAR(3) NOT NULL);
CREATE TABLE finance_invoice (
 id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, vendor_id UUID NOT NULL REFERENCES vendor(id), reference VARCHAR(80) NOT NULL,
 amount NUMERIC(18,2) NOT NULL CHECK(amount>0), currency VARCHAR(3) NOT NULL, due_date DATE NOT NULL, status VARCHAR(20) NOT NULL,
 created_by VARCHAR(100) NOT NULL, decision_reason VARCHAR(500), version BIGINT NOT NULL,
 UNIQUE(vendor_id,reference), CHECK(status IN ('RECEIVED','APPROVED','REJECTED','PAID'))
);
CREATE TABLE ledger_account (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, code VARCHAR(30) NOT NULL, currency VARCHAR(3) NOT NULL, UNIQUE(code,currency));
CREATE TABLE journal_entry (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, reference VARCHAR(120) NOT NULL UNIQUE, currency VARCHAR(3) NOT NULL, description VARCHAR(300) NOT NULL);
CREATE TABLE ledger_line (
 id UUID PRIMARY KEY, journal_id UUID NOT NULL REFERENCES journal_entry(id), account_id UUID NOT NULL REFERENCES ledger_account(id),
 debit NUMERIC(18,2) NOT NULL CHECK(debit>=0), credit NUMERIC(18,2) NOT NULL CHECK(credit>=0),
 CHECK((debit>0 AND credit=0) OR (credit>0 AND debit=0))
);
CREATE TABLE bank_transaction (
 id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, external_id VARCHAR(100) NOT NULL UNIQUE, reference VARCHAR(120) NOT NULL,
 counterparty VARCHAR(120) NOT NULL, amount NUMERIC(18,2) NOT NULL CHECK(amount>0), currency VARCHAR(3) NOT NULL, booked_on DATE NOT NULL,
 matched BOOLEAN NOT NULL, version BIGINT NOT NULL
);
CREATE TABLE reconciliation_match (
 id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 invoice_id UUID NOT NULL UNIQUE REFERENCES finance_invoice(id), bank_transaction_id UUID NOT NULL UNIQUE REFERENCES bank_transaction(id), matched_by VARCHAR(100) NOT NULL
);
CREATE TABLE audit_event (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, action VARCHAR(80) NOT NULL, actor VARCHAR(100) NOT NULL, resource_id UUID NOT NULL);
CREATE TABLE outbox_event (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, kind VARCHAR(80) NOT NULL, aggregate_id UUID NOT NULL, payload TEXT NOT NULL, published_at TIMESTAMP WITH TIME ZONE, attempts INTEGER NOT NULL);
CREATE TABLE idempotency_record (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, actor VARCHAR(100) NOT NULL, request_key VARCHAR(80) NOT NULL, fingerprint VARCHAR(64) NOT NULL, response TEXT, UNIQUE(actor,request_key));
CREATE TABLE agent_run (id UUID PRIMARY KEY, created_at TIMESTAMP WITH TIME ZONE NOT NULL, invoice_id UUID NOT NULL REFERENCES finance_invoice(id), invoice_version BIGINT NOT NULL, mode VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL, recommendation TEXT, trace TEXT NOT NULL, error_code VARCHAR(80));
CREATE INDEX invoice_currency_status ON finance_invoice(currency,status);
CREATE INDEX ledger_line_account ON ledger_line(account_id);
CREATE INDEX bank_currency_matched ON bank_transaction(currency,matched);
CREATE INDEX audit_resource_time ON audit_event(resource_id,created_at);
CREATE INDEX outbox_pending ON outbox_event(published_at,created_at);
CREATE INDEX agent_invoice_time ON agent_run(invoice_id,created_at);
