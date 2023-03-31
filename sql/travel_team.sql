create database travel_team;

use travel_team;

create table team
(
    id           bigint auto_increment comment '主键'
        primary key,
    user_id      bigint                                  not null comment '创建人 id',
    team_name    varchar(50)                             not null comment '团队名',
    icon_url     varchar(255)                            not null comment '团队图标 URL',
    cover_url    varchar(255)                            not null comment '团队封面 URL',
    intro        varchar(1024) default ''                not null comment '团队介绍',
    team_size    int           default 0                 not null comment '团队人数',
    capacity     int           default 0                 not null comment '团队容量',
    news_count   int           default 0                 not null comment '团队动态数',
    travel_count int           default 0                 not null comment '团队游记数',
    is_audit     tinyint       default 0                 not null comment '是否需要审核（0：不需要 1：需要）',
    team_state   int           default 0                 not null comment '团队状态（0：正常 1：异常 2：已解散）',
    create_time  datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '团队表';

create table team_apply
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_id     bigint                             not null comment '用户 id',
    team_id     bigint                             not null comment '团队 id',
    audit_state int      default 0                 not null comment '申请状态（0：审批中 1：同意 2：拒绝）',
    team_state  int      default 0                 not null comment '团队状态（0：正常 1：异常 2：已解散）',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '团队申请表';

create table team_news
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_id     bigint                             not null comment '用户 id',
    team_id     bigint                             not null comment '团队 id',
    content     varchar(1024)                      not null comment '动态内容',
    image_url   varchar(1024)                      null comment '图片 URL',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '团队动态表';

create table team_wall
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_id     bigint                             not null comment '用户 id',
    team_id     bigint                             not null comment '团队 id',
    content     varchar(1024)                      not null comment '墙内容',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '团队墙表';


