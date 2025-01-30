-- users
create table if not exists users (

    id              bigint       not null auto_increment primary key    comment '사용자 ID',
    name            varchar(50)  not null                               comment '사용자 이름',
    max_points      int          not null default 0                     comment '최대 보유 가능 포인트',
    created_at      datetime(6)  not null default current_timestamp     comment '생성 일시',
    updated_at      datetime(6)      null default current_timestamp     comment '수정 일시'
);

-- points
create table if not exists points (

    id                  bigint       not null auto_increment primary key    comment '포인트 ID',
    user_id             bigint       not null                               comment '사용자 ID',
    amount              int          not null                               comment '포인트 금액',
    remaining_amount    int          not null                               comment '잔여 포인트',
    transaction_type    varchar(50)  not null                               comment '거래 유형',
    manual              tinyint      not null default 0                     comment '수동 여부',
    expire_at           date         null                                   comment '만료 일',
    expired             tinyint      not null default 0                     comment '만료 처리 여부',
    order_id            bigint       null                                   comment '주문 ID',
    related_point_id    bigint       null                                   comment '관련 포인트 ID(포인트 거래 출처)',
    created_at          datetime(6)  not null default current_timestamp     comment '생성 일시',
    updated_at          datetime(6)      null default current_timestamp     comment '수정 일시'
);

-- point_configures
create table if not exists point_configures (

    id                      bigint       not null auto_increment primary key    comment '포인트 설정 ID',
    max_amount_per_add      int          not null                               comment '최대 단일 적립 금액',
    active                  tinyint      not null default 0                     comment '활성 여부',
    created_at              datetime(6)  not null default current_timestamp     comment '생성 일시',
    updated_at              datetime(6)      null default current_timestamp     comment '수정 일시'
);

-- point_usages
create table if not exists point_usages (

    id                  bigint       not null auto_increment primary key    comment '포인트 사용 ID',
    point_id            bigint       not null                               comment '포인트 ID',
    related_point_id    bigint       null                                   comment '관련 포인트 ID(포인트 사용 출처)',
    amount              int          not null                               comment '사용 포인트 금액',
    created_at          datetime(6)  not null default current_timestamp     comment '생성 일시',
    updated_at          datetime(6)      null default current_timestamp     comment '수정 일시'
);