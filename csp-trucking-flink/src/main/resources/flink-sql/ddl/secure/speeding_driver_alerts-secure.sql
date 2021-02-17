CREATE TABLE flink_speeding_driver_alerts (
    alertTimeLong BIGINT,
    alertTime timestamp(3),
    driverId INT,
    driverName STRING,
    route    STRING,
    speed    INT
) WITH (
    'connector.type'         = 'kafka',
    'connector.version'      = 'universal',
    'connector.topic'        = 'alerts-speeding-drivers',
    'connector.properties.bootstrap.servers' = 'messaging-cluster-1-broker0.gvettica.xcu2-8y8x.dev.cldr.work:9093,messaging-cluster-1-broker1.gvettica.xcu2-8y8x.dev.cldr.work:9093,messaging-cluster-1-broker2.gvettica.xcu2-8y8x.dev.cldr.work:9093',
    'connector.properties.client.id' = 'flink-sql-truck-alerts-producer',
    'connector.properties.security.protocol'            = 'SASL_SSL',
    'connector.properties.sasl.kerberos.service.name'   = 'kafka',
    'connector.properties.ssl.truststore.location'      = '/tmp/gvetticaden_dfx.jks',      
    'format.type' = 'json'
);
