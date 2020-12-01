insert into "group" (id, created_at, updated_at, name, kontakt_io_api_key) values (1, now(), now(), 'Unassigned', '') on conflict do nothing;

update beacon_data set group_id = 1 where group_id is null;

alter table beacon_data alter column group_id set not null;