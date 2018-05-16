create database JobCrawler;

create table Job (
  id integer auto_increment,
  job_id varchar(256) unique not null,
  job_name varchar(64),
  detail_url_path varchar(1024),
  city varchar(16) not null default '',
  work_experience varchar(8) comment '工作年限',
  degree varchar(16) comment '学历',
  treatment varchar(16) comment '待遇',
  detail varchar(65535) comment '岗位详情，如技能要求、jd',
  company varchar(32) comment '公司',
  source varchar(16) comment '信息来源，如拉钩、猎聘',
  primary key (id)
) character set = utf8;