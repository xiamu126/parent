drop database if exists znld;
CREATE DATABASE znld DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use znld;

create table execute_command(
  id int unsigned auto_increment primary key,
  objId int unsigned not null,
  objInstId int unsigned not null,
  resId int unsigned not null,
  value varchar(32) not null,
  description varchar(32) not null,
  timeout tinyint unsigned default 5 not null,
  constraint unique_value unique (value)
);

create table onenet_config_device(
  id int unsigned auto_increment primary key,
  apiKey varchar(50) not null ,
  deviceId varchar(50) not null,
  imei varchar(50) not null,
  objId int unsigned not null,
  objInstId int unsigned not null,
  resId int unsigned not null,
  name varchar(30) not null ,
  description varchar(10) not null,
  timeout tinyint unsigned default 5 not null,
  longitude varchar(20) not null,
  latitude varchar(20) not null,
  deviceName varchar(50) not null,
  checked bit default true comment '是否需要监测',
  constraint unique_objId_objInstId_resId unique (objId, objInstId, resId),
  constraint unique_name unique (name)
);

create table user(
  id varchar(32) not null primary key,
  name varchar(32) not null,
  password varchar(32) not null,
  phone varchar(11) null,
  email varchar(32) null,
  gender tinyint unsigned null,
  age tinyint unsigned null,
  contactAddress varchar(64) null,
  realName varchar(10) null,
  idCardNo varchar(18) null,
  lastLoginTime datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  lastLoginIp varchar(50) null,
  authorities varchar(50) not null default 'USER' comment '该用户拥有的权限'
);

create table video_config(
  id int unsigned auto_increment primary key,
  rtspUrl varchar(100) null,
  rtmpUrl varchar(100) null,
  recordAudio bit default false not null,
  cameraId varchar(32) not null,
  clientId varchar(32) not null
);
alter table video_config add constraint fk_video_user foreign key (clientId)references user (id) on delete restrict on update cascade;

alter table onenet_config_device add column apiKey varchar(50) not null after id;
alter table onenet_config_device add column name varchar(10) not null after resId;
alter table onenet_config_device change column code name varchar(10) not null;
alter table onenet_config_device modify column name varchar(30) not null;
alter table onenet_config_device add constraint unique_name unique (name);
alter table onenet_config_device add checked bit default true comment '是否需要监测';
alter table user add column authorities varchar(50) not null default 'USER' comment '该用户拥有的权限';
alter table user add column lastLoginTime datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;
alter table user add column lastLoginIp varchar(50) null;
alter table video_config modify column clientId varchar(32) not null;


delete from znld.onenet_config_device where true;
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3304, 0, 5700, 'shidu', '湿度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3303, 0, 5700, 'wendu', '温度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5513, 'bd_weidu', '北斗纬度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5514, 'bd_jingdu', '北斗经度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5518, 'bd_sjcuo', '北斗时间戳', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5515, 'bd_gaodu', '北斗高度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5516, 'bd_jdu', '北斗精度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5517, 'bd_sudu', '北斗速度', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3303, 0, 5701, 'wddanwei', '温度单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3304, 0, 5701, 'sddanwei', '湿度单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3315, 0, 5700, 'qiya', '气压', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3315, 0, 5701, 'qydanwei', '气压单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3321, 0, 5700, 'gaodu', '高度', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3321, 0, 5701, 'gddanwei', '高度单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3321, 0, 5750, 'cgqmingzi_gaodu', '传感器名字', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3324, 0, 5700, 'zaoyin', '噪音', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3324, 0, 5701, 'zydanwei', '噪音单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3324, 0, 5750, 'cgqmingzi_zaoyin', '传感器名字', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3301, 0, 5700, 'ziwaixian', '紫外线', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3301, 0, 5701, 'zwxdanwei', '紫外线单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 0, 5700, 'pm25', 'PM2.5', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 0, 5701, 'pm25danwei', 'PM2.5单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 0, 5750, 'cgqmingzi_pm25', '传感器名字', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '42939715', '868194030006003', 9999, 0, 9999, 'test', '测试属性', 5, '', '', '路灯0002', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 1, 5700, 'pm10', 'PM10', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 1, 5701, 'pm10danwei', 'PM10单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked)
VALUES ('fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 1, 5750, 'cgqmingzi_pm10', '传感器名字', 5, '', '', '路灯0001', false);
