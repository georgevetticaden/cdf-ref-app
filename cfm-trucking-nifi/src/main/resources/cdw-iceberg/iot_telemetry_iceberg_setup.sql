create database iot_telemetry;

CREATE  TABLE iot_telemetry.geo_location_telemetry
(eventtime string, eventtimelong bigint, eventsource string, truckid int, drivername string, 
routeid int, route string, eventtype string, 
latitude double, longitude double, correlationid bigint, 
geoaddress string
)
PARTITIONED BY (driverId int)
STORED BY ICEBERG;