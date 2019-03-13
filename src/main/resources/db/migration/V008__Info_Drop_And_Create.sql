drop table if exists info;
create table if not exists info (
   id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    address varchar(255),
    cap varchar(255),
    latitude varchar(255),
    location varchar(255),
    longitude varchar(255),
    floor varchar(255),
    instance_id varchar(255),
    major int4 not null,
    minor int4 not null,
    name varchar(255),
    namespace varchar(255),
    open_data_poi_id varchar(255),
    order_symbol varchar(255),
    uuid uuid,
    website varchar(255),
    primary key (id)
);