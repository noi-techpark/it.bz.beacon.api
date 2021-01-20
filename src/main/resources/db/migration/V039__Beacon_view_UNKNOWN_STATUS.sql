alter table beacon_data add column flag_api_accessible boolean not null default false;

create or replace view beacon as
with
   raw_beacon_data as (
      select id,
             manufacturer_id,
             manufacturer,
             name,
             description,
             lat_beacon as lat,
             lng_beacon as lng,
             lat_poi as info_lat,
             lng_poi as info_lng,
             location_type,
             location_description,
             to_timestamp((remote_beacon ->> 'lastSeen')::bigint)::timestamp as last_seen,
             trusted_updated_at,
             coalesce(battery_level, (remote_beacon ->> 'batteryLevel')::integer) as battery_level,
             (remote_beacon ->> 'iBeacon')::boolean as i_beacon,
             (remote_beacon ->> 'telemetry')::boolean as telemetry,
             (remote_beacon ->> 'eddystoneUid')::boolean as eddystone_uid,
             (remote_beacon ->> 'eddystoneUrl')::boolean as eddystone_url,
             (remote_beacon ->> 'eddystoneTlm')::boolean as eddystone_tlm,
             (remote_beacon ->> 'eddystoneEid')::boolean as eddystone_eid,
             (remote_beacon ->> 'eddystoneEtlm')::boolean as eddystone_etlm,
             (remote_beacon ->> 'uuid')::uuid as uuid,
             (remote_beacon ->> 'major')::integer as major,
             (remote_beacon ->> 'minor')::integer as minor,
             (remote_beacon ->> 'url')::text as url,
             (remote_beacon ->> 'namespace')::text as namespace,
             (remote_beacon ->> 'instanceId')::text as instance_id,
             (remote_beacon ->> 'interval')::integer as interval,
             (remote_beacon ->> 'txPower')::integer as tx_power,
             (remote_beacon ->> 'pendingConfiguration')::json as pending_configuration,
             (remote_beacon ->> 'name')::text as internal_name,
             group_id,
             flag_api_accessible
      from beacon_data
   )
select id,
       manufacturer_id,
       manufacturer,
       name,
       description,
       lat,
       lng,
       info_lat,
       info_lng,
       location_type,
       location_description,
       last_seen,
       trusted_updated_at,
       battery_level,
       i_beacon,
       telemetry,
       eddystone_uid,
       eddystone_url,
       eddystone_tlm,
       eddystone_eid,
       eddystone_etlm,
       uuid,
       major,
       minor,
       url,
       namespace,
       instance_id,
       interval,
       tx_power,
       pending_configuration,
       internal_name,
       group_id,
       case
          when not flag_api_accessible then 'NOT_ACCESSIBLE'
          when pending_configuration is not null then 'CONFIGURATION_PENDING'
          when now() - last_seen > '1 year'::interval then 'UNKNOWN_STATUS'
          when exists(select * from issue where beacon_data_id = raw_beacon_data.id and solution_id is null) then 'ISSUE'
          when battery_level is not null and battery_level < 5 then 'BATTERY_LOW'
          else 'OK' end as status
from raw_beacon_data;