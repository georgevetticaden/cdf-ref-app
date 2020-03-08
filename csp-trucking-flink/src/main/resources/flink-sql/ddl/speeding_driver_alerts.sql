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
    'connector.properties.bootstrap.servers' = 'XXX',
    'format.type' = 'json'
);