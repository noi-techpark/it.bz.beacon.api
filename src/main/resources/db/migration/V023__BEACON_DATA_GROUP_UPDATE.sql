alter table beacon_data add column group_id bigint;

alter table beacon_data add constraint FK_beacon_data_group foreign key (group_id) references "group" (id);