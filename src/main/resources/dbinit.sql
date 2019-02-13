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
    )
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
    )
create table beacon_image (
       id int8 not null,
        created_at timestamp not null,
        updated_at timestamp not null,
        beacon_id int8 not null,
        file_name varchar(255) not null,
        primary key (id)
    )
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
    )
create table issue_solution (
       id int8 not null,
        created_at timestamp not null,
        updated_at timestamp not null,
        issue bytea,
        solution varchar(255) not null,
        solution_description text,
        primary key (id)
    )
alter table "user" drop constraint UK_sb8bbouer5wak8vyiiy4pf2bx
alter table "user" add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username)
alter table beacon_data drop constraint UK2x2vcndj2x3u5bje9n1sv7cqk
alter table beacon_data add constraint UK2x2vcndj2x3u5bje9n1sv7cqk unique (manufacturer_id, manufacturer)
create sequence hibernate_sequence start 1 increment 1
alter table issueadd constraint FKhbve5ev5htnpoykq9lporie1bforeign key (solution_id)references issue_solution
insert into "user" (id, username, password, name, created_at, updated_at) values (1, 'admin', '$2y$04$CrEwSZC.F21.YuXUN.tSgeMN4ToqQ4Rqb2uzmOqv2R4aUMRuAwHBS', 'Admin', '2019-01-01 00:00:00', '2019-01-01 00:00:00')
