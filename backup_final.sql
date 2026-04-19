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
INSERT INTO `diagnosis_record` VALUES (1,1,'楂樿鍘?,'PRIMARY','缁х画鎺у埗琛€鍘?,'2026-03-16 18:49:07'),(2,2,'鎱㈡€ц儍鐐?,'PRIMARY','娉ㄦ剰楗瑙勫緥','2026-03-16 18:49:07'),(3,3,'楂樿鍘?,'PRIMARY','缁х画鎺у埗琛€鍘嬶紝寤鸿鏃╂櫄鐩戞祴','2026-03-13 10:05:00'),(4,4,'2 鍨嬬硸灏跨梾','PRIMARY','缁х画鍙ｆ湇闄嶇硸鑽苟鎺у埗楗','2026-03-13 10:35:00'),(5,5,'杩囨晱鎬ч蓟鐐?,'PRIMARY','閬垮厤鎺ヨЕ杩囨晱鍘燂紝蹇呰鏃跺鐥囩敤鑽?,'2026-03-14 15:05:00'),(6,6,'缂洪搧鎬ц传琛€','PRIMARY','寤鸿琛ラ搧骞?2 鍛ㄥ悗澶嶆煡琛€甯歌','2026-03-15 09:25:00'),(7,7,'鍐犲績鐥?,'PRIMARY','','2026-03-16 19:22:51'),(8,7,'楂樿鍘?,'PRIMARY','','2026-03-16 19:22:51'),(9,8,'楂樿鍘?,'PRIMARY','','2026-03-16 19:23:42'),(10,9,'鑴傝偑鑲?,'PRIMARY','','2026-03-16 19:23:43'),(11,10,'鍋忓ご鐥?,'PRIMARY','','2026-03-16 19:23:43'),(12,11,'鍐犲績鐥?,'PRIMARY','','2026-03-16 19:23:44'),(13,12,'2 鍨嬬硸灏跨梾','PRIMARY','','2026-03-16 19:23:45'),(14,13,'鎱㈡€у捊鐐?,'PRIMARY','','2026-03-16 19:23:45'),(15,14,'缂洪搧鎬ц传琛€','PRIMARY','','2026-03-16 19:23:46'),(16,15,'鎱㈡€ч樆濉炴€ц偤鐤剧梾','PRIMARY','','2026-03-16 19:23:47'),(17,16,'鐢茬姸鑵虹粨鑺?,'PRIMARY','','2026-03-16 19:23:47'),(18,17,'鑵拌倢鍔虫崯','PRIMARY','','2026-03-16 19:23:48'),(19,18,'楂樿剛琛€鐥?,'PRIMARY','','2026-03-16 19:23:49'),(20,19,'鎱㈡€ц儍鐐?,'PRIMARY','','2026-03-16 19:23:50'),(21,20,'楠ㄥ叧鑺傜値','PRIMARY','','2026-03-16 19:23:50'),(22,21,'杩囨晱鎬ч蓟鐐?,'PRIMARY','','2026-03-16 19:23:51'),(23,22,'鍋ュ悍浣撴','PRIMARY','','2026-03-16 19:23:52'),(24,23,'楂樿鍘?,'PRIMARY','','2026-03-16 19:23:53'),(25,24,'鎱㈡€ц儍鐐?,'PRIMARY','','2026-03-16 19:23:53'),(26,25,'2 鍨嬬硸灏跨梾','PRIMARY','','2026-03-16 19:23:54'),(27,26,'杩囨晱鎬ч蓟鐐?,'PRIMARY','','2026-03-16 19:23:55'),(28,27,'缂洪搧鎬ц传琛€','PRIMARY','','2026-03-16 19:23:56'),(29,28,'楂樿鍘?,'PRIMARY','','2026-03-16 19:23:56'),(30,29,'鍐犲績鐥?,'PRIMARY','','2026-03-16 19:23:57'),(32,31,'鑳冮绠″弽娴?,'PRIMARY','','2026-03-16 19:23:59'),(34,32,'鎰熷啋','PRIMARY','','2026-03-17 14:29:07'),(36,34,'楂樼儳','PRIMARY','','2026-03-17 16:08:04'),(37,33,'鑲捐櫄','PRIMARY','','2026-03-17 16:42:06'),(38,35,'椋庢箍','PRIMARY','','2026-03-17 16:56:24'),(39,30,'鎱㈡€у捊鐐?,'PRIMARY','','2026-03-17 17:13:01'),(41,37,'椋庢箍','PRIMARY','','2026-03-17 18:06:28'),(42,36,'璐','PRIMARY','','2026-03-17 18:06:42'),(43,39,'鑳冩簝鐤?,'PRIMARY','','2026-03-17 18:29:01'),(45,38,'鑵跨柤','PRIMARY','','2026-03-18 13:51:26'),(48,40,'鑲虹値','PRIMARY','','2026-03-18 13:55:51'),(49,41,'蹇冪粸鐥?,'PRIMARY','','2026-03-18 14:10:56'),(50,42,'鑴戞孩琛€','PRIMARY','','2026-03-18 14:26:06'),(51,45,'b','PRIMARY','','2026-03-18 16:36:06'),(53,43,'c','PRIMARY','','2026-03-18 16:36:36');
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
INSERT INTO `drug_info` VALUES (1,'D2026001','姘ㄦ隘鍦板钩鐗?,'5mg*28鐗?,'绀惧尯鍒惰嵂鍘?,'鐩?,114,20,'2026-03-16 18:49:07','2026-03-17 16:05:24','ORAL',26.50,0),(2,'D2026002','濂ョ編鎷夊攽鑲犳憾鑳跺泭','20mg*14绮?,'鍋ュ悍鑽笟','鐩?,77,15,'2026-03-16 18:49:07','2026-03-17 16:05:24','ORAL',32.00,0),(3,'D2026003','浜岀敳鍙岃儘鐗?,'0.5g*60鐗?,'绀惧尯鍒惰嵂鍘?,'鐡?,54,15,'2026-03-16 19:19:13','2026-03-17 16:05:24','INJECTION',48.00,0),(4,'D2026004','姘浄浠栧畾鐗?,'10mg*12鐗?,'鍋ュ悍鑽笟','鐩?,37,10,'2026-03-16 19:19:13','2026-03-17 16:05:24','INJECTION',15.00,0),(5,'D2026005','鐞ョ弨閰镐簹閾佺墖','0.1g*24鐗?,'姘戠敓鑽笟','鐩?,29,8,'2026-03-16 19:19:13','2026-03-17 16:05:24','INJECTION',12.00,0),(6,'D20260316192157158843','闃垮徃鍖规灄鑲犳憾鐗?,'100mg*30鐗?,'鍗庡悍鑽笟','鐩?,115,20,'2026-03-16 19:21:57','2026-03-18 14:06:17','ORAL',13.00,0),(7,'D20260316192157851458','缂矙鍧﹁兌鍥?,'80mg*14绮?,'瀹夊畞鍒惰嵂','鐩?,84,15,'2026-03-16 19:21:57','2026-03-18 14:06:17','ORAL',10.00,0),(8,'D20260316192158334496','甯冩礇鑺紦閲婅兌鍥?,'0.3g*24绮?,'搴锋皯鑽笟','鐩?,95,20,'2026-03-16 19:21:58','2026-03-18 14:06:17','ORAL',10.50,0),(9,'D20260316192158782415','闃胯帿瑗挎灄鑳跺泭','0.25g*24绮?,'浠佸拰鍒惰嵂','鐩?,106,20,'2026-03-16 19:21:58','2026-03-18 14:06:17','ORAL',24.00,0),(10,'D20260316192159219917','缁寸敓绱燘12鐗?,'25ug*100鐗?,'鎯犳皯鑽笟','鐡?,55,10,'2026-03-16 19:21:59','2026-03-18 14:06:17','ORAL',17.00,0),(31,'D20260317180138519776','鎰熷悍','10mg*15绮?,'姘戠敓鑽笟','鐩?,1,10,'2026-03-17 18:01:38','2026-03-18 14:22:45','ORAL',6.00,0);
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
INSERT INTO `drug_inventory_log` VALUES (1,1,'IN',120,0,120,'鏉庤嵂甯?,'鏈熷垵鍏ュ簱','2026-03-16 18:49:07'),(2,2,'IN',80,0,80,'鏉庤嵂甯?,'鏈熷垵鍏ュ簱','2026-03-16 18:49:07'),(3,1,'OUT',2,120,118,'鏉庤嵂甯?,'闂ㄨ瘖鍙戣嵂','2026-03-16 18:49:07'),(4,2,'OUT',3,80,77,'???','澶勬柟鍙戣嵂','2026-03-16 18:56:14'),(5,2,'IN',3,77,80,'admin','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-16 18:56:15'),(6,2,'OUT',1,80,79,'admin','缂栬緫灏辫瘖璁板綍閲嶆柊鍙戣嵂','2026-03-16 18:56:15'),(7,2,'IN',1,79,80,'admin','鍒犻櫎灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-16 18:56:15'),(8,3,'IN',60,0,60,'鏉庤嵂甯?,'鏈熷垵鍏ュ簱','2026-03-12 09:00:00'),(9,4,'IN',45,0,45,'鏉庤嵂甯?,'鏈熷垵鍏ュ簱','2026-03-12 09:10:00'),(10,5,'IN',35,0,35,'鏉庤嵂甯?,'鏈熷垵鍏ュ簱','2026-03-12 09:20:00'),(11,3,'OUT',2,60,58,'鏉庤嵂甯?,'闂ㄨ瘖鍙戣嵂','2026-03-13 10:35:00'),(12,4,'OUT',2,45,43,'鏉庤嵂甯?,'闂ㄨ瘖鍙戣嵂','2026-03-14 15:20:00'),(13,6,'IN',120,0,120,'绯荤粺鍒濆鍖?,'鏂板缓鑽搧','2026-03-16 19:21:57'),(14,7,'IN',90,0,90,'绯荤粺鍒濆鍖?,'鏂板缓鑽搧','2026-03-16 19:21:57'),(15,8,'IN',100,0,100,'绯荤粺鍒濆鍖?,'鏂板缓鑽搧','2026-03-16 19:21:58'),(16,9,'IN',110,0,110,'绯荤粺鍒濆鍖?,'鏂板缓鑽搧','2026-03-16 19:21:58'),(17,10,'IN',60,0,60,'绯荤粺鍒濆鍖?,'鏂板缓鑽搧','2026-03-16 19:21:59'),(18,6,'OUT',2,120,118,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:22:51'),(19,7,'OUT',2,90,88,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:42'),(20,8,'OUT',1,100,99,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:43'),(21,6,'OUT',1,118,117,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:44'),(22,7,'OUT',1,88,87,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:44'),(23,3,'OUT',2,58,56,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:45'),(24,9,'OUT',2,110,108,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:45'),(25,5,'OUT',1,35,34,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:46'),(26,10,'OUT',1,60,59,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:46'),(27,9,'OUT',2,108,106,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:47'),(28,8,'OUT',1,99,98,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:48'),(29,2,'OUT',1,80,79,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:50'),(30,8,'OUT',1,98,97,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:50'),(31,4,'OUT',1,43,42,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:51'),(32,1,'OUT',1,117,116,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:53'),(33,2,'OUT',1,79,78,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:53'),(34,3,'OUT',2,56,54,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:54'),(35,4,'OUT',1,42,41,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:55'),(36,5,'OUT',1,34,33,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:56'),(37,7,'OUT',2,87,85,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:56'),(38,6,'OUT',1,117,116,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:57'),(39,2,'OUT',1,78,77,'寮犲尰鐢?,'澶勬柟鍙戣嵂','2026-03-16 19:23:59'),(40,3,'IN',2,54,56,'鏉庤嵂甯?,'澶勬柟鑽?,'2026-03-16 20:03:36'),(42,8,'OUT',1,97,96,'doctor','缂栬緫灏辫瘖璁板綍閲嶆柊鍙戣嵂','2026-03-17 14:01:39'),(43,8,'IN',1,96,97,'doctor','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-17 14:29:07'),(44,8,'OUT',1,97,96,'doctor','缂栬緫灏辫瘖璁板綍閲嶆柊鍙戣嵂','2026-03-17 14:29:07'),(45,3,'IN',50,0,50,'鏉庤嵂甯?,'娉ㄥ皠鑽墿鏈熷垵鍏ュ簱','2026-03-17 15:21:25'),(46,4,'IN',80,0,80,'鏉庤嵂甯?,'娉ㄥ皠鑽墿鏈熷垵鍏ュ簱','2026-03-17 15:21:25'),(47,5,'IN',60,0,60,'鏉庤嵂甯?,'娉ㄥ皠鑽墿鏈熷垵鍏ュ簱','2026-03-17 15:21:25'),(48,8,'OUT',1,96,95,'doctor','缂栬緫灏辫瘖璁板綍閲嶆柊鍙戣嵂','2026-03-17 15:25:33'),(49,8,'IN',1,95,96,'doctor','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-17 16:08:04'),(50,8,'OUT',1,96,95,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 16:08:42'),(51,3,'OUT',1,56,55,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 16:08:42'),(52,1,'OUT',1,116,115,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-17 16:56:48'),(53,5,'OUT',2,33,31,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-17 16:56:48'),(54,10,'OUT',1,59,58,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 17:13:42'),(55,4,'OUT',1,41,40,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 17:13:42'),(56,31,'IN',15,0,15,'鏉庤嵂甯?,'','2026-03-17 18:02:06'),(57,6,'OUT',1,116,115,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 18:04:32'),(58,4,'OUT',1,40,39,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 18:04:32'),(59,6,'IN',1,115,116,'doctor','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-17 18:06:42'),(60,4,'IN',1,39,40,'doctor','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-17 18:06:42'),(61,1,'OUT',1,115,114,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-17 18:07:12'),(62,4,'OUT',1,40,39,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-17 18:07:12'),(63,6,'OUT',1,116,115,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 18:07:29'),(64,4,'OUT',1,39,38,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 18:07:29'),(65,7,'OUT',1,85,84,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 18:30:03'),(66,5,'OUT',1,31,30,'pharmacist','鑽埧纭鍙栬嵂','2026-03-17 18:30:03'),(67,9,'OUT',2,106,104,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-17 18:30:31'),(68,3,'OUT',1,55,54,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-17 18:30:31'),(69,9,'IN',2,104,106,'doctor','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-18 13:51:26'),(70,3,'IN',1,54,55,'doctor','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-18 13:51:26'),(71,31,'OUT',2,15,13,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 13:51:48'),(72,3,'OUT',1,55,54,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 13:51:48'),(73,31,'OUT',1,13,12,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 13:54:13'),(74,31,'IN',1,12,13,'鐜嬩簲','缂栬緫灏辫瘖璁板綍鍥為€€搴撳瓨','2026-03-18 13:55:32'),(75,31,'OUT',1,13,12,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 14:11:15'),(76,31,'IN',9,12,21,'鏉庤嵂甯?,'','2026-03-18 14:24:53'),(77,31,'OUT',13,21,8,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 14:25:00'),(78,31,'OUT',6,8,2,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 14:27:38'),(79,10,'OUT',3,58,55,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 16:36:52'),(80,4,'OUT',1,38,37,'pharmacist','鑽埧纭鍙栬嵂','2026-03-18 16:36:52'),(81,31,'OUT',1,2,1,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-18 16:37:09'),(82,5,'OUT',1,30,29,'榛勮嵂甯?,'鑽埧纭鍙栬嵂','2026-03-18 16:37:09');
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
INSERT INTO `patient` VALUES (1,'P2026001','鍒樺缓鍥?,'鐢?,58,'13900000001','110101196801010011','鏈濋槼鍖虹ぞ鍖鸿矾 1 鍙?,'闈掗湁绱犺繃鏁?,'楂樿鍘?5 骞?,'2026-03-16 18:49:07','2026-03-16 18:49:07',NULL),(2,'P2026002','闄堜附鍗?,'濂?,42,'13900000002','110101198402020022','娴锋穩鍖哄仴搴疯 2 鍙?,'鏃?,'鑳冪値鐥呭彶','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL),(3,'P2026003','鐜嬬鍏?,'濂?,67,'13900000003','110101195903030033','涓板彴鍖哄畨搴疯 3 鍙?,'纾鸿兒绫昏嵂鐗╄繃鏁?,'2 鍨嬬硸灏跨梾 8 骞淬€侀璐ㄧ枏鏉?,'2026-03-16 19:19:13','2026-03-16 19:30:00',NULL),(4,'P2026004','璧靛織寮?,'鐢?,35,'13900000004','110101199103040044','閫氬窞鍖哄悍澶嶈矾 8 鍙?,'鏃?,'杩囨晱鎬ч蓟鐐庡弽澶嶅彂浣?,'2026-03-16 19:19:13','2026-03-16 19:30:00',NULL),(5,'P2026005','瀛欐収鏁?,'濂?,29,'13900000005','110101199701050055','鏄屽钩鍖哄拰骞抽噷 6 鍙?,'娴烽矞杩囨晱','浜у悗 6 涓湀锛岃交搴﹁传琛€','2026-03-16 19:19:13','2026-03-16 19:30:00',NULL),(6,'P20260316192159718730','鍛ㄦ鑺?,'濂?,61,'13900000006','110101196502060066','鐭虫櫙灞卞尯鍜岃皭璺?9 鍙?,'鏃?,'鍐犲績鐥?3 骞?,'2026-03-16 19:21:59','2026-03-16 19:30:00',NULL),(7,'P20260316192200195149','鏉庢捣宄?,'鐢?,47,'13900000007','110101197903070077','瑗垮煄鍖哄畨瀹氶棬 12 鍙?,'闈掗湁绱犺繃鏁?,'鑴傝偑鑲?,'2026-03-16 19:22:00','2026-03-16 19:30:00',NULL),(8,'P20260316192200669655','楂樻晱','濂?,33,'13900000008','110101199304080088','鏈濋槼鍖鸿偛鎵嶈矾 15 鍙?,'鏃?,'鍋忓ご鐥?,'2026-03-16 19:22:00','2026-03-16 19:30:00',NULL),(9,'P20260316192201167315','鏉滄槑','鐢?,72,'13900000009','110101195404090099','娴锋穩鍖哄闄㈣矾 20 鍙?,'鏃?,'楂樿鍘嬨€佸啝蹇冪梾','2026-03-16 19:22:01','2026-03-16 19:30:00',NULL),(10,'P20260316192201650269','浣曟槬姊?,'濂?,54,'13900000010','110101197202100010','澶у叴鍖哄悍搴勮 5 鍙?,'纾鸿兒杩囨晱','2 鍨嬬硸灏跨梾 5 骞?,'2026-03-16 19:22:01','2026-03-16 19:30:00',NULL),(11,'P20260316192202156355','閮缓鍐?,'鐢?,39,'13900000011','110101198711110011','涓板彴鍖哄叴鍗庤矾 18 鍙?,'鏃?,'鎱㈡€у捊鐐?,'2026-03-16 19:22:02','2026-03-16 19:30:00',NULL),(12,'P20260316192202674190','缃椾腹','濂?,28,'13900000012','110101199802120012','閫氬窞鍖烘柊鍩庝笢閲?7 鍙?,'娴烽矞杩囨晱','缂洪搧鎬ц传琛€','2026-03-16 19:22:02','2026-03-16 19:30:00',NULL),(13,'P20260316192203192608','閮戝浗寮?,'鐢?,65,'13900000013','110101196103130013','鏄屽钩鍖洪緳娉藉洯 3 鍙?,'鏃?,'鎱㈤樆鑲?,'2026-03-16 19:22:03','2026-03-16 19:30:00',NULL),(14,'P20260316192203724200','鍞愰洩','濂?,31,'13900000014','110101199503140014','椤轰箟鍖哄垢绂忚タ琛?11 鍙?,'鏃?,'鐢茬姸鑵虹粨鑺?,'2026-03-16 19:22:03','2026-03-16 19:30:00',NULL),(15,'P20260316192204236174','褰織杩?,'鐢?,44,'13900000015','110101198203150015','闂ㄥご娌熷尯姘稿畾闀?6 鍙?,'鏃?,'鑵拌倢鍔虫崯','2026-03-16 19:22:04','2026-03-16 19:30:00',NULL),(16,'P20260316192204804453','闊╀附濞?,'濂?,58,'13900000016','110101196803160016','鎴垮北鍖烘嫳杈拌閬?2 鍙?,'闃垮徃鍖规灄杩囨晱','楂樿剛琛€鐥?,'2026-03-16 19:22:04','2026-03-16 19:30:00',NULL),(17,'P20260316192205338862','鏇规枃鍗?,'鐢?,26,'13900000017','110101200003170017','涓滃煄鍖哄畨涔愯儭鍚?4 鍙?,'鏃?,'鑳冪値','2026-03-16 19:22:05','2026-03-16 19:30:00',NULL),(18,'P20260316192205899415','娌堢帀鐝?,'濂?,70,'13900000018','110101195603180018','鎬€鏌斿尯闈掓槬璺?13 鍙?,'鏃?,'楠ㄥ叧鑺傜値','2026-03-16 19:22:05','2026-03-16 19:30:00',NULL),(19,'P20260316192206458273','璁告櫒','鐢?,36,'13900000019','110101199003190019','瀵嗕簯鍖洪紦妤煎崡澶ц 10 鍙?,'鏃?,'榧荤値','2026-03-16 19:22:06','2026-03-16 19:30:00','/avatars/patient_cfb4b7f9e181.jpg'),(20,'P20260316192207032611','钂嬫鎬?,'濂?,24,'13900000020','110101200202200020','寤跺簡鍖哄Λ姘村寳琛?1 鍙?,'鏃?,'浣撴澶嶆煡','2026-03-16 19:22:07','2026-03-16 19:30:00','/avatars/patient_312347c9a001.png'),(21,'P20260317180315187267','寮犱笁','鐢?,25,'18182133256','412828200109013021','','鏃?,'鏃?,'2026-03-17 18:03:15','2026-03-17 18:03:15',NULL),(22,'P20260317180525737436','鏉庡洓','鐢?,25,'18182133259','412828200109013023','','鏃?,'鏃?,'2026-03-17 18:05:25','2026-03-17 18:05:25','/avatars/patient_8819c0306fd0.jpg'),(23,'P20260317182737272509','瀛愭','濂?,20,'','412828200603175012','','鏃?,'鏃?,'2026-03-17 18:27:37','2026-03-17 18:27:37',NULL),(24,'P20260318140927840862','鏃跺','鐢?,45,'11122233345','012345678912345678','','鏃?,'鏃?,'2026-03-18 14:09:27','2026-03-18 14:09:27',NULL),(25,'P20260318163445352327','aa','鐢?,20,'','123456789123456789','','a','a','2026-03-18 16:34:45','2026-03-18 16:34:45',NULL);
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
INSERT INTO `prescription_record` VALUES (1,1,1,'姘ㄦ隘鍦板钩鐗?,'5mg','姣忔棩涓€娆?,14,2,'2026-03-16 18:49:07',26.50,53.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL,NULL),(2,2,2,'濂ョ編鎷夊攽鑲犳憾鑳跺泭','20mg','姣忔棩涓€娆?,7,1,'2026-03-16 18:49:07',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 18:49:07','2026-03-16 18:49:07',NULL,NULL),(3,3,1,'姘ㄦ隘鍦板钩鐗?,'5mg','姣忔棩涓€娆?,14,1,'2026-03-13 10:06:00',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-13 10:06:00','2026-03-13 10:06:00',NULL,NULL),(4,4,3,'浜岀敳鍙岃儘鐗?,'0.5g','姣忔棩涓€娆?,14,2,'2026-03-13 10:36:00',48.00,96.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-13 10:36:00','2026-03-13 10:36:00','2026-03-13 10:36:00',NULL),(5,5,4,'姘浄浠栧畾鐗?,'10mg','姣忔棩涓€娆?,7,2,'2026-03-14 15:06:00',15.00,30.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-14 15:06:00','2026-03-14 15:06:00','2026-03-14 15:06:00',NULL),(6,6,5,'鐞ョ弨閰镐簹閾佺墖','0.1g','姣忔棩涓夋',10,1,'2026-03-15 09:26:00',12.00,12.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-15 09:26:00','2026-03-15 09:26:00','2026-03-15 09:26:00',NULL),(7,7,6,'闃垮徃鍖规灄鑲犳憾鐗?,'100mg','姣忔棩涓€娆?,14,2,'2026-03-16 19:22:51',13.00,26.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:22:51','2026-03-16 19:22:51',NULL,NULL),(8,8,7,'缂矙鍧﹁兌鍥?,'80mg','姣忔棩涓€娆?,14,2,'2026-03-16 19:23:42',10.00,20.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:42','2026-03-16 19:23:42',NULL,NULL),(9,10,8,'甯冩礇鑺紦閲婅兌鍥?,'0.3g','鐤肩棝鏃舵湇',3,1,'2026-03-16 19:23:43',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:43','2026-03-16 19:23:43',NULL,NULL),(10,11,6,'闃垮徃鍖规灄鑲犳憾鐗?,'100mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:44',13.00,13.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:44','2026-03-16 19:23:44',NULL,NULL),(11,11,7,'缂矙鍧﹁兌鍥?,'80mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:44',10.00,10.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:44','2026-03-16 19:23:44',NULL,NULL),(12,12,3,'浜岀敳鍙岃儘鐗?,'0.5g','姣忔棩涓ゆ',14,2,'2026-03-16 19:23:45',48.00,96.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:45','2026-03-16 19:23:45','2026-03-16 19:23:45',NULL),(13,13,9,'闃胯帿瑗挎灄鑳跺泭','0.25g','姣忔棩涓夋',5,2,'2026-03-16 19:23:45',24.00,48.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:45','2026-03-16 19:23:45',NULL,NULL),(14,14,5,'鐞ョ弨閰镐簹閾佺墖','0.1g','姣忔棩涓夋',10,1,'2026-03-16 19:23:46',12.00,12.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:46','2026-03-16 19:23:46','2026-03-16 19:23:46',NULL),(15,14,10,'缁寸敓绱燘12鐗?,'25ug','姣忔棩涓夋',10,1,'2026-03-16 19:23:46',17.00,17.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:46','2026-03-16 19:23:46',NULL,NULL),(16,15,9,'闃胯帿瑗挎灄鑳跺泭','0.25g','姣忔棩涓夋',7,2,'2026-03-16 19:23:47',24.00,48.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:47','2026-03-16 19:23:47',NULL,NULL),(17,17,8,'甯冩礇鑺紦閲婅兌鍥?,'0.3g','姣忔棩涓ゆ',5,1,'2026-03-16 19:23:48',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:48','2026-03-16 19:23:48',NULL,NULL),(18,19,2,'濂ョ編鎷夊攽鑲犳憾鑳跺泭','20mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:50',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:50','2026-03-16 19:23:50',NULL,NULL),(19,20,8,'甯冩礇鑺紦閲婅兌鍥?,'0.3g','姣忔棩涓ゆ',5,1,'2026-03-16 19:23:50',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:50','2026-03-16 19:23:50',NULL,NULL),(20,21,4,'姘浄浠栧畾鐗?,'10mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:51',15.00,15.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:51','2026-03-16 19:23:51','2026-03-16 19:23:51',NULL),(21,23,1,'姘ㄦ隘鍦板钩鐗?,'5mg','姣忔棩涓€娆?,14,1,'2026-03-16 19:23:53',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:53','2026-03-16 19:23:53',NULL,NULL),(22,24,2,'濂ョ編鎷夊攽鑲犳憾鑳跺泭','20mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:53',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:53','2026-03-16 19:23:53',NULL,NULL),(23,25,3,'浜岀敳鍙岃儘鐗?,'0.5g','姣忔棩涓ゆ',14,2,'2026-03-16 19:23:54',48.00,96.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:54','2026-03-16 19:23:54','2026-03-16 19:23:54',NULL),(24,26,4,'姘浄浠栧畾鐗?,'10mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:55',15.00,15.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:55','2026-03-16 19:23:55','2026-03-16 19:23:55',NULL),(25,27,5,'鐞ョ弨閰镐簹閾佺墖','0.1g','姣忔棩涓夋',10,1,'2026-03-16 19:23:56',12.00,12.00,'ORAL','PAID','DISPENSED','COMPLETED','2026-03-16 19:23:56','2026-03-16 19:23:56','2026-03-16 19:23:56',NULL),(26,28,7,'缂矙鍧﹁兌鍥?,'80mg','姣忔棩涓€娆?,14,2,'2026-03-16 19:23:56',10.00,20.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:56','2026-03-16 19:23:56',NULL,NULL),(27,29,6,'闃垮徃鍖规灄鑲犳憾鐗?,'100mg','姣忔棩涓€娆?,7,1,'2026-03-16 19:23:57',13.00,13.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:57','2026-03-16 19:23:57',NULL,NULL),(28,31,2,'濂ョ編鎷夊攽鑲犳憾鑳跺泭','20mg','姣忔棩涓€娆?,14,1,'2026-03-16 19:23:59',32.00,32.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-16 19:23:59','2026-03-16 19:23:59',NULL,NULL),(30,32,8,'甯冩礇鑺紦閲婅兌鍥?,'1','3',1,1,'2026-03-17 14:29:07',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 14:29:07','2026-03-17 14:29:07',NULL,NULL),(32,34,8,'甯冩礇鑺紦閲婅兌鍥?,'3','3',1,1,'2026-03-17 16:08:04',10.50,10.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 16:08:42','2026-03-17 16:08:43',NULL,NULL),(33,34,3,'浜岀敳鍙岃儘鐗?,'1','2',1,1,'2026-03-17 16:08:04',48.00,48.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 16:08:42','2026-03-17 16:08:43','2026-03-17 16:08:50',NULL),(34,33,2,'濂ョ編鎷夊攽鑲犳憾鑳跺泭','3','3',1,2,'2026-03-17 16:42:06',32.00,64.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 16:42:06','2026-03-17 16:42:06',NULL,NULL),(35,33,4,'姘浄浠栧畾鐗?,'2','3',1,1,'2026-03-17 16:42:06',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 16:42:06','2026-03-17 16:42:06','2026-03-17 16:42:06',NULL),(36,35,1,'姘ㄦ隘鍦板钩鐗?,'3','3',1,1,'2026-03-17 16:56:24',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 16:56:49','2026-03-17 16:56:49',NULL,NULL),(37,35,5,'鐞ョ弨閰镐簹閾佺墖','1','1',1,2,'2026-03-17 16:56:24',12.00,24.00,'INJECTION','PAID','DISPENSED','PENDING','2026-03-17 16:56:49','2026-03-17 16:56:49',NULL,NULL),(38,30,10,'缁寸敓绱燘12鐗?,'3','3',1,1,'2026-03-17 17:13:01',17.00,17.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 17:13:42','2026-03-17 17:13:42',NULL,NULL),(39,30,4,'姘浄浠栧畾鐗?,'1','3',1,1,'2026-03-17 17:13:01',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 17:13:42','2026-03-17 17:13:42','2026-03-17 17:13:56','pharmacist'),(42,37,1,'姘ㄦ隘鍦板钩鐗?,'3','3',1,1,'2026-03-17 18:06:28',26.50,26.50,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 18:07:12','2026-03-17 18:07:13',NULL,NULL),(43,37,4,'姘浄浠栧畾鐗?,'1','1',1,1,'2026-03-17 18:06:28',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 18:07:12','2026-03-17 18:07:13','2026-03-17 18:07:39','榛勮嵂甯?),(44,36,6,'闃垮徃鍖规灄鑲犳憾鐗?,'3','3',1,1,'2026-03-17 18:06:42',13.00,13.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 18:07:29','2026-03-17 18:07:29',NULL,NULL),(45,36,4,'姘浄浠栧畾鐗?,'1','1',1,1,'2026-03-17 18:06:42',15.00,15.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 18:07:29','2026-03-17 18:07:29','2026-03-17 18:07:46','pharmacist'),(46,39,7,'缂矙鍧﹁兌鍥?,'3','3',1,1,'2026-03-17 18:29:01',10.00,10.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-17 18:30:03','2026-03-17 18:30:03',NULL,NULL),(47,39,5,'鐞ョ弨閰镐簹閾佺墖','','1',1,1,'2026-03-17 18:29:01',12.00,12.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-17 18:30:03','2026-03-17 18:30:03','2026-03-17 18:30:58','pharmacist'),(50,38,31,'鎰熷悍','16','3',1,2,'2026-03-18 13:51:26',6.00,12.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 13:51:47','2026-03-18 13:51:48',NULL,NULL),(51,38,3,'浜岀敳鍙岃儘鐗?,'1','1',1,1,'2026-03-18 13:51:26',48.00,48.00,'INJECTION','PAID','DISPENSED','COMPLETED','2026-03-18 13:51:47','2026-03-18 13:51:48','2026-03-18 13:52:07','pharmacist'),(54,40,31,'鎰熷悍','10','1',1,1,'2026-03-18 13:55:51',6.00,6.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 14:11:15','2026-03-18 14:11:16',NULL,NULL),(55,41,31,'鎰熷悍','3鐗?,'3',1,13,'2026-03-18 14:10:56',6.00,78.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 14:11:38','2026-03-18 14:25:01',NULL,NULL),(56,42,31,'鎰熷悍','5鐗?娆?,'3',1,6,'2026-03-18 14:26:06',6.00,36.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 14:27:38','2026-03-18 14:27:39',NULL,NULL),(57,45,10,'缁寸敓绱燘12鐗?,'5','3',1,3,'2026-03-18 16:36:06',17.00,51.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 16:36:51','2026-03-18 16:36:52',NULL,NULL),(58,45,4,'姘浄浠栧畾鐗?,'3','3',1,1,'2026-03-18 16:36:06',15.00,15.00,'INJECTION','PAID','DISPENSED','PENDING','2026-03-18 16:36:51','2026-03-18 16:36:52',NULL,'pharmacist'),(61,43,31,'鎰熷悍','1','1',1,1,'2026-03-18 16:36:36',6.00,6.00,'ORAL','PAID','DISPENSED','NOT_REQUIRED','2026-03-18 16:37:08','2026-03-18 16:37:10',NULL,NULL),(62,43,5,'鐞ョ弨閰镐簹閾佺墖','3','3',1,1,'2026-03-18 16:36:36',12.00,12.00,'INJECTION','PAID','DISPENSED','PENDING','2026-03-18 16:37:08','2026-03-18 16:37:10',NULL,'榛勮嵂甯?);
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
INSERT INTO `sys_role` VALUES (1,'ADMIN','绯荤粺绠＄悊鍛?,'2026-03-16 18:49:07'),(2,'DOCTOR','鍖荤敓','2026-03-16 18:49:07'),(3,'PHARMACIST','鑽笀','2026-03-16 18:49:07'),(4,'RECEPTION','瀵艰瘖/鍓嶅彴','2026-03-16 18:49:07');
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
INSERT INTO `sys_user` VALUES (1,'admin','123456','绯荤粺绠＄悊鍛?,'13800000001',1,'2026-03-16 18:49:07','2026-03-16 21:10:05','110101199001010011','/avatars/user_700e1d01c6b9.jpg',NULL,NULL,NULL),(2,'doctor','123456','寮犲尰鐢?,'13800000002',1,'2026-03-16 18:49:07','2026-03-17 14:25:47','110101198502150022','/avatars/user_ad83b6c9d396.jpg','鍏ㄧ璇婂',NULL,NULL),(3,'pharmacist','123456','鏉庤嵂甯?,'13800000003',1,'2026-03-16 18:49:07','2026-03-17 14:34:56','110101199203200033','/avatars/user_854fd6e21faa.jpg',NULL,'瑗胯嵂鎴?,NULL),(4,'reception','123456','鐜嬪墠鍙?,'13800000004',1,'2026-03-16 18:49:07','2026-03-17 14:34:56','110101199505250044',NULL,NULL,NULL,'涓€鍙峰墠鍙?),(7,'榛勮嵂甯?,'123456','榛勮嵂甯?,NULL,1,'2026-03-17 16:38:19','2026-03-17 16:38:19','110101199804086963',NULL,NULL,'瑗胯嵂鎴?,NULL),(8,'鐜嬩簲','123456','鐜嬩簲',NULL,1,'2026-03-17 18:24:02','2026-03-17 18:24:02','110101200310115388',NULL,'鍏ㄧ璇婂',NULL,NULL);
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
INSERT INTO `visit_record` VALUES (1,1,'V2026001','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶存檿銆佽鍘嬫尝鍔?,'2026-03-10 09:30:00','寤鸿瑙勫緥鐩戞祴琛€鍘?,'2026-03-16 18:49:07','COMPLETED',NULL),(2,2,'V2026002','寮犲尰鐢?,'娑堝寲鍐呯','鑳冮儴涓嶉€?,'2026-03-11 14:00:00','楗娓呮贰','2026-03-16 18:49:07','COMPLETED',NULL),(3,1,'V2026003','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶嶈瘖锛岃鍘嬫櫒璧峰亸楂?,'2026-03-13 10:00:00','缁х画鐩戞祴琛€鍘嬪苟鎺у埗鐩愭憚鍏?,'2026-03-16 19:19:13','COMPLETED',NULL),(4,3,'V2026004','寮犲尰鐢?,'鍐呭垎娉岄棬璇?,'椁愬悗琛€绯栨尝鍔紝鍋舵湁涔忓姏','2026-03-13 10:30:00','寤鸿瑙勫緥澶嶆煡绌鸿吂鍙婇鍚庤绯?,'2026-03-16 19:19:13','COMPLETED',NULL),(5,4,'V2026005','寮犲尰鐢?,'鑰抽蓟鍠夐棬璇?,'榧诲銆佹墦鍠峰殢 1 鍛?,'2026-03-14 15:00:00','鑰冭檻瀛ｈ妭鎬ц繃鏁忥紝娉ㄦ剰鐜娓呮磥','2026-03-16 19:19:13','COMPLETED',NULL),(6,5,'V2026006','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','浜у悗澶存檿銆佷箯鍔?,'2026-03-15 09:20:00','娉ㄦ剰浼戞伅锛岄ギ椋熻ˉ閾?,'2026-03-16 19:19:13','COMPLETED',NULL),(7,6,'V20260316192251907668','寮犲尰鐢?,'蹇冨唴绉?,'鑳搁椃銆佹椿鍔ㄥ悗姘旂煭','2026-03-12 09:00:00','寤鸿瀹屽杽蹇冪數鍥?,'2026-03-16 19:22:51','COMPLETED',NULL),(8,6,'V20260316192342490213','寮犲尰鐢?,'蹇冨唴绉?,'澶嶈瘖锛岃鍘嬫帶鍒朵竴鑸?,'2026-03-16 09:00:00','缁х画闅忚','2026-03-16 19:23:42','COMPLETED',NULL),(9,7,'V20260316192343327150','寮犲尰鐢?,'娑堝寲鍐呯','鍙充笂鑵逛笉閫?,'2026-03-12 10:00:00','娉ㄦ剰浣庤剛楗','2026-03-16 19:23:43','COMPLETED',NULL),(10,8,'V20260316192343981131','寮犲尰鐢?,'绁炵粡鍐呯','鍙嶅澶寸棝 3 澶?,'2026-03-12 11:00:00','閬垮厤鐔','2026-03-16 19:23:43','COMPLETED',NULL),(11,9,'V20260316192344625618','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','鑳稿墠鍖轰笉閫?,'2026-03-12 14:00:00','寤鸿瀹跺睘闄悓澶嶈瘖','2026-03-16 19:23:44','COMPLETED',NULL),(12,10,'V20260316192345271406','寮犲尰鐢?,'鍐呭垎娉岄棬璇?,'绌鸿吂琛€绯栧亸楂?,'2026-03-12 15:00:00','寤鸿鐩戞祴琛€绯?,'2026-03-16 19:23:45','COMPLETED',NULL),(13,11,'V20260316192345919382','寮犲尰鐢?,'鑰抽蓟鍠夐棬璇?,'鍜藉共鍜界棝','2026-03-13 09:00:00','灏戠儫閰?,'2026-03-16 19:23:45','COMPLETED',NULL),(14,12,'V20260316192346572410','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','涔忓姏銆佸ご鏅?,'2026-03-13 10:00:00','娉ㄦ剰浼戞伅','2026-03-16 19:23:46','COMPLETED',NULL),(15,13,'V20260316192347240495','寮犲尰鐢?,'鍛煎惛鍐呯','鍜冲椊銆佸挸鐥板姞閲?,'2026-03-13 11:00:00','閬垮厤鍙楀噳','2026-03-16 19:23:47','COMPLETED',NULL),(16,14,'V20260316192347922872','寮犲尰鐢?,'鍐呭垎娉岄棬璇?,'浣撴鍙戠幇鐢茬姸鑵虹粨鑺?,'2026-03-13 14:00:00','瀹氭湡澶嶆煡瓒呭０','2026-03-16 19:23:47','COMPLETED',NULL),(17,15,'V20260316192348608791','寮犲尰鐢?,'楠ㄧ闂ㄨ瘖','鑵伴儴閰哥棝','2026-03-13 15:00:00','鍑忓皯涔呭潗','2026-03-16 19:23:48','COMPLETED',NULL),(18,16,'V20260316192349316219','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','琛€鑴傚鏌?,'2026-03-14 09:00:00','缁х画楗绠＄悊','2026-03-16 19:23:49','COMPLETED',NULL),(19,17,'V20260316192350030505','寮犲尰鐢?,'娑堝寲鍐呯','楗悗鑳冭儉','2026-03-14 10:00:00','娓呮贰楗','2026-03-16 19:23:50','COMPLETED',NULL),(20,18,'V20260316192350768238','寮犲尰鐢?,'楠ㄧ闂ㄨ瘖','鑶濆叧鑺傜柤鐥?,'2026-03-14 11:00:00','閬垮厤闀挎椂闂寸珯绔?,'2026-03-16 19:23:50','COMPLETED',NULL),(21,19,'V20260316192351514285','寮犲尰鐢?,'鑰抽蓟鍠夐棬璇?,'鏅ㄨ捣榧诲','2026-03-14 14:00:00','娉ㄦ剰閫氶','2026-03-16 19:23:51','COMPLETED',NULL),(22,20,'V20260316192352268457','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','鍋ュ悍鍜ㄨ','2026-03-14 15:00:00','寤鸿瑙勫緥浣滄伅','2026-03-16 19:23:52','COMPLETED',NULL),(23,1,'V20260316192353051498','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶存檿澶嶆煡','2026-03-15 09:00:00','缁х画鐩戞祴','2026-03-16 19:23:53','COMPLETED',NULL),(24,2,'V20260316192353819697','寮犲尰鐢?,'娑堝寲鍐呯','鑳冭儉澶嶈瘖','2026-03-15 10:00:00','鎸夋椂鐢ㄨ嵂','2026-03-16 19:23:53','COMPLETED',NULL),(25,3,'V20260316192354591251','寮犲尰鐢?,'鍐呭垎娉岄棬璇?,'涔忓姏澶嶈瘖','2026-03-15 11:00:00','鐩戞祴琛€绯?,'2026-03-16 19:23:54','COMPLETED',NULL),(26,4,'V20260316192355353652','寮犲尰鐢?,'鑰抽蓟鍠夐棬璇?,'榧荤棐鎵撳柗鍤?,'2026-03-15 14:00:00','鍑忓皯绮夊皹鏆撮湶','2026-03-16 19:23:55','COMPLETED',NULL),(27,5,'V20260316192356168320','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','涔忓姏澶嶈瘖','2026-03-15 15:00:00','缁х画琛ラ搧','2026-03-16 19:23:56','COMPLETED',NULL),(28,6,'V20260316192356979303','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶存檿銆佽鍘嬪亸楂?,'2026-03-16 10:00:00','寤鸿鍔ㄦ€佽鍘嬬洃娴?,'2026-03-16 19:23:56','COMPLETED',NULL),(29,9,'V20260316192357824252','寮犲尰鐢?,'蹇冨唴绉?,'娲诲姩鍚庤兏闂?,'2026-03-16 11:00:00','蹇呰鏃惰浆涓婄骇鍖婚櫌','2026-03-16 19:23:57','COMPLETED',NULL),(30,11,'V20260316192358659889','寮犲尰鐢?,'鑰抽蓟鍠夐棬璇?,'鍜介儴寮傜墿鎰?,'2026-03-16 14:00:00','閬垮厤杈涜荆鍒烘縺','2026-03-16 19:23:58','COMPLETED',NULL),(31,17,'V20260316192359479142','寮犲尰鐢?,'娑堝寲鍐呯','鍙嶉吀','2026-03-16 15:00:00','椁愬悗鍕跨珛鍗冲钩鍗?,'2026-03-16 19:23:59','COMPLETED',NULL),(32,20,'V20260317140039222735','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','鍙戠儳','2026-03-17 14:00:39','澶氬枬鐑按','2026-03-17 14:00:39','COMPLETED',1),(33,19,'V20260317140401899571','xxx','鍏ㄧ闂ㄨ瘖','鑵扮柤','2026-03-17 14:04:02','娉ㄦ剰韬綋','2026-03-17 14:04:01','COMPLETED',1),(34,20,'V20260317142821426746','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶寸柤','2026-03-17 14:28:21','娉ㄥ皠閫€鐑ц嵂','2026-03-17 14:28:21','COMPLETED',1),(35,19,'V20260317165445308127','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','鑵跨柤','2026-03-17 16:54:45','绌垮帤鐐?,'2026-03-17 16:54:45','COMPLETED',1),(36,21,'V20260317180322042591','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶存檿','2026-03-17 18:03:22','娉ㄦ剰楗','2026-03-17 18:03:22','COMPLETED',1),(37,22,'V20260317180628121247','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','浣撳瘨','2026-03-28 00:00:00','娉ㄦ剰楗','2026-03-17 18:06:28','COMPLETED',NULL),(38,23,'V20260317182744622637','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','澶嶆煡','2026-03-17 18:27:45','楠ㄦ姌','2026-03-17 18:27:44','COMPLETED',1),(39,22,'V20260317182751861742','鐜嬩簲','鍏ㄧ闂ㄨ瘖','鑳冪柤','2026-03-17 18:27:52','娉ㄦ剰楗','2026-03-17 18:27:51','COMPLETED',1),(40,23,'V20260318135307107690','鐜嬩簲','鍏ㄧ闂ㄨ瘖','鍜冲椊','2026-03-18 13:53:07','','2026-03-18 13:53:07','COMPLETED',1),(41,24,'V20260318140948788514','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','鏃?,'2026-03-18 14:09:49','鍚冨ソ鍠濆ソ','2026-03-18 14:09:48','COMPLETED',1),(42,24,'V20260318142325234371','鐜嬩簲','鍏ㄧ闂ㄨ瘖','澶寸柤','2026-03-18 14:23:25','闄嶅帇','2026-03-18 14:23:25','COMPLETED',1),(43,19,'V20260318142625276717','寮犲尰鐢?,'鍏ㄧ闂ㄨ瘖','c','2026-03-18 14:26:25','c','2026-03-18 14:26:25','COMPLETED',1),(44,25,'V20260318163452783335','鐜嬩簲','鍏ㄧ闂ㄨ瘖','寰呮帴璇?,'2026-03-18 16:34:53','','2026-03-18 16:34:52','PENDING',1),(45,25,'V20260318163454495902','鐜嬩簲','鍏ㄧ闂ㄨ瘖','b','2026-03-18 16:34:54','b','2026-03-18 16:34:54','COMPLETED',2);
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

-- Dump completed on 2026-03-28 13:40:28
