CREATE OR REPLACE VIEW public.info_beacon_data AS
 SELECT info.id,
    info.created_at,
    info.updated_at,
    info.address,
    info.cap,
    info.latitude,
    info.location,
    info.longitude,
    info.floor,
    info.instance_id,
    info.major,
    info.minor,
    info.name,
    info.namespace,
    info.uuid,
    info.website,
    beacon_data.battery_level,
    beacon_data.trusted_updated_at,
    ( SELECT count(*)::integer AS count
           FROM issue
          WHERE issue.beacon_data_id::text = beacon_data.id::text) AS open_issue_count
   FROM info
     LEFT JOIN beacon_data ON info.id::text = beacon_data.id::text;
