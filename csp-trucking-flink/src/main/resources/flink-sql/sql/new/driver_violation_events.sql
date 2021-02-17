INSERT INTO flink_driver_violation_events 
  SELECT 
       geo_events.eventTime, geo_events.eventTimeLong, geo_events.eventSource,
       geo_events.truckId, geo_events.driverId, geo_events.driverName , geo_events.routeId, 
       geo_events.route, geo_events.eventType,
       geo_events.latitude , geo_events.longitude , geo_events.correlationId , geo_events.geoAddress, 
       speed_events.speed 
  FROM
    flink_truck_geo_events as geo_events,
    flink_truck_speed_events as speed_events
  where
    geo_events.driverId = speed_events.driverId AND
    geo_events.event_time BETWEEN 
        speed_events.event_time - INTERVAL '1' SECOND AND 
        speed_events.event_time + INTERVAL '1' SECOND AND
    geo_events.eventType <> 'Normal' ;