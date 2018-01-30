package backend;

import java.util.ArrayList;

public class Mediator {
	ExperimentRunner runner;
	MatlabConnector connector;
		
	public Mediator(){
		connector = new MatlabConnector();
		connector.connect();
	}
	
	public void disconnectMatlab(){
		connector.disconnect();
	}
	
	public double[] runOptimization(int swarmsize, int maxiterations, int sampleSize){
		Optimizer opt = new Optimizer(connector);
		//For now, return random values, later on, this will plug-in with Eren's PSO
		double[] optimalPoint = opt.runOptimization(connector, swarmsize, maxiterations, sampleSize);
		//System.out.println("Optimal point: (" + optimalPoint[0] + ", " + optimalPoint[1] + ")");
		return optimalPoint;
	}
	
	public ArrayList<double[]> runDOE(double[] centerPoint, double percentage, int replicates, int factorLevels, String design){
		//Run both models using the values of the experimental design matrix
		runner = new ExperimentRunner(centerPoint, percentage, replicates, factorLevels, design);
		ArrayList<double[]> outcomeFirstModel = runner.runExperiment(0, connector);
		ArrayList<double[]> outcomeSecondModel = runner.runExperiment(1, connector);
		//connector.disconnect();
		//Analyze the outcome
		StatisticalAnalyzer analyzer = new StatisticalAnalyzer();
		//Store the corresponding p-values to return
		ArrayList<double[]> pvalues = new ArrayList<double[]>();
		//Perform an Anderson-Darling test
		double[] adpvalue = analyzer.AndersonDarlingTest(outcomeFirstModel, outcomeSecondModel, replicates);
		//Perform a Kolmogorov-Smirnov test
		double[] kspvalue = analyzer.KolmogorovSmirnovTest(outcomeFirstModel, outcomeSecondModel, replicates);
		//Perform a Chi-squared test
		//double[] cspvalue = analyzer.ChiSquaredTest(outcomeFirstModel, outcomeSecondModel, replicates);
		pvalues.add(adpvalue);
		pvalues.add(kspvalue);
		//pvalues.add(cspvalue);
		
		return pvalues;
	}
	
	public ArrayList<double[]> runDOE(int factorlevels, int replicates){
		runner = new ExperimentRunner(factorlevels, replicates);
		//Run both models using the values of the experimental design matrix
		ArrayList<double[]> outcomeFirstModel = runner.runExperiment(0, connector);
		ArrayList<double[]> outcomeSecondModel = runner.runExperiment(1, connector);
		//connector.disconnect();
		//Analyze the outcome
		StatisticalAnalyzer analyzer = new StatisticalAnalyzer();
		//Store the corresponding p-values to return
		ArrayList<double[]> pvalues = new ArrayList<double[]>();
		//Perform an Anderson-Darling test
		double[] adpvalue = analyzer.AndersonDarlingTest(outcomeFirstModel, outcomeSecondModel, replicates);
		//Perform a Kolmogorov-Smirnov test
		double[] kspvalue = analyzer.KolmogorovSmirnovTest(outcomeFirstModel, outcomeSecondModel, replicates);
		//Perform a Chi-squared test
		//double[] cspvalue = analyzer.ChiSquaredTest(outcomeFirstModel, outcomeSecondModel, replicates);
		pvalues.add(adpvalue);
		pvalues.add(kspvalue);
		//pvalues.add(cspvalue);
		
		return pvalues;
	}
	
	public String[][] getDesignMatrix(){
		return runner.getExperimentMatrix();
	}
}
