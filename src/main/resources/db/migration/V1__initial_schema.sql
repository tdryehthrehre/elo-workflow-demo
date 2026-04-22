-- Mask: document type definition (analogous to ELO masks)
CREATE TABLE mask (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- MaskField: field definition within a mask
CREATE TABLE mask_field (
    id              BIGSERIAL PRIMARY KEY,
    mask_id         BIGINT NOT NULL REFERENCES mask(id) ON DELETE CASCADE,
    field_name      VARCHAR(100) NOT NULL,
    field_type      VARCHAR(50) NOT NULL,
    required        BOOLEAN NOT NULL DEFAULT FALSE,
    keyword_list_id BIGINT,
    sort_order      INTEGER NOT NULL DEFAULT 0
);

-- KeywordList: controlled vocabulary (analogous to ELO keyword lists)
CREATE TABLE keyword_list (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE keyword_entry (
    id              BIGSERIAL PRIMARY KEY,
    keyword_list_id BIGINT NOT NULL REFERENCES keyword_list(id) ON DELETE CASCADE,
    entry_value     VARCHAR(200) NOT NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0
);

ALTER TABLE mask_field ADD CONSTRAINT fk_mask_field_kwlist
    FOREIGN KEY (keyword_list_id) REFERENCES keyword_list(id);

-- Sord: the core document object (analogous to ELO Sord)
CREATE TABLE sord (
    id                BIGSERIAL PRIMARY KEY,
    short_description VARCHAR(255) NOT NULL,
    mask_id           BIGINT REFERENCES mask(id),
    parent_id         BIGINT REFERENCES sord(id),
    node_type         VARCHAR(20) NOT NULL DEFAULT 'DOCUMENT',
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- SordField: metadata values on a Sord
CREATE TABLE sord_field (
    id          BIGSERIAL PRIMARY KEY,
    sord_id     BIGINT NOT NULL REFERENCES sord(id) ON DELETE CASCADE,
    field_name  VARCHAR(100) NOT NULL,
    field_value VARCHAR(4000)
);

-- WFNode: workflow instance attached to a Sord
CREATE TABLE wf_node (
    id          BIGSERIAL PRIMARY KEY,
    sord_id     BIGINT NOT NULL REFERENCES sord(id),
    status      VARCHAR(30) NOT NULL DEFAULT 'INCOMING',
    assignee    VARCHAR(100),
    wf_comment  VARCHAR(4000),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- WFTransition: immutable audit log of status changes
CREATE TABLE wf_transition (
    id          BIGSERIAL PRIMARY KEY,
    wf_node_id  BIGINT NOT NULL REFERENCES wf_node(id),
    from_status VARCHAR(30) NOT NULL,
    to_status   VARCHAR(30) NOT NULL,
    performed_by VARCHAR(100),
    wf_comment  VARCHAR(4000),
    transitioned_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Seed data: basic masks
INSERT INTO mask (name, description) VALUES
    ('Invoice', 'Incoming and outgoing invoices'),
    ('Contract', 'Legal contracts and agreements'),
    ('Correspondence', 'Letters, emails, and general correspondence');

INSERT INTO keyword_list (name) VALUES
    ('DocumentStatus'),
    ('Department');

INSERT INTO keyword_entry (keyword_list_id, entry_value, sort_order) VALUES
    (1, 'Draft', 1), (1, 'Final', 2), (1, 'Archived', 3),
    (2, 'Finance', 1), (2, 'Legal', 2), (2, 'HR', 3), (2, 'Management', 4);
