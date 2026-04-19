-- 挂号功能：为 visit_record 增加状态与排队号
ALTER TABLE visit_record ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED';
ALTER TABLE visit_record ADD COLUMN queue_number INT DEFAULT NULL;
