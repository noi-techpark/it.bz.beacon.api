alter table public.order_data add column if not exists zone_id int4;
alter table public.order_data add column if not exists zone_code varchar(255);

alter table order_data drop constraint if exists UK6jbjply47krw2n2j2wtaj08ar;
alter table order_data add constraint UK6jbjply47krw2n2j2wtaj08ar unique (zone_id, zone_code);