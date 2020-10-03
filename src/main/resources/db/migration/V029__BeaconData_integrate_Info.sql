ALTER TABLE beacon_data RENAME lat TO lat_beacon;
ALTER TABLE beacon_data RENAME lng TO lng_beacon;

ALTER TABLE beacon_data ADD COLUMN address text;
ALTER TABLE beacon_data ADD COLUMN cap character varying(255);
ALTER TABLE beacon_data ADD COLUMN location text;
ALTER TABLE beacon_data ADD COLUMN lat_poi double precision;
ALTER TABLE beacon_data ADD COLUMN lng_poi double precision;
ALTER TABLE beacon_data ADD COLUMN floor character varying(255);
ALTER TABLE beacon_data ADD COLUMN instance_id character varying(255);
ALTER TABLE beacon_data ADD COLUMN major integer;
ALTER TABLE beacon_data ADD COLUMN minor integer;
ALTER TABLE beacon_data ADD COLUMN name_poi text;
ALTER TABLE beacon_data ADD COLUMN namespace character varying(255);
ALTER TABLE beacon_data ADD COLUMN uuid uuid;
ALTER TABLE beacon_data ADD COLUMN website text;

update beacon_data
set address = info.address,
    cap = info.cap,
    location = info.location,
    lat_poi = info.latitude,
    lng_poi = info.longitude,
    floor = info.floor,
    instance_id = info.instance_id,
    major = info.major,
    minor = info.minor,
    name_poi = info.name,
    namespace = info.namespace,
    uuid = info.uuid,
    website = info.website
from info
where beacon_data.id = info.id;