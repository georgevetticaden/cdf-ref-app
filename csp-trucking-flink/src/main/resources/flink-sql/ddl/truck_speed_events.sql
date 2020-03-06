CREATE TABLE truck_speed_events (
  eventTime	STRING,
  eventTimeLong BIGINT,
  eventSource    STRING,
  truckId    INT,
  driverId    INT,
  driverName STRING,
  routeId 	 INT,
  route		 STRING,
  speed	INT,
  event_time AS CAST(from_unixtime(floor(eventTimeLong/1000)) AS TIMESTAMP(3)),
  WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND    
) WITH (
	'connector.type'    	 = 'kafka',
	'connector.version' 	 = 'universal',
	'connector.topic'   	 = 'syndicate-speed-event-json',
	'connector.startup-mode' = 'latest-offset',
	'connector.properties.bootstrap.servers' = 'XXX',
    'connector.properties.group.id' = 'flink-sql-truck-speed-consumer',
	'format.type' = 'json'
);