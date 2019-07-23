package cloudera.cdf.csp.flink.examples;

import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikipediaAnalysis {

	private static final Logger LOG = LoggerFactory.getLogger(WikipediaAnalysis.class);
	
    public static void main(String[] args) throws Exception {

    	LOG.info("starting wiki job..");
    	StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
    	
    	DataStream<WikipediaEditEvent> edits = see.addSource(new WikipediaEditsSource());

    	KeyedStream<WikipediaEditEvent, String> keyedEdits = edits
    		    .keyBy(new KeySelector<WikipediaEditEvent, String>() {
    		        @Override
    		        public String getKey(WikipediaEditEvent event) {
    		            return event.getUser();
    		        }
    		    });    	
    	
    	DataStream<Tuple2<String, Long>> result = keyedEdits
    		    .timeWindow(Time.seconds(5))
    		    .fold(new Tuple2<>("", 0L), new FoldFunction<WikipediaEditEvent, Tuple2<String, Long>>() {
    		        @Override
    		        public Tuple2<String, Long> fold(Tuple2<String, Long> acc, WikipediaEditEvent event) {
    		            acc.f0 = event.getUser();
    		            acc.f1 += event.getByteDiff();
    		            LOG.info("New summ for user["+acc.f0 + "] is: " + acc.f1 + "]");
    		            return acc;
    		        }
    		    });
    	
    	result.print();

    	see.execute();    	
    	LOG.info("finished executing..");

    }
    
}
