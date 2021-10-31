CREATE TABLE `ssb`.`ssb_default`.`alerts_speeding` (
  `alert_time` TIMESTAMP(3),
  `driverId` BIGINT,
  `driverName` VARCHAR(2147483647),
  `route` VARCHAR(2147483647),
  `speed` BIGINT,
  WATERMARK FOR `alert_time` AS `alert_time` - INTERVAL '3' SECOND
) COMMENT 'speed_events_json'
WITH (
  'properties.bootstrap.servers' = '<<REPLACE>>',
  'properties.sasl.jaas.config' = 'org.apache.kafka.common.security.plain.PlainLoginModule required username="<<REPLACE>>" password="<<REPLACE>>";',
  'properties.auto.offset.reset' = 'earliest',
  'connector' = 'kafka',
  'properties.request.timeout.ms' = '120000',
  'properties.transaction.timeout.ms' = '900000',
  'properties.sasl.mechanism' = 'PLAIN',
  'format' = 'json',
  'topic' = 'alerts-speeding-drivers',
  'properties.security.protocol' = 'SASL_SSL',
  'scan.startup.mode' = 'earliest-offset'
);