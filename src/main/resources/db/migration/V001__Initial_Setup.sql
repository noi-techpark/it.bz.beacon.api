create sequence hibernate_sequence;

create table "user" (
    id int8 not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    email varchar(255),
    name varchar(255),
    password varchar(255),
    surname varchar(255),
    username varchar(255),
    primary key (id)
);

alter table "user" add constraint UK_user_username unique (username);

create table beacon_data (
    id int8 not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    description text,
    lat float4 not null,
    lng float4 not null,
    location_description text,
    location_type int4 not null,
    manufacturer varchar(255) not null,
    manufacturer_id varchar(255) not null,
    name varchar(255),
    primary key (id)
);

alter table beacon_data add constraint UK_beacon_data_manufacturer_id_manufacturer unique (manufacturer_id, manufacturer);

create table beacon_image (
    id int8 not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    beacon_id int8 not null,
    file_name varchar(255) not null,
    primary key (id)
);

create table issue (
    id int8 not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    beacon_data bytea,
    problem varchar(255),
    problem_description text,
    reporter varchar(255),
    solution_id int8,
    primary key (id)
);

create table issue_solution (
    id int8 not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    issue bytea,
    solution varchar(255) not null,
    solution_description text,
    primary key (id)
);

alter table issue add constraint FK_issue_solution_id_issue_solution foreign key (solution_id) references issue_solution;
