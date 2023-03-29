create table truck_geo_events_2 
(eventTime string, eventTimeLong bigint , eventSource string, truckId int, 
driverId int, driverName string, 
routeId int, route string, eventType string, 
latitude double, longitude double, correlationId int,
primary key (eventTimeLong)
)