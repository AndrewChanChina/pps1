update ofVersion set version=1 where name='pubsubservice';

create table feed_record(
	id 				int not null auto_increment,
	feed			varchar(1000) not null,
	part_id			int not null,
	create_time		varchar(64),
	feed_name		varchar(64),
	primary key(id)
);

insert into feed_record values('http://www.youku.com/index/rss_cool_v/',101,'2011-08-06','�ſ��Ƽ���Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_comment_day_videos/duration/1',102,'2011-08-06','�ſ�������������Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_hot_day_videos/duration/1',103,'2011-08-06','�ſ������������Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_comment_day_videos/duration/2',104,'2011-08-06','�ſ᱾�����������Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_hot_day_videos/duration/2',105,'2011-08-06','�ſ᱾����������Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_comment_day_videos/duration/3',106,'2011-08-06','�ſ᱾�����������Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_hot_day_videos/duration/3',107,'2011-08-06','�ſ᱾����������Ƶ');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/91',108,'2011-08-06','�ſ���Ѷ');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/92',109,'2011-08-06','�ſ�ԭ��');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/97',110,'2011-08-06','�ſ���Ӿ�');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/86',111,'2011-08-06','�ſ�����');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/96',112,'2011-08-06','�ſ��Ӱ');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/98',113,'2011-08-06','�ſ�����');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/95',114,'2011-08-06','�ſ�����');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/99',115,'2011-08-06','�ſ���Ϸ');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/100',116,'2011-08-06','�ſᶯ��');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/89',117,'2011-08-06','�ſ�ʱ��');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/90',118,'2011-08-06','�ſ�ĸӤ');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/104',119,'2011-08-06','�ſ�����');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/88',120,'2011-08-06','�ſ�����');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/105',121,'2011-08-06','�ſ�Ƽ�');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/87',122,'2011-08-06','�ſ����');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/87',123,'2011-08-06','�ſ����');










































































