create database travel_official;
use travel_official;
create table derivative
(
    id               bigint auto_increment comment '主键'
        primary key,
    user_id          bigint                             not null comment '所属用户 id',
    official_id      bigint                             not null comment '所属官方 id',
    derivative_name  varchar(100)                       not null comment '周边名称',
    cover_url        varchar(1536)                      not null comment '封面 URL',
    price            double   default 0                 not null comment '周边价格',
    intro            varchar(255)                       not null comment '周边介绍',
    obtain_method    tinyint  default 0                 not null comment '获取方式（0：现金 1：代币）',
    total_count      int      default 0                 not null comment '周边数量',
    type_id          int                                not null comment '类型 id',
    view_count       int      default 0                 not null comment '浏览量',
    obtain_count     int      default 0                 not null comment '周边获取次数',
    derivative_state int      default 0                 not null comment '周边状态（0：正常 1：异常 2：下架）',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '周边表';

create table notification
(
    id                 bigint auto_increment comment '主键'
        primary key,
    user_id            bigint                                 not null comment '所属用户 id',
    official_id        bigint                                 not null comment '所属官方 id',
    type_id            int                                    not null comment '官方类型 id',
    title              varchar(100)                           not null comment '标题',
    cover_url          varchar(512)                           not null comment '封面 URL',
    intro              varchar(255) default ''                not null comment '资讯通知首句话',
    detail             text                                   not null comment '资讯通知详情',
    view_count         int          default 0                 not null comment '浏览量',
    notification_state int          default 0                 not null comment '资源状态（0：正常 1：异常 2：删除）',
    create_time        datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time        datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '资讯通知表';

create table official
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint                                 not null comment '所属用户 id',
    official_name  varchar(50)                            not null comment '官方名',
    province       varchar(20)                            not null comment '省份',
    city           varchar(20)                            not null comment '城市',
    location       varchar(255)                           not null comment '地点',
    lat_and_long   varchar(70)                            not null comment '经纬度',
    cover_url      varchar(512)                           null comment '封面 URL',
    video_url      varchar(512)                           null comment '视频 URL',
    type_id        int                                    not null comment '类型 id',
    contact        varchar(255)                           null comment '联系方式',
    tag            varchar(512) default ''                not null comment '标签',
    intro          varchar(255) default ''                not null comment '官方首句话',
    like_count     int          default 0                 not null comment '点赞量',
    review_count   int          default 0                 not null comment '点评量',
    favorite_count int          default 0                 not null comment '收藏量',
    view_count     int          default 0                 not null comment '浏览量',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '官方表';

create table official_apply
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                             not null comment '所属用户 id',
    official_name varchar(50)                        not null comment '官方名',
    province      varchar(20)                        not null comment '省份',
    city          varchar(20)                        not null comment '城市',
    location      varchar(255)                       not null comment '地点',
    type_id       int                                not null comment '类型 id',
    contact       varchar(255)                       null comment '联系方式',
    material      varchar(255)                       null comment '佐证材料',
    apply_state   int      default 0                 not null comment '申请状态（0：待审批 1：已同意 2：已拒绝）',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '官方申请表';

create table official_detail
(
    id          bigint auto_increment comment '主键'
        primary key,
    official_id bigint                             not null comment '所属官方 id',
    detail      text                               not null comment '官方详情',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '官方详情表';

create table official_resource
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint                                 not null comment '所属用户 id',
    official_id    bigint                                 not null comment '所属官方 id',
    cover_url      varchar(512)                           not null comment '封面 URL',
    price          varchar(100)                           null comment '价格',
    title          varchar(100)                           not null comment '标题',
    type_id        int                                    not null comment '类型 id',
    intro          varchar(255) default ''                not null comment '官方资源首句话',
    like_count     int          default 0                 not null comment '点赞量',
    view_count     int          default 0                 not null comment '浏览量',
    resource_state int          default 0                 not null comment '资源状态（0：正常 1：异常 2：下架）',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '官方资源表';

create table official_type
(
    id          int auto_increment comment '主键'
        primary key,
    type_name   varchar(30)                        not null comment '类型名称',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '官方类型表';

create table resource_detail
(
    id                   bigint auto_increment comment '主键'
        primary key,
    official_resource_id bigint                             not null comment '所属官方资源 id',
    detail               text                               not null comment '官方资源详情',
    create_time          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '官方资源详情表';

create table review
(
    id              bigint auto_increment comment '主键'
        primary key,
    user_id         bigint                                 not null comment '用户 id',
    review_obj_type int                                    not null comment '点评类型',
    review_obj_id   bigint                                 not null comment '点评对象',
    content         varchar(2048)                          not null comment '点评内容',
    location        varchar(255) default ''                not null comment '地理位置',
    like_count      int          default 0                 not null comment '点赞量',
    reply_count     int          default 0                 not null comment '回复量',
    is_deleted      tinyint      default 0                 not null comment '是否删除',
    create_time     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '点评表';


