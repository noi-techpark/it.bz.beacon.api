alter table issue drop column if exists beacon_data;
alter table issue add column if not exists beacon_data_id int8;
alter table issue drop constraint if exists FKn3tkf7lgl2hald844n35p695i;
alter table issue add constraint FKn3tkf7lgl2hald844n35p695i foreign key (beacon_data_id) references beacon_data;