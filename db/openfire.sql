
CREATE DATABASE /*!32312 IF NOT EXISTS*/`openfire` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `openfire`;

/*Table structure for table `content` */
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` tinyint(10) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Data for the table `content` */
insert  into `test`(`id`,`title`) values (1,'test table title');


DROP TABLE IF EXISTS `smitRegisteredPushServiceID`;
CREATE TABLE `smitRegisteredPushServiceID` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `pushServiceID` varchar(18) NOT NULL,
  `serviceType` varchar(50) DEFAULT NULL,
  `userName` varchar(150) DEFAULT NULL,
  `userAccount` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `smitOfflinePushIQ`;
CREATE TABLE `smitOfflinePushIQ` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `collapseKey` varchar(20) NOT NULL,
  `IQText` varchar(1024) DEFAULT NULL,
  `IQSize` smallint(10) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `sendTo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `smitUserAccountResource`;
CREATE TABLE `smitUserAccountResource` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userAccount` varchar(50) NOT NULL,
  `resource` varchar(50) DEFAULT NULL,
  `deviceName` varchar(50) DEFAULT NULL,
  `deviceId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;