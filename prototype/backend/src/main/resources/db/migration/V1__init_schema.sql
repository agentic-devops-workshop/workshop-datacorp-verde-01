-- V1__init_schema.sql
-- Initial schema for SIFAP 2.0 Modular Monolith
-- Implements: REQ-BEN-001, REQ-PAY-001, REQ-AUD-001, REQ-PROG-001

-- Schemas per bounded context
CREATE SCHEMA IF NOT EXISTS beneficiary;
CREATE SCHEMA IF NOT EXISTS payment;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS admin;

-- Admin: Social Programs
CREATE TABLE admin.social_program (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    type            CHAR(1) NOT NULL CHECK (type IN ('A', 'P', 'T')),
    status          CHAR(1) NOT NULL DEFAULT 'A' CHECK (status IN ('A', 'I')),
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);

-- Admin: Reference Parameters (regional factors, income brackets, etc.)
CREATE TABLE admin.reference_parameter (
    id              BIGSERIAL PRIMARY KEY,
    category        VARCHAR(50) NOT NULL,
    code            VARCHAR(20) NOT NULL,
    value           NUMERIC(12, 6) NOT NULL,
    description     VARCHAR(200),
    valid_from      DATE NOT NULL DEFAULT CURRENT_DATE,
    valid_until     DATE,
    UNIQUE (category, code, valid_from)
);

-- Beneficiary
CREATE TABLE beneficiary.beneficiary (
    id                  BIGSERIAL PRIMARY KEY,
    cpf                 CHAR(11) NOT NULL UNIQUE,
    name                VARCHAR(200) NOT NULL,
    birth_date          DATE NOT NULL,
    status              CHAR(1) NOT NULL DEFAULT 'A' CHECK (status IN ('A', 'S', 'C', 'I', 'D')),
    region_code         INTEGER NOT NULL CHECK (region_code BETWEEN 1 AND 27),
    num_dependents      INTEGER NOT NULL DEFAULT 0,
    family_income       NUMERIC(12, 2) NOT NULL DEFAULT 0,
    social_program_id   BIGINT NOT NULL REFERENCES admin.social_program(id),
    base_value          NUMERIC(12, 2) NOT NULL,
    created_at          DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at          DATE
);

CREATE INDEX idx_beneficiary_status ON beneficiary.beneficiary(status);
CREATE INDEX idx_beneficiary_program ON beneficiary.beneficiary(social_program_id);

-- Beneficiary: Dependents
CREATE TABLE beneficiary.beneficiary_dependent (
    id              BIGSERIAL PRIMARY KEY,
    beneficiary_id  BIGINT NOT NULL REFERENCES beneficiary.beneficiary(id),
    name            VARCHAR(200) NOT NULL,
    birth_date      DATE NOT NULL,
    relationship    VARCHAR(50) NOT NULL,
    cpf             CHAR(11)
);

CREATE INDEX idx_dependent_beneficiary ON beneficiary.beneficiary_dependent(beneficiary_id);

-- Payment
CREATE TABLE payment.payment (
    id                  BIGSERIAL PRIMARY KEY,
    beneficiary_id      BIGINT NOT NULL,
    cpf                 CHAR(11) NOT NULL,
    competence_year_month VARCHAR(7) NOT NULL, -- YYYY-MM
    status              CHAR(1) NOT NULL DEFAULT 'G' CHECK (status IN ('G', 'P', 'D', 'E')),
    type                CHAR(1) NOT NULL DEFAULT 'N' CHECK (type IN ('N', 'D')),
    gross_value         NUMERIC(12, 2) NOT NULL,
    net_value           NUMERIC(12, 2) NOT NULL,
    total_discounts     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    thirteenth_value    NUMERIC(12, 2),
    christmas_bonus     NUMERIC(12, 2),
    social_program_id   BIGINT NOT NULL,
    batch_run_id        BIGINT,
    created_at          DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (beneficiary_id, competence_year_month)
);

CREATE INDEX idx_payment_competence ON payment.payment(competence_year_month);
CREATE INDEX idx_payment_status ON payment.payment(status);
CREATE INDEX idx_payment_cpf ON payment.payment(cpf);

-- Payment: Batch Run tracking
CREATE TABLE payment.payment_batch_run (
    id              BIGSERIAL PRIMARY KEY,
    competence      VARCHAR(7) NOT NULL,
    program_id      BIGINT NOT NULL,
    total_generated INTEGER NOT NULL DEFAULT 0,
    total_value     NUMERIC(14, 2) NOT NULL DEFAULT 0,
    started_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    finished_at     TIMESTAMP,
    status          VARCHAR(20) NOT NULL DEFAULT 'RUNNING'
);

-- Audit
CREATE TABLE audit.audit_event (
    id              BIGSERIAL PRIMARY KEY,
    action          CHAR(2) NOT NULL CHECK (action IN ('IN', 'AL', 'CO', 'CN', 'DV', 'EX')),
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       VARCHAR(100) NOT NULL,
    user_id         VARCHAR(100) NOT NULL,
    description     TEXT,
    timestamp       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity ON audit.audit_event(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit.audit_event(user_id);
CREATE INDEX idx_audit_timestamp ON audit.audit_event(timestamp);
