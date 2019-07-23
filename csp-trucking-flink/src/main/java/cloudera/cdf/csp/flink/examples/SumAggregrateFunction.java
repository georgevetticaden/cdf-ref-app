package cloudera.cdf.csp.flink.examples;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SumAggregrateFunction implements AggregateFunction<WikipediaEditEvent, Tuple2<String, Long>, Tuple2<String, Long>> {


	private static final long serialVersionUID = -5097499857961303781L;
	private static final Logger LOG = LoggerFactory.getLogger(SumAggregrateFunction.class);

	@Override
	public Tuple2<String, Long> createAccumulator() {
		return new Tuple2<>("", 0L);
	}

	@Override
	public Tuple2<String, Long> add(WikipediaEditEvent value,
			Tuple2<String, Long> accumulator) {
		accumulator.f0 = value.getUser();
		accumulator.f1 = accumulator.f1 + value.getByteDiff();
        LOG.info("New sum for user["+accumulator.f0 + "] is: " + accumulator.f1 + "]");
		return accumulator;
	}

	@Override
	public Tuple2<String, Long> getResult(Tuple2<String, Long> accumulator) {
		return accumulator;
	}

	@Override
	public Tuple2<String, Long> merge(Tuple2<String, Long> a,
			Tuple2<String, Long> b) {
		a.f1 = a.f1 + b.f1;
		return a;
	}


}
