drop database if exists RBAC_V2;
create database RBAC_V2 default character set utf8mb4 collate utf8mb4_unicode_ci;
use RBAC_V2;

create table authority_group(
  id                   varchar(32) not null primary key,
  name                 varchar(32) not null  comment '名称',
  parent_id            varchar(32) not null default 0 comment '上级权限模块编号，0为顶级模块',
  position             int not null default 0 comment '在当前权限模块层级中的位置，0为第一个位置',
  organization_id      varchar(32) not null comment '此权限组所属的组织',
  status               tinyint not null default 0 comment '0：正常，1：删除'
);

create table authority(
  id                   varchar(32) not null primary key,
  name                 varchar(32) not null comment '名称',
  authority_group_id   varchar(32) not null comment '权限组编号',
  uri                  varchar(1024) not null default '' comment '具体的路径；默认为空字符串，即不能访问任何路径',
  status               tinyint not null default 0 comment '0：正常，1：冻结，2：删除'
);

create table user(
  id                varchar(32) not null primary key,
  name              varchar(32) not null,
  password          varchar(32) not null,
  phone             varchar(11) not null default '',
  email             varchar(32) not null default '',
  gender            tinyint not null default 3 comment '0为女，1为男，3为未知',
  age               tinyint not null default 0 ,
  contact_address   varchar(100) not null default '' comment '联系地址',
  real_name         varchar(10) not null default '' comment '真实姓名',
  id_card_no        varchar(18) not null default '' comment '身份证号',
  last_login_time   timestamp not null default CURRENT_TIMESTAMP,
  last_login_ip     varchar(20) not null default '',
  organization_id   varchar(32) not null comment '组织编号',
  status            tinyint not null default 0 comment '0：正常，1：冻结，2：删除'
);
create table organization(
  id                   varchar(32) not null primary key comment '编号',
  name                 varchar(32) not null comment '名称',
  parent_id            varchar(32) not null default '' comment '上级组织编号，空字符串为顶级组织',
  position             int not null default 0 comment '在当前组织层级的位置',
  status               tinyint not null default 0 comment '0：正常，1：冻结，2：删除',
  oauth2_client_id     varchar(256) not null comment 'oauth2认证信息，不同于组织表，用于用户名密码授权模式'
);

create table role(
  id       varchar(32) not null primary key comment '编号',
  name     varchar(32) not null comment '名称',
  organization_id varchar(32) not null comment '此角色所属的组织',
  status   tinyint not null default 0 comment '0：可用，1：冻结'
);

create table user_role(
  id        varchar(32) not null primary key comment '编号',
  user_id   varchar(32) not null comment '用户编号',
  role_id   varchar(32) not null comment '角色编号'
);

create table role_auth(
  id        varchar(32) not null primary key comment '编号',
  role_id   varchar(32) not null comment '角色编号',
  auth_id   varchar(32) not null comment '权限编号'
);


delimiter //
create procedure rbac_init()
begin
  declare 'ERROR' condition for sqlstate '45000';
  declare o_id varchar(32);
  declare a_group_id varchar(32);
  declare r_id varchar(32);
  declare au_id varchar(32);
  declare u_id varchar(32);
  # 清空所有表
  delete from organization where true;
  delete from user where true;
  delete from authority_group where true;
  delete from organization_authority_group where true;
  delete from authority where true;
  delete from role where true;
  delete from user_role where true;
  delete from role_auth where true;
  # 创建测试组织
  insert into organization(id, name, oauth2_client_id) value (replace(uuid(), '-', ''),'神宇北斗测试', 'sybd_znld_test');
  select id into o_id from organization where name = '神宇北斗测试';
  if o_id is null or o_id = '' then
    signal 'ERROR' set message_text = 'o_id is null';
  end if;
  # 创建测试用户
  insert into user(id, name, password, organization_id)
  value (replace(uuid(), '-', ''), 'sybd_test_admin', '$2a$10$EumDON8cvvcKVk5QwQwHm.q2WsUoCD43Y8W0uCzkoRCHeAXsDEOSK', o_id);
  insert into user(id, name, password, organization_id)
    value (replace(uuid(), '-', ''), 'sybd_test_user', '$2a$10$EumDON8cvvcKVk5QwQwHm.q2WsUoCD43Y8W0uCzkoRCHeAXsDEOSK', o_id);
  # 创建测试权限组
  insert into authority_group(id, name, organization_id) value (replace(uuid(), '-', ''), '神宇北斗测试权限组1', o_id);
  select id into a_group_id from authority_group where name = '神宇北斗测试权限组1';
  if a_group_id is null or a_group_id = '' then
    signal 'ERROR' set message_text = 'a_group_id is null';
  end if;
  # 创建测试权限
  insert into authority(id, name, uri, authority_group_id) value (replace(uuid(), '-', ''), '神宇北斗测试权限1', '/api/v1/user/**', a_group_id);
  insert into authority(id, name, uri, authority_group_id) value (replace(uuid(), '-', ''), '神宇北斗测试权限2', '/api/v1/device/**', a_group_id);
  # 创建角色
  insert into role(id, name) value (replace(uuid(), '-', ''), '管理员1');
  insert into role(id, name) value (replace(uuid(), '-', ''), '用户1');
  # 创建角色权限
  select id into r_id from role where name = '管理员1';
  if r_id is null or r_id = '' then
    signal 'ERROR' set message_text = 'r_id is null';
  end if;
  select id into au_id from authority where name = '神宇北斗测试权限1' and authority_group_id = a_group_id;
  if au_id is null or au_id = '' then
    signal 'ERROR' set message_text = 'au_id is null';
  end if;
  insert into role_auth(id, role_id, auth_id) value (replace(uuid(), '-', ''), r_id, au_id);

  select id into au_id from authority where name = '神宇北斗测试权限2' and authority_group_id = a_group_id;
  if au_id is null or au_id = '' then
    signal 'ERROR' set message_text = 'au_id is null';
  end if;
  insert into role_auth(id, role_id, auth_id) value (replace(uuid(), '-', ''), r_id, au_id);

  select id into r_id from role where name = '用户1';
  if r_id is null or r_id = '' then
    signal 'ERROR' set message_text = 'r_id is null';
  end if;
  select id into au_id from authority where name = '神宇北斗测试权限1' and authority_group_id = a_group_id;
  if au_id is null or au_id = '' then
    signal 'ERROR' set message_text = 'au_id is null';
  end if;
  insert into role_auth(id, role_id, auth_id) value (replace(uuid(), '-', ''), r_id, au_id);
  # 创建用户角色
  select id into u_id from user where name = 'sybd_test_admin' and organization_id = o_id;
  if u_id is null or u_id = '' then
    signal 'ERROR' set message_text = 'u_id is null';
  end if;
  select id into r_id from role where name = '管理员1';
  if r_id is null or r_id = '' then
    signal 'ERROR' set message_text = 'r_id is null';
  end if;
  insert into user_role(id, user_id, role_id) value (replace(uuid(), '-', ''), u_id, r_id);

  select id into u_id from user where name = 'sybd_test_user' and organization_id = o_id;
  if u_id is null or u_id = '' then
    signal 'ERROR' set message_text = 'u_id is null';
  end if;
  select id into r_id from role where name = '用户1';
  if r_id is null or r_id = '' then
    signal 'ERROR' set message_text = 'r_id is null';
  end if;
  insert into user_role(id, user_id, role_id) value (replace(uuid(), '-', ''), u_id, r_id);
end //
delimiter ;
call rbac_init();
drop procedure if exists rbac_init;
