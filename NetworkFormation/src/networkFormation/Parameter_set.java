package networkFormation;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Parameter_set {
	
	//List of parameters to set for each run
	final static Parameters p = RunEnvironment.getInstance().getParameters();
	
	public static int runTime = 1000;	//number of model steps in which one node is removed and another added to the network
	
	public static double Pn = 0.4;      //probability of forming grooming ties with an individual who is groomed by the mother
	public static double Pr = 0.2; 		//probability of forming grooming ties with an individual who is not groomed by the mother
	public static double Pb = 1;		//probability of forming grooming ties with the mother
	public static double Pwr = 0.1;		//probability of effort in tie to mother's partner
	public static double Pwn = 0.4;		//probability of effort in tie to random
	public static int maxGroomingEffort = 19; //maximum grooming effort (between two individuals)
	
	public static double cosineMeanLength = 25; //number of steps to calculate the mean and sd of cosine similarity values 
	
	public Parameter_set(){
		Pn = (Double)p.getValue("Pn");
		Pr = (Double)p.getValue("Pr");
		Pb = (Double)p.getValue("Pb");
		Pwr = (Double)p.getValue("Pwr");
		Pwn = (Double)p.getValue("Pwn");
		runTime = (Integer)p.getValue("runTime");
	}
}
