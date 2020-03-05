CREATE TABLE truck_geo_events (
  eventTime	STRING,
  eventTimeLong BIGINT,
  eventSource    STRING,
  truckId    INT,
  driverId    INT,
  driverName STRING,
  routeId 	 INT,
  route		 STRING,
  eventType  STRING,
  latitude	DOUBLE,
  longitude DOUBLE,
  correlationId	INT,
  geoAddress STRING
) WITH (
	'connector.type'    	 = 'kafka',
	'connector.version' 	 = 'universal',
	'connector.topic'   	 = 'syndicate-geo-event-json',
	'connector.startup-mode' = 'latest-offset',
	'connector.properties.bootstrap.servers' = 'XXX',
    'connector.properties.group.id' = 'flink-sql-truck-geo-consumer',
	'format.type' = 'json'
);