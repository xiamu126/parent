drop database if exists MINI_STAR;
create database MINI_STAR default character set utf8mb4 collate utf8mb4_unicode_ci;
use MINI_STAR;

create table twinkle_beauty_group(
  id                   varchar(32) not null primary key  comment '编号',
  begin_time           timestamp not null  comment '开始时间',
  end_time             timestamp not null  comment '结束时间',
  status               tinyint unsigned not null  comment '已经上传=0，正在运行=1，自动停止=2，人为终止=3，网络错误=4，未知错误=5',
  region_id            varchar(32) not null  comment '所属区域'
);

create table twinkle_beauty(
  id                       varchar(32) not null primary key comment '编号',
  color                    varchar(256) not null comment '颜色组，采用rgb方式，格式：255 255 255; 255 255 255; 123 123 123;',
  type                     tinyint unsigned not null comment '呼吸灯=0，跑马灯=1，五彩灯=2',
  rate                     tinyint unsigned not null  comment '速率，就是间隔',
  user_id                  varchar(32) not null  comment '创建或修改人',
  twinkle_beauty_group_id  varchar(32) not null  comment '所属组'
);

delimiter //
create procedure mini_star_init()
begin
    declare my_error condition for sqlstate '45000';
    set @user_table = 'RBAC_V2.user';
    set @region_table = 'ZNLD_V3.regionName';
    set @r_name = '神宇北斗测试区域1';
    set @r_id = null;
    set @s = concat('select id into @r_id from', @region_table, 'where name=\'', @r_name, '\'');
    prepare stmt from @s;
    execute stmt;
    deallocate prepare stmt;
    if @r_id is null then
        signal my_error set message_text = 'r_id is null';
    end if;
    insert into twinkle_beauty_group(id, begin_time, end_time, status, region_id)
    values (replace(uuid(), '-', ''), now(),date_add(now(), interval 10 minute), 0, @r_id);
    begin
        declare g_id varchar(32) default null;
        select id into g_id from twinkle_beauty_group limit 1;
        if g_id is null then
            signal my_error set message_text = 'g_id is null';
        end if;
        set @u_id = null;
        set @u_name = 'sybd_test_admin';
        set @s = concat('select id into @u_id from', @user_table, 'where name=\'', @u_name, '\'');
        if @u_id is null then
            signal my_error set message_text = 'u_id is null';
        end if;
        insert into twinkle_beauty(id, color, type, rate, user_id, twinkle_beauty_group_id)
        values (replace(uuid(), '-', ''), '255 255 255; 255 255 255; 123 123 123;', 0, 100, @u_id, g_id);
    end;
end //
delimiter ;
call mini_star_init();
drop procedure if exists mini_star_init;