/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloudera.cdf.csp.flink.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudera.cdf.csp.flink.util.Constants.KAFKA_PREFIX;
import static cloudera.cdf.csp.flink.util.Constants.K_KEYSTORE_PASSWORD;
import static cloudera.cdf.csp.flink.util.Constants.K_PROPERTIES_FILE;
import static cloudera.cdf.csp.flink.util.Constants.K_SCHEMA_REG_SSL_CLIENT_KEY;
import static cloudera.cdf.csp.flink.util.Constants.K_SCHEMA_REG_URL;
import static cloudera.cdf.csp.flink.util.Constants.K_TRUSTSTORE_PASSWORD;
import static cloudera.cdf.csp.flink.util.Constants.K_TRUSTSTORE_PATH;

public class Utils {

	private static Logger LOG = LoggerFactory.getLogger(Utils.class);

	public static ParameterTool parseArgs(String[] args) throws IOException {

		// Processing job properties
		ParameterTool params = ParameterTool.fromArgs(args);
		if (params.has(K_PROPERTIES_FILE)) {
			params = ParameterTool.fromPropertiesFile(params.getRequired(K_PROPERTIES_FILE)).mergeWith(params);
		}

		LOG.info("### Job parameters:");
		for (String key : params.getProperties().stringPropertyNames()) {
			LOG.info("Job Param: {}={}", key, params.get(key));
		}

		return params;
	}

	public static Properties readKafkaProperties(ParameterTool params) {
		Properties properties = new Properties();
		for (String key : params.getProperties().stringPropertyNames()) {
			if (key.startsWith(KAFKA_PREFIX)) {
				properties.setProperty(key.substring(KAFKA_PREFIX.length()), params.get(key));
			}
		}

		LOG.info("### Kafka parameters:");
		for (String key : properties.stringPropertyNames()) {
			LOG.info("Kafka param: {}={}", key, properties.get(key));
		}
		return properties;
	}

	public static Map<String, Object> readSchemaRegistryProperties(ParameterTool params) {
		

		//See if there are parameters to talk to secure SR
		Map<String, String> sslClientConfig = null;
		if(StringUtils.isNotEmpty(params.get(K_SCHEMA_REG_SSL_CLIENT_KEY + "." + K_TRUSTSTORE_PATH))) {
			sslClientConfig = new HashMap<String, String>();
			sslClientConfig.put(K_TRUSTSTORE_PATH, params.get(K_SCHEMA_REG_SSL_CLIENT_KEY + "." + K_TRUSTSTORE_PATH));
			//Don't think we need password
			//sslClientConfig.put(K_TRUSTSTORE_PASSWORD, params.get(K_SCHEMA_REG_SSL_CLIENT_KEY + "." + K_TRUSTSTORE_PASSWORD));
		}

		//Setting up schema registry client
		Map<String, Object> schemaRegistryConf = new HashMap<>();
		schemaRegistryConf.put(K_SCHEMA_REG_URL, params.get(K_SCHEMA_REG_URL));
		if(sslClientConfig != null)
			schemaRegistryConf.put(K_SCHEMA_REG_SSL_CLIENT_KEY, sslClientConfig);

		LOG.info("### Schema Registry parameters:");
		for (String key : schemaRegistryConf.keySet()) {
			LOG.info("Schema Registry param: {}={}", key, schemaRegistryConf.get(key));
		}
		return schemaRegistryConf;
	}
	
//	public static Map<String, Object> readSchemaRegistryPropertiesNonSecure(ParameterTool params) {
//
//
//		Map<String, Object> schemaRegistryConf = new HashMap<>();
//		schemaRegistryConf.put(K_SCHEMA_REG_URL, params.getRequired(K_SCHEMA_REG_URL));
//		LOG.info("### Schema Registry parameters:");
//		for (String key : schemaRegistryConf.keySet()) {
//			LOG.info("Schema Registry param: {}={}", key, schemaRegistryConf.get(key));
//		}
//		return schemaRegistryConf;
//	}	
	
	

}
