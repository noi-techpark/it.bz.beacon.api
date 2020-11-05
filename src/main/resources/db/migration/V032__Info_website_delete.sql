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
       ( select count(*)::integer as count
         from issue
         where issue.beacon_data_id::text = beacon_data.id::text and issue.solution_id is null) as open_issue_count
from beacon_data;

ALTER TABLE beacon_data DROP COLUMN website;