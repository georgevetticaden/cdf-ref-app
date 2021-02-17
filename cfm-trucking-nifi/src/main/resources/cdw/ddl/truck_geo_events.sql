create database truck_fleet_db;

CREATE TABLE truck_fleet_db.hive_truck_geo_events
(eventTimeLong timestamp, eventSource string, truckId int, 
driverId int, driverName string, 
routeId int, route string, eventType string, 
latitude double, longitude double, correlationId int, 
geoAddress string
)
CLUSTERED BY (driverId) INTO 5 BUCKETS
STORED AS ORC
TBLPROPERTIES(
'transactional'='true'
);