package networkFormation;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Parameter_set {
	
	//List of parameters to set for each run
	final static Parameters p = RunEnvironment.getInstance().getParameters();
	
	public static int runTime = 500;
	
	public static int initialNodeSize = 50;
	public static int initialEdgeSize = 25;
	
	public static double Pn = 0.8;
	public static double Pr = 0.1;
	public static double Pb = 1;
	
	public Parameter_set(){
		Pn = (Double)p.getValue("Pn");
		Pr = (Double)p.getValue("Pr");
		Pb = (Double)p.getValue("Pb");
		initialNodeSize = (Integer)p.getValue("initNodes");
		initialEdgeSize = (Integer)p.getValue("initEdges");
		runTime = (Integer)p.getValue("runTime");
	}
}
