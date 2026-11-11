-- 基础分类与地点，供演示使用
BEGIN;

INSERT INTO categories (name) VALUES
    ('证件'), ('电子产品'), ('雨伞'), ('钥匙'), ('其他')
ON CONFLICT (name) DO NOTHING;

INSERT INTO locations (building, floor) VALUES
    ('图书馆', '1F'),
    ('图书馆', '2F'),
    ('教学楼A', '1F'),
    ('食堂', '1F'),
    ('操场', NULL);

COMMIT;
