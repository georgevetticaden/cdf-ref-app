CREATE TABLE `ssb`.`ssb_default`.`geo_events_json` (
  `eventTime` VARCHAR(2147483647),
  `eventTimeLong` BIGINT,
  `eventSource` VARCHAR(2147483647),
  `truckId` BIGINT,
  `driverId` BIGINT,
  `driverName` VARCHAR(2147483647),
  `routeId` BIGINT,
  `route` VARCHAR(2147483647),
  `eventType` VARCHAR(2147483647),
  `latitude` DOUBLE,
  `longitude` DOUBLE,
  `correlationId` BIGINT,
  `geoAddress` VARCHAR(2147483647),
  `eventTimestamp` TIMESTAMP(3) METADATA FROM 'timestamp',
  WATERMARK FOR `eventTimestamp` AS `eventTimestamp` - INTERVAL '3' SECOND
) COMMENT 'geo_events_json'
WITH (
  'properties.bootstrap.servers' = '<REPLACE>',
  'properties.sasl.jaas.config' = 'org.apache.kafka.common.security.plain.PlainLoginModule required username="<<REPLACE>>" password="<<REPLACE>>";',
  'properties.auto.offset.reset' = 'earliest',
  'connector' = 'kafka',
  'properties.request.timeout.ms' = '120000',
  'properties.transaction.timeout.ms' = '900000',
  'properties.sasl.mechanism' = 'PLAIN',
  'format' = 'json',
  'topic' = 'syndicate-geo-event-json',
  'properties.security.protocol' = 'SASL_SSL',
  'scan.startup.mode' = 'earliest-offset'
)



