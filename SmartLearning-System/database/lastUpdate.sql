-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: studysmartdb
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chapter`
--

DROP TABLE IF EXISTS `chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter` (
  `id` int NOT NULL AUTO_INCREMENT,
  `subject_id` int NOT NULL,
  `title` varchar(200) NOT NULL,
  `summary_text` text,
  `order_index` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chapter_subject` (`subject_id`,`order_index`),
  CONSTRAINT `chapter_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter`
--

LOCK TABLES `chapter` WRITE;
/*!40000 ALTER TABLE `chapter` DISABLE KEYS */;
INSERT INTO `chapter` VALUES (5,23,'Mệnh đề và tập hợp','Đây là nội dung tiên quyết của Toán 10',1,NULL,NULL);
/*!40000 ALTER TABLE `chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter_attachment`
--

DROP TABLE IF EXISTS `chapter_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_attachment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `chapter_id` int NOT NULL,
  `type` enum('SUMMARY','CONTENT') NOT NULL,
  `filename` varchar(255) NOT NULL,
  `filepath` varchar(500) NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `extension` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `chapter_attachment` (`chapter_id`),
  CONSTRAINT `chapter_attachment` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chapter_attachment_chk_1` CHECK ((`type` in (_utf8mb4'SUMMARY',_utf8mb4'CONTENT')))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_attachment`
--

LOCK TABLES `chapter_attachment` WRITE;
/*!40000 ALTER TABLE `chapter_attachment` DISABLE KEYS */;
INSERT INTO `chapter_attachment` VALUES (15,5,'CONTENT','1.TapHop.pdf','https://res.cloudinary.com/dao8z029z/raw/upload/v1757508425/chapters/5/chapters/5/1_TapHop-1757508410129','2025-09-10 19:47:06','pdf');
/*!40000 ALTER TABLE `chapter_attachment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter_progress`
--

DROP TABLE IF EXISTS `chapter_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_progress` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `chapter_id` int NOT NULL,
  `percent` int DEFAULT NULL,
  `last_score` decimal(5,2) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_progress` (`student_id`,`chapter_id`),
  KEY `progress_chapter_id` (`chapter_id`),
  CONSTRAINT `chapter_progress_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `progress_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_progress`
--

LOCK TABLES `chapter_progress` WRITE;
/*!40000 ALTER TABLE `chapter_progress` DISABLE KEYS */;
INSERT INTO `chapter_progress` VALUES (7,75,5,0,0.00,'2025-09-11 15:41:56'),(8,72,5,0,0.00,'2025-09-10 19:44:38');
/*!40000 ALTER TABLE `chapter_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class` (
  `id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
INSERT INTO `class` VALUES (6,'10A1','2025-09-10 18:14:46','2025-09-10 20:49:00'),(7,'10A2','2025-09-10 19:35:01','2025-09-10 19:35:01');
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `essay_response`
--

DROP TABLE IF EXISTS `essay_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `essay_response` (
  `submission_id` int NOT NULL,
  `question_id` int NOT NULL,
  `answer_essay` longtext NOT NULL,
  PRIMARY KEY (`submission_id`,`question_id`),
  KEY `idx_essay_response_q` (`question_id`),
  CONSTRAINT `essay_responses_question` FOREIGN KEY (`question_id`) REFERENCES `exercise_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `essay_responses_submission` FOREIGN KEY (`submission_id`) REFERENCES `exercise_submission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `essay_response`
--

LOCK TABLES `essay_response` WRITE;
/*!40000 ALTER TABLE `essay_response` DISABLE KEYS */;
INSERT INTO `essay_response` VALUES (81,11,'Mệnh đề là mệnh đề chứ gì');
/*!40000 ALTER TABLE `essay_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise`
--

DROP TABLE IF EXISTS `exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise` (
  `id` int NOT NULL AUTO_INCREMENT,
  `chapter_id` int NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `description` text,
  `type` enum('ESSAY','MCQ') NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `excercise_chapter` (`chapter_id`),
  KEY `fk_exercise_created_by` (`created_by`),
  CONSTRAINT `excercise_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_exercise_created_by` FOREIGN KEY (`created_by`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise`
--

LOCK TABLES `exercise` WRITE;
/*!40000 ALTER TABLE `exercise` DISABLE KEYS */;
INSERT INTO `exercise` VALUES (4,5,'Trắc nghiệm mệnh đề và tập hợp bla bla','Bài tập trắc nghiệm cho mệnh đề và tập hợp','MCQ','2025-09-10 20:54:21',69),(7,5,'Bài tập về mệnh đề và tập hợp','','ESSAY','2025-09-10 20:54:21',69);
/*!40000 ALTER TABLE `exercise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_answer`
--

DROP TABLE IF EXISTS `exercise_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_answer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL,
  `answer_text` varchar(255) NOT NULL,
  `is_correct` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `exercise_answers_question` (`question_id`),
  CONSTRAINT `exercise_answers_question` FOREIGN KEY (`question_id`) REFERENCES `exercise_question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_answer`
--

LOCK TABLES `exercise_answer` WRITE;
/*!40000 ALTER TABLE `exercise_answer` DISABLE KEYS */;
INSERT INTO `exercise_answer` VALUES (14,7,'1 có phải là số nguyên tố không ?',0),(15,7,'Các bạn rất chăm học',1),(16,7,'2 + 2 bằng mấy',0),(17,8,'18 chia hết cho 9',0),(18,8,'3n chia hết cho 9 , n là số tự nhiên',1),(19,8,'2109 là số nguyên tố',0),(20,8,'Nếu một số chia hết cho 18 thì số đó chia hết cho 9',0);
/*!40000 ALTER TABLE `exercise_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_question`
--

DROP TABLE IF EXISTS `exercise_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_question` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exercise_id` int NOT NULL,
  `order_index` int NOT NULL,
  `question` text NOT NULL,
  `solution` longtext,
  PRIMARY KEY (`id`),
  KEY `excercise_question_id` (`exercise_id`),
  CONSTRAINT `excercise_question_id` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_question`
--

LOCK TABLES `exercise_question` WRITE;
/*!40000 ALTER TABLE `exercise_question` DISABLE KEYS */;
INSERT INTO `exercise_question` VALUES (7,4,1,'Trong các câu sau, câu nào là mệnh đề ?','Câu B á'),(8,4,2,'Câu nào là mệnh đề chứa biến','Câu B đó'),(11,7,1,'Mệnh đề là gì ??','');
/*!40000 ALTER TABLE `exercise_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_submission`
--

DROP TABLE IF EXISTS `exercise_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_submission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exercise_id` int NOT NULL,
  `student_id` int NOT NULL,
  `status` enum('DRAFT','COMPLETED','GRADED') NOT NULL,
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `grade` decimal(5,2) DEFAULT NULL,
  `feedback` text,
  PRIMARY KEY (`id`),
  KEY `idx_submission_exercise` (`exercise_id`),
  KEY `exercise_submissions_student_idx` (`student_id`),
  KEY `idx_es_exercise` (`exercise_id`),
  KEY `idx_es_student` (`student_id`),
  KEY `idx_es_status` (`status`),
  CONSTRAINT `exercise_submissions` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exercise_submissions_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_submission`
--

LOCK TABLES `exercise_submission` WRITE;
/*!40000 ALTER TABLE `exercise_submission` DISABLE KEYS */;
INSERT INTO `exercise_submission` VALUES (81,7,75,'GRADED','2025-09-11 15:41:55',5.00,'Ngu qua');
/*!40000 ALTER TABLE `exercise_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mcq_response`
--

DROP TABLE IF EXISTS `mcq_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mcq_response` (
  `submission_id` int NOT NULL,
  `question_id` int NOT NULL,
  `answer_id` int NOT NULL,
  PRIMARY KEY (`submission_id`,`question_id`),
  KEY `idx_mcq_response_q` (`question_id`),
  KEY `mcq_responses_exAnswer` (`answer_id`),
  CONSTRAINT `mcq_responses_exAnswer` FOREIGN KEY (`answer_id`) REFERENCES `exercise_answer` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mcq_responses_exQuestion` FOREIGN KEY (`question_id`) REFERENCES `exercise_question` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mcq_responses_exSubmission` FOREIGN KEY (`submission_id`) REFERENCES `exercise_submission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mcq_response`
--

LOCK TABLES `mcq_response` WRITE;
/*!40000 ALTER TABLE `mcq_response` DISABLE KEYS */;
/*!40000 ALTER TABLE `mcq_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `teacher_id` int NOT NULL,
  `type` enum('SUBMISSION','EXERCISE','SUBMIT') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci,
  `is_readed` tinyint(1) NOT NULL DEFAULT '0',
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notification_student` (`student_id`),
  KEY `idx_notification_teacher` (`teacher_id`),
  KEY `idx_notification_is_readed` (`is_readed`),
  KEY `idx_notification_sent_at` (`sent_at`),
  CONSTRAINT `fk_notification_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (35,75,69,'SUBMISSION','Bài đã được chấm: Mệnh đề và tập hợp','Mã bài nộp #77 | Điểm: 10 | Nhận xét: Giỏi lắm em',1,'2025-09-10 20:11:33'),(36,75,69,'EXERCISE','Học sinh đã nộp bài tập Bài tập về mệnh đề và tập hợp','Mã bài nộp #80\nVui lòng vào hệ thống để xem chi tiết và chấm điểm.',1,'2025-09-10 20:39:44'),(37,75,69,'EXERCISE','Học sinh đã nộp bài tập Bài tập về mệnh đề và tập hợp','Mã bài nộp #81\nVui lòng vào hệ thống để xem chi tiết và chấm điểm.',0,'2025-09-11 15:41:56'),(38,75,69,'SUBMISSION','Bài đã được chấm: Mệnh đề và tập hợp','Mã bài nộp #81 | Điểm: 5 | Nhận xét: Ngu qua',0,'2025-09-11 16:08:50');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `user_id` int NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (70),(71),(72),(75);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_class`
--

DROP TABLE IF EXISTS `student_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_class` (
  `student_id` int NOT NULL,
  `class_id` int NOT NULL,
  PRIMARY KEY (`student_id`,`class_id`),
  KEY `student_class_ibfk_1_idx` (`student_id`,`class_id`),
  KEY `fk_sc_class` (`class_id`),
  CONSTRAINT `fk_sc_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sc_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_class`
--

LOCK TABLES `student_class` WRITE;
/*!40000 ALTER TABLE `student_class` DISABLE KEYS */;
INSERT INTO `student_class` VALUES (70,6),(71,6),(72,6),(75,6),(70,7),(71,7),(72,7),(75,7);
/*!40000 ALTER TABLE `student_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_schedule`
--

DROP TABLE IF EXISTS `student_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_schedule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `subject_id` int NOT NULL,
  `study_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_schedule_subject` (`subject_id`),
  KEY `idx_student_date` (`student_id`,`study_date`),
  CONSTRAINT `fk_schedule_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_schedule_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_schedule`
--

LOCK TABLES `student_schedule` WRITE;
/*!40000 ALTER TABLE `student_schedule` DISABLE KEYS */;
INSERT INTO `student_schedule` VALUES (12,75,23,'2025-09-12','14:30:00','16:30:00','Ôn chương 1','2025-09-10 14:03:32','2025-09-11 09:10:18');
/*!40000 ALTER TABLE `student_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_subject`
--

DROP TABLE IF EXISTS `student_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_subject` (
  `student_id` int NOT NULL,
  `subject_id` int NOT NULL,
  PRIMARY KEY (`student_id`,`subject_id`),
  KEY `idx_student_subject_subject` (`subject_id`),
  CONSTRAINT `fk_student_subject_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_student_subject_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_subject`
--

LOCK TABLES `student_subject` WRITE;
/*!40000 ALTER TABLE `student_subject` DISABLE KEYS */;
INSERT INTO `student_subject` VALUES (70,23),(71,23),(72,23),(75,23),(70,24),(71,24),(72,24),(75,24),(70,25),(71,25),(72,25),(75,25),(70,26),(71,26),(72,26),(75,26);
/*!40000 ALTER TABLE `student_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `description` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (23,'Toán 10','https://res.cloudinary.com/dao8z029z/image/upload/v1757502838/tir54kiehmfcsbsl7oqp.png','Môn học gồm các chương hàm số, tập hợp,xác suất...','2025-09-10 18:13:59','2025-09-10 20:17:32'),(24,'Lịch sử 10','https://res.cloudinary.com/dao8z029z/image/upload/v1757507345/piarux82vdzwphbnmwcj.jpg','Học về lịch sử Việt Nam','2025-09-10 19:29:06','2025-09-10 19:29:06'),(25,'Sinh học 10 ','https://res.cloudinary.com/dao8z029z/image/upload/v1757507546/fvzl4apblbqohspyckhx.jpg','Học về động vật, thiên nhiên hoang dã','2025-09-10 19:31:47','2025-09-10 19:32:27'),(26,'Toán 10','https://res.cloudinary.com/dao8z029z/image/upload/v1757507652/v4feckzwglwevoacq9bs.png','Cũng là toán mà giáo viên khác dạy','2025-09-10 19:34:13','2025-09-10 19:41:10');
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `user_id` int NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
INSERT INTO `teacher` VALUES (69),(73),(74);
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_assignment`
--

DROP TABLE IF EXISTS `teacher_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_assignment` (
  `teacher_id` int NOT NULL,
  `subject_id` int NOT NULL,
  `class_id` int NOT NULL,
  PRIMARY KEY (`teacher_id`,`subject_id`,`class_id`),
  KEY `fk_ta_class` (`class_id`),
  KEY `fk_ta_subject` (`subject_id`),
  CONSTRAINT `fk_ta_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ta_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ta_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_assignment`
--

LOCK TABLES `teacher_assignment` WRITE;
/*!40000 ALTER TABLE `teacher_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_class`
--

DROP TABLE IF EXISTS `teacher_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_class` (
  `teacher_id` int NOT NULL,
  `class_id` int NOT NULL,
  PRIMARY KEY (`teacher_id`,`class_id`),
  KEY `teacher_class_ibfk_2` (`class_id`),
  CONSTRAINT `fk_class_teacher` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `teacher_class_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_class`
--

LOCK TABLES `teacher_class` WRITE;
/*!40000 ALTER TABLE `teacher_class` DISABLE KEYS */;
INSERT INTO `teacher_class` VALUES (69,6),(73,6),(73,7),(74,7);
/*!40000 ALTER TABLE `teacher_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_subject`
--

DROP TABLE IF EXISTS `teacher_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_subject` (
  `teacher_id` int NOT NULL,
  `subject_id` int NOT NULL,
  PRIMARY KEY (`teacher_id`,`subject_id`),
  KEY `teacher_subject_ibfk_2` (`subject_id`),
  CONSTRAINT `teacher_subject_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `teacher_subject_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_subject`
--

LOCK TABLES `teacher_subject` WRITE;
/*!40000 ALTER TABLE `teacher_subject` DISABLE KEYS */;
INSERT INTO `teacher_subject` VALUES (69,23),(69,24),(73,24),(69,25),(73,25),(69,26);
/*!40000 ALTER TABLE `teacher_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `role` enum('STUDENT','TEACHER','ADMIN') NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `one_time_password` varchar(64) DEFAULT NULL,
  `otp_requested_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (68,'admin@gmail.com','$2a$10$5X9gmAQebD7kSBapMkrt7e5k3Tggf2aSJeJM/LyC1DmVtRhaFaFZ.','Administrator','ADMIN',NULL,'2025-09-09 19:33:01','2025-09-09 19:33:01',NULL,NULL),(69,'2251012075khang@ou.edu.vn','$2a$10$23NCJwLehzve9tVaa81qjuM1ZRj/wQ9ReTqIkShxWOgiDqN9xndD6','Khang Teacher','TEACHER','https://res.cloudinary.com/dao8z029z/image/upload/v1757490551/s5ye1bpund76nlawy7nc.jpg','2025-09-10 14:49:13','2025-09-10 14:49:13',NULL,NULL),(70,'poticutee@gmail.com','$2a$10$W/I7txCL8SmkGqSuokNcjulI3e2VUcZKxCd9r.h2ZZJvKmLAGffbC','Khang Student','STUDENT','https://res.cloudinary.com/dao8z029z/image/upload/v1757490593/kdmbbsoh2z4gnrbxaje7.jpg','2025-09-10 14:49:54','2025-09-10 14:49:54',NULL,NULL),(71,'student1@gmail.com','$2a$10$jC6LYyxvugk2DMmzTbtea.dfw6aCl7EBGYLOMyyI.W.T57Ide.OmS','Student1','STUDENT','https://res.cloudinary.com/dao8z029z/image/upload/v1757490669/fo0nsselzhukqg7kiz3s.jpg','2025-09-10 14:51:10','2025-09-10 14:51:10',NULL,NULL),(72,'student2@gmail.com','$2a$10$k6/efOujOYViARm7OfB7Q.T2GDPwhrIJRwvkPzGgEXa.mJj2o5u.2','Student2','STUDENT','https://res.cloudinary.com/dao8z029z/image/upload/v1757490709/u8immvjrghdllg4u5w2m.jpg','2025-09-10 14:51:51','2025-09-10 14:51:51',NULL,NULL),(73,'teacher2@gmail.com','$2a$10$BLqHp2qlau8UVoO674v6GuvV.nY2zcQc.JdkIeQPpi50mmiCNfeS6','Teacher 2','TEACHER','https://res.cloudinary.com/dao8z029z/image/upload/v1757490799/knozszeqh1cfifiwi0fd.png','2025-09-10 14:53:21','2025-09-10 14:53:21',NULL,NULL),(74,'teacher1@gmail.com','$2a$10$Fd4/bwKEjxIPxGvBCV3YKu36Xq63JaEy7de8m7dpGrV1fAem/u/ZK','Teacher 1','TEACHER','https://res.cloudinary.com/dao8z029z/image/upload/v1757490855/pub0jnz2hccy5e39rzaa.png','2025-09-10 14:54:17','2025-09-10 14:54:17',NULL,NULL),(75,'duykhanggt5@gmail.com','$2a$10$Copew1pCL/llFOZhAa19fua6MbyqBn/RdA1AVpren62LKCYrbyzX.','Duy Khang','STUDENT','https://lh3.googleusercontent.com/a/ACg8ocJYMFXJyWbsLAIpfZ6SmY2oUt40VN0p22OGsxFCCCtV_qfQSt0=s96-c','2025-09-10 14:58:35','2025-09-11 16:10:08',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-11 19:17:29
