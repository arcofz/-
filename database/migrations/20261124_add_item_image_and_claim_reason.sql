-- 2026-11-24 信息发布支持图片,认领驳回支持理由
-- 可重复执行(IF NOT EXISTS),与 schema.sql 保持一致

BEGIN;

ALTER TABLE items ADD COLUMN IF NOT EXISTS image_url VARCHAR(512);

ALTER TABLE claims ADD COLUMN IF NOT EXISTS reason TEXT;

COMMIT;
