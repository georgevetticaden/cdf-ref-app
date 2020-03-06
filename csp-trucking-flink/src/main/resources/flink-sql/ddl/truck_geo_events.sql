CREATE TABLE truck_geo_events (
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
    'connector.properties.bootstrap.servers' = 'ec2-18-222-24-163.us-east-2.compute.amazonaws.com:9092,ec2-3-136-85-138.us-east-2.compute.amazonaws.com:9092,ec2-18-218-162-215.us-east-2.compute.amazonaws.com:9092',
    'connector.properties.group.id' = 'flink-sql-truck-geo-consumer',
    'connector.properties.zookeeper.connect' = 'XXX',
    'format.type' = 'json'
);