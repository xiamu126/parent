drop database if exists ZNLD_V2;
CREATE DATABASE ZNLD_V2 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use ZNLD_V2;

create table execute_command(
  id           int unsigned not null auto_increment primary key,
  obj_id        int unsigned not null,
  obj_inst_id    int unsigned not null,
  res_id        int unsigned not null,
  value        varchar(32) not null,
  description  varchar(32) not null,
  timeout      tinyint unsigned default 5 not null
);
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3300, 0, 5700, '000', '心跳');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3201, 0, 5551, '001', '屏幕_开');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3201, 0, 5551, '002', '屏幕_关');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3201, 1, 5551, '003', '气象站_开');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3201, 1, 5551, '004', '气象站_关');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3302, 0, 5700, '101', '气象站_数据上传');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3302, 0, 5700, '102', '路灯状态查询');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3302, 0, 5700, '103', '位置信息查询');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3318, 0, 5700, 'A', '气象信息上传频率');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3318, 1, 5700, 'B', '位置信息上传频率');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3318, 2, 5700, 'C', '路灯状态信息上传频率');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3201, 0, 5503, 104, '路灯与平台握手');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3303, 0, 5700, 200, '环境监测开始上传信息');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3303, 0, 5700, 201, '环境监测停止上传信息');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3303, 0, 5700, 202, '位置信息开始上传');
INSERT INTO execute_command (obj_id, obj_inst_id, res_id, value, description) VALUES (3303, 0, 5700, 203, '位置信息停止上传');

create table lamp(
  id           varchar(32) not null primary key,
  api_key       varchar(50) not null ,
  device_id     varchar(50) not null,
  imei         varchar(50) not null,
  obj_id        int unsigned not null,
  obj_inst_id    int unsigned not null,
  res_id        int unsigned not null,
  name         varchar(30) not null ,
  description  varchar(10) default '' not null,
  timeout      tinyint unsigned default 5 not null,
  longitude    varchar(20) default '' not null,
  latitude     varchar(20) default '' not null,
  device_name   varchar(50) not null,
  status       tinyint unsigned default 0  not null comment '0：启用但需要监测，1：启用但不需要监测，2：删除',
  organization_id varchar(32) not null comment '所属组织'
);
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3304, 0, 5700, 'shidu', '湿度', 5, '', '', '路灯0001', 0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3303, 0, 5700, 'wendu', '温度', 5, '', '', '路灯0001', 0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3336, 0, 5513, 'bd_weidu', '北斗纬度', 5, '', '', '路灯0001', 0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3336, 0, 5514, 'bd_jingdu', '北斗经度', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3336, 0, 5518, 'bd_sjcuo', '北斗时间戳', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3336, 0, 5515, 'bd_gaodu', '北斗高度', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3336, 0, 5516, 'bd_jdu', '北斗精度', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3336, 0, 5517, 'bd_sudu', '北斗速度', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3303, 0, 5701, 'wddanwei', '温度单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3304, 0, 5701, 'sddanwei', '湿度单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3315, 0, 5700, 'qiya', '气压', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3315, 0, 5701, 'qydanwei', '气压单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3321, 0, 5700, 'gaodu', '高度', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3321, 0, 5701, 'gddanwei', '高度单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3321, 0, 5750, 'cgqmingzi_gaodu', '传感器名字', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3324, 0, 5700, 'zaoyin', '噪音', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3324, 0, 5701, 'zydanwei', '噪音单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3324, 0, 5750, 'cgqmingzi_zaoyin', '传感器名字', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3301, 0, 5700, 'ziwaixian', '紫外线', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3301, 0, 5701, 'zwxdanwei', '紫外线单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3325, 0, 5700, 'pm25', 'PM2.5', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3325, 0, 5701, 'pm25danwei', 'PM2.5单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3325, 0, 5750, 'cgqmingzi_pm25', '传感器名字', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '42939715', '868194030006003', 9999, 0, 9999, 'test', '测试属性', 5, '', '', '路灯0002',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3325, 1, 5700, 'pm10', 'PM10', 5, '', '', '路灯0001',  0, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3325, 1, 5701, 'pm10danwei', 'PM10单位', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');
INSERT INTO lamp (id, api_key, device_id, imei, obj_id, obj_inst_id, res_id, name, description, timeout, longitude, latitude, device_name, status, organization_id)
VALUES (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', 3325, 1, 5750, 'cgqmingzi_pm10', '传感器名字', 5, '', '', '路灯0001',  1, 'b56e2648491e11e993a60242ac110006');

create table lamp_region(
  id         varchar(32) not null primary key comment '编号',
  lamp_id    varchar(32) not null comment '路灯编号',
  region_id  varchar(32) not null comment '区域编号'
);

create table region(
  id      varchar(32)  not null primary key,
  name    varchar(50)  not null comment '区域名字',
  status  tinyint unsigned default 0  not null comment '0：启用，1：冻结'
);

create table video_config(
  id      varchar(32)  not null primary key,
  rtsp_url varchar(100) not null,
  rtmp_url varchar(100) not null,
  record_audio bit default false not null ,
  organization_id varchar(32) not null
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
