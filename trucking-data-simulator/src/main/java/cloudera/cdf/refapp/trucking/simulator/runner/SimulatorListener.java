package cloudera.cdf.refapp.trucking.simulator.runner;

import akka.actor.UntypedActor;


public class SimulatorListener extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof SimulationResultsSummary)
			System.out.println(message.toString());
//		getContext().system().shutdown();
	}
}
