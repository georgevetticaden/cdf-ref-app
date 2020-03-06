SELECT geo_events.eventTime, geo_events.event_time, 'joined_stream' as eventSource,  
	   geo_events.truckId , geo_events.driverId , geo_events.driverName, 
	   speed_events.speed, geo_events.routeId, geo_events.route,
	   geo_events.eventType , geo_events.latitude , geo_events.longitude, 
	   geo_events.correlationId, geo_events.geoAddress
FROM
	truck_geo_events as geo_events,
	truck_speed_events as speed_events
where
	geo_events.driverId = speed_events.driverId AND
	geo_events.event_time BETWEEN 
		speed_events.event_time - INTERVAL '1' SECOND AND 
		speed_events.event_time + INTERVAL '1' SECOND;