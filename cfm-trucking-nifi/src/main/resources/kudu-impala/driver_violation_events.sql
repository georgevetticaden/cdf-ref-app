CREATE TABLE truck_fleet_db.kudu_driver_violation_events
(  
  eventTimeLong timestamp,
  driverId    INT,
  eventTime STRING,
  eventSource    STRING,
  truckId    INT,
  driverName STRING,
  routeId    INT,
  route      STRING,
  eventType  STRING,
  latitude  DOUBLE,
  longitude DOUBLE,
  correlationId INT,
  geoAddress STRING,
  speed    INT,
  primary key (eventTimeLong)
)
PARTITION BY HASH PARTITIONS 4
STORED AS KUDU
TBLPROPERTIES ('kudu.num_tablet_replicas' = '1');