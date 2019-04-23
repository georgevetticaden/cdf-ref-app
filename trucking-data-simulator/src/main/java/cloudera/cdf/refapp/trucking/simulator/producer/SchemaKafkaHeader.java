package cloudera.cdf.refapp.trucking.simulator.producer;

import org.apache.kafka.common.header.Header;

public class SchemaKafkaHeader implements Header {

	
	private String schemaKey;
	private String schemaNameValue;

	public SchemaKafkaHeader (String key, String value) {
		this.schemaKey = key;
		this.schemaNameValue = value;	}
	
	@Override
	public String key() {
		return schemaKey;
	}

	@Override
	public byte[] value() {
		return schemaNameValue.getBytes();
	}
	
	@Override
	public String toString() {
		return "Schema Key: " + schemaKey + ", Schema Value: " + schemaNameValue;
	}

}
