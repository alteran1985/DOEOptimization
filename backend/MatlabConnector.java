package backend;
import matlabcontrol.*;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabConnectionException;

public class MatlabConnector {
	MatlabProxyFactory factory;
	MatlabProxy proxy;
	
	public MatlabConnector(){
		//Create a new MatlabProxyFactory
		MatlabProxyFactoryOptions.Builder options = new MatlabProxyFactoryOptions.Builder();
		options.build();
		options.setHidden(true);
		options.setUsePreviouslyControlledSession(true);
		factory = new MatlabProxyFactory();
	}
	
	public void connect(){
		try {
			proxy = factory.getProxy();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect(){
		proxy.disconnect();
	}
	
	public double runSimulation(double Kd, double Kp, double Ki, int controllerType) {
		double numericResult = 0;
		try{
			//Change the path to wherever you saved the Matlab quadcopter model
			proxy.eval("cd(\'C:\\Users\\ALejandro\\Documents\\Alejandro\\MDA Project\\QuadcopterModel\')"); //Move to the model directory
			//proxy.eval("cd(\'C:\\Users\\azt0018\\Documents\\Alejandro\\MDA Project\\QuadcopterModel\')"); //Move to the model directory
			Object[] result = proxy.returningEval("simulateNew(" + Kd + "," + Kp + "," + Ki + "," + controllerType + ")", 1);
			numericResult = ((double[])result[0])[0];
		}catch(MatlabInvocationException ex1){
			System.out.print(ex1.getMessage());
		}
		return numericResult;
	}
}
