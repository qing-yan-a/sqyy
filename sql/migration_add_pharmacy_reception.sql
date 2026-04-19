-- 药师药房、前台属性
ALTER TABLE sys_user ADD COLUMN pharmacy VARCHAR(64) DEFAULT NULL;
ALTER TABLE sys_user ADD COLUMN reception_desk VARCHAR(64) DEFAULT NULL;

UPDATE sys_user u
JOIN sys_user_role sur ON u.id = sur.user_id
JOIN sys_role r ON sur.role_id = r.id
SET u.pharmacy = '西药房'
WHERE r.role_code = 'PHARMACIST';

UPDATE sys_user u
JOIN sys_user_role sur ON u.id = sur.user_id
JOIN sys_role r ON sur.role_id = r.id
SET u.reception_desk = '一号前台'
WHERE r.role_code = 'RECEPTION';
