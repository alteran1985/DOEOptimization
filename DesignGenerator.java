package backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DesignGenerator {
	int[][] design_matrix;
	int numberOfFactors;
	int numberOfFactorLevels;
	int numberOfFactorLevelCombinations;
	ArrayList<String[]> factorValues;
	String[][] value_matrix;
	
	public DesignGenerator(int factors, int factorLevels, int replicates, String design){
		numberOfFactors = factors;
		numberOfFactorLevels = factorLevels;
		//Select the coding symbols
		int[] values = new int[numberOfFactorLevels];
		if(numberOfFactorLevels == 2){
			values[0]=-1; values[1]=1;
		}else if(numberOfFactorLevels > 2){
			for(int i=0; i<numberOfFactorLevels;i++){
				values[i] = i;
			}
		}else{
			System.out.println("Sorry, but you must have at least 2 factors to design an experiment.");
		}
		switch(design){
		case "FullFactorial":
			design_matrix = generateFullFactorialMatrix(numberOfFactors, numberOfFactorLevels, values, replicates);
			//printEncodedDesignMatrix();
			break;
		case "FractionalFactorial":
			design_matrix = generateHalfFractionMatrix(numberOfFactors, numberOfFactorLevels, values, replicates);
			break;
		case "LatinHypercube":
			design_matrix = generateLatinHypercubeMatrix(numberOfFactors, numberOfFactorLevels, values, replicates);
		}
		printEncodedDesignMatrix();
	}
	
	public int[][] getDesignMatrix(){
		return design_matrix;
	}
	
	private int[][] generateFullFactorialMatrix(int numberOfFactors, int numberOfFactorLevels, int[] values, int replicates){
		//System.out.println("Generating a new design matrix for a full factorial with " + numberOfFactors + " factors and " + numberOfFactorLevels + " factor levels.");
		//Total number of factor level combinations
		numberOfFactorLevelCombinations = (int) Math.pow(numberOfFactorLevels, numberOfFactors) * replicates;
		//System.out.println("Number of factors: " + numberOfFactors);
		//System.out.println("Number of factor levels: " + numberOfFactorLevels);
		//System.out.println("Number of factor level combinations: " + numberOfFactorLevelCombinations);
		int[][] experiment_matrix= new int[numberOfFactorLevelCombinations][numberOfFactors];
		
		int step_size;
		int steps = 0;
		int steps_restart = 0;
		
		for(int k=0; k<replicates; k++){
			for(int i=0; i<numberOfFactors; i++){
				//Assign the step size = k^i
				step_size = (int) Math.pow(numberOfFactorLevels, i);
				//System.out.println("Step size = " + step_size);
				//System.out.println("Sequence repeated after " + Math.pow(numberOfFactorLevels, i+1) + " times");
				int current_index = 0;
				for(int j=0; j<numberOfFactorLevelCombinations; j++){
					experiment_matrix[j][i] = values[current_index];
					//System.out.println("Value: " + values[current_index]);
					//Do all the necessary updates
					steps++;
					steps_restart++;
					//System.out.println("Steps: " + steps + " Steps restart: " + steps_restart);
					if(steps == step_size){
						current_index++;
						steps = 0;
					}
					if((steps_restart == Math.pow(numberOfFactorLevels, i+1))){
						current_index = 0;
						steps_restart = 0;
					}
				}
			}
		}
		return experiment_matrix;
	}
	
	private int[][] generateHalfFractionMatrix(int numberOfFactors, int numberOfFactorLevels, int[] values, int replicates){
		System.out.println("Generating a new design matrix for a half-fraction factorial with " + numberOfFactors + " factors and " + numberOfFactorLevels + " factor levels.");
		numberOfFactorLevelCombinations = (int) Math.pow(numberOfFactorLevels, numberOfFactors - 1) * replicates;
		numberOfFactorLevels = 2;
		int[][] experiment_matrix = new int[numberOfFactorLevelCombinations][numberOfFactors];
		int[][] temporal_matrix = new int[numberOfFactorLevelCombinations][numberOfFactors - 1];
		
		//Generate an experiment matrix for k-1 factors
		temporal_matrix = generateFullFactorialMatrix(numberOfFactors - 1, numberOfFactorLevels, values, replicates);
		//Copy to the current experiment design matrix
		for(int k=0; k<replicates; k++){
			for(int i=0; i<numberOfFactorLevelCombinations; i++){
				int new_value = 1;
				for(int j=0; j<numberOfFactors-1; j++){
					new_value *= temporal_matrix[i][j];
					experiment_matrix[i][j] = temporal_matrix[i][j];
				}
				experiment_matrix[i][numberOfFactors-1] = new_value;
			}
		}

		return experiment_matrix;
	}
	
	private int[][] generateLatinHypercubeMatrix(int numberOfFactors, int numberOfFactorLevels, int[] values, int replicates){
		//System.out.println("Generating a new design matrix for a full factorial with " + numberOfFactors + " factors and " + numberOfFactorLevels + " factor levels.");
		//Total number of factor level combinations
		numberOfFactorLevelCombinations = numberOfFactorLevels * replicates;
		//System.out.println("Number of factors: " + numberOfFactors);
		//System.out.println("Number of factor levels: " + numberOfFactorLevels);
		//System.out.println("Number of factor level combinations: " + numberOfFactorLevelCombinations);
		int[][] experiment_matrix= new int[numberOfFactorLevelCombinations][numberOfFactors];
		 
		Permutation permGen = new Permutation();
		for (int c = 0; c < numberOfFactors; c++) { 
			int[] perm = permGen.getPerm(numberOfFactorLevels); 
			for (int r = 0; r < numberOfFactorLevels; r++) { 
			    experiment_matrix[r][c] = perm[r]; 
			} 
		}
		
		for(int i=1; i<replicates;i++){
			for(int j=0; j<numberOfFactorLevels; j++){
				for(int k=0; k<numberOfFactors; k++){
					experiment_matrix[i*numberOfFactorLevels+j][k]=experiment_matrix[j][k];
				}
			}
		}
 
		return experiment_matrix;
	}
	
	public String[][] getDesignMatrixValues(ArrayList<String[]> values){
		factorValues = values;
		value_matrix = new String[design_matrix.length][design_matrix[0].length];
		
		for(int i=0; i<numberOfFactors; i++){ //loop over the columns, each column is a factor
			for(int j=0; j<numberOfFactorLevelCombinations; j++){ //loop over all the factor level combinations
				if(numberOfFactorLevels == 2){
					if(design_matrix[j][i]==-1)
						value_matrix[j][i] = values.get(i)[0];
					else
						value_matrix[j][i] = values.get(i)[1];
				}else{
					value_matrix[j][i] = values.get(i)[design_matrix[j][i]-1];
				}
			}
		}
		
		return value_matrix;
	}

	public void printEncodedDesignMatrix(){
		System.out.println("Encoded experiment matrix: ");
		
		for(int i=0; i<design_matrix.length; i++){
			for(int j=0; j<design_matrix[0].length; j++){
				System.out.print(design_matrix[i][j] + "\t");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	public void printDesignMatrix(){
		System.out.println("Experiment matrix: ");
		
		for(int i=0; i<value_matrix.length; i++){
			for(int j=0; j<value_matrix[0].length; j++){
				System.out.print(value_matrix[i][j] + "\t");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
}
