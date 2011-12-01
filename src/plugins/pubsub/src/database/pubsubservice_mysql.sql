INSERT INTO ofVersion(name,version) values('pubsubservice',0);

CREATE TABLE smitsubscribe (
	id    			int               	not null auto_increment,
	jid  			varchar(64)       	not null,
	feed  			varchar(1000)     	not null,
	sub_time		varchar(64)		  	not null,
	PRIMARY KEY(id)
);

CREATE TABLE smitatomrecord (
	id 				int 			 	not null auto_increment,
	title			varchar(250),
	link 			varchar(1000),
	atom_id			varchar(250),
	update_time		varchar(64),
	feed_url		varchar(1000),
	create_time		varchar(64),
	PRIMARY KEY(id)
);

CREATE TABLE smitrssrecord (
	id				int					not null auto_increment,
	title			varchar(250),
	link			varchar(1000),		
	guid			varchar(250),	
	pubDate			varchar(64),
	feed_url		varchar(1000),
	create_time		varchar(64),
	PRIMARY KEY(id)	
);