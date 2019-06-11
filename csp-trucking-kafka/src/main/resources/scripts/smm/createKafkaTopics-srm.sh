#!/bin/bash
if [ $# -ne 1 ]; then
        echo "Please pass ZK:Port to run as the first argument."
else
	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-west-raw-sensors;

	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-central-raw-sensors;

	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-east-raw-sensors;
	
	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-europe-raw-sensors;	

	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-geo-event-avro;

	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-speed-event-avro;

	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-geo-event-json;

	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-speed-event-json;


	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-oil;	
	
	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-battery;
	
	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-transmission;
	
	/usr/bin/kafka-topics \
	--create \
	--zookeeper $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-all-geo-critical-events;
		
    
     /usr/bin/kafka-topics \
    --create \
    --zookeeper $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic gateway-geo-raw-sensor;    
    
     /usr/bin/kafka-topics \
    --create \
    --zookeeper $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic gateway-speed-raw-sensor;      
       	
				
fi