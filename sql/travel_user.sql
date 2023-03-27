create table collection
(
    id                  bigint auto_increment comment '主键'
        primary key,
    user_id             bigint                             not null comment '用户 id',
    favorite_id         bigint   default 0                 not null comment '收藏夹 id',
    collection_obj_type int                                not null comment '收藏对象类型',
    collection_obj_id   bigint                             not null comment '收藏对象 id',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '收藏时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '收藏表';

create table favorite
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                             not null comment '用户 id',
    favorite_name varchar(255)                       not null comment '收藏夹名称',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '收藏夹';

create table follow
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint                             not null comment '用户 id',
    follow_user_id bigint                             not null comment '被关注用户 id',
    follow_state   tinyint  default 0                 null comment '关注状态（0：关注 1：取消关注）',
    update_time    datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    create_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '关注时间'
)
    comment '关注表';

create table history
(
    id               bigint auto_increment comment '主键'
        primary key,
    user_id          bigint                             not null comment '用户 id',
    history_obj_type int                                not null comment '浏览记录类型',
    history_obj_id   bigint                             not null comment '浏览对象 id',
    is_deleted       tinyint  default 0                 not null comment '是否删除',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '浏览时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '历史记录表';

create table inform
(
    id              bigint auto_increment comment '主键'
        primary key,
    user_id         bigint                                  not null comment '举报人 id',
    inform_obj_type int                                     not null comment '举报对象类型',
    inform_obj_id   bigint                                  not null comment '举报对象 id',
    content         varchar(1024) default ''                not null comment '举报内容',
    inform_tag      int           default 0                 not null comment '举报标签',
    is_deleted      tinyint       default 0                 not null comment '是否删除（0：未删除 1：已删除）',
    create_time     datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '举报表';

create table message
(
    id               bigint auto_increment comment '主键'
        primary key,
    user_id          bigint                             not null comment '用户 id',
    content          varchar(512)                       not null comment '消息内容',
    message_obj_type int                                not null comment '消息关联类型',
    message_obj_id   bigint                             not null comment '消息关联对象 id',
    message_state    int      default 0                 not null comment '消息状态（0：未读 1：已读）',
    is_deleted       tinyint  default 0                 not null comment '是否删除（0：未删除 1：已删除）',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '消息表';

create table user
(
    id          bigint auto_increment
        primary key,
    official_id bigint       default 0                 not null comment '所管官方 id',
    type_id     int          default 0                 not null comment '官方类型 id',
    team_id     varchar(255) default ''                not null comment '所加团队 id 数组',
    token       int          default 0                 not null comment '代币数',
    location    varchar(255) default ''                not null comment '地理位置',
    user_role   int          default 0                 not null comment '用户角色（0：普通用户 1：官方用户 2：管理员）',
    user_state  tinyint      default 0                 not null comment '用户状态（0：正常 1：异常）',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户表';

create table user_info
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                             not null comment '用户 id',
    user_account  varchar(50)                        not null comment '用户账号',
    user_password varchar(50)                        not null comment '用户密码',
    user_name     varchar(255)                       null comment '用户昵称',
    user_avatar   varchar(255)                       null comment '头像 URL',
    phone         varchar(30)                        null comment '手机号',
    email         varchar(30)                        null comment '用户邮箱',
    signature     varchar(255)                       null comment '个性签名',
    birth         datetime                           null comment '生日',
    sex           tinyint                            null comment '性别（0：女 1：男）',
    like_num      int      default 0                 not null comment '获赞数',
    view_count    int      default 0                 not null comment '浏览数',
    follow_count  int      default 0                 not null comment '关注数',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户基本信息表';

create table user_like
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                             not null comment '用户 id',
    like_obj_type int                                not null comment '点赞类型',
    like_obj_id   int                                not null comment '点赞对象 id',
    like_state    tinyint  default 0                 null comment '点赞状态（0：点赞 1：取消点赞）',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '点赞时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '点赞表';


