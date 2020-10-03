alter table order_data drop constraint fkh35r77fclsn5w8extdmrx7imm;

drop view info_beacon_data;
drop table info;

create or replace view info as
select id as id,
       created_at,
       updated_at,
       address,
       cap,
       lat_poi as latitude,
       location,
       lng_poi as longitude,
       floor,
       instance_id,
       major,
       minor,
       name_poi as name,
       namespace,
       uuid,
       website,
       battery_level,
       trusted_updated_at,
       ( select count(*)::integer as count
         from issue
         where issue.beacon_data_id::text = beacon_data.id::text and issue.solution_id is null) as open_issue_count
from beacon_data;
