alter table "user" add column admin boolean not null default false;

update "user" set admin = true where username = 'admin';