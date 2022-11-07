drop database if exists transaction;
create database transaction;

drop table if exists transaction.`student`;
create table if not exists transaction.`student` (
    `id`           bigint unsigned auto_increment comment '自增主键' primary key,
    `user_name`    varchar(64)                         not null comment '名称',
    `deleted`      bit(1)    default b'0'              not null comment '状态:0: 未删除 1: 已删除 (公共字段)',
    `create_time`  timestamp default current_timestamp not null comment '创建时间 (公共字段)',
    `update_time`  timestamp default current_timestamp not null comment '最后更新时间 (公共字段)'
) comment '用户信息表' engine = InnoDB
    charset = utf8mb4
    collate = utf8mb4_general_ci;

drop table if exists transaction.`class`;
create table if not exists transaction.`class` (
    `id`           bigint unsigned auto_increment comment '自增主键' primary key,
    `class_name`    varchar(64)                         not null comment '名称',
    `deleted`      bit(1)    default b'0'              not null comment '状态:0: 未删除 1: 已删除 (公共字段)',
    `create_time`  timestamp default current_timestamp not null comment '创建时间 (公共字段)',
    `update_time`  timestamp default current_timestamp not null comment '最后更新时间 (公共字段)'
) comment '用户信息表' engine = InnoDB
    charset = utf8mb4
    collate = utf8mb4_general_ci;