create database travel_travel;
use travel_travel;

create table article
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint                                 not null comment '所属用户 id',
    title          varchar(255)                           not null comment '标题',
    cover_url      varchar(512)                           not null comment '封面 URL',
    team_id        bigint       default 0                 not null comment '所属团队 id',
    permission     int          default 0                 not null comment '权限（0：公开 1：部分可见 2：私密）',
    tag            varchar(512) default ''                not null comment '标签',
    intro          varchar(255)                           not null comment '文章首句话',
    location       varchar(255) default ''                not null comment '地理位置',
    like_count     int          default 0                 not null comment '点赞量',
    comment_count  int          default 0                 not null comment '评论量',
    favorite_count int          default 0                 not null comment '收藏量',
    view_count     int          default 0                 not null comment '浏览量',
    article_state  int          default 0                 not null comment '文章状态（0：正常 1：异常 2：删除）',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '文章表';

create table article_detail
(
    id          bigint auto_increment comment '主键'
        primary key,
    article_id  bigint                             not null comment '所属文章 id',
    detail      text                               not null comment '文章详情',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '文章详情表';

create table comment
(
    id               bigint auto_increment comment '主键'
        primary key,
    user_id          bigint                                 not null comment '用户 id',
    comment_obj_type int                                    not null comment '评论类型（文章、视频）',
    comment_obj_id   bigint                                 not null comment '评论类型下的对象 id',
    parent_user_id   bigint       default 0                 not null comment '父评论用户 id',
    top_id           bigint       default 0                 not null comment '顶级评论 id',
    content          varchar(2048)                          not null comment '评论内容',
    location         varchar(255) default ''                not null comment '地理位置',
    like_count       int          default 0                 not null comment '点赞量',
    reply_count      int          default 0                 not null comment '回复量',
    is_deleted       tinyint      default 0                 not null comment '是否删除',
    create_time      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '评论表';

create table video
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint                                 not null comment '所属用户 id',
    title          varchar(255)                           not null comment '标题',
    cover_url      varchar(512)                           not null comment '封面 URL',
    team_id        bigint       default 0                 not null comment '所属团队 id',
    intro          varchar(1024)                          not null comment '介绍',
    video_url      varchar(255)                           not null comment '视频 URL',
    permission     int          default 0                 not null comment '权限（0：公开 1：部分可见 2：私密）',
    tag            varchar(512) default ''                not null comment '标签',
    location       varchar(255) default ''                not null comment '地理位置',
    like_count     int          default 0                 not null comment '点赞量',
    comment_count  int          default 0                 not null comment '评论量',
    favorite_count int          default 0                 not null comment '收藏量',
    view_count     int          default 0                 not null comment '浏览量',
    video_state    int          default 0                 not null comment '文章状态（0：正常 1：异常 2：删除）',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '视频表';


