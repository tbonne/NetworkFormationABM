package networkFormation;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;

public class Node {
	
	int age;
	//Context context;
	//ContinuousSpace space;
	
	//public Node(Context cont,ContinuousSpace <Object > space, Network net) {
	public Node() {	
		//context =cont;
		age=0;//RandomHelper.nextIntFromTo(0, Params.maxAge);
		//this.space=space;

	}
	
	public void step(){
		//nothing right now
		age++;
		
	}

}