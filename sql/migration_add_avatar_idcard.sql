-- 为系统用户添加身份证和头像
ALTER TABLE sys_user ADD COLUMN id_card VARCHAR(18) DEFAULT NULL;
ALTER TABLE sys_user ADD COLUMN avatar VARCHAR(255) DEFAULT NULL;

-- 为患者添加头像
ALTER TABLE patient ADD COLUMN avatar VARCHAR(255) DEFAULT NULL;

-- 为现有用户生成 18 位随机身份证号（仅演示用）
UPDATE sys_user SET id_card = '110101199001010011' WHERE id = 1;
UPDATE sys_user SET id_card = '110101198502150022' WHERE id = 2;
UPDATE sys_user SET id_card = '110101199203200033' WHERE id = 3;
UPDATE sys_user SET id_card = '110101199505250044' WHERE id = 4;
