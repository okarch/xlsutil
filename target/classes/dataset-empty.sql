-- MySQL dump 10.13  Distrib 5.6.28, for Linux (x86_64)
--
-- Host: localhost    Database: dataset
-- ------------------------------------------------------
-- Server version	5.6.28-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_dataset`
--

DROP TABLE IF EXISTS `t_dataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset` (
  `datasetid` varchar(36) NOT NULL,
  `study` varchar(50) DEFAULT NULL,
  `setname` varchar(128) DEFAULT NULL,
  `datatype` varchar(128) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(20) DEFAULT NULL,
  `stamp` bigint(20) DEFAULT NULL,
  `trackid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`datasetid`),
  KEY `i_ds_sna` (`study`),
  KEY `i_ds_sty` (`setname`),
  KEY `i_ds_tid` (`trackid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset`
--

LOCK TABLES `t_dataset` WRITE;
/*!40000 ALTER TABLE `t_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dataset_organization`
--

DROP TABLE IF EXISTS `t_dataset_organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset_organization` (
  `orgid` bigint(20) NOT NULL,
  `orgname` varchar(50) DEFAULT NULL,
  `siteid` varchar(20) DEFAULT NULL,
  `countryid` smallint(5) unsigned DEFAULT NULL,
  `orgtype` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`orgid`),
  KEY `i_dsor_nam` (`orgname`),
  KEY `i_dsor_sit` (`siteid`),
  KEY `i_dsor_cou` (`countryid`),
  KEY `i_dsor_typ` (`orgtype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset_organization`
--

LOCK TABLES `t_dataset_organization` WRITE;
/*!40000 ALTER TABLE `t_dataset_organization` DISABLE KEYS */;
INSERT INTO `t_dataset_organization` VALUES (-2,'Merck Biopharma',NULL,0,'sponsor'),(0,'Unknown',NULL,0,'unknown');
/*!40000 ALTER TABLE `t_dataset_organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dataset_processing`
--

DROP TABLE IF EXISTS `t_dataset_processing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset_processing` (
  `datasetid` varchar(36) DEFAULT NULL,
  `stepid` bigint(20) DEFAULT NULL,
  `timeid` bigint(20) DEFAULT NULL,
  `step` int(11) DEFAULT NULL,
  `processed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(128) DEFAULT NULL,
  `trackid` bigint(20) DEFAULT NULL,
  KEY `i_dspc_sid` (`datasetid`),
  KEY `i_dspc_trd` (`stepid`),
  KEY `i_dspc_tmd` (`timeid`),
  KEY `i_dspc_pro` (`processed`),
  KEY `i_dspc_tid` (`trackid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset_processing`
--

LOCK TABLES `t_dataset_processing` WRITE;
/*!40000 ALTER TABLE `t_dataset_processing` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_dataset_processing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dataset_step`
--

DROP TABLE IF EXISTS `t_dataset_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset_step` (
  `stepid` bigint(20) NOT NULL,
  `step` varchar(80) DEFAULT NULL,
  `stepdesc` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`stepid`),
  KEY `i_dss_step` (`step`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset_step`
--

LOCK TABLES `t_dataset_step` WRITE;
/*!40000 ALTER TABLE `t_dataset_step` DISABLE KEYS */;
INSERT INTO `t_dataset_step` VALUES (0,'unknown',NULL),(1,'setup',NULL),(2,'send',NULL),(3,'receive',NULL),(4,'upload',NULL),(5,'check',NULL),(6,'release',NULL),(7,'delay',NULL),(8,'done',NULL);
/*!40000 ALTER TABLE `t_dataset_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dataset_time`
--

DROP TABLE IF EXISTS `t_dataset_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset_time` (
  `timeid` bigint(20) NOT NULL,
  `orgid` bigint(20) DEFAULT NULL,
  `timepoint` varchar(50) DEFAULT NULL,
  `timetype` varchar(20) DEFAULT NULL,
  `expdt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`timeid`),
  KEY `i_dsti_oid` (`orgid`),
  KEY `i_dsti_vis` (`timepoint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset_time`
--

LOCK TABLES `t_dataset_time` WRITE;
/*!40000 ALTER TABLE `t_dataset_time` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_dataset_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dataset_tracker`
--

DROP TABLE IF EXISTS `t_dataset_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset_tracker` (
  `trackid` bigint(20) NOT NULL,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `previd` bigint(20) DEFAULT NULL,
  `item` varchar(30) DEFAULT NULL,
  `activity` varchar(50) DEFAULT NULL,
  `uid` bigint(20) DEFAULT NULL,
  `remark` varchar(254) DEFAULT NULL,
  `content` text,
  PRIMARY KEY (`trackid`),
  KEY `i_dstra_pid` (`previd`),
  KEY `i_dstra_ite` (`item`),
  KEY `i_dstra_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset_tracker`
--

LOCK TABLES `t_dataset_tracker` WRITE;
/*!40000 ALTER TABLE `t_dataset_tracker` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_dataset_tracker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dataset_user`
--

DROP TABLE IF EXISTS `t_dataset_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dataset_user` (
  `userid` bigint(20) NOT NULL,
  `muid` varchar(20) DEFAULT NULL,
  `username` varchar(80) DEFAULT NULL,
  `apikey` varchar(20) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `active` varchar(5) DEFAULT NULL,
  `roles` bigint(20) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`),
  KEY `i_dsus_muid` (`muid`),
  KEY `i_dsus_una` (`username`),
  KEY `i_dsus_api` (`apikey`),
  KEY `i_dsus_ema` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dataset_user`
--

LOCK TABLES `t_dataset_user` WRITE;
/*!40000 ALTER TABLE `t_dataset_user` DISABLE KEYS */;
INSERT INTO `t_dataset_user` VALUES (0,'m01061','Oliver Karch','220466','Oliver.K.Karch@merckgroup.com','true',1,'2015-02-20 00:01:01'),(1,'guest','Guest','','bmdm@merckgroup.com','true',2,'2015-02-20 00:01:01');
/*!40000 ALTER TABLE `t_dataset_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_upload_batch`
--

DROP TABLE IF EXISTS `t_upload_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_upload_batch` (
  `uploadid` bigint(20) NOT NULL,
  `templateid` bigint(20) DEFAULT NULL,
  `uploaded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `userid` bigint(20) DEFAULT NULL,
  `md5sum` varchar(32) DEFAULT NULL,
  `upload` text,
  PRIMARY KEY (`uploadid`),
  KEY `i_uplb_tid` (`templateid`),
  KEY `i_uplb_uid` (`userid`),
  KEY `i_uplb_md5` (`md5sum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_upload_batch`
--

LOCK TABLES `t_upload_batch` WRITE;
/*!40000 ALTER TABLE `t_upload_batch` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_upload_batch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_upload_log`
--

DROP TABLE IF EXISTS `t_upload_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_upload_log` (
  `logid` bigint(20) NOT NULL,
  `uploadid` bigint(20) DEFAULT NULL,
  `logstamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `level` varchar(10) DEFAULT NULL,
  `line` bigint(20) DEFAULT NULL,
  `message` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`logid`),
  KEY `i_log_uid` (`uploadid`),
  KEY `i_log_lst` (`logstamp`),
  KEY `i_log_lev` (`level`),
  KEY `i_log_lin` (`line`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_upload_log`
--

LOCK TABLES `t_upload_log` WRITE;
/*!40000 ALTER TABLE `t_upload_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_upload_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_upload_raw`
--

DROP TABLE IF EXISTS `t_upload_raw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_upload_raw` (
  `md5sum` varchar(32) NOT NULL,
  `upload` text,
  PRIMARY KEY (`md5sum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_upload_raw`
--

LOCK TABLES `t_upload_raw` WRITE;
/*!40000 ALTER TABLE `t_upload_raw` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_upload_raw` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_upload_template`
--

DROP TABLE IF EXISTS `t_upload_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_upload_template` (
  `templateid` bigint(20) NOT NULL,
  `templatename` varchar(80) DEFAULT NULL,
  `template` text,
  `trackid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`templateid`),
  KEY `i_uplt_tna` (`templatename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_upload_template`
--

LOCK TABLES `t_upload_template` WRITE;
/*!40000 ALTER TABLE `t_upload_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_upload_template` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-21 14:48:14
