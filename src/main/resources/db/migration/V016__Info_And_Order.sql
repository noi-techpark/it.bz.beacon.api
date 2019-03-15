drop table if exists info;
create table if not exists info (
   id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    address text,
    cap varchar(255),
    latitude double precision,
    location text,
    longitude double precision,
    floor varchar(255),
    instance_id varchar(255),
    major int4 not null,
    minor int4 not null,
    name text,
    namespace varchar(255),
    uuid uuid,
    website text,
    primary key (id)
);

drop table if exists "order";
create table if not exists "order" (
   id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
   order_symbol varchar(255),
    primary key (id)
);