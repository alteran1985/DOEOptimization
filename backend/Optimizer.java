package backend;
import net.sourceforge.jswarm_pso.*;

public class Optimizer {
	MatlabConnector connector;
	
	public Optimizer(MatlabConnector matlabRunner){
		connector = matlabRunner;
	}
	
	public double[] runOptimization(MatlabConnector connector, int swarmsize, int maxiterations, int sampleSize){
		double[] optimalPoint;
		//Create and unleash the swarm
		Swarm swarm = new Swarm(swarmsize, new MyParticle(), new MyFitness(sampleSize));
		swarm.setMaxPosition(10);
		swarm.setMinPosition(0);
		//Optimize a few times
		for(int i = 0; i < maxiterations; i++ ) swarm.evolve();
		//Get the best position found
		optimalPoint = swarm.getBestPosition();
		
		return optimalPoint;
	}
	
	public class MyFitness extends FitnessFunction{
		int sample;
		public MyFitness(int sampleSize){
			sample = sampleSize;
		}
		public double evaluate(double[] position){
			double fitness = 0;
			double distanceModel1 = 0;
			double distanceModel2 = 0;
			
			//We run each model 5 times to get an expected value
			for(int i=0;i<sample;i++){
				distanceModel1 += connector.runSimulation(position[0], position[1], 5.5, 0);
				distanceModel2 += connector.runSimulation(position[0], position[1], 5.5, 1);
			}
			fitness = Math.abs((distanceModel1-distanceModel2)/sample);
			
			return fitness;
		}
	}
}
