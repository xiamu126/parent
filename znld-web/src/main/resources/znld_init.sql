drop database if exists znld_test;
CREATE DATABASE znld_test DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

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


INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (12, 3300, 0, 5700, '000', '心跳', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (13, 3201, 0, 5551, '001', '屏幕_开', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (14, 3201, 0, 5551, '002', '屏幕_关', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (15, 3201, 1, 5551, '003', '气象站_开', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (16, 3201, 1, 5551, '004', '气象站_关', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (17, 3302, 0, 5700, '101', '气象站_数据上传', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (18, 3302, 0, 5700, '102', '路灯状态查询', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (19, 3302, 0, 5700, '103', '位置信息查询', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (20, 3318, 0, 5700, 'A', '气象信息上传频率', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (21, 3318, 1, 5700, 'B', '位置信息上传频率', 15);
INSERT INTO znld.execute_command (id, objId, objInstId, resId, value, description, timeout) VALUES (22, 3318, 2, 5700, 'C', '路灯状态信息上传频率', 15);

INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (1, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3304, 0, 5700, 'shidu', '湿度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (2, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3303, 0, 5700, 'wendu', '温度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (3, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5513, 'bd_weidu', '北斗纬度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (4, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5514, 'bd_jingdu', '北斗经度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (5, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5518, 'bd_sjcuo', '北斗时间戳', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (25, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5515, 'bd_gaodu', '北斗高度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (26, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5516, 'bd_jdu', '北斗精度', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (27, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3336, 0, 5517, 'bd_sudu', '北斗速度', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (28, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3303, 0, 5701, 'wddanwei', '温度单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (29, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3304, 0, 5701, 'sddanwei', '湿度单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (30, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3315, 0, 5700, 'qiya', '气压', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (31, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3315, 0, 5701, 'qydanwei', '气压单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (32, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3321, 0, 5700, 'gaodu', '高度', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (33, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3321, 0, 5701, 'gddanwei', '高度单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (34, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3321, 0, 5750, 'cgqmingzi_gaodu', '传感器名字', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (35, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3324, 0, 5700, 'zaoyin', '噪音', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (36, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3324, 0, 5701, 'zydanwei', '噪音单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (37, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3324, 0, 5750, 'cgqmingzi_zaoyin', '传感器名字', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (38, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3301, 0, 5700, 'ziwaixian', '紫外线', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (39, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3301, 0, 5701, 'zwxdanwei', '紫外线单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (40, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 0, 5700, 'pm25', 'PM2.5', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (41, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 0, 5701, 'pm25danwei', 'PM2.5单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (42, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 0, 5750, 'cgqmingzi_pm25', '传感器名字', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (43, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '42939715', '868194030006003', 9999, 0, 9999, 'test', '测试属性', 5, '', '', '路灯0002', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (44, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 1, 5700, 'pm10', 'PM10', 5, '', '', '路灯0001', true);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (45, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 1, 5701, 'pm10danwei', 'PM10单位', 5, '', '', '路灯0001', false);
INSERT INTO znld.onenet_config_device (id, apiKey, deviceId, imei, objId, objInstId, resId, name, description, timeout, longitude, latitude, deviceName, checked) VALUES (48, 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '505253765', '868194030006003', 3325, 1, 5750, 'cgqmingzi_pm10', '传感器名字', 5, '', '', '路灯0001', false);

INSERT INTO znld.user (id, name, password, phone, email, gender, age, contactAddress, realName, idCardNo, lastLoginTime, lastLoginIp, authorities) VALUES ('0b494009f37711e88347000c294eb278', 'yyy', '123456', null, null, null, 17, null, null, null, '2018-12-29 15:26:17', null, 'USER');
INSERT INTO znld.user (id, name, password, phone, email, gender, age, contactAddress, realName, idCardNo, lastLoginTime, lastLoginIp, authorities) VALUES ('3abc2efb3f5b485bad806632deb2b2cd', 'xxx', 'D9840773233FA6B19FDE8CAF765402F5', '2383838381', null, null, null, null, null, null, '2018-12-29 15:29:13', null, 'USER,ADMIN,TEST');
INSERT INTO znld.user (id, name, password, phone, email, gender, age, contactAddress, realName, idCardNo, lastLoginTime, lastLoginIp, authorities) VALUES ('77796a41d73a4e6ea6b9e3ca1499384b', 'test', 'E75F5A44F80B2E19D7828ED6F9D7C8AF', null, null, null, null, null, null, null, '2018-12-29 15:36:27', null, 'USER,ADMIN,TEST');
INSERT INTO znld.user (id, name, password, phone, email, gender, age, contactAddress, realName, idCardNo, lastLoginTime, lastLoginIp, authorities) VALUES ('a48bfa0ff9e711e880a0000c294eb278', 'qqq', '123456', null, null, null, 18, null, null, null, '2018-12-29 15:26:17', null, 'USER');

INSERT INTO znld.video_config (id, rtspUrl, rtmpUrl, recordAudio, cameraId, clientId) VALUES (1, 'rtsp://admin:admin888@192.168.111.64:554/Streaming/Channels/101', 'rtmp://localhost:1935/live/livestream', false, '362c7e0a149a11e98372000c294eb278', '77796a41d73a4e6ea6b9e3ca1499384b');
