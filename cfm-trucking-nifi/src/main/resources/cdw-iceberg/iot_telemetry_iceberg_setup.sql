create database iot_telemetry;

CREATE  TABLE iot_telemetry.geo_location_telemetry
(eventtime string, eventtimelong bigint, eventsource string, truckid int, drivername string, 
routeid int, route string, eventtype string, 
latitude double, longitude double, correlationid bigint
)
PARTITIONED BY (driverId int)
STORED BY ICEBERG;


describe extended iot_telemetry.geo_location_telemetry;

select * from iot_telemetry.geo_location_telemetry where eventtype != 'Normal'

select count(*) from iot_telemetry.geo_location_telemetry;


drop table iot_telemetry.geo_location_telemetry;