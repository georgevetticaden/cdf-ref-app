CREATE TABLE speeding_driver_alerts (
    alert_time    TIMESTAMP(3),
    driverId INT,
    driverName STRING,
    route    STRING,
    speed    INT
) WITH (
    'connector.type'         = 'kafka',
    'connector.version'      = 'universal',
    'connector.topic'        = 'alerts-speeding-drivers',
    'connector.properties.bootstrap.servers' = 'kafka-broker-1::9093',
    'connector.properties.client.id' = 'flink-sql-truck-alerts-producer',
    'connector.properties.security.protocol'            = 'SASL_SSL',
    'connector.properties.sasl.kerberos.service.name'   = 'kafka',
    'connector.properties.ssl.truststore.location'      = '/var/lib/cloudera-scm-agent/agent-cert/cm-auto-global_truststore.jks',      
    'format.type' = 'json'
);