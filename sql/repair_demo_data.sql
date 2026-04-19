SET NAMES utf8mb4;
USE community_hospital;

UPDATE patient
SET name = CASE id
    WHEN 3 THEN '王秀兰'
    WHEN 4 THEN '赵志强'
    WHEN 5 THEN '孙慧敏'
    WHEN 6 THEN '周桂芳'
    WHEN 7 THEN '李海峰'
    WHEN 8 THEN '高敏'
    WHEN 9 THEN '杜明'
    WHEN 10 THEN '何春梅'
    WHEN 11 THEN '郭建军'
    WHEN 12 THEN '罗丹'
    WHEN 13 THEN '郑国强'
    WHEN 14 THEN '唐雪'
    WHEN 15 THEN '彭志远'
    WHEN 16 THEN '韩丽娜'
    WHEN 17 THEN '曹文博'
    WHEN 18 THEN '沈玉珍'
    WHEN 19 THEN '许晨'
    WHEN 20 THEN '蒋欣怡'
    ELSE name
END,
gender = CASE id
    WHEN 3 THEN '女'
    WHEN 4 THEN '男'
    WHEN 5 THEN '女'
    WHEN 6 THEN '女'
    WHEN 7 THEN '男'
    WHEN 8 THEN '女'
    WHEN 9 THEN '男'
    WHEN 10 THEN '女'
    WHEN 11 THEN '男'
    WHEN 12 THEN '女'
    WHEN 13 THEN '男'
    WHEN 14 THEN '女'
    WHEN 15 THEN '男'
    WHEN 16 THEN '女'
    WHEN 17 THEN '男'
    WHEN 18 THEN '女'
    WHEN 19 THEN '男'
    WHEN 20 THEN '女'
    ELSE gender
END,
address = CASE id
    WHEN 3 THEN '丰台区安康街 3 号'
    WHEN 4 THEN '通州区康复路 8 号'
    WHEN 5 THEN '昌平区和平里 6 号'
    WHEN 6 THEN '石景山区和谐路 9 号'
    WHEN 7 THEN '西城区安定门 12 号'
    WHEN 8 THEN '朝阳区育才路 15 号'
    WHEN 9 THEN '海淀区学院路 20 号'
    WHEN 10 THEN '大兴区康庄街 5 号'
    WHEN 11 THEN '丰台区兴华路 18 号'
    WHEN 12 THEN '通州区新城东里 7 号'
    WHEN 13 THEN '昌平区龙泽园 3 号'
    WHEN 14 THEN '顺义区幸福西街 11 号'
    WHEN 15 THEN '门头沟区永定镇 6 号'
    WHEN 16 THEN '房山区拱辰街道 2 号'
    WHEN 17 THEN '东城区安乐胡同 4 号'
    WHEN 18 THEN '怀柔区青春路 13 号'
    WHEN 19 THEN '密云区鼓楼南大街 10 号'
    WHEN 20 THEN '延庆区妫水北街 1 号'
    ELSE address
END,
allergy_history = CASE id
    WHEN 3 THEN '磺胺类药物过敏'
    WHEN 4 THEN '无'
    WHEN 5 THEN '海鲜过敏'
    WHEN 6 THEN '无'
    WHEN 7 THEN '青霉素过敏'
    WHEN 8 THEN '无'
    WHEN 9 THEN '无'
    WHEN 10 THEN '磺胺过敏'
    WHEN 11 THEN '无'
    WHEN 12 THEN '海鲜过敏'
    WHEN 13 THEN '无'
    WHEN 14 THEN '无'
    WHEN 15 THEN '无'
    WHEN 16 THEN '阿司匹林过敏'
    WHEN 17 THEN '无'
    WHEN 18 THEN '无'
    WHEN 19 THEN '无'
    WHEN 20 THEN '无'
    ELSE allergy_history
END,
medical_history = CASE id
    WHEN 3 THEN '2 型糖尿病 8 年、骨质疏松'
    WHEN 4 THEN '过敏性鼻炎反复发作'
    WHEN 5 THEN '产后 6 个月，轻度贫血'
    WHEN 6 THEN '冠心病 3 年'
    WHEN 7 THEN '脂肪肝'
    WHEN 8 THEN '偏头痛'
    WHEN 9 THEN '高血压、冠心病'
    WHEN 10 THEN '2 型糖尿病 5 年'
    WHEN 11 THEN '慢性咽炎'
    WHEN 12 THEN '缺铁性贫血'
    WHEN 13 THEN '慢阻肺'
    WHEN 14 THEN '甲状腺结节'
    WHEN 15 THEN '腰肌劳损'
    WHEN 16 THEN '高脂血症'
    WHEN 17 THEN '胃炎'
    WHEN 18 THEN '骨关节炎'
    WHEN 19 THEN '鼻炎'
    WHEN 20 THEN '体检复查'
    ELSE medical_history
END
WHERE id BETWEEN 3 AND 20;

UPDATE drug_info
SET drug_name = CASE id
    WHEN 3 THEN '二甲双胍片'
    WHEN 4 THEN '氯雷他定片'
    WHEN 5 THEN '琥珀酸亚铁片'
    WHEN 6 THEN '阿司匹林肠溶片'
    WHEN 7 THEN '缬沙坦胶囊'
    WHEN 8 THEN '布洛芬缓释胶囊'
    WHEN 9 THEN '阿莫西林胶囊'
    WHEN 10 THEN '维生素B12片'
    ELSE drug_name
END,
specification = CASE id
    WHEN 3 THEN '0.5g*60片'
    WHEN 4 THEN '10mg*12片'
    WHEN 5 THEN '0.1g*24片'
    WHEN 6 THEN '100mg*30片'
    WHEN 7 THEN '80mg*14粒'
    WHEN 8 THEN '0.3g*24粒'
    WHEN 9 THEN '0.25g*24粒'
    WHEN 10 THEN '25ug*100片'
    ELSE specification
END,
manufacturer = CASE id
    WHEN 3 THEN '社区制药厂'
    WHEN 4 THEN '健康药业'
    WHEN 5 THEN '民生药业'
    WHEN 6 THEN '华康药业'
    WHEN 7 THEN '安宁制药'
    WHEN 8 THEN '康民药业'
    WHEN 9 THEN '仁和制药'
    WHEN 10 THEN '惠民药业'
    ELSE manufacturer
END,
unit = CASE id
    WHEN 3 THEN '瓶'
    WHEN 4 THEN '盒'
    WHEN 5 THEN '盒'
    WHEN 6 THEN '盒'
    WHEN 7 THEN '盒'
    WHEN 8 THEN '盒'
    WHEN 9 THEN '盒'
    WHEN 10 THEN '瓶'
    ELSE unit
END
WHERE id BETWEEN 3 AND 10;

UPDATE visit_record
SET doctor_name = '张医生',
department = CASE id
    WHEN 3 THEN '全科门诊'
    WHEN 4 THEN '内分泌门诊'
    WHEN 5 THEN '耳鼻喉门诊'
    WHEN 6 THEN '全科门诊'
    WHEN 7 THEN '心内科'
    WHEN 8 THEN '心内科'
    WHEN 9 THEN '消化内科'
    WHEN 10 THEN '神经内科'
    WHEN 11 THEN '全科门诊'
    WHEN 12 THEN '内分泌门诊'
    WHEN 13 THEN '耳鼻喉门诊'
    WHEN 14 THEN '全科门诊'
    WHEN 15 THEN '呼吸内科'
    WHEN 16 THEN '内分泌门诊'
    WHEN 17 THEN '骨科门诊'
    WHEN 18 THEN '全科门诊'
    WHEN 19 THEN '消化内科'
    WHEN 20 THEN '骨科门诊'
    WHEN 21 THEN '耳鼻喉门诊'
    WHEN 22 THEN '全科门诊'
    WHEN 23 THEN '全科门诊'
    WHEN 24 THEN '消化内科'
    WHEN 25 THEN '内分泌门诊'
    WHEN 26 THEN '耳鼻喉门诊'
    WHEN 27 THEN '全科门诊'
    WHEN 28 THEN '全科门诊'
    WHEN 29 THEN '心内科'
    WHEN 30 THEN '耳鼻喉门诊'
    WHEN 31 THEN '消化内科'
    ELSE department
END,
chief_complaint = CASE id
    WHEN 3 THEN '复诊，血压晨起偏高'
    WHEN 4 THEN '餐后血糖波动，偶有乏力'
    WHEN 5 THEN '鼻塞、打喷嚏 1 周'
    WHEN 6 THEN '产后头晕、乏力'
    WHEN 7 THEN '胸闷、活动后气短'
    WHEN 8 THEN '复诊，血压控制一般'
    WHEN 9 THEN '右上腹不适'
    WHEN 10 THEN '反复头痛 3 天'
    WHEN 11 THEN '胸前区不适'
    WHEN 12 THEN '空腹血糖偏高'
    WHEN 13 THEN '咽干咽痛'
    WHEN 14 THEN '乏力、头晕'
    WHEN 15 THEN '咳嗽、咳痰加重'
    WHEN 16 THEN '体检发现甲状腺结节'
    WHEN 17 THEN '腰部酸痛'
    WHEN 18 THEN '血脂复查'
    WHEN 19 THEN '饭后胃胀'
    WHEN 20 THEN '膝关节疼痛'
    WHEN 21 THEN '晨起鼻塞'
    WHEN 22 THEN '健康咨询'
    WHEN 23 THEN '头晕复查'
    WHEN 24 THEN '胃胀复诊'
    WHEN 25 THEN '乏力复诊'
    WHEN 26 THEN '鼻痒打喷嚏'
    WHEN 27 THEN '乏力复诊'
    WHEN 28 THEN '头晕、血压偏高'
    WHEN 29 THEN '活动后胸闷'
    WHEN 30 THEN '咽部异物感'
    WHEN 31 THEN '反酸'
    ELSE chief_complaint
END,
notes = CASE id
    WHEN 3 THEN '继续监测血压并控制盐摄入'
    WHEN 4 THEN '建议规律复查空腹及餐后血糖'
    WHEN 5 THEN '考虑季节性过敏，注意环境清洁'
    WHEN 6 THEN '注意休息，饮食补铁'
    WHEN 7 THEN '建议完善心电图'
    WHEN 8 THEN '继续随访'
    WHEN 9 THEN '注意低脂饮食'
    WHEN 10 THEN '避免熬夜'
    WHEN 11 THEN '建议家属陪同复诊'
    WHEN 12 THEN '建议监测血糖'
    WHEN 13 THEN '少烟酒'
    WHEN 14 THEN '注意休息'
    WHEN 15 THEN '避免受凉'
    WHEN 16 THEN '定期复查超声'
    WHEN 17 THEN '减少久坐'
    WHEN 18 THEN '继续饮食管理'
    WHEN 19 THEN '清淡饮食'
    WHEN 20 THEN '避免长时间站立'
    WHEN 21 THEN '注意通风'
    WHEN 22 THEN '建议规律作息'
    WHEN 23 THEN '继续监测'
    WHEN 24 THEN '按时用药'
    WHEN 25 THEN '监测血糖'
    WHEN 26 THEN '减少粉尘暴露'
    WHEN 27 THEN '继续补铁'
    WHEN 28 THEN '建议动态血压监测'
    WHEN 29 THEN '必要时转上级医院'
    WHEN 30 THEN '避免辛辣刺激'
    WHEN 31 THEN '餐后勿立即平卧'
    ELSE notes
END
WHERE id BETWEEN 3 AND 31;

UPDATE diagnosis_record
SET diagnosis_name = CASE id
    WHEN 3 THEN '高血压'
    WHEN 4 THEN '2 型糖尿病'
    WHEN 5 THEN '过敏性鼻炎'
    WHEN 6 THEN '缺铁性贫血'
    WHEN 7 THEN '冠心病'
    WHEN 8 THEN '高血压'
    WHEN 9 THEN '高血压'
    WHEN 10 THEN '脂肪肝'
    WHEN 11 THEN '偏头痛'
    WHEN 12 THEN '冠心病'
    WHEN 13 THEN '2 型糖尿病'
    WHEN 14 THEN '慢性咽炎'
    WHEN 15 THEN '缺铁性贫血'
    WHEN 16 THEN '慢性阻塞性肺疾病'
    WHEN 17 THEN '甲状腺结节'
    WHEN 18 THEN '腰肌劳损'
    WHEN 19 THEN '高脂血症'
    WHEN 20 THEN '慢性胃炎'
    WHEN 21 THEN '骨关节炎'
    WHEN 22 THEN '过敏性鼻炎'
    WHEN 23 THEN '健康体检'
    WHEN 24 THEN '高血压'
    WHEN 25 THEN '慢性胃炎'
    WHEN 26 THEN '2 型糖尿病'
    WHEN 27 THEN '过敏性鼻炎'
    WHEN 28 THEN '缺铁性贫血'
    WHEN 29 THEN '高血压'
    WHEN 30 THEN '冠心病'
    WHEN 31 THEN '慢性咽炎'
    WHEN 32 THEN '胃食管反流'
    ELSE diagnosis_name
END,
description = CASE id
    WHEN 3 THEN '继续控制血压，建议早晚监测'
    WHEN 4 THEN '继续口服降糖药并控制饮食'
    WHEN 5 THEN '避免接触过敏原，必要时对症用药'
    WHEN 6 THEN '建议补铁并 2 周后复查血常规'
    ELSE description
END
WHERE id BETWEEN 3 AND 32;

UPDATE prescription_record p
JOIN drug_info d ON p.drug_id = d.id
SET p.drug_name = d.drug_name,
    p.dosage = CASE p.id
        WHEN 3 THEN '5mg'
        WHEN 4 THEN '0.5g'
        WHEN 5 THEN '10mg'
        WHEN 6 THEN '0.1g'
        WHEN 7 THEN '100mg'
        WHEN 8 THEN '80mg'
        WHEN 9 THEN '0.3g'
        WHEN 10 THEN '100mg'
        WHEN 11 THEN '80mg'
        WHEN 12 THEN '0.5g'
        WHEN 13 THEN '0.25g'
        WHEN 14 THEN '0.1g'
        WHEN 15 THEN '25ug'
        WHEN 16 THEN '0.25g'
        WHEN 17 THEN '0.3g'
        WHEN 18 THEN '20mg'
        WHEN 19 THEN '0.3g'
        WHEN 20 THEN '10mg'
        WHEN 21 THEN '5mg'
        WHEN 22 THEN '20mg'
        WHEN 23 THEN '0.5g'
        WHEN 24 THEN '10mg'
        WHEN 25 THEN '0.1g'
        WHEN 26 THEN '80mg'
        WHEN 27 THEN '100mg'
        WHEN 28 THEN '20mg'
        ELSE p.dosage
    END,
    p.frequency = CASE p.id
        WHEN 3 THEN '每日一次'
        WHEN 4 THEN '每日一次'
        WHEN 5 THEN '每日一次'
        WHEN 6 THEN '每日三次'
        WHEN 7 THEN '每日一次'
        WHEN 8 THEN '每日一次'
        WHEN 9 THEN '疼痛时服'
        WHEN 10 THEN '每日一次'
        WHEN 11 THEN '每日一次'
        WHEN 12 THEN '每日两次'
        WHEN 13 THEN '每日三次'
        WHEN 14 THEN '每日三次'
        WHEN 15 THEN '每日三次'
        WHEN 16 THEN '每日三次'
        WHEN 17 THEN '每日两次'
        WHEN 18 THEN '每日一次'
        WHEN 19 THEN '每日两次'
        WHEN 20 THEN '每日一次'
        WHEN 21 THEN '每日一次'
        WHEN 22 THEN '每日一次'
        WHEN 23 THEN '每日两次'
        WHEN 24 THEN '每日一次'
        WHEN 25 THEN '每日三次'
        WHEN 26 THEN '每日一次'
        WHEN 27 THEN '每日一次'
        WHEN 28 THEN '每日一次'
        ELSE p.frequency
    END
WHERE p.id BETWEEN 3 AND 28;

UPDATE drug_inventory_log
SET operator_name = CASE
        WHEN id IN (8, 9, 10, 11, 12) THEN '李药师'
        WHEN id >= 18 THEN '张医生'
        ELSE operator_name
    END,
    remark = CASE
        WHEN id IN (8, 9, 10) THEN '期初入库'
        WHEN id IN (11, 12) THEN '门诊发药'
        ELSE remark
    END
WHERE id >= 8;
