drop table if exists "order";

drop table if exists order_data;
create table if not exists order_data (
   id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    order_symbol varchar(255),
    primary key (id)
);