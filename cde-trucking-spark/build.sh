#!/bin/bash

mvn clean package -DskipTests=true
cp target/cdf-ref-app.jar /Users/gvetticaden/Dropbox/Hortonworks/Development/spark/spark-3.1.1-bin-hadoop3.2/examples/jars


