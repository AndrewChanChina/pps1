INSERT INTO ofVersion (name, version) VALUES ('pushservice', 0);

CREATE TABLE `smitRegisteredPushServiceID` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `pushServiceID` varchar(18) NOT NULL,
  `serviceType` varchar(50) DEFAULT NULL,
  `userName` varchar(150) DEFAULT NULL,
  `userAccount` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `smitOfflinePushIQ` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `collapseKey` varchar(20) NOT NULL,
  `IQText` varchar(1024) DEFAULT NULL,
  `IQSize` smallint(10) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `sendTo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `smitUserAccountResource` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userAccount` varchar(50) NOT NULL,
  `resource` varchar(50) DEFAULT NULL,
  `deviceName` varchar(50) DEFAULT NULL,
  `deviceId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;