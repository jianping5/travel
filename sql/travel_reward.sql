create table consume_record
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                             not null comment '用户 id',
    content       varchar(300)                       not null comment '消费信息',
    token_account int      default 0                 not null comment '代币金额',
    consume_type  int                                not null comment '消费关联类型',
    consume_id    bigint                             not null comment '消费关联对象 id',
    is_deleted    tinyint  default 0                 not null comment '是否删除',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '消费记录表';

create table exchange_record
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                             not null comment '用户 id',
    derivative_id bigint                             not null comment '周边 id',
    token_account int      default 0                 not null comment '代币金额',
    certificate   varchar(300)                       not null comment '兑换凭证',
    is_deleted    tinyint  default 0                 not null comment '是否删除',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '兑换记录表';

create table reward_record
(
    id              bigint auto_increment comment '主键'
        primary key,
    user_id         bigint                             not null comment '用户 id',
    reward_obj_type int                                not null comment '奖励对象类型',
    reward_obj_id   bigint                             not null comment '奖励对象 id',
    like_count      int      default 0                 not null comment '点赞量',
    comment_count   int      default 0                 not null comment '评论量',
    collect_count   int      default 0                 not null comment '收藏量',
    reward_count    int      default 0                 not null comment '代币金额',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '奖励记录表';

create table reward_task
(
    id          bigint auto_increment comment '主键'
        primary key,
    task_type   int                                not null comment '任务类型',
    task_name   varchar(255)                       not null comment '任务名称',
    reward      int                                not null comment '任务奖励（代币）',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '任务表';


