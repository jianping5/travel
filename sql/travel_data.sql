create database travel_data;

use travel_data;

create table behavior
(
    id                bigint auto_increment comment '主键'
        primary key,
    user_id           varchar(255)                       not null comment '用户 id',
    behavior_obj_type int                                not null comment '行为对象类型',
    behavior_obj_id   bigint                             not null comment '行为对象 id',
    behavior_type     bigint   default 0                 not null comment '行为类型',
    view_count        int      default 0                 not null comment '访问次数',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户行为表';

create table search_record
(
    id           bigint auto_increment comment '主键'
        primary key,
    user_id      bigint                             not null comment '用户 id',
    keyword      varchar(255)                       not null comment '检索关键字',
    search_count int      default 0                 not null comment '检索次数',
    is_deleted   tinyint  default 0                 not null comment '是否删除',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '检索记录表';

create table tag
(
    id            bigint auto_increment comment '主键'
        primary key,
    tag_name      varchar(255)                       not null comment '标签名',
    tag_type      int                                not null comment '标签类型',
    tag_count     int      default 0                 not null comment '次数',
    is_customized tinyint  default 0                 not null comment '是否为自定义的（0：默认 1：自定义）',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '标签表';


