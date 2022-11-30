INSERT INTO alerts_speeding_drivers
SELECT windowEnd, driverAvgSpeed.driverId, driverAvgSpeed.driverName, driverAvgSpeed.route, driverAvgSpeed.driverAvgSpeed
FROM
(
  SELECT TUMBLE_END(geo_events.eventTimestamp, INTERVAL '3' MINUTE) as windowEnd,
       geo_events.driverId,geo_events.driverName,geo_events.route,
       avg(speed_events.speed) as driverAvgSpeed
  FROM
    geo_events_json as geo_events,
    speed_events_json as speed_events
  where
    geo_events.driverId = speed_events.driverId AND
    geo_events.eventTimestamp BETWEEN 
        speed_events.eventTimestamp - INTERVAL '1' SECOND AND 
        speed_events.eventTimestamp + INTERVAL '1' SECOND
  GROUP BY
    TUMBLE(geo_events.eventTimestamp, INTERVAL '3' MINUTE),
    geo_events.driverId,
    geo_events.driverName,
    geo_events.route
) driverAvgSpeed
WHERE
driverAvgSpeed.driverAvgSpeed > 80
