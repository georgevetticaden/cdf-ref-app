#!/bin/bash
if [ $# -ne 1 ]; then
        echo "Please pass Broker:Port to run as the first argument."
else
	
	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 3 \
	--partitions 10 \
	--topic gateway-central-raw-sensors-dfx;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 3 \
	--partitions 10 \
	--topic syndicate-geo-event-json-dfx;

	/usr/bin/kafka-topics \
	--create \
	--command-config client.properties \
	--bootstrap-server $1 \
	--replication-factor 3 \
	--partitions 10 \
	--topic syndicate-speed-event-json-dfx;

	
       	    
       	
				
fi