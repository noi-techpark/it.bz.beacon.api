create table "group"
(
  id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  name text,
  primary key(id),
  CONSTRAINT group_name_key UNIQUE (name)
);

create unique index UK_group_lower_name on "group" (lower(name));

create table user_role
(
  id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  group_id bigint not null,
  user_id bigint not null,
  role_id varchar(255) not null,
  primary key (id)
);

alter table user_role add constraint UK_user_role_group_user unique (group_id, user_id);

alter table user_role add constraint FK_user_role_group foreign key (group_id) references "group" (id);

alter table user_role add constraint FK_user_role_user foreign key (user_id) references "user" (id);