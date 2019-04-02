drop database if exists ZNLD_V3;
create database ZNLD_V3 default character set utf8mb4 collate utf8mb4_unicode_ci;
use ZNLD_V3;

create table onenet_resource(
  id                   varchar(32) not null primary key,
  obj_id               int unsigned not null  comment '',
  obj_inst_id          int unsigned not null  comment '',
  res_id               int unsigned not null  comment '',
  value                varchar(32) not null  comment '对于命令资源，这个值就是具体的命令，对于一般资源，这个值暂时不用',
  description          varchar(100) not null  comment '',
  timeout              tinyint unsigned not null default 5  comment '',
  type                 tinyint unsigned not null default 0 comment '0：命令资源，1：数值资源，2：单位资源，3：状态资源，4：其它资源'
);
create table lamp_resource(
  id                   varchar(32) not null primary key,
  lamp_id              varchar(32) not null  comment '',
  onenet_resource_id   varchar(32) not null  comment '',
  status               tinyint not null default 0 comment '0：需要监测，1：跳过监测即平台不会读取这个资源的值'
);
create table lamp(
  id                   varchar(32) not null primary key,
  api_key              varchar(50) not null  comment '',
  device_id            varchar(50) not null  comment '',
  imei                 varchar(50) not null  comment '',
  device_name          varchar(50) not null  comment '路灯名字',
  longitude            varchar(20) not null default '' comment '经度',
  latitude             varchar(20) not null default '' comment '纬度',
  status               tinyint not null default 0  comment '0：路灯正常运行中，1：路灯处于故障状态，2：路灯报废',
  organization_id      varchar(32) not null  comment '所属组织'
);
create table lamp_region(
  id         varchar(32) not null primary key,
  lamp_id    varchar(32) not null comment '路灯编号',
  region_id  varchar(32) not null comment '区域编号'
);

create table region(
  id      varchar(32)  not null primary key,
  name    varchar(50)  not null comment '区域名字',
  status  tinyint unsigned default 0 not null comment '0：启用，1：冻结'
);

create table video_config(
  id               varchar(32)  not null primary key,
  rtsp_url         varchar(100) not null,
  rtmp_url         varchar(100) not null,
  record_audio     bit default false not null ,
  organization_id  varchar(32) not null
);

create table http_log(
  id                   varchar(32) not null primary key,
  path                 text not null  comment '请求路径',
  method               varchar(10) not null  comment '请求方法',
  header               text not null  comment '请求头',
  body                 text not null  comment '请求体',
  ip                   varchar(20) not null  comment '请求者的IP',
  trigger_time         timestamp not null default CURRENT_TIMESTAMP  comment '发生时间'
);

delimiter //
create procedure znld_init()
begin
  # 创建资源数据
  begin
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3300, 0, 5700, '000', '心跳', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5551, '001', '屏幕_开', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5551, '002', '屏幕_关', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 1, 5551, '003', '气象站_开', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 1, 5551, '004', '气象站_关', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3302, 0, 5700, '101', '气象站_数据上传', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3302, 0, 5700, '102', '路灯状态查询', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3302, 0, 5700, '103', '位置信息查询', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3318, 0, 5700, 'A', '气象信息上传频率', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3318, 1, 5700, 'B', '位置信息上传频率', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3318, 2, 5700, 'C', '路灯状态信息上传频率', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5503, '104', '路灯与平台握手', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3303, 0, 5700, '200', '环境监测开始上传信息', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3303, 0, 5700, '201', '环境监测停止上传信息', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3303, 0, 5700, '202', '位置信息开始上传', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 303, 0, 5700, '203', '位置信息停止上传', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3303, 0, 5500, '', '环境监测整体是否开始上传信息，0为停止，1为开始', 3);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3303, 0, 5700, '', '温度值', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3303, 0, 5701, '', '温度单位', 2);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3304, 0, 5700, '', '湿度值', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3304, 0, 5701, '', '湿度单位', 2);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3325, 0, 5700, '', 'PM2.5值', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3325, 0, 5701, '', 'PM2.5单位', 2);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3325, 0, 5750, '', '传感器名字', 4);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3325, 1, 5700, '', 'PM10', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3325, 1, 5701, '', 'PM10单位', 2);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3325, 1, 5750, '', '传感器名字', 4);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5500, '', '位置信息是否开始自动上传，0为停止，1为开始', 3);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5513, '', '北斗经度', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5514, '', '北斗纬度', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5515, '', '北斗高度', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5516, '', '北斗精度', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5517, '', '北斗速度', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3336, 0, 5518, '', '北斗时间戳', 1);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5503, '', '握手状态，0为停止，1为已连接onenet，2为已握手', 3);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5501, '', '景观灯工作状态，0为停止，1为正常', 3);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5502, '', '心跳状态，0为停止，1为正常', 3);
  end;

  # 添加路灯数据
  begin
    set @organization_table = 'RBAC_V2.organization';
    set @o_name = '神宇北斗测试';
    set @o_id = null;
    set @s = concat('select id into @o_id from ', @organization_table, ' where name = \'', @o_name, '\'');
    prepare stmt from @s;
    execute stmt;
    deallocate prepare stmt;
    insert into lamp(id, api_key, device_id, imei, device_name, organization_id) values (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', '路灯0001', @o_id);
    insert into lamp(id, api_key, device_id, imei, device_name, organization_id) values (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '42939715', '868194030006003', '路灯0002',  @o_id);
    insert into lamp(id, api_key, device_id, imei, device_name, organization_id) values (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '520914939', '868194030005971', '路灯0003', @o_id);
  end;

  # 关联路灯与资源
  begin
    declare l_done boolean default false;
    declare r_done boolean default false;
    declare tmp_lamp_id varchar(32);
    declare tmp_res_id varchar(32);
    declare l_cur cursor for select id from lamp;
    declare r_cur cursor for select id from onenet_resource where type = 1;
    declare continue handler for not found set l_done = true;
    open l_cur;
    while l_done != true do
      fetch l_cur into tmp_lamp_id;
      begin
        declare continue handler for not found set r_done = true;
        open r_cur;
        while r_done != true do
          fetch r_cur into tmp_res_id;
          insert into lamp_resource(id, lamp_id, onenet_resource_id) value (replace(uuid(), '-', ''), tmp_lamp_id, tmp_res_id);
        end while;
        close r_cur;
      end;
    end while;
    close l_cur;
  end;

  # 创建区域数据
  begin
    insert into region (id, name) values (replace(uuid(), '-', ''), '神宇北斗测试区域1');
    insert into region (id, name) values (replace(uuid(), '-', ''), '神宇北斗测试区域2');
    insert into region (id, name) values (replace(uuid(), '-', ''), '神宇北斗测试区域3');
  end;

  # 创建路灯区域
  begin
    declare r_done boolean default false;
    declare l_done boolean default false;
    declare tmp_region_id varchar(32);
    declare tmp_lamp_id varchar(32);
    declare r_cur cursor for select id from region;
    declare continue handler for not found set r_done = true;
    open r_cur;
    while r_done != true do
      fetch r_cur into tmp_region_id;
      begin
        declare l_cur cursor for select id from lamp;
        declare continue handler for not found set l_done = true;
        open l_cur;
        inner_loop:
        while l_done != true do
          fetch l_cur into tmp_lamp_id;
          insert into lamp_region(id, lamp_id, region_id) values (replace(uuid(), '-', ''), tmp_lamp_id, tmp_region_id);
          leave inner_loop;
        end while;
        close l_cur;
      end;
    end while;
    close r_cur;
  end;
end //
delimiter ;
call znld_init();
drop procedure if exists znld_init;

/*alter table video_config add constraint fk_video_user foreign key (clientId)references user (id) on delete restrict on update cascade;
alter table onenet_config_device add column apiKey varchar(50) not null after id;
alter table onenet_config_device add column name varchar(10) not null after resId;
alter table onenet_config_device change column code name varchar(10) not null;
alter table onenet_config_device modify column name varchar(30) not null;
alter table onenet_config_device add constraint unique_name unique (name);
alter table onenet_config_device add checked bit default true comment '是否需要监测';
alter table user add column authorities varchar(50) not null default 'USER' comment '该用户拥有的权限';
alter table user add column lastLoginTime datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;
alter table user add column lastLoginIp varchar(50) null;
alter table video_config modify column clientId varchar(32) not null;*/
