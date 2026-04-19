-- 医生科室：为 sys_user 增加 department 字段
ALTER TABLE sys_user ADD COLUMN department VARCHAR(64) DEFAULT NULL;
-- 为已有医生设置默认科室（按实际医生用户 ID 调整）
UPDATE sys_user u
JOIN sys_user_role sur ON u.id = sur.user_id
JOIN sys_role r ON sur.role_id = r.id
SET u.department = '全科诊室'
WHERE r.role_code = 'DOCTOR';
