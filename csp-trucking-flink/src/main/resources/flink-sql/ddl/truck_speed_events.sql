CREATE TABLE truck_speed_events (
  eventTime	STRING,
  eventTimeLong BIGINT,
  eventSource    STRING,
  truckId    INT,
  driverId    INT,
  driverName STRING,
  routeId 	 INT,
  route		 STRING,
  speed	INT 
) WITH (
	'connector.type'    	 = 'kafka',
	'connector.version' 	 = 'universal',
	'connector.topic'   	 = 'syndicate-speed-event-json',
	'connector.startup-mode' = 'latest-offset',
	'connector.properties.bootstrap.servers' = 'XXX',
    'connector.properties.group.id' = 'flink-sql-truck-speed-consumer',
	'format.type' = 'json'
);