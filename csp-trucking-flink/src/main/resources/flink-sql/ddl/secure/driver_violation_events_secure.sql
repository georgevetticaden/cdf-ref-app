CREATE TABLE flink_driver_violation_events (
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
  speed    INT
) WITH (
    'connector.type'         = 'kafka',
    'connector.version'      = 'universal',
    'connector.topic'        = 'driver-violation-events',
    'connector.properties.bootstrap.servers' = 'messaging-cluster-1-broker0.gvettica.xcu2-8y8x.dev.cldr.work:9093,messaging-cluster-1-broker1.gvettica.xcu2-8y8x.dev.cldr.work:9093,messaging-cluster-1-broker2.gvettica.xcu2-8y8x.dev.cldr.work:9093',
    'connector.properties.client.id' = 'flink-sql-truck-alerts-producer',
    'connector.properties.security.protocol'            = 'SASL_SSL',
    'connector.properties.sasl.kerberos.service.name'   = 'kafka',
    'connector.properties.ssl.truststore.location'      = '/tmp/gvetticaden_dfx.jks',      
    'format.type' = 'json'
);
