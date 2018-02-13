package backend;

import java.util.ArrayList;
import org.apache.commons.math3.stat.inference.*;
import de.lmu.ifi.dbs.elki.math.statistics.tests.StandardizedTwoSampleAndersonDarlingTest;;

public class StatisticalAnalyzer {
	public StatisticalAnalyzer(){
		
	}
	
	public double[] AndersonDarlingTest(ArrayList<double[]> outcome1, ArrayList<double[]> outcome2, int replicates){
		double[] pvalues = new double[outcome1.size()/replicates];
		
		for(int i=0; i<outcome1.size()/replicates; i++){
			double[] model1 = new double[replicates];
			double[] model2 = new double[replicates];
			for(int j=0; j<replicates; j++){
				model1[j]=outcome1.get(i+j)[2];
				model2[j]=outcome2.get(i+j)[2];
			}
			double[][] samples = new double[2][replicates];
			samples[0] = model1;
			samples[1] = model2;
			//Run the test
			//System.out.print("Running the Anderson-Darling test for point (" + outcome1.get(i)[0] + ", " + outcome1.get(i)[1] + "),");
			StandardizedTwoSampleAndersonDarlingTest test = new StandardizedTwoSampleAndersonDarlingTest();
			//double statistic = test.deviation(model1, model2);
			double statistic = Math.abs(test.deviation(samples));
			//System.out.print(" Value of the statistic: " + statistic + ",");
			double pvalue = pvalueADTest(statistic, replicates);
			//System.out.println(" p-value = " + pvalue);
			pvalues[i] = pvalue;
		}
		
		return pvalues;
	}
	
	public double[] KolmogorovSmirnovTest(ArrayList<double[]> outcome1, ArrayList<double[]> outcome2, int replicates){
		double[] pvalues = new double[outcome1.size()/replicates];
		
		for(int i=0; i<outcome1.size()/replicates; i++){
			double[] model1 = new double[replicates];
			double[] model2 = new double[replicates];
			for(int j=0; j<replicates; j++){
				model1[j]=outcome1.get(i+j)[2];
				model2[j]=outcome2.get(i+j)[2];
			}
			//Run the test
			//System.out.print("Running the Kolmogorov-Smirnov test for point (" + outcome1.get(i)[0] + ", " + outcome1.get(i)[1] + "), ");
			KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
			double pvalue = test.kolmogorovSmirnovStatistic(model1, model2);
			//System.out.println(" p-value = " + pvalue);
			pvalues[i] = pvalue;
		}
		
		return pvalues;
	}
	
	public double[] ChiSquaredTest(ArrayList<double[]> outcome1, ArrayList<double[]> outcome2, int replicates){
		double[] pvalues = new double[outcome1.size()/replicates];
		
		for(int i=0; i<outcome1.size()/replicates; i++){
			double[] model1 = new double[replicates];
			double[] model2 = new double[replicates];
			for(int j=0; j<replicates; j++){
				model1[j]=outcome1.get(i+j)[2];
				model2[j]=outcome2.get(i+j)[2];
			}
			//Set up the bins
			double minValue = findMin(model1, model2);
			double maxValue = findMax(model1, model2);
			int bins = getNumberOfBins(replicates);
			long[] counts1 = getBinCount(model1, minValue, maxValue, bins);
			long[] counts2 = getBinCount(model2, minValue, maxValue, bins);
			
			//Run the test
			//System.out.print("Running the Chi-Squared test for point (" + outcome1.get(i)[0] + ", " + outcome1.get(i)[1] + "), ");
			ChiSquareTest test = new ChiSquareTest();
			double pvalue = test.chiSquareTestDataSetsComparison(counts1, counts2);
			//System.out.println(" p-value = " + pvalue);
			pvalues[i] = pvalue;
		}
		
		return pvalues;
	}
	
	private double pvalueADTest(double statistic, int samplesize){
		double pvalue = 0;
		double value1, value2, value3, value4, value5;
		
		value1 = 0.326;
		value2 = 1.225;
		value3 = 1.960;
		value4 = 2.719;
		value5 = 3.752;
		
		if(statistic >= value5)
			pvalue = 0.01;
		else if(statistic >= value4)
			pvalue = 0.025;
		else if(statistic >= value3)
			pvalue = 0.05;
		else if(statistic >= value2)
			pvalue = 0.1;
		else if(statistic >= value1)
			pvalue = 0.25;
		else
			pvalue = 0.5;
		
		return pvalue;
	}
	
	private double findMin(double[] array1, double[] array2){
		double minValue = 1000000;
		
		for(int i=0; i<array1.length; i++){
			if(array1[i] < minValue)
				minValue = array1[i];
			if(array2[i] < minValue)
				minValue = array2[i];
		}
		//System.out.println("Smallest observation: " + minValue);
		return minValue;
	}
	
	private double findMax(double[] array1, double[] array2){
		double maxValue = 0;
		
		for(int i=0; i<array1.length; i++){
			if(array1[i] > maxValue)
				maxValue = array1[i];
			if(array2[i] > maxValue)
				maxValue = array2[i];
		}
		//System.out.println("Largest observation: " + maxValue);		
		return maxValue;
	}
	
	private long[] getBinCount(double[] data, double min, double max, int numBins){
		long[] counts = new long[numBins];
		double binSize = (max - min)/numBins;
	//	System.out.println("Number of bins: " + numBins + ", Bin size: " + binSize);

		for (double d : data) {
			int bin = (int) ((d - min) / binSize); // changed this from numBins
		    if (bin < 0) { /* this data is smaller than min */ }
		    else if (bin >= numBins) { /* this data point is bigger than max */ }
		    else {
		      counts[bin] += 1;
		    }
		}
		printBins(counts, min, max, numBins);
		return counts;
	}
	
	private void printBins(long[] counts, double min, double max, int numBins){
		//Find the size of each bin
		double binSize = (max-min)/numBins;
		int count = 0;
		
		//System.out.println("\nBin counts:");
		for(int i=0; i<counts.length; i++){
			//System.out.println("[" + (min+(i*binSize)) + ", " + (min+((i+1)*binSize)) + "]:" + counts[i]);
			count += counts[i];
		}
		//System.out.println("Total items in bins:" + count);
	}
	
	private int getNumberOfBins(int replicates){
		int bins = 0;
		
		if(replicates<=10)
			bins = 2;
		else if(replicates<=50)
			bins = 4;
		else if(replicates<=100)
			bins = 5;
		return bins;
	}
}
