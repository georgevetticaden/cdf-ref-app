CREATE TABLE truck_fleet_db.kudu_speeding_driver_alerts
(  
    alertTimeLong timestamp,
    alertTime string,
    driverId INT,
    driverName STRING,
    route    STRING,
    speed    INT,
    primary key (alertTimeLong)
)
PARTITION BY HASH PARTITIONS 4
STORED AS KUDU
TBLPROPERTIES ('kudu.num_tablet_replicas' = '1');