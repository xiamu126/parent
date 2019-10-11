drop database if exists ZNLD_V4;
create database ZNLD_V4 default character set utf8mb4 collate utf8mb4_unicode_ci;
use ZNLD_V4;

create table ministar_effect(
    id int primary key auto_increment,
    name varchar(50) not null,
    type varchar(50) not null,
    colors varchar(100) not null,
    speed int not null,
    brightness int not null,
    organization_id varchar(32) not null
);

create table ministar_task(
    id int primary key auto_increment,
    area_id varchar(32) not null,
    user_id varchar(32) not null,
    area_type int not null comment '0表示针对街道区域的，1表示针对单个路灯的',
    begin_time timestamp not null,
    end_time timestamp not null,
    status int not null comment '0表示等待中，1表示已经结束',
    organization_id varchar(32) not null,
    effect_type varchar(50) not null,
    colors varchar(100) not null,
    speed int not null,
    brightness int not null,
    title varchar(50) not null,
    cmd varchar(1024) not null comment '具体的发送给硬件的指令',
    trigger_time timestamp not null default CURRENT_TIMESTAMP comment '发生时间'
);

create table data_environment(
    id varchar(32) not null primary key,
    device_id int not null,
    imei varchar(20) not null,
    datastream_id varchar(20) not null,
    name varchar(20) not null,
    value varchar(20) not null,
    at timestamp not null
);
create table data_device_onoff(
    id varchar(32) not null primary key,
    device_id int not null,
    imei varchar(20) not null,
    datastream_id varchar(20) not null,
    name varchar(20) not null,
    value boolean not null,
    at timestamp not null
);
create table data_location(
    id varchar(32) not null primary key,
    device_id int not null,
    imei varchar(20) not null,
    datastream_id varchar(20) not null,
    name varchar(20) not null,
    value varchar(20) not null,
    at timestamp not null
);
create table data_angle(
    id varchar(32) not null primary key,
    device_id int not null,
    imei varchar(20) not null,
    datastream_id varchar(20) not null,
    name varchar(20) not null,
    value varchar(20) not null,
    at timestamp not null
);

create table app(
  id               varchar(32) not null primary key,
  name             varchar(32) not null comment '此app的名字',
  version_code     int not null comment '版本检测主要看这个',
  version_name     varchar(32) not null comment '这个版本号是给用户看的，版本检测不依赖于此',
  url              varchar(100) not null  comment '下载地址',
  description      varchar(100) not null  comment '此app的描述',
  api_url          varchar(100) not null comment '这个app的默认api接口地址'
);
create table onenet_resource(
  id                   varchar(32) not null primary key,
  obj_id               int not null  comment '',
  obj_inst_id          int not null  comment '',
  res_id               int not null  comment '',
  value                varchar(32) not null  comment '对于命令资源，这个值就是具体的命令，对于一般资源，这个值暂时不用',
  description          varchar(100) not null  comment '',
  timeout              tinyint not null default 5  comment '',
  type                 tinyint not null default 0 comment '0：命令资源，1：数值资源，2：单位资源，3：状态资源，4：其它资源'
);
alter table onenet_resource add index idx_obj_inst_res(obj_id, obj_inst_id, res_id);
create table lamp_resource(
  id                   varchar(32) not null primary key,
  lamp_id              varchar(32) not null  comment '',
  onenet_resource_id   varchar(32) not null  comment '',
  status               tinyint not null default 0 comment '0：需要监测这个环境资源，1：需要监测这个其它资源，2：跳过监测即平台不会读取这个资源的值，'
);
create table lamp(
  id                   varchar(32) not null primary key,
  api_key              varchar(50) not null  comment '',
  device_id            varchar(50) not null  comment '',
  imei                 varchar(50) not null  comment '',
  device_name          varchar(50) not null  comment '路灯名字',
  longitude            varchar(20) not null default '' comment '经度',
  latitude             varchar(20) not null default '' comment '纬度',
  status               tinyint not null default 0  comment '0：路灯正常运行中，1：路灯处于故障状态，2：路灯报废, 3：为虚拟路灯',
  x_angle              float not null default 0 comment '倾斜状态',
  y_angle              float not null default 0 comment '倾斜状态',
  link_to              int not null default 0 comment '',
  weight               int not null default 0 comment '',
  environment          boolean not null default false comment '是否包含环境监测功能，默认为不包含'
);
create table lamp_region(
  id         varchar(32) not null primary key,
  lamp_id    varchar(32) not null comment '路灯编号',
  region_id  varchar(32) not null comment '区域编号'
);

create table region(
  id                 varchar(32)  not null primary key,
  name               varchar(50)  not null comment '区域名字',
  type               tinyint default 0 not null comment '0：对应着真实的物理街道（区域），1：虚拟区域；一盏路灯只能属于一个物理区域，但可以属于多个虚拟区域',
  organization_id    varchar(32) not null  comment '所属组织',
  status             tinyint default 0 not null comment '0：启用，1：删除'
);

create table lamp_camera(
    id         varchar(32) not null primary key,
    lamp_id    varchar(32) not null,
    camera_id  varchar(32) not null
);

create table camera(
  id               varchar(32)  not null primary key,
  rtsp_url         varchar(100) not null,
  rtmp             json not null,
  flv_rul          varchar(100) not null,
  record_audio     bit default false not null comment '是否录制声音',
  status           tinyint default 0 not null comment '0：启用，1：禁用'
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
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5700, '000', '心跳', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5701, '001', '屏幕_开', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5702, '002', '屏幕_关', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5703, '003', '环境监测_开', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5704, '004', '环境监测_关', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5705, '102', '路灯状态查询', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5706, '103', '位置信息查询', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5707, '104', '路灯与平台握手', 0);

    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5708, 'A', '气象信息上传频率', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5709, 'B', '位置信息上传频率', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5710, 'C', '路灯状态信息上传频率', 0);

    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5711, '200', '环境监测开始上传信息', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5712, '201', '环境监测停止上传信息', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5713, '202', '位置信息开始上传', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5714, '203', '位置信息停止上传', 0);
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5715, '204', '景观灯工作状态，0为停止，1为正常', 0);

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
    insert into onenet_resource (id, obj_id, obj_inst_id, res_id, value, description, type) values (replace(uuid(), '-', ''), 3201, 0, 5502, '', '心跳状态，0为停止，1为正常', 3);
  end;

  # 添加路灯数据
  begin
    insert into lamp(id, api_key, device_id, imei, device_name, status) values (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '518452664', '868194030005849', '路灯0001', 2);
    insert into lamp(id, api_key, device_id, imei, device_name, status) values (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '42939715', '868194030006003', '路灯0002', 2);
    insert into lamp(id, api_key, device_id, imei, device_name) values (replace(uuid(), '-', ''), 'fN8PGSJ3VoIOSoznGWuGeC25PGY=', '520914939', '868194030005971', '路灯0003');
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

  # 创建摄像头数据
  begin
      insert into camera(id, rtsp_url, rtmp_url) values (replace(uuid(), '-', ''), 'rtsp:\/\/admin:admin888@192.168.11.64:554/Streaming/Channels/101', 'rtmp:\/\/localhost:1935/live/livestream');
  end;

  # 创建路灯摄像头数据
  begin
      declare `ERROR` condition for sqlstate '45000';
      set @l_id = null;
      select id into @l_id from lamp where device_name = '路灯0003' and status = 0;
      if @l_id is null then
          signal `ERROR` set message_text = 'l_id is null';
      end if;
      set @c_id = null;
      select id into @c_id from camera where true limit 1;
      if @c_id is null then
        signal `ERROR` set message_text = 'c_id is null';
      end if;
      insert into lamp_camera(id, lamp_id, camera_id) values (replace(uuid(), '-', ''), @l_id, @c_id);
  end;

  # 创建区域数据
  begin
    set @organization_table = 'RBAC_V2.organization';
    set @o_name = '神宇北斗测试';
    set @o_id = null;
    set @s = concat('select id into @o_id from ', @organization_table, ' where name = \'', @o_name, '\'');
    prepare stmt from @s;
    execute stmt;
    deallocate prepare stmt;
    insert into region (id, name, organization_id) values (replace(uuid(), '-', ''), '神宇北斗测试区域1', @o_id);
    insert into region (id, name, organization_id) values (replace(uuid(), '-', ''), '神宇北斗测试区域2', @o_id);
    insert into region (id, name, organization_id) values (replace(uuid(), '-', ''), '神宇北斗测试区域3', @o_id);
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
