drop view info;

create or replace view info as
select id as id,
       created_at,
       updated_at,
       address,
       cap,
       case when lat_poi is not null and lat_poi != 0 then lat_poi else lat_beacon end as latitude,
       location,
       case when lng_poi is not null and lng_poi != 0 then lng_poi else lng_beacon end as longitude,
       floor,
       instance_id,
       major,
       minor,
       name_poi as name,
       namespace,
       uuid,
       null::text as website,
       battery_level,
       trusted_updated_at,
       (remote_beacon ->> 'txPower')::integer as tx_power,
       (case when remote_beacon is null then null
             when (remote_beacon ->> 'pendingConfiguration') is not null then 'PLANNED'
             else 'INSTALLED' end) as status,
       not exists( select * from issue where issue.beacon_data_id = beacon_data.id and issue.solution_id is null)
           and trusted_updated_at is not null and now() - trusted_updated_at < '1 year'::interval
           and battery_level is not null and battery_level >= 5 as online
from beacon_data;
