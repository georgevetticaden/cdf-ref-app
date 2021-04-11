#!/bin/bash
if [ $# -ne 1 ]; then
        echo "Please pass Broker:Port to run as the first argument."
else
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-west-raw-sensors;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-central-raw-sensors;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-east-raw-sensors;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-europe-raw-sensors;	

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-geo-event-avro;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-speed-event-avro;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-geo-event-json;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-speed-event-json;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic alerts-speeding-drivers;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-oil;	
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-battery;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-transmission;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic syndicate-all-geo-critical-events;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic fleet-supply-chain;	
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic route-planning ;	
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic load-optimization ;		
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic fuel-logistics ;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic supply-chain ;	
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic predictive-alerts ;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic energy-mgmt ;	
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic audit-events ;		
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic compliance ;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic adjudication ;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic approval ;	
	
	/usr/bin/kafka-topics \
    --create \
    --command-config client.properties \
    --bootstrap-server $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic driver-violation-events;	
    
     /usr/bin/kafka-topics \
    --create \
    --command-config client.properties \
    --bootstrap-server $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic driver-average-speed;
    
     /usr/bin/kafka-topics \
    --create \
    --command-config client.properties \
    --bootstrap-server $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic gateway-geo-raw-sensor;    
    
     /usr/bin/kafka-topics \
    --create \
    --command-config client.properties \
    --bootstrap-server $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic gateway-speed-raw-sensor;      
    
     /usr/bin/kafka-topics \
    --create \
    --command-config client.properties \
    --bootstrap-server $1 \
    --replication-factor 2 \
    --partitions 3 \
    --topic speed-events-filtered;         	 
    
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-west-raw-sensors-json;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-central-raw-sensors-json;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-east-raw-sensors-json;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic gateway-europe-raw-sensors-json;	
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic emea-pos-sales-events;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic 2. us-pos-sales-events;
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 2 \
	--partitions 3 \
	--topic apac-pos-sales-events;	       
       	       
       	
				
fi