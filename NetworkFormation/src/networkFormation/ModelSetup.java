package networkFormation;

import java.util.ArrayList;
import java.util.Collections;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.JungNetwork;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class ModelSetup implements ContextBuilder<Object>{
	
	private static Context mainContext;
	public static ContinuousSpace <Object > space;
	public static ArrayList<Node> allNodes;
	public static ArrayList<Node> allInputNodes;
	public static ArrayList<RepastEdge> allEdges;
	public static Network network;
	
	private static int nodeSize ;
	private static int landSize ;
	
	public Context<Object> build(Context<Object> context){
		System.out.println("Running Network Formation model");

		/********************************
		 * 								*
		 * initialize model parameters	*
		 * 								*
		 *******************************/

		mainContext = context; //static link to context
		allNodes = new ArrayList<Node>();	
		allInputNodes = new ArrayList<Node>();	
		allEdges = new ArrayList<RepastEdge>();
		Parameter_set p = new Parameter_set();

		nodeSize = p.initialNodeSize;
		landSize = 1000;

		System.out.println("Building geog");
		
		//ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		//space = spaceFactory.createContinuousSpace("space", context , new RandomCartesianAdder <Object >(), new repast.simphony.space.continuous.StrictBorders(), landSize, landSize); 

		NetworkBuilder <Object> netBuilder = new NetworkBuilder <Object > ("Social network", context , false); 
		network = netBuilder.buildNetwork();
		

		/************************************
		 * 							        *
		 * Adding Nodes to the landscape	*
		 * 							        *
		 * *********************************/

		System.out.println("adding nodes"); 
		
		//Coordinate groupCoord = new Coordinate(RandomHelper.nextDoubleFromTo((0),(landSize)), RandomHelper.nextDoubleFromTo(0,(landSize)));
		//Coordinate groupCoord = new Coordinate((landSize)/2.0, (landSize)/2.0);

		for (int j = 0; j < nodeSize; j++){

			//add node
			//Coordinate coord = new Coordinate(RandomHelper.nextDoubleFromTo(groupCoord.x+(-Params.maxSocialDistance),(Params.maxSocialDistance)), groupCoord.y+RandomHelper.nextDoubleFromTo(-Params.maxSocialDistance,(Params.maxSocialDistance)));
			//while(coord.x<0 || coord.y<0 || coord.x>this.landSize || coord.y>this.landSize){
			//	coord = new Coordinate(groupCoord.x+RandomHelper.nextDoubleFromTo(-Params.maxSocialDistance,Params.maxSocialDistance), groupCoord.y+RandomHelper.nextDoubleFromTo(-Params.maxSocialDistance,(Params.maxSocialDistance)));
			//}
			//Node node = new Node(context,space,globalNet);
			Node node = new Node();
			context.add(node);
			allNodes.add(node);
			//space.moveTo(node, coord.x,coord.y);
			
			//if(j==nodeSize-1){
			//	context.remove(node);
			///	allNodes.remove(node);
			//}
		}

		/************************************
		 * 							        *
		 * Adding Edges to the Social network	*
		 * 							        *
		 * *********************************/		

		
		Network <Object > net = (Network <Object >)context.getProjection("Social network");

		int initalEdges = p.initialEdgeSize;
		while(initalEdges>0){
			Collections.shuffle(allNodes);
			network.addEdge(allNodes.get(0), allNodes.get(1));
			//Edge re = new Edge(allNodes.get(0), allNodes.get(1),true,0.8);
			//context.add(re);
			//net.addEdge(re);
			initalEdges--;
		}
		
		
		/************************************
		 * 							        *
		 * Scheduler to synchronize runs	*
		 * 							        *
		 * *********************************/

		//executor takes care of the processing of the schedule
		Executor executor = new Executor();
		createSchedule(executor);

		return context;
	}
	
	private void createSchedule(Executor executor){

		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		
		//ScheduleParameters agentStepParams_space = ScheduleParameters.createRepeating(1, 2, 2); //start, interval, priority (high number = higher priority)
		//schedule.schedule(agentStepParams_space,executor,"removeOneIndividual");

		//ScheduleParameters agentStepParams_Nodes = ScheduleParameters.createRepeating(2, 2, 1); //start, interval, priority (high number = higher priority)
		//schedule.schedule(agentStepParams_Nodes,executor,"addNewIndividual");

		ScheduleParameters agentStepParams_death = ScheduleParameters.createRepeating(1, 1, 0); //start, interval, priority (high number = higher priority)
		schedule.schedule(agentStepParams_death,executor,"step");

	}
	
	
	/**********************************get and set methods *********************************************/
	
	public static Context getContext(){
		return mainContext;
	}
	
	public static Network getNetwork(){
		return network;
	}
	
	public static ContinuousSpace getSpace(){
		return space;
	}
	
	public static ArrayList<Node> getNodes(){
		return allNodes;
	}
	
	public static void addNode(Node n){
		allNodes.add(n);
	}
	
	public static void removeNode(Node n){
		allNodes.add(n);
	}
	public static Node getRandNode(){
		Collections.shuffle(allNodes);
		return allNodes.get(0);
	}

}