-- MySQL dump 10.11
--
-- Host: localhost    Database: hellp
-- ------------------------------------------------------
-- Server version       5.0.51b-log

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
-- Table structure for table `data`
--

DROP TABLE IF EXISTS `data`;

SET character_set_client = utf8;
CREATE TABLE `data` (
  `keyId` int(11) default NULL,
  `paramId` varchar(10) default NULL,
  `value` varchar(10) default NULL,
  `valueType` varchar(21) default NULL,
  `hrsOffset` int(11) default NULL,
  KEY `keyNDX` (`keyId`),
  KEY `paramId_idx` (`paramId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

