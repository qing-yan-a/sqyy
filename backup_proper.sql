-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: community_hospital
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `diagnosis_record`
--

DROP TABLE IF EXISTS `diagnosis_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `visit_id` bigint NOT NULL,
  `diagnosis_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL,
  `diagnosis_type` varchar(32) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PRIMARY',
  `description` text COLLATE utf8mb4_general_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diagnosis_record`
--

LOCK TABLES `diagnosis_record` WRITE;
/*!40000 ALTER TABLE `diagnosis_record` DISABLE KEYS */;
INSERT INTO `diagnosis_record` VALUES (1,1,'高血压','PRIMARY','继续控制血压','2026-03-16 18:49:07'),(2,2,'慢性胃炎','PRIMARY','注意饮食规律','2026-03-16 18:49:07'),(3,3,'高血压','PRIMARY','继续控制血压，建议早晚监测','2026-03-13 10:05:00'),(4,4,'2 型糖尿病','PRIMARY','继续口服降糖药并控制饮食','2026-03-13 10:35:00'),(5,5,'过敏性鼻炎','PRIMARY','避免接触过敏原，必要时对症用药','2026-03-14 15:05:00'),(6,6,'缺铁性贫血','PRIMARY','建议补铁并 2 周后复查血常规','2026-03-15 09:25:00'),(7,7,'冠心病','PRIMARY','','2026-03-16 19:22:51'),(8,7,'高血压','PRIMARY','','2026-03-16 19:22:51'),(9,8,'高血压','PRIMARY','','2026-03-16 19:23:42'),(10,9,'脂肪肝','PRIMARY','','2026-03-16 19:23:43'),(11,10,'偏头痛','PRIMARY','','2026-03-16 19:23:43'),(12,11,'冠心病','PRIMARY','','2026-03-16 19:23:44'),(13,12,'2 型糖尿病','PRIMARY','','2026-03-16 19:23:45'),(14,13,'慢性咽炎','PRIMARY','','2026-03-16 19:23:45'),(15,14,'缺铁性贫血','PRIMARY','','2026-03-16 19:23:46'),(16,15,'慢性阻塞性肺疾病','PRIMARY','','2026-03-16 19:23:47'),(17,16,'甲状腺结节','PRIMARY','','2026-03-16 19:23:47'),(18,17,'腰肌劳损','PRIMARY','','2026-03-16 19:23:48'),(19,18,'高脂血症','PRIMARY','','2026-03-16 19:23:49'),(20,19,'慢性胃炎','PRIMARY','','2026-03-16 19:23:50'),(21,20,'骨关节炎','PRIMARY','','2026-03-16 19:23:50'),(22,21,'过敏性鼻炎','PRIMARY','','2026-03-16 19:23:51'),(23,22,'健康体检','PRIMARY','','2026-03-16 19:23:52'),(24,23,'高血压','PRIMARY','','2026-03-16 19:23:53'),(25,24,'慢性胃炎','PRIMARY','','2026-03-16 19:23:53'),(26,25,'2 型糖尿病','PRIMARY','','2026-03-16 19:23:54'),(27,26,'过敏性鼻炎','PRIMARY','','2026-03-16 19:23:55'),(28,27,'缺铁性贫血','PRIMARY','','2026-03-16 19:23:56'),(29,28,'高血压','PRIMARY','','2026-03-16 19:23:56'),(30,29,'冠心病','PRIMARY','','2026-03-16 19:23:57'),(32,31,'胃食管反流','PRIMARY','','2026-03-16 19:23:59'),(34,32,'感冒','PRIMARY','','2026-03-17 14:29:07'),(36,34,'高烧','PRIMARY','','2026-03-17 16:08:04'),(37,33,'肾虚','PRIMARY','','2026-03-17 16:42:06'),(38,35,'风湿','PRIMARY','','2026-03-17 16:56:24'),(39,30,'慢性咽炎','PRIMARY','','2026-03-17 17:13:01'),(41,37,'风湿','PRIMARY','','2026-03-17 18:06:28'),(42,36,'贫血','PRIMARY','','2026-03-17 18:06:42'),(43,39,'胃溃疡','PRIMARY','','2026-03-17 18:29:01'),(45,38,'腿疼','PRIMARY','','2026-03-18 13:51:26'),(48,40,'肺炎','PRIMARY','','2026-03-18 13:55:51'),(49,41,'心绞痛','PRIMARY','','2026-03-18 14:10:56'),(50,42,'脑溢血','PRIMARY','','2026-03-18 14:26:06'),(51,45,'b','PRIMARY','','2026-03-18 16:36:06'),(53,43,'c','PRIMARY','','2026-03-18 16:36:36');
/*!40000 ALTER TABLE `diagnosis_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug_info`
--

DROP TABLE IF EXISTS `drug_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `drug_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `drug_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL,
  `specification` varchar(128) COLLATE utf8mb4_general_ci NOT NULL,
  `manufacturer` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `unit` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `warning_stock` int NOT NULL DEFAULT '10',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `drug_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ORAL',
  `unit_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `reserved_stock` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `drug_code` (`drug_code`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug_info`
--

LOCK TABLES `drug_info` WRITE;
/*!40000 ALTER TABLE `drug_info` DISABLE KEYS */;
INSERT INTO `drug_info` VALUES (1,'D2026001','氨氯地平片','5mg*28片','社区制药厂','盒',114,20,'2026-03-16 18:49:07','2026-03-17 16:05:24','ORAL',26.50,0),(2,'D2026002','奥美拉唑肠溶胶囊','20mg*14粒','健康药业','盒',77,15,'2026-03-16 18:49:07','2026-03-17 16:05:24','ORAL',32.00,0),(3,'D2026003','二甲双胍片','0.5g*60片','社区制药厂','瓶',54,15,'2026-03-16 19:19:13','2026-03-17 16:05:24','INJECTION',48.00,0),(4,'D2026004','氯雷他定片','10mg*12片','健康药业','盒',37,10,'2026-03-16 19:19:13','2026-03-17 16:05:24','INJECTION',15.00,0),(5,'D2026005','琥珀酸亚铁片','0.1g*24片','民生药业','盒',29,8,'2026-03-16 19:19:13','2026-03-17 16:05:24','INJECTION',12.00,0),(6,'D20260316192157158843','阿司匹林肠溶片','100mg*30片','华康药业','盒',115,20,'2026-03-16 19:21:57','2026-03-18 14:06:17','ORAL',13.00,0),(7,'D20260316192157851458','缬沙坦胶囊','80mg*14粒','安宁制药','盒',84,15,'2026-03-16 19:21:57','2026-03-18 14:06:17','ORAL',10.00,0),(8,'D20260316192158334496','布洛芬缓释胶囊','0.3g*24粒','康民药业','盒',95,20,'2026-03-16 19:21:58','2026-03-18 14:06:17','ORAL',10.50,0),(9,'D20260316192158782415','阿莫西林胶囊','0.25g*24粒','仁和制药','盒',106,20,'2026-03-16 19:21:58','2026-03-18 14:06:17','ORAL',24.00,0),(10,'D20260316192159219917','维生素B12片','25ug*100片','惠民药业','瓶',55,10,'2026-03-16 19:21:59','2026-03-18 14:06:17','ORAL',17.00,0),(31,'D20260317180138519776','感康','10mg*15粒','民生药业','盒',1,10,'2026-03-17 18:01:38','2026-03-18 14:22:45','ORAL',6.00,0);
/*!40000 ALTER TABLE `drug_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug_inventory_log`
--

DROP TABLE IF EXISTS `drug_inventory_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_inventory_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `drug_id` bigint NOT NULL,
  `change_type` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int NOT NULL,
  `before_stock` int NOT NULL,
  `after_stock` int NOT NULL,
  `operator_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug_inventory_log`
--

LOCK TABLES `drug_inventory_log` WRITE;
/*!40000 ALTER TABLE `drug_inventory_log` DISABLE KEYS */;
INSERT INTO `drug_inventory_log` VALUES (1,1,'IN',120,0,120,'李药师','期初入库','2026-03-16 18:49:07'),(2,2,'IN',80,0,80,'李药师','期初入库','2026-03-16 18:49:07'),(3,1,'OUT',2,120,118,'李药师','门诊发药','2026-03-16 18:49:07'),(4,2,'OUT',3,80,77,'???','处方发药','2026-03-16 18:56:14'),(5,2,'IN',3,77,80,'admin','编辑就诊记录回退库存','2026-03-16 18:56:15'),(6,2,'OUT',1,80,79,'admin','编辑就诊记录重新发药','2026-03-16 18:56:15'),(7,2,'IN',1,79,80,'admin','删除就诊记录回退库存','2026-03-16 18:56:15'),(8,3,'IN',60,0,60,'李药师','期初入库','2026-03-12 09:00:00'),(9,4,'IN',45,0,45,'李药师','期初入库','2026-03-12 09:10:00'),(10,5,'IN',35,0,35,'李药师','期初入库','2026-03-12 09:20:00'),(11,3,'OUT',2,60,58,'李药师','门诊发药','2026-03-13 10:35:00'),(12,4,'OUT',2,45,43,'李药师','门诊发药','2026-03-14 15:20:00'),(13,6,'IN',120,0,120,'系统初始化','新建药品','2026-03-16 19:21:57'),(14,7,'IN',90,0,90,'系统初始化','新建药品','2026-03-16 19:21:57'),(15,8,'IN',100,0,100,'系统初始化','新建药品','2026-03-16 19:21:58'),(16,9,'IN',110,0,110,'系统初始化','新建药品','2026-03-16 19:21:58'),(17,10,'IN',60,0,60,'系统初始化','新建药品','2026-03-16 19:21:59'),(18,6,'OUT',2,120,118,'张医生','处方发药','2026-03-16 19:22:51'),(19,7,'OUT',2,90,88,'张医生','处方发药','2026-03-16 19:23:42'),(20,8,'OUT',1,100,99,'张医生','处方发药','2026-03-16 19:23:43'),(21,6,'OUT',1,118,117,'张医生','处方发药','2026-03-16 19:23:44'),(22,7,'OUT',1,88,87,'张医生','处方发药','2026-03-16 19:23:44'),(23,3,'OUT',2,58,56,'张医生','处方发药','2026-03-16 19:23:45'),(24,9,'OUT',2,110,108,'张医生','处方发药','2026-03-16 19:23:45'),(25,5,'OUT',1,35,34,'张医生','处方发药','2026-03-16 19:23:46'),(26,10,'OUT',1,60,59,'张医生','处方发药','2026-03-16 19:23:46'),(27,9,'OUT',2,108,106,'张医生','处方发药','2026-03-16 19:23:47'),(28,8,'OUT',1,99,98,'张医生','处方发药','2026-03-16 19:23:48'),(29,2,'OUT',1,80,79,'张医生','处方发药','2026-03-16 19:23:50'),(30,8,'OUT',1,98,97,'张医生','处方发药','2026-03-16 19:23:50'),(31,4,'OUT',1,43,42,'张医生','处方发药','2026-03-16 19:23:51'),(32,1,'OUT',1,117,116,'张医生','处方发药','2026-03-16 19:23:53'),(33,2,'OUT',1,79,78,'张医生','处方发药','2026-03-16 19:23:53'),(34,3,'OUT',2,56,54,'张医生','处方发药','2026-03-16 19:23:54'),(35,4,'OUT',1,42,41,'张医生','处方发药','2026-03-16 19:23:55'),(36,5,'OUT',1,34,33,'张医生','处方发药','2026-03-16 19:23:56'),(37,7,'OUT',2,87,85,'张医生','处方发药','2026-03-16 19:23:56'),(38,6,'OUT',1,117,116,'张医生','处方发药','2026-03-16 19:23:57'),(39,2,'OUT',1,78,77,'张医生','处方发药','2026-03-16 19:23:59'),(40,3,'IN',2,54,56,'李药师','处方药','2026-03-16 20:03:36'),(42,8,'OUT',1,97,96,'doctor','编辑就诊记录重新发药','2026-03-17 14:01:39'),(43,8,'IN',1,96,97,'doctor','编辑就诊记录回退库存','2026-03-17 14:29:07'),(44,8,'OUT',1,97,96,'doctor','编辑就诊记录重新发药','2026-03-17 14:29:07'),(45,3,'IN',50,0,50,'李药师','注射药物期初入库','2026-03-17 15:21:25'),(46,4,'IN',80,0,80,'李药师','注射药物期初入库','2026-03-17 15:21:25'),(47,5,'IN',60,0,60,'李药师','注射药物期初入库','2026-03-17 15:21:25'),(48,8,'OUT',1,96,95,'doctor','编辑就诊记录重新发药','2026-03-17 15:25:33'),(49,8,'IN',1,95,96,'doctor','编辑就诊记录回退库存','2026-03-17 16:08:04'),(50,8,'OUT',1,96,95,'pharmacist','药房确认取药','2026-03-17 16:08:42'),(51,3,'OUT',1,56,55,'pharmacist','药房确认取药','2026-03-17 16:08:42'),(52,1,'OUT',1,116,115,'黄药师','药房确认取药','2026-03-17 16:56:48'),(53,5,'OUT',2,33,31,'黄药师','药房确认取药','2026-03-17 16:56:48'),(54,10,'OUT',1,59,58,'pharmacist','药房确认取药','2026-03-17 17:13:42'),(55,4,'OUT',1,41,40,'pharmacist','药房确认取药','2026-03-17 17:13:42'),(56,31,'IN',15,0,15,'李药师','','2026-03-17 18:02:06'),(57,6,'OUT',1,116,115,'pharmacist','药房确认取药','2026-03-17 18:04:32'),(58,4,'OUT',1,40,39,'pharmacist','药房确认取药','2026-03-17 18:04:32'),(59,6,'IN',1,115,116,'doctor','编辑就诊记录回退库存','2026-03-17 18:06:42'),(60,4,'IN',1,39,40,'doctor','编辑就诊记录回退库存','2026-03-17 18:06:42'),(61,1,'OUT',1,115,114,'黄药师','药房确认取药','2026-03-17 18:07:12'),(62,4,'OUT',1,40,39,'黄药师','药房确认取药','2026-03-17 18:07:12'),(63,6,'OUT',1,116,115,'pharmacist','药房确认取药','2026-03-17 18:07:29'),(64,4,'OUT',1,39,38,'pharmacist','药房确认取药','2026-03-17 18:07:29'),(65,7,'OUT',1,85,84,'pharmacist','药房确认取药','2026-03-17 18:30:03'),(66,5,'OUT',1,31,30,'pharmacist','药房确认取药','2026-03-17 18:30:03'),(67,9,'OUT',2,106,104,'黄药师','药房确认取药','2026-03-17 18:30:31'),(68,3,'OUT',1,55,54,'黄药师','药房确认取药','2026-03-17 18:30:31'),(69,9,'IN',2,104,106,'doctor','编辑就诊记录回退库存','2026-03-18 13:51:26'),(70,3,'IN',1,54,55,'doctor','编辑就诊记录回退库存','2026-03-18 13:51:26'),(71,31,'OUT',2,15,13,'pharmacist','药房确认取药','2026-03-18 13:51:48'),(72,3,'OUT',1,55,54,'pharmacist','药房确认取药','2026-03-18 13:51:48'),(73,31,'OUT',1,13,12,'pharmacist','药房确认取药','2026-03-18 13:54:13'),(74,31,'IN',1,12,13,'王五','编辑就诊记录回退库存','2026-03-18 13:55:32'),(75,31,'OUT',1,13,12,'pharmacist','药房确认取药','2026-03-18 14:11:15'),(76,31,'IN',9,12,21,'李药师','','2026-03-18 14:24:53'),(77,31,'OUT',13,21,8,'pharmacist','药房确认取药','2026-03-18 14:25:00'),(78,31,'OUT',6,8,2,'pharmacist','药房确认取药','2026-03-18 14:27:38'),(79,10,'OUT',3,58,55,'pharmacist','药房确认取药','2026-03-18 16:36:52'),(80,4,'OUT',1,38,37,'pharmacist','药房确认取药','2026-03-18 16:36:52'),(81,31,'OUT',1,2,1,'黄药师','药房确认取药','2026-03-18 16:37:09'),(82,5,'OUT',1,30,29,'黄药师','药房确认取药','2026-03-18 16:37:09');
/*!40000 ALTER TABLE `drug_inventory_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_no` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `gender` varchar(16) COLLATE utf8mb4_general_ci NOT NULL,
  `age` int NOT NULL,
  `phone` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_card` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `allergy_history` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `medical_history` text COLLATE utf8mb4_general_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `patient_no` (`patient_no`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (1,'P2026001','刘建国','男',58,'13900000001','110101196801010011','朝阳区社区路 1 号','青霉素过敏','高血压 5 年','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL),(2,'P2026002','陈丽华','女',42,'13900000002','110101198402020022','海淀区健康街 2 号','无','胃炎病史','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL),(3,'P2026003','王秀兰','女',67,'13900000003','110101195903030033','丰台区安康街 3 号','磺胺类药物过敏','2 型糖尿病 8 年、骨质疏松','2026-03-16 19:19:13','2026-03-16 19:30:00',NULL),(4,'P2026004','赵志强','男',35,'13900000004','110101199103040044','通州区康复路 8 号','无','过敏性鼻炎反复发作','2026-03-16 19:19:13','2026-03-16 19:30:00',NULL),(5,'P2026005','孙慧敏','女',29,'13900000005','110101199701050055','昌平区和平里 6 号','海鲜过敏','产后 6 个月，轻度贫血','2026-03-16 19:19:13','2026-03-16 19:30:00',NULL),(6,'P20260316192159718730','周桂芳','女',61,'13900000006','110101196502060066','石景山区和谐路 9 号','无','冠心病 3 年','2026-03-16 19:21:59','2026-03-16 19:30:00',NULL),(7,'P20260316192200195149','李海峰','男',47,'13900000007','110101197903070077','西城区安定门 12 号','青霉素过敏','脂肪肝','2026-03-16 19:22:00','2026-03-16 19:30:00',NULL),(8,'P20260316192200669655','高敏','女',33,'13900000008','110101199304080088','朝阳区育才路 15 号','无','偏头痛','2026-03-16 19:22:00','2026-03-16 19:30:00',NULL),(9,'P20260316192201167315','杜明','男',72,'13900000009','110101195404090099','海淀区学院路 20 号','无','高血压、冠心病','2026-03-16 19:22:01','2026-03-16 19:30:00',NULL),(10,'P20260316192201650269','何春梅','女',54,'13900000010','110101197202100010','大兴区康庄街 5 号','磺胺过敏','2 型糖尿病 5 年','2026-03-16 19:22:01','2026-03-16 19:30:00',NULL),(11,'P20260316192202156355','郭建军','男',39,'13900000011','110101198711110011','丰台区兴华路 18 号','无','慢性咽炎','2026-03-16 19:22:02','2026-03-16 19:30:00',NULL),(12,'P20260316192202674190','罗丹','女',28,'13900000012','110101199802120012','通州区新城东里 7 号','海鲜过敏','缺铁性贫血','2026-03-16 19:22:02','2026-03-16 19:30:00',NULL),(13,'P20260316192203192608','郑国强','男',65,'13900000013','110101196103130013','昌平区龙泽园 3 号','无','慢阻肺','2026-03-16 19:22:03','2026-03-16 19:30:00',NULL),(14,'P20260316192203724200','唐雪','女',31,'13900000014','110101199503140014','顺义区幸福西街 11 号','无','甲状腺结节','2026-03-16 19:22:03','2026-03-16 19:30:00',NULL),(15,'P20260316192204236174','彭志远','男',44,'13900000015','110101198203150015','门头沟区永定镇 6 号','无','腰肌劳损','2026-03-16 19:22:04','2026-03-16 19:30:00',NULL),(16,'P20260316192204804453','韩丽娜','女',58,'13900000016','110101196803160016','房山区拱辰街道 2 号','阿司匹林过敏','高脂血症','2026-03-16 19:22:04','2026-03-16 19:30:00',NULL),(17,'P20260316192205338862','曹文博','男',26,'13900000017','110101200003170017','东城区安乐胡同 4 号','无','胃炎','2026-03-16 19:22:05','2026-03-16 19:30:00',NULL),(18,'P20260316192205899415','沈玉珍','女',70,'13900000018','110101195603180018','怀柔区青春路 13 号','无','骨关节炎','2026-03-16 19:22:05','2026-03-16 19:30:00',NULL),(19,'P20260316192206458273','许晨','男',36,'13900000019','110101199003190019','密云区鼓楼南大街 10 号','无','鼻炎','2026-03-16 19:22:06','2026-03-16 19:30:00','/avatars/patient_cfb4b7f9e181.jpg'),(20,'P20260316192207032611','蒋欣怡','女',24,'13900000020','110101200202200020','延庆区妫水北街 1 号','无','体检复查','2026-03-16 19:22:07','2026-03-16 19:30:00','/avatars/patient_312347c9a001.png'),(21,'P20260317180315187267','张三','男',25,'18182133256','412828200109013021','','无','无','2026-03-17 18:03:15','2026-03-17 18:03:15',NULL),(22,'P20260317180525737436','李四','男',25,'18182133259','412828200109013023','','无','无','2026-03-17 18:05:25','2026-03-17 18:05:25','/avatars/patient_8819c0306fd0.jpg'),(23,'P20260317182737272509','子欣','女',20,'','412828200603175012','','无','无','2026-03-17 18:27:37','2026-03-17 18:27:37',NULL),(24,'P20260318140927840862','时夜','男',45,'11122233345','012345678912345678','','无','无','2026-03-18 14:09:27','2026-03-18 14:09:27',NULL),(25,'P20260318163445352327','aa','男',20,'','123456789123456789','','a','a','2026-03-18 16:34:45','2026-03-18 16:34:45',NULL);
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescription_record`
--

DROP TABLE IF EXISTS `prescription_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `visit_id` bigint NOT NULL,
  `drug_id` bigint NOT NULL,
  `drug_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL,
  `dosage` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `frequency` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `days` int DEFAULT '1',
  `quantity` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `unit_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `line_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `drug_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ORAL',
  `payment_status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'UNPAID',
  `pickup_status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING',
  `injection_status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_REQUIRED',
  `paid_at` datetime DEFAULT NULL,
  `picked_up_at` datetime DEFAULT NULL,
  `injection_completed_at` datetime DEFAULT NULL,
  `injection_assignee_username` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescription_record`
--

LOCK TABLES `prescription_record` WRITE;
/*!40000 ALTER TABLE `prescription_record` DISABLE KEYS */;
INSERT INTO `prescription_record` VALUES (1,1,1,'氨氯地平片','5mg','每日一次',14,2,'2026-03-16 18:49:07',26.50,53.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL,NULL),(2,2,2,'奥美拉唑肠溶胶囊','20mg','每日一次',7,1,'2026-03-16 18:49:07',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL,NULL),(3,3,1,'氨氯地平片','5mg','每日一次',14,1,'2026-03-13 10:06:00',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-13 10:06:00','2026-03-13 10:06:00',NULL,NULL),(4,4,3,'二甲双胍片','0.5g','每日一次',14,2,'2026-03-13 10:36:00',48.00,96.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-13 10:36:00','2026-03-13 10:36:00','2026-03-13 10:36:00',NULL),(5,5,4,'氯雷他定片','10mg','每日一次',7,2,'2026-03-14 15:06:00',15.00,30.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-14 15:06:00','2026-03-14 15:06:00','2026-03-14 15:06:00',NULL),(6,6,5,'琥珀酸亚铁片','0.1g','每日三次',10,1,'2026-03-15 09:26:00',12.00,12.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-15 09:26:00','2026-03-15 09:26:00','2026-03-15 09:26:00',NULL),(7,7,6,'阿司匹林肠溶片','100mg','每日一次',14,2,'2026-03-16 19:22:51',13.00,26.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:22:51','2026-03-16 19:22:51',NULL,NULL),(8,8,7,'缬沙坦胶囊','80mg','每日一次',14,2,'2026-03-16 19:23:42',10.00,20.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:42','2026-03-16 19:23:42',NULL,NULL),(9,10,8,'布洛芬缓释胶囊','0.3g','疼痛时服',3,1,'2026-03-16 19:23:43',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:43','2026-03-16 19:23:43',NULL,NULL),(10,11,6,'阿司匹林肠溶片','100mg','每日一次',7,1,'2026-03-16 19:23:44',13.00,13.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:44','2026-03-16 19:23:44',NULL,NULL),(11,11,7,'缬沙坦胶囊','80mg','每日一次',7,1,'2026-03-16 19:23:44',10.00,10.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:44','2026-03-16 19:23:44',NULL,NULL),(12,12,3,'二甲双胍片','0.5g','每日两次',14,2,'2026-03-16 19:23:45',48.00,96.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:45','2026-03-16 19:23:45','2026-03-16 19:23:45',NULL),(13,13,9,'阿莫西林胶囊','0.25g','每日三次',5,2,'2026-03-16 19:23:45',24.00,48.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:45','2026-03-16 19:23:45',NULL,NULL),(14,14,5,'琥珀酸亚铁片','0.1g','每日三次',10,1,'2026-03-16 19:23:46',12.00,12.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:46','2026-03-16 19:23:46','2026-03-16 19:23:46',NULL),(15,14,10,'维生素B12片','25ug','每日三次',10,1,'2026-03-16 19:23:46',17.00,17.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:46','2026-03-16 19:23:46',NULL,NULL),(16,15,9,'阿莫西林胶囊','0.25g','每日三次',7,2,'2026-03-16 19:23:47',24.00,48.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:47','2026-03-16 19:23:47',NULL,NULL),(17,17,8,'布洛芬缓释胶囊','0.3g','每日两次',5,1,'2026-03-16 19:23:48',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:48','2026-03-16 19:23:48',NULL,NULL),(18,19,2,'奥美拉唑肠溶胶囊','20mg','每日一次',7,1,'2026-03-16 19:23:50',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:50','2026-03-16 19:23:50',NULL,NULL),(19,20,8,'布洛芬缓释胶囊','0.3g','每日两次',5,1,'2026-03-16 19:23:50',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:50','2026-03-16 19:23:50',NULL,NULL),(20,21,4,'氯雷他定片','10mg','每日一次',7,1,'2026-03-16 19:23:51',15.00,15.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:51','2026-03-16 19:23:51','2026-03-16 19:23:51',NULL),(21,23,1,'氨氯地平片','5mg','每日一次',14,1,'2026-03-16 19:23:53',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:53','2026-03-16 19:23:53',NULL,NULL),(22,24,2,'奥美拉唑肠溶胶囊','20mg','每日一次',7,1,'2026-03-16 19:23:53',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:53','2026-03-16 19:23:53',NULL,NULL),(23,25,3,'二甲双胍片','0.5g','每日两次',14,2,'2026-03-16 19:23:54',48.00,96.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:54','2026-03-16 19:23:54','2026-03-16 19:23:54',NULL),(24,26,4,'氯雷他定片','10mg','每日一次',7,1,'2026-03-16 19:23:55',15.00,15.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:55','2026-03-16 19:23:55','2026-03-16 19:23:55',NULL),(25,27,5,'琥珀酸亚铁片','0.1g','每日三次',10,1,'2026-03-16 19:23:56',12.00,12.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:56','2026-03-16 19:23:56','2026-03-16 19:23:56',NULL),(26,28,7,'缬沙坦胶囊','80mg','每日一次',14,2,'2026-03-16 19:23:56',10.00,20.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:56','2026-03-16 19:23:56',NULL,NULL),(27,29,6,'阿司匹林肠溶片','100mg','每日一次',7,1,'2026-03-16 19:23:57',13.00,13.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:57','2026-03-16 19:23:57',NULL,NULL),(28,31,2,'奥美拉唑肠溶胶囊','20mg','每日一次',14,1,'2026-03-16 19:23:59',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:59','2026-03-16 19:23:59',NULL,NULL),(30,32,8,'布洛芬缓释胶囊','1','3',1,1,'2026-03-17 14:29:07',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 14:29:07','2026-03-17 14:29:07',NULL,NULL),(32,34,8,'布洛芬缓释胶囊','3','3',1,1,'2026-03-17 16:08:04',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 16:08:42','2026-03-17 16:08:43',NULL,NULL),(33,34,3,'二甲双胍片','1','2',1,1,'2026-03-17 16:08:04',48.00,48.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 16:08:42','2026-03-17 16:08:43','2026-03-17 16:08:50',NULL),(34,33,2,'奥美拉唑肠溶胶囊','3','3',1,2,'2026-03-17 16:42:06',32.00,64.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 16:42:06','2026-03-17 16:42:06',NULL,NULL),(35,33,4,'氯雷他定片','2','3',1,1,'2026-03-17 16:42:06',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 16:42:06','2026-03-17 16:42:06','2026-03-17 16:42:06',NULL),(36,35,1,'氨氯地平片','3','3',1,1,'2026-03-17 16:56:24',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 16:56:49','2026-03-17 16:56:49',NULL,NULL),(37,35,5,'琥珀酸亚铁片','1','1',1,2,'2026-03-17 16:56:24',12.00,24.00,'INJECTION','PAID','DISPENSED','PENDING','2026-03-17 16:56:49','2026-03-17 16:56:49',NULL,NULL),(38,30,10,'维生素B12片','3','3',1,1,'2026-03-17 17:13:01',17.00,17.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 17:13:42','2026-03-17 17:13:42',NULL,NULL),(39,30,4,'氯雷他定片','1','3',1,1,'2026-03-17 17:13:01',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 17:13:42','2026-03-17 17:13:42','2026-03-17 17:13:56','pharmacist'),(42,37,1,'氨氯地平片','3','3',1,1,'2026-03-17 18:06:28',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 18:07:12','2026-03-17 18:07:13',NULL,NULL),(43,37,4,'氯雷他定片','1','1',1,1,'2026-03-17 18:06:28',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 18:07:12','2026-03-17 18:07:13','2026-03-17 18:07:39','黄药师'),(44,36,6,'阿司匹林肠溶片','3','3',1,1,'2026-03-17 18:06:42',13.00,13.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 18:07:29','2026-03-17 18:07:29',NULL,NULL),(45,36,4,'氯雷他定片','1','1',1,1,'2026-03-17 18:06:42',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 18:07:29','2026-03-17 18:07:29','2026-03-17 18:07:46','pharmacist'),(46,39,7,'缬沙坦胶囊','3','3',1,1,'2026-03-17 18:29:01',10.00,10.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 18:30:03','2026-03-17 18:30:03',NULL,NULL),(47,39,5,'琥珀酸亚铁片','','1',1,1,'2026-03-17 18:29:01',12.00,12.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 18:30:03','2026-03-17 18:30:03','2026-03-17 18:30:58','pharmacist'),(50,38,31,'感康','16','3',1,2,'2026-03-18 13:51:26',6.00,12.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 13:51:47','2026-03-18 13:51:48',NULL,NULL),(51,38,3,'二甲双胍片','1','1',1,1,'2026-03-18 13:51:26',48.00,48.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-18 13:51:47','2026-03-18 13:51:48','2026-03-18 13:52:07','pharmacist'),(54,40,31,'感康','10','1',1,1,'2026-03-18 13:55:51',6.00,6.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 14:11:15','2026-03-18 14:11:16',NULL,NULL),(55,41,31,'感康','3片','3',1,13,'2026-03-18 14:10:56',6.00,78.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 14:11:38','2026-03-18 14:25:01',NULL,NULL),(56,42,31,'感康','5片/次','3',1,6,'2026-03-18 14:26:06',6.00,36.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 14:27:38','2026-03-18 14:27:39',NULL,NULL),(57,45,10,'维生素B12片','5','3',1,3,'2026-03-18 16:36:06',17.00,51.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 16:36:51','2026-03-18 16:36:52',NULL,NULL),(58,45,4,'氯雷他定片','3','3',1,1,'2026-03-18 16:36:06',15.00,15.00,'INJECTION','PAID','DISPENSED','PENDING','2026-03-18 16:36:51','2026-03-18 16:36:52',NULL,'pharmacist'),(61,43,31,'感康','1','1',1,1,'2026-03-18 16:36:36',6.00,6.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 16:37:08','2026-03-18 16:37:10',NULL,NULL),(62,43,5,'琥珀酸亚铁片','3','3',1,1,'2026-03-18 16:36:36',12.00,12.00,'INJECTION','PAID','DISPENSED','PENDING','2026-03-18 16:37:08','2026-03-18 16:37:10',NULL,'黄药师');
/*!40000 ALTER TABLE `prescription_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `role_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'ADMIN','系统管理员','2026-03-16 18:49:07'),(2,'DOCTOR','医生','2026-03-16 18:49:07'),(3,'PHARMACIST','药师','2026-03-16 18:49:07'),(4,'RECEPTION','导诊/前台','2026-03-16 18:49:07');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_card` varchar(18) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `department` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pharmacy` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `reception_desk` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','123456','系统管理员','13800000001',1,'2026-03-16 18:49:07','2026-03-16 21:10:05','110101199001010011','/avatars/user_700e1d01c6b9.jpg',NULL,NULL,NULL),(2,'doctor','123456','张医生','13800000002',1,'2026-03-16 18:49:07','2026-03-17 14:25:47','110101198502150022','/avatars/user_ad83b6c9d396.jpg','全科诊室',NULL,NULL),(3,'pharmacist','123456','李药师','13800000003',1,'2026-03-16 18:49:07','2026-03-17 14:34:56','110101199203200033','/avatars/user_854fd6e21faa.jpg',NULL,'西药房',NULL),(4,'reception','123456','王前台','13800000004',1,'2026-03-16 18:49:07','2026-03-17 14:34:56','110101199505250044',NULL,NULL,NULL,'一号前台'),(7,'黄药师','123456','黄药师',NULL,1,'2026-03-17 16:38:19','2026-03-17 16:38:19','110101199804086963',NULL,NULL,'西药房',NULL),(8,'王五','123456','王五',NULL,1,'2026-03-17 18:24:02','2026-03-17 18:24:02','110101200310115388',NULL,'全科诊室',NULL,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,1,'2026-03-16 18:49:07'),(2,2,2,'2026-03-16 18:49:07'),(3,3,3,'2026-03-16 18:49:07'),(4,4,4,'2026-03-16 18:49:07'),(7,7,3,'2026-03-17 16:38:19'),(8,8,2,'2026-03-17 18:24:02');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visit_record`
--

DROP TABLE IF EXISTS `visit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `visit_no` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `doctor_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `department` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `chief_complaint` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `visit_time` datetime NOT NULL,
  `notes` text COLLATE utf8mb4_general_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'COMPLETED',
  `queue_number` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `visit_no` (`visit_no`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visit_record`
--

LOCK TABLES `visit_record` WRITE;
/*!40000 ALTER TABLE `visit_record` DISABLE KEYS */;
INSERT INTO `visit_record` VALUES (1,1,'V2026001','张医生','全科门诊','头晕、血压波动','2026-03-10 09:30:00','建议规律监测血压','2026-03-16 18:49:07','COMPLETED',NULL),(2,2,'V2026002','张医生','消化内科','胃部不适','2026-03-11 14:00:00','饮食清淡','2026-03-16 18:49:07','COMPLETED',NULL),(3,1,'V2026003','张医生','全科门诊','复诊，血压晨起偏高','2026-03-13 10:00:00','继续监测血压并控制盐摄入','2026-03-16 19:19:13','COMPLETED',NULL),(4,3,'V2026004','张医生','内分泌门诊','餐后血糖波动，偶有乏力','2026-03-13 10:30:00','建议规律复查空腹及餐后血糖','2026-03-16 19:19:13','COMPLETED',NULL),(5,4,'V2026005','张医生','耳鼻喉门诊','鼻塞、打喷嚏 1 周','2026-03-14 15:00:00','考虑季节性过敏，注意环境清洁','2026-03-16 19:19:13','COMPLETED',NULL),(6,5,'V2026006','张医生','全科门诊','产后头晕、乏力','2026-03-15 09:20:00','注意休息，饮食补铁','2026-03-16 19:19:13','COMPLETED',NULL),(7,6,'V20260316192251907668','张医生','心内科','胸闷、活动后气短','2026-03-12 09:00:00','建议完善心电图','2026-03-16 19:22:51','COMPLETED',NULL),(8,6,'V20260316192342490213','张医生','心内科','复诊，血压控制一般','2026-03-16 09:00:00','继续随访','2026-03-16 19:23:42','COMPLETED',NULL),(9,7,'V20260316192343327150','张医生','消化内科','右上腹不适','2026-03-12 10:00:00','注意低脂饮食','2026-03-16 19:23:43','COMPLETED',NULL),(10,8,'V20260316192343981131','张医生','神经内科','反复头痛 3 天','2026-03-12 11:00:00','避免熬夜','2026-03-16 19:23:43','COMPLETED',NULL),(11,9,'V20260316192344625618','张医生','全科门诊','胸前区不适','2026-03-12 14:00:00','建议家属陪同复诊','2026-03-16 19:23:44','COMPLETED',NULL),(12,10,'V20260316192345271406','张医生','内分泌门诊','空腹血糖偏高','2026-03-12 15:00:00','建议监测血糖','2026-03-16 19:23:45','COMPLETED',NULL),(13,11,'V20260316192345919382','张医生','耳鼻喉门诊','咽干咽痛','2026-03-13 09:00:00','少烟酒','2026-03-16 19:23:45','COMPLETED',NULL),(14,12,'V20260316192346572410','张医生','全科门诊','乏力、头晕','2026-03-13 10:00:00','注意休息','2026-03-16 19:23:46','COMPLETED',NULL),(15,13,'V20260316192347240495','张医生','呼吸内科','咳嗽、咳痰加重','2026-03-13 11:00:00','避免受凉','2026-03-16 19:23:47','COMPLETED',NULL),(16,14,'V20260316192347922872','张医生','内分泌门诊','体检发现甲状腺结节','2026-03-13 14:00:00','定期复查超声','2026-03-16 19:23:47','COMPLETED',NULL),(17,15,'V20260316192348608791','张医生','骨科门诊','腰部酸痛','2026-03-13 15:00:00','减少久坐','2026-03-16 19:23:48','COMPLETED',NULL),(18,16,'V20260316192349316219','张医生','全科门诊','血脂复查','2026-03-14 09:00:00','继续饮食管理','2026-03-16 19:23:49','COMPLETED',NULL),(19,17,'V20260316192350030505','张医生','消化内科','饭后胃胀','2026-03-14 10:00:00','清淡饮食','2026-03-16 19:23:50','COMPLETED',NULL),(20,18,'V20260316192350768238','张医生','骨科门诊','膝关节疼痛','2026-03-14 11:00:00','避免长时间站立','2026-03-16 19:23:50','COMPLETED',NULL),(21,19,'V20260316192351514285','张医生','耳鼻喉门诊','晨起鼻塞','2026-03-14 14:00:00','注意通风','2026-03-16 19:23:51','COMPLETED',NULL),(22,20,'V20260316192352268457','张医生','全科门诊','健康咨询','2026-03-14 15:00:00','建议规律作息','2026-03-16 19:23:52','COMPLETED',NULL),(23,1,'V20260316192353051498','张医生','全科门诊','头晕复查','2026-03-15 09:00:00','继续监测','2026-03-16 19:23:53','COMPLETED',NULL),(24,2,'V20260316192353819697','张医生','消化内科','胃胀复诊','2026-03-15 10:00:00','按时用药','2026-03-16 19:23:53','COMPLETED',NULL),(25,3,'V20260316192354591251','张医生','内分泌门诊','乏力复诊','2026-03-15 11:00:00','监测血糖','2026-03-16 19:23:54','COMPLETED',NULL),(26,4,'V20260316192355353652','张医生','耳鼻喉门诊','鼻痒打喷嚏','2026-03-15 14:00:00','减少粉尘暴露','2026-03-16 19:23:55','COMPLETED',NULL),(27,5,'V20260316192356168320','张医生','全科门诊','乏力复诊','2026-03-15 15:00:00','继续补铁','2026-03-16 19:23:56','COMPLETED',NULL),(28,6,'V20260316192356979303','张医生','全科门诊','头晕、血压偏高','2026-03-16 10:00:00','建议动态血压监测','2026-03-16 19:23:56','COMPLETED',NULL),(29,9,'V20260316192357824252','张医生','心内科','活动后胸闷','2026-03-16 11:00:00','必要时转上级医院','2026-03-16 19:23:57','COMPLETED',NULL),(30,11,'V20260316192358659889','张医生','耳鼻喉门诊','咽部异物感','2026-03-16 14:00:00','避免辛辣刺激','2026-03-16 19:23:58','COMPLETED',NULL),(31,17,'V20260316192359479142','张医生','消化内科','反酸','2026-03-16 15:00:00','餐后勿立即平卧','2026-03-16 19:23:59','COMPLETED',NULL),(32,20,'V20260317140039222735','张医生','全科门诊','发烧','2026-03-17 14:00:39','多喝热水','2026-03-17 14:00:39','COMPLETED',1),(33,19,'V20260317140401899571','xxx','全科门诊','腰疼','2026-03-17 14:04:02','注意身体','2026-03-17 14:04:01','COMPLETED',1),(34,20,'V20260317142821426746','张医生','全科门诊','头疼','2026-03-17 14:28:21','注射退烧药','2026-03-17 14:28:21','COMPLETED',1),(35,19,'V20260317165445308127','张医生','全科门诊','腿疼','2026-03-17 16:54:45','穿厚点','2026-03-17 16:54:45','COMPLETED',1),(36,21,'V20260317180322042591','张医生','全科门诊','头晕','2026-03-17 18:03:22','注意饮食','2026-03-17 18:03:22','COMPLETED',1),(37,22,'V20260317180628121247','张医生','全科门诊','体寒','2026-03-28 00:00:00','注意饮食','2026-03-17 18:06:28','COMPLETED',NULL),(38,23,'V20260317182744622637','张医生','全科门诊','复查','2026-03-17 18:27:45','骨折','2026-03-17 18:27:44','COMPLETED',1),(39,22,'V20260317182751861742','王五','全科门诊','胃疼','2026-03-17 18:27:52','注意饮食','2026-03-17 18:27:51','COMPLETED',1),(40,23,'V20260318135307107690','王五','全科门诊','咳嗽','2026-03-18 13:53:07','','2026-03-18 13:53:07','COMPLETED',1),(41,24,'V20260318140948788514','张医生','全科门诊','无','2026-03-18 14:09:49','吃好喝好','2026-03-18 14:09:48','COMPLETED',1),(42,24,'V20260318142325234371','王五','全科门诊','头疼','2026-03-18 14:23:25','降压','2026-03-18 14:23:25','COMPLETED',1),(43,19,'V20260318142625276717','张医生','全科门诊','c','2026-03-18 14:26:25','c','2026-03-18 14:26:25','COMPLETED',1),(44,25,'V20260318163452783335','王五','全科门诊','待接诊','2026-03-18 16:34:53','','2026-03-18 16:34:52','PENDING',1),(45,25,'V20260318163454495902','王五','全科门诊','b','2026-03-18 16:34:54','b','2026-03-18 16:34:54','COMPLETED',2);
/*!40000 ALTER TABLE `visit_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-28 13:41:20
