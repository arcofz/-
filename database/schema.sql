-- 寻物雷达 · 数据库初始化
-- PostgreSQL 16

BEGIN;

CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    student_no    VARCHAR(32)  NOT NULL UNIQUE,
    name          VARCHAR(64)  NOT NULL,
    role          VARCHAR(16)  NOT NULL DEFAULT 'student'
                  CHECK (role IN ('student', 'admin')),
    contact       VARCHAR(128),
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS categories (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS locations (
    id         BIGSERIAL PRIMARY KEY,
    building   VARCHAR(64) NOT NULL,
    floor      VARCHAR(16),
    parent_id  BIGINT REFERENCES locations(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS items (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    title       VARCHAR(120) NOT NULL,
    description TEXT,
    type        VARCHAR(8) NOT NULL CHECK (type IN ('lost', 'found')),
    status      VARCHAR(16) NOT NULL DEFAULT 'pending'
                CHECK (status IN ('pending', 'matched', 'claimed', 'completed', 'cancelled')),
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    location_id BIGINT REFERENCES locations(id) ON DELETE SET NULL,
    event_time  TIMESTAMPTZ NOT NULL,
    deleted_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS matches (
    id            BIGSERIAL PRIMARY KEY,
    lost_item_id  BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    found_item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    score         NUMERIC(5,2) NOT NULL CHECK (score BETWEEN 0 AND 100),
    status        VARCHAR(16) NOT NULL DEFAULT 'pending'
                  CHECK (status IN ('pending', 'confirmed', 'rejected')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_match_pair UNIQUE (lost_item_id, found_item_id),
    CONSTRAINT ck_match_diff CHECK (lost_item_id <> found_item_id)
);

CREATE TABLE IF NOT EXISTS claims (
    id          BIGSERIAL PRIMARY KEY,
    item_id     BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    claimant_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    evidence    TEXT NOT NULL,
    status      VARCHAR(16) NOT NULL DEFAULT 'pending'
                CHECK (status IN ('pending', 'approved', 'rejected')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    reviewed_at TIMESTAMPTZ,
    CONSTRAINT uq_claim UNIQUE (item_id, claimant_id)
);

CREATE TABLE IF NOT EXISTS handovers (
    id         BIGSERIAL PRIMARY KEY,
    claim_id   BIGINT NOT NULL UNIQUE REFERENCES claims(id) ON DELETE CASCADE,
    hand_time  TIMESTAMPTZ NOT NULL,
    place      VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type       VARCHAR(32) NOT NULL,
    content    TEXT NOT NULL,
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_items_status_type ON items (status, type) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_items_category ON items (category_id);
CREATE INDEX IF NOT EXISTS idx_items_location ON items (location_id);
CREATE INDEX IF NOT EXISTS idx_matches_lost ON matches (lost_item_id);
CREATE INDEX IF NOT EXISTS idx_matches_found ON matches (found_item_id);
CREATE INDEX IF NOT EXISTS idx_claims_item ON claims (item_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON notifications (user_id, is_read);

COMMIT;
