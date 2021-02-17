CREATE TABLE flink_truck_geo_events (
  eventTime STRING,
  eventTimeLong BIGINT,
  eventSource    STRING,
  truckId    INT,
  driverId    INT,
  driverName STRING,
  routeId    INT,
  route      STRING,
  eventType  STRING,
  latitude  DOUBLE,
  longitude DOUBLE,
  correlationId INT,
  geoAddress STRING,
  event_time AS CAST(from_unixtime(floor(eventTimeLong/1000)) AS TIMESTAMP(3)),
  WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND  
) WITH (
    'connector.type'         = 'kafka',
    'connector.version'      = 'universal',
    'connector.topic'        = 'syndicate-geo-event-json',
    'connector.startup-mode' = 'latest-offset',
    'connector.properties.bootstrap.servers' = 'messaging-cluster-1-broker0.gvettica.xcu2-8y8x.dev.cldr.work:9093,messaging-cluster-1-broker1.gvettica.xcu2-8y8x.dev.cldr.work:9093,messaging-cluster-1-broker2.gvettica.xcu2-8y8x.dev.cldr.work:9093',
    'connector.properties.group.id' = 'flink-sql-truck-geo-consumer',
    'connector.properties.security.protocol'            = 'SASL_SSL',
    'connector.properties.sasl.kerberos.service.name'   = 'kafka',
    'connector.properties.ssl.truststore.location'      = '/tmp/gvetticaden_dfx.jks',    
    'format.type' = 'json'
);s