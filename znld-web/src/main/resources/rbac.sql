drop database if exists RBAC;
create database RBAC default character set utf8mb4 collate utf8mb4_unicode_ci;
use RBAC;

create table authority_group(
  id                   varchar(32) not null primary key,
  name                 varchar(20) not null  comment '名称',
  parent_id            varchar(32) not null comment '上级权限模块编号，0为顶级模块',
  position             int unsigned default 0 not null comment '在当前权限模块层级中的位置，0为第一个位置',
  status               tinyint unsigned default 0 not null comment '0：正常，1：冻结'
);

create table organization_authority_group(
  id                  varchar(32) not null primary key,
  organization_id     varchar(32) not null,
  authority_group_id  varchar(32) not null
);

create table authority(
  id                   varchar(32) not null primary key,
  name                 varchar(20) not null comment '名称',
  authority_group_id   varchar(32) not null comment '权限模块编号',
  url                  varchar(200) not null  comment '具体的路径',
  type                 int unsigned default 0 not null comment '0：菜单，1：按钮，2：其他',
  status               tinyint unsigned default 0 not null comment '0：正常，1：冻结，2：删除'
);

create table user(
  id                varchar(32) not null primary key,
  name              varchar(32) not null,
  password          varchar(32) not null,
  phone             varchar(11) default '' not null,
  email             varchar(32) default '' not null,
  gender            tinyint unsigned default 3 not null comment '0为女，1为男，3为未知',
  age               tinyint unsigned default 0 not null,
  contact_address    varchar(100) default '' not null comment '联系地址',
  real_name          varchar(10) default '' not null comment '真实姓名',
  id_card_no          varchar(18) default '' not null comment '身份证号',
  last_login_time     timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  last_login_ip       varchar(20) not null default '',
  organization_id   varchar(32) not null comment '组织编号',
  status            tinyint unsigned not null default 0 comment '0：正常，1：冻结，2：删除'
);

create table organization(
  id                   varchar(32) not null primary key comment '编号',
  name                 varchar(50) not null comment '名称',
  parent_id            varchar(32) not null comment '上级组织编号，空字符串为顶级组织',
  position             int unsigned not null default 0 comment '在当前组织层级的位置',
  status               tinyint unsigned not null default 0 comment '0：正常，1：冻结，2：删除',
  oauth2_client_id     varchar(256) not null comment 'oauth2认证信息，不同于组织表，用于用户名密码授权模式'
);

create table role(
  id                   varchar(32) not null primary key comment '编号',
  name                 varchar(30) not null not null  comment '名称',
  type                 tinyint unsigned not null default 0 comment '0：管理员，1：普通用户，2：游客',
  status               tinyint unsigned not null default 0 comment '0：可用，1：冻结'
);

create table user_role(
  id                   varchar(32) not null primary key comment '编号',
  user_id              varchar(32) not null comment '用户编号',
  role_id              varchar(32) not null comment '角色编号'
);

create table role_auth(
  id                   varchar(32) not null primary key comment '编号',
  role_id              varchar(32) not null comment '角色编号',
  auth_id              varchar(32) not null comment '权限编号'
);



