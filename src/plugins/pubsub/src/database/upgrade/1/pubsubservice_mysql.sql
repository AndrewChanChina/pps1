update ofVersion set version=1 where name='pubsubservice';

create table feed_record(
	id 				int not null auto_increment,
	feed			varchar(1000) not null,
	part_id			int not null,
	create_time		varchar(64),
	feed_name		varchar(64),
	primary key(id)
);

insert into feed_record values('http://www.youku.com/index/rss_cool_v/',101,'2011-08-06','优酷推荐视频');
insert into feed_record values('http://www.youku.com/index/rss_comment_day_videos/duration/1',102,'2011-08-06','优酷今日评论最多视频');
insert into feed_record values('http://www.youku.com/index/rss_hot_day_videos/duration/1',103,'2011-08-06','优酷今日浏览最多视频');
insert into feed_record values('http://www.youku.com/index/rss_comment_day_videos/duration/2',104,'2011-08-06','优酷本周评论最多视频');
insert into feed_record values('http://www.youku.com/index/rss_hot_day_videos/duration/2',105,'2011-08-06','优酷本周浏览最多视频');
insert into feed_record values('http://www.youku.com/index/rss_comment_day_videos/duration/3',106,'2011-08-06','优酷本月评论最多视频');
insert into feed_record values('http://www.youku.com/index/rss_hot_day_videos/duration/3',107,'2011-08-06','优酷本月浏览最多视频');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/91',108,'2011-08-06','优酷资讯');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/92',109,'2011-08-06','优酷原创');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/97',110,'2011-08-06','优酷电视剧');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/86',111,'2011-08-06','优酷娱乐');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/96',112,'2011-08-06','优酷电影');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/98',113,'2011-08-06','优酷体育');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/95',114,'2011-08-06','优酷音乐');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/99',115,'2011-08-06','优酷游戏');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/100',116,'2011-08-06','优酷动漫');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/89',117,'2011-08-06','优酷时尚');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/90',118,'2011-08-06','优酷母婴');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/104',119,'2011-08-06','优酷汽车');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/88',120,'2011-08-06','优酷旅游');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/105',121,'2011-08-06','优酷科技');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/87',122,'2011-08-06','优酷教育');
insert into feed_record values('http://www.youku.com/index/rss_category_videos/cateid/87',123,'2011-08-06','优酷教育');










































































