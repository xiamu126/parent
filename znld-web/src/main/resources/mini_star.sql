drop database if exists MINI_STAR;
create database MINI_STAR default character set utf8mb4 collate utf8mb4_unicode_ci;
use MINI_STAR;

create table twinkle_beauty_group(
  id                   varchar(32) not null primary key  comment '编号',
  begin_time           timestamp not null  comment '开始时间',
  end_time             timestamp not null  comment '结束时间',
  status               tinyint not null  comment '已经上传=0，正在运行=1，自动停止=2，人为终止=3，网络错误=4，未知错误=5',
  region_id            varchar(32) not null  comment '所属区域'
);

create table twinkle_beauty(
  id                      varchar(32) not null primary key comment '编号',
  color                   varchar(100) not null comment '颜色组，采用rgb方式，格式：255 255 255; 255 255 255; 123 123 123;',
  type                    tinyint not null comment '呼吸灯=0，跑马灯=1，五彩灯=2',
  rate                    tinyint not null  comment '速率，就是间隔',
  user_id              varchar(32) not null  comment '创建或修改人',
  twinkle_beauty_group_id varchar(32) not null  comment '所属组'
);