package cloudera.cdf.csp.flink.refapp.trucking.sink;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSink<T> implements SinkFunction<T> {

	private static final long serialVersionUID = 2514064095582758962L;
	private static final Logger LOG = LoggerFactory.getLogger(LogSink.class);

    @Override
    public void invoke(T value, Context context) throws Exception {
        if (value != null) {
            LOG.info(value.toString());
        }
    }
}