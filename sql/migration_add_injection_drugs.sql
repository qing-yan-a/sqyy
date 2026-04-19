-- 药品类型：口服 / 注射
ALTER TABLE drug_info ADD COLUMN drug_type VARCHAR(20) NOT NULL DEFAULT 'ORAL';
ALTER TABLE drug_info ADD COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00;

ALTER TABLE prescription_record ADD COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00;
ALTER TABLE prescription_record ADD COLUMN line_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;
ALTER TABLE prescription_record ADD COLUMN drug_type VARCHAR(20) NOT NULL DEFAULT 'ORAL';
ALTER TABLE prescription_record ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID';
ALTER TABLE prescription_record ADD COLUMN pickup_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
ALTER TABLE prescription_record ADD COLUMN injection_status VARCHAR(20) NOT NULL DEFAULT 'NOT_REQUIRED';
ALTER TABLE prescription_record ADD COLUMN paid_at DATETIME NULL;
ALTER TABLE prescription_record ADD COLUMN picked_up_at DATETIME NULL;
ALTER TABLE prescription_record ADD COLUMN injection_completed_at DATETIME NULL;

UPDATE drug_info SET unit_price = 26.50 WHERE drug_code = 'D2026001';
UPDATE drug_info SET unit_price = 32.00 WHERE drug_code = 'D2026002';

-- 插入注射类药物
INSERT INTO drug_info (drug_code, drug_name, specification, manufacturer, unit, stock, warning_stock, unit_price, drug_type) VALUES
('D2026003', '注射用头孢曲松钠', '1.0g/支', '华北制药', '支', 50, 10, 48.00, 'INJECTION'),
('D2026004', '维生素C注射液', '5ml:1g/支', '石药集团', '支', 80, 15, 15.00, 'INJECTION'),
('D2026005', '地塞米松磷酸钠注射液', '1ml:5mg/支', '天津药业', '支', 60, 10, 12.00, 'INJECTION');

UPDATE drug_info SET unit_price = 48.00, drug_type = 'INJECTION' WHERE drug_code = 'D2026003';
UPDATE drug_info SET unit_price = 15.00, drug_type = 'INJECTION' WHERE drug_code = 'D2026004';
UPDATE drug_info SET unit_price = 12.00, drug_type = 'INJECTION' WHERE drug_code = 'D2026005';

-- 插入注射类药物期初入库流水
INSERT INTO drug_inventory_log (drug_id, change_type, quantity, before_stock, after_stock, operator_name, remark)
SELECT d.id, 'IN', 50, 0, 50, '李药师', '注射药物期初入库'
FROM drug_info d
WHERE d.drug_code = 'D2026003'
  AND NOT EXISTS (
    SELECT 1 FROM drug_inventory_log l
    WHERE l.drug_id = d.id AND l.change_type = 'IN' AND l.remark = '注射药物期初入库'
  );

-- 历史处方因旧逻辑已在医生开药时扣减库存，迁移后标记为已收款、已取药
UPDATE prescription_record p
LEFT JOIN drug_info d ON d.id = p.drug_id
SET p.unit_price = COALESCE(NULLIF(p.unit_price, 0), d.unit_price, 0),
    p.line_amount = COALESCE(NULLIF(p.line_amount, 0), COALESCE(d.unit_price, 0) * p.quantity),
    p.drug_type = COALESCE(d.drug_type, 'ORAL'),
    p.payment_status = 'PAID',
    p.pickup_status = 'DISPENSED',
    p.injection_status = CASE
        WHEN COALESCE(d.drug_type, 'ORAL') = 'INJECTION' THEN 'COMPLETED'
        ELSE 'NOT_REQUIRED'
    END,
    p.paid_at = COALESCE(p.paid_at, p.created_at),
    p.picked_up_at = COALESCE(p.picked_up_at, p.created_at),
    p.injection_completed_at = CASE
        WHEN COALESCE(d.drug_type, 'ORAL') = 'INJECTION' THEN COALESCE(p.injection_completed_at, p.created_at)
        ELSE NULL
    END;

INSERT INTO drug_inventory_log (drug_id, change_type, quantity, before_stock, after_stock, operator_name, remark)
SELECT d.id, 'IN', 80, 0, 80, '李药师', '注射药物期初入库'
FROM drug_info d
WHERE d.drug_code = 'D2026004'
  AND NOT EXISTS (
    SELECT 1 FROM drug_inventory_log l
    WHERE l.drug_id = d.id AND l.change_type = 'IN' AND l.remark = '注射药物期初入库'
  );

INSERT INTO drug_inventory_log (drug_id, change_type, quantity, before_stock, after_stock, operator_name, remark)
SELECT d.id, 'IN', 60, 0, 60, '李药师', '注射药物期初入库'
FROM drug_info d
WHERE d.drug_code = 'D2026005'
  AND NOT EXISTS (
    SELECT 1 FROM drug_inventory_log l
    WHERE l.drug_id = d.id AND l.change_type = 'IN' AND l.remark = '注射药物期初入库'
  );
