-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: przychodnia
-- ------------------------------------------------------
-- Server version	5.7.35-log

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fname` varchar(20) NOT NULL,
  `lname` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `pesel` varchar(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clinic`
--

DROP TABLE IF EXISTS `clinic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clinic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpl1vtivuucg7171bc26q3m146` (`adder_id`),
  CONSTRAINT `FKpl1vtivuucg7171bc26q3m146` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clinic_rooms`
--

DROP TABLE IF EXISTS `clinic_rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clinic_rooms` (
  `clinics_id` int(11) NOT NULL,
  `rooms_number` int(11) NOT NULL,
  KEY `FK63h0a58fqnr82g7mqfb86xkq6` (`rooms_number`),
  KEY `FKfvo93nqjo7e4l5bfgj2qq783h` (`clinics_id`),
  CONSTRAINT `FK63h0a58fqnr82g7mqfb86xkq6` FOREIGN KEY (`rooms_number`) REFERENCES `room` (`number`),
  CONSTRAINT `FKfvo93nqjo7e4l5bfgj2qq783h` FOREIGN KEY (`clinics_id`) REFERENCES `clinic` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `degree`
--

DROP TABLE IF EXISTS `degree`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `degree` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full` varchar(50) NOT NULL,
  `short` varchar(10) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqkwch4ra40po0974p0r2pqprk` (`adder_id`),
  CONSTRAINT `FKqkwch4ra40po0974p0r2pqprk` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fname` varchar(20) NOT NULL,
  `lname` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `pesel` varchar(11) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  `degree_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKanob0d6bvfu3klnfk02jhyiji` (`adder_id`),
  KEY `FKmrvx7108yutugt125756da5gn` (`degree_id`),
  CONSTRAINT `FKanob0d6bvfu3klnfk02jhyiji` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`),
  CONSTRAINT `FKmrvx7108yutugt125756da5gn` FOREIGN KEY (`degree_id`) REFERENCES `degree` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctor_clinics`
--

DROP TABLE IF EXISTS `doctor_clinics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_clinics` (
  `doctors_id` int(11) NOT NULL,
  `clinics_id` int(11) NOT NULL,
  KEY `FKh2bp1xqlfexyt8eiv6yn20f92` (`clinics_id`),
  KEY `FKd6yrhy3l67vcb3gths6yyfcmh` (`doctors_id`),
  CONSTRAINT `FKd6yrhy3l67vcb3gths6yyfcmh` FOREIGN KEY (`doctors_id`) REFERENCES `doctor` (`id`),
  CONSTRAINT `FKh2bp1xqlfexyt8eiv6yn20f92` FOREIGN KEY (`clinics_id`) REFERENCES `clinic` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctor_specializations`
--

DROP TABLE IF EXISTS `doctor_specializations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_specializations` (
  `doctors_id` int(11) NOT NULL,
  `specializations_id` int(11) NOT NULL,
  KEY `FKtq093xfm4t9m4wtnunuqd6e6r` (`specializations_id`),
  KEY `FKlnbnhgyqf9cy7giihfa7xvfpr` (`doctors_id`),
  CONSTRAINT `FKlnbnhgyqf9cy7giihfa7xvfpr` FOREIGN KEY (`doctors_id`) REFERENCES `doctor` (`id`),
  CONSTRAINT `FKtq093xfm4t9m4wtnunuqd6e6r` FOREIGN KEY (`specializations_id`) REFERENCES `specialization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3a6no70gt2a0g3ivus52h82mt` (`adder_id`),
  CONSTRAINT `FK3a6no70gt2a0g3ivus52h82mt` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fname` varchar(20) NOT NULL,
  `lname` varchar(30) NOT NULL,
  `pesel` varchar(11) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK37mvv9bajatjl8kl40tahit8f` (`adder_id`),
  CONSTRAINT `FK37mvv9bajatjl8kl40tahit8f` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_from` date NOT NULL,
  `hour_from` time NOT NULL,
  `until` time NOT NULL,
  `doctor_id` int(11) DEFAULT NULL,
  `room_number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmh96fg24x7cdhex42l051fmhp` (`doctor_id`),
  KEY `FKo5xm60ctai37uokkakle4q269` (`room_number`),
  CONSTRAINT `FKmh96fg24x7cdhex42l051fmhp` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`),
  CONSTRAINT `FKo5xm60ctai37uokkakle4q269` FOREIGN KEY (`room_number`) REFERENCES `room` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `number` int(11) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`number`),
  KEY `FKoao5fnp5kqqcf3t24x1wkrvhr` (`adder_id`),
  CONSTRAINT `FKoao5fnp5kqqcf3t24x1wkrvhr` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_equipment`
--

DROP TABLE IF EXISTS `room_equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_equipment` (
  `rooms_number` int(11) NOT NULL,
  `equipment_id` int(11) NOT NULL,
  KEY `FKrmdm2ihfq0rjieivqjmnty6hq` (`equipment_id`),
  KEY `FKkeeth7at6hg74b6sgcll6n5of` (`rooms_number`),
  CONSTRAINT `FKkeeth7at6hg74b6sgcll6n5of` FOREIGN KEY (`rooms_number`) REFERENCES `room` (`number`),
  CONSTRAINT `FKrmdm2ihfq0rjieivqjmnty6hq` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `duration` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `price` double NOT NULL,
  `doctor_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6hoq8a5vlhpt29sk7l305uq8e` (`doctor_id`),
  CONSTRAINT `FK6hoq8a5vlhpt29sk7l305uq8e` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `specialization`
--

DROP TABLE IF EXISTS `specialization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialization` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `adder_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeinljvr6cxjcn1vfbjnh571gu` (`adder_id`),
  CONSTRAINT `FKeinljvr6cxjcn1vfbjnh571gu` FOREIGN KEY (`adder_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visit`
--

DROP TABLE IF EXISTS `visit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `note` varchar(5000) DEFAULT NULL,
  `taken_place` int(11) DEFAULT NULL,
  `time` time NOT NULL,
  `doctor_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc63541y8ppkvsovm00gumv90t` (`doctor_id`),
  KEY `FKrban5yeabnx30seqm69jw44e` (`patient_id`),
  CONSTRAINT `FKc63541y8ppkvsovm00gumv90t` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`),
  CONSTRAINT `FKrban5yeabnx30seqm69jw44e` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visit_services`
--

DROP TABLE IF EXISTS `visit_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visit_services` (
  `visits_id` int(11) NOT NULL,
  `services_id` int(11) NOT NULL,
  KEY `FKc0j8pwhs4he7hh4shbas1awfa` (`services_id`),
  KEY `FKydv7vy56mdluw3bpw8jo2913` (`visits_id`),
  CONSTRAINT `FKc0j8pwhs4he7hh4shbas1awfa` FOREIGN KEY (`services_id`) REFERENCES `service` (`id`),
  CONSTRAINT `FKydv7vy56mdluw3bpw8jo2913` FOREIGN KEY (`visits_id`) REFERENCES `visit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

CREATE UNIQUE INDEX patient_pesel_index
USING BTREE
ON patient (pesel)

CREATE UNIQUE INDEX doctor_pesel_index
USING BTREE
ON doctor (pesel)

CREATE UNIQUE INDEX specialization_name_index
USING BTREE
ON specialization(name)

CREATE UNIQUE INDEX clinic_name_index
USING BTREE
ON clinic(name)

-- Dump completed on 2022-01-27  1:36:07
