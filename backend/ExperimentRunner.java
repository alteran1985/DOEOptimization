package backend;

import java.util.ArrayList;
import java.util.HashMap;

public class ExperimentRunner {
	ArrayList<String[]> values;
	String[][] design_matrix;
	
	public ExperimentRunner(double[] centerPoint, double percentage, int replicates, int factorLevels, String design){
		//The experiment design will be centered on the center point, with the edges percentage% from the center point
		//We store the values of each factor in the values array list
		values = new ArrayList<String[]>();
		percentage = percentage/100;
		//For the first factor
		String[] firstFactor = new String[factorLevels];
		double change = centerPoint[0]*percentage;
		double upperLimit = centerPoint[0] + change;
		double lowerLimit = centerPoint[0] - change;
		double step = (upperLimit-lowerLimit)/factorLevels;
		firstFactor[0] = new Double(lowerLimit).toString();
		for(int i=1; i<factorLevels-1; i++){
			firstFactor[i] = new Double(lowerLimit + (step*i)).toString();
		}
		firstFactor[factorLevels-1] = new Double(upperLimit).toString();

		//For the second factor
		String[] secondFactor = new String[factorLevels];
		change = centerPoint[1]*percentage;
		upperLimit = centerPoint[1] + change;
		lowerLimit = centerPoint[1] - change;
		step = (upperLimit-lowerLimit)/factorLevels;
		secondFactor[0] = new Double(lowerLimit).toString();
		for(int i=1; i<factorLevels-1; i++){
			secondFactor[i] = new Double(lowerLimit + (step*i)).toString();
		}
		secondFactor[factorLevels-1] = new Double(upperLimit).toString();		
		
		//Add to the values array list
		values.add(firstFactor);
		values.add(secondFactor);
		//Generate the design matrix
		generateExperimentMatrix(replicates, factorLevels, design);
	}
	
	public ExperimentRunner(int factorlevels, int replicates){
		int numberOfFactorLevelCombinations = (factorlevels * factorlevels) * replicates;
		design_matrix = new String[numberOfFactorLevelCombinations][2];
		//The experiment design will be centered on the center point, with the edges percentage% from the center point
		//We store the values of each factor in the values array list
		//values = new ArrayList<String[]>();
		double minValue = 0;
		double maxValue = 10;
		double increase = (maxValue-minValue)/(factorlevels-1);
		//For the first factor
		String[] firstFactor = new String[factorlevels];
		String[] secondFactor = new String[factorlevels];
		double value = 0;
		for(int i=0; i<factorlevels; i++){
			firstFactor[i] = new Double(value).toString();
			secondFactor[i] = new Double(value).toString();
			value += increase;
		}
		
		for(int i=0;i<firstFactor.length;i++){
			System.out.println("First factor " + (i+1) + ": " + firstFactor[i] + ", Second factor " + (i+1) + ": " + secondFactor[i]);
		}
		
		int currentcombination = 0;
		for(int n=0; n<replicates; n++){
			for(int i=0; i<firstFactor.length; i++){
				for(int j=0; j<secondFactor.length; j++){
					design_matrix[currentcombination][0] = firstFactor[i];
					design_matrix[currentcombination][1] = secondFactor[j];
					currentcombination++;
				}
			}
		}
	}
	
	public ArrayList<double[]> runExperiment(int model, MatlabConnector connector){
		//Array list where we store the outcome of each run
		ArrayList<double[]> outcome = new ArrayList<double[]>();
		//Run through each factor combination in the design matrix and run the model
		for(int i=0;i<design_matrix.length;i++){
			//System.out.print("Running factor level combination " + (i+1));
			//For each factor level combination, we store the values of each factor and the experimental outcome
			double[] experimentRun = {0,0,0};
			String[] row = design_matrix[i];
			double modelOutput = connector.runSimulation(Double.parseDouble(row[0]), Double.parseDouble(row[1]), 5.5, model);
			//Store information in the experiment outcome matrix
			experimentRun[0] = Double.parseDouble(row[0]);
			experimentRun[1] = Double.parseDouble(row[1]);
			experimentRun[2] = modelOutput;
			outcome.add(experimentRun);
			//System.out.println(" Distance traveled: " + modelOutput);
		}
		return outcome;
	}
	
	private void generateExperimentMatrix(int replicates, int factorLevels, String experimentDesign){
		int numberOfFactors = 2;
		int numberOfFactorLevels = factorLevels;
	    DesignGenerator generator = new DesignGenerator(numberOfFactors, numberOfFactorLevels, replicates, experimentDesign);
	 	design_matrix = generator.getDesignMatrixValues(values);
	 	//Print out the matrix
	 	generator.printDesignMatrix();
	}
	
	private void generateExperimentMatrixDOE(int replicates, int factorlevels){
		int numberOfFactors = 2;
		String experimentDesign = "FullFactorial";
		DesignGenerator generator = new DesignGenerator(numberOfFactors, factorlevels, replicates, experimentDesign);
		generator.printEncodedDesignMatrix();
	 	design_matrix = generator.getDesignMatrixValues(values);
	 	generator.printDesignMatrix();
	}
	
	public String[][] getExperimentMatrix(){
		return design_matrix;
	}
	
	public void printDesignMatrix(){
		for(int i=0; i<design_matrix.length;i++){
			for(int j=0; j<design_matrix[0].length; j++){
				System.out.print(design_matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}
}
