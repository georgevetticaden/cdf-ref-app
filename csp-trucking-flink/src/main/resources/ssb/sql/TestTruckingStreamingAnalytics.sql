/* Look at data in the speed table */
select * from speed_events_json;

/* Calculate speed of drivers over 3 minute window */
SELECT TUMBLE_END(speed_events.eventTimestamp, INTERVAL '3' MINUTE) as windowEnd,
     speed_events.driverId,speed_events.driverName,speed_events.route,
     avg(speed_events.speed) as driverAvgSpeed
FROM
  speed_events_json as speed_events
GROUP BY
  TUMBLE(speed_events.eventTimestamp, INTERVAL '3' MINUTE),
  speed_events.driverId,
  speed_events.driverName,
  speed_events.route

/* Find speeding drivers */
SELECT windowEnd, driverAvgSpeed.driverId, driverAvgSpeed.driverName, driverAvgSpeed.route, driverAvgSpeed.driverAvgSpeed
FROM
(
  SELECT TUMBLE_END(speed_events.eventTimestamp, INTERVAL '3' MINUTE) as windowEnd,
     speed_events.driverId,speed_events.driverName,speed_events.route,
     avg(speed_events.speed) as driverAvgSpeed
  FROM
  speed_events_json as speed_events
  GROUP BY
  TUMBLE(speed_events.eventTimestamp, INTERVAL '3' MINUTE),
  speed_events.driverId,
  speed_events.driverName,
  speed_events.route
) driverAvgSpeed
WHERE
driverAvgSpeed.driverAvgSpeed > 80