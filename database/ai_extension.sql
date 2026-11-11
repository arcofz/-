-- 可选：AI 识图扩展
-- 需要先安装 pgvector 扩展；image_embedding 的维度按所用 embedding 模型调整
-- 下面以 1024 维为例

CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE items ADD COLUMN IF NOT EXISTS image_url TEXT;
ALTER TABLE items ADD COLUMN IF NOT EXISTS ai_keywords JSONB;
ALTER TABLE items ADD COLUMN IF NOT EXISTS ai_status VARCHAR(16) NOT NULL DEFAULT 'none'
    CHECK (ai_status IN ('none', 'pending', 'done', 'failed'));
ALTER TABLE items ADD COLUMN IF NOT EXISTS image_embedding vector(1024);

CREATE INDEX IF NOT EXISTS idx_items_ai_keywords ON items USING GIN (ai_keywords);
CREATE INDEX IF NOT EXISTS idx_items_image_embedding
    ON items USING hnsw (image_embedding vector_cosine_ops);
