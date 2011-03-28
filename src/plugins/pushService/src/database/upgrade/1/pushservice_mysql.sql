DROP TABLE ofRegisteredIDTable;
DROP TABLE ofPushIQ;
CREATE TABLE ofRegisteredIDTable (
   pushServiceID       	varchar(50)      NOT NULL,
   serviceType     			varchar(50)      NOT NULL,
   userName     			varchar(50)      NOT NULL,
   userAccount     			varchar(255)     NOT NULL,
   PRIMARY KEY (pushServiceID)
);
CREATE TABLE ofPushIQ (
   collapseKey       			varchar(50)     NOT NULL,
   IQText     						varchar(1024)   NOT NULL,
   IQSize           			BIGINT         NOT NULL,
   creationDate						varchar(15)     NOT NULL,
   PRIMARY KEY (IQId)
);
UPDATE ofVersion set version=1 where name='pushservice';