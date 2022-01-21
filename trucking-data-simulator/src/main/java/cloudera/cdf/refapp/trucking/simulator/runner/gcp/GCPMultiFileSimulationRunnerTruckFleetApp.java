package cloudera.cdf.refapp.trucking.simulator.runner.gcp;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import cloudera.cdf.refapp.trucking.simulator.domain.SecurityType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.TruckConfiguration;
import cloudera.cdf.refapp.trucking.simulator.messages.StartSimulation;
import cloudera.cdf.refapp.trucking.simulator.runner.SimulationMaster;
import cloudera.cdf.refapp.trucking.simulator.runner.SimulatorListener;

public class GCPMultiFileSimulationRunnerTruckFleetApp {
	public static void main(String[] args) {
			try {
				
				final int numberOfEvents = Integer.parseInt(args[0]);	
				final Class eventEmitterClass = Class.forName(args[1]);
				final Class eventCollectorClass = Class.forName(args[2]);
				final long demoId = Long.parseLong(args[3]);				
				String routesDirectory = args[4];
				final int delayBetweenEvents = Integer.valueOf(args[5]);
				
				String eventSourceString = args[6];
				EventSourceType eventSource = EventSourceType.valueOf(eventSourceString);
				
				String bucketKey = args[7];
				
				final int numOfEventsPerFile = Integer.valueOf(args[8]);
				
				final String s3BucketName = args[9];
				
				
				
							
				TruckConfiguration.initialize(routesDirectory);
				
				/* Setting this all trucks avaialble */
				final int numberOfEventEmitters=TruckConfiguration.freeRoutePool.size();
		

				ActorSystem system = ActorSystem.create("EventSimulator");
				
				final ActorRef listener = system.actorOf(
						Props.create(SimulatorListener.class), "listener");
				final ActorRef eventCollector = system.actorOf(
						Props.create(eventCollectorClass, bucketKey, eventSource, numOfEventsPerFile, s3BucketName), "eventCollector");
				System.out.println(eventCollector.path());
				
				
				final ActorRef master = system.actorOf(new Props(
						new UntypedActorFactory() {
							public UntypedActor create() {
								return new SimulationMaster(
										numberOfEventEmitters,
										eventEmitterClass, listener, numberOfEvents, demoId, delayBetweenEvents);
							}
						}), "master");
				
				master.tell(new StartSimulation(), master);
			} catch (NumberFormatException e) {
				System.err.println("Invalid number of emitters: "
						+ e.getMessage());
			} catch (ClassNotFoundException e) {
				System.err.println("Cannot find classname: " + e.getMessage());
			}
		
	}
}
