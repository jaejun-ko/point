create table users (
    id bigint not null auto_increment primary key,
    name varchar(50) not null,
    max_points int not null
);