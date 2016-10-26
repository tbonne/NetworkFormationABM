package networkFormation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Executor {

	/******************************************************************************************************/
	/************************************ Main model behaivour ********************************************/
	/******************************************************************************************************/
		
	//The network as a whole goes through this each time step
	public static void step (){
		
		//modify network
		removeOneIndividual();
		addNewIndividual();
		
		//process nodes (right now this only measures degree of each node at each time step)
		for(Node node : ModelSetup.getNodes()){
			node.step();
		}
		
		//run network measures (This does nothing right now. TODO: cosine similarity, graph level clustering coefficient & betweenness)
		//recordNetwork();
	}

	
	/******************************************************************************************************/
	/************************************ Behaviour details ***********************************************/
	/******************************************************************************************************/
	
	
	public static void removeOneIndividual(){
		Node dead = ModelSetup.getRandNode();
		dead.setDead(1);
	}

	
	public static void addNewIndividual(){

		//choose random mother
		Node mother = ModelSetup.getRandNode();

		//create new id
		Node newID = new Node();
		ModelSetup.getContext().add(newID);
		ModelSetup.addNode(newID);

		//create ties
		createTies(newID, mother);

	}

	private static void createTies(Node offspring, Node mother){

		Network net = ModelSetup.getNetwork();

		//get mother links (if any)
		List<Node> motherPartners = mother.getMyNeigh();

		//loop though and create links based on Pn, Pr, and Pb
		for(Node node:ModelSetup.getNodes()){

			//If the node is not the offspring in question
			if(node.equals(offspring)==false){
				
				//If this node is the mother
				if(node.equals(mother)){
					//this is Pb
					if(RandomHelper.nextDouble()<Parameter_set.Pb){
						net.addEdge(offspring, mother);	
					}
				
				//If this node is not the mother
				} else {

					//this is Pn: mother's partner
					if(motherPartners.contains(node)){
						if(RandomHelper.nextDouble()<Parameter_set.Pn){
							net.addEdge(offspring, node);
						}

					//this is Pr: unknown partner
					} else {
						if(RandomHelper.nextDouble()<Parameter_set.Pr){
							net.addEdge(offspring, node);
						}
					}
				}
			}
		}
		
		offspring.myNeigh = IteratorUtils.toList(net.getAdjacent(offspring).iterator());
		recordNetworkStats(offspring, mother);
		
	}


	public static void age(){
		//nothing now
	}
	
	private static void recordNetworkStats(Node offspring, Node mother){
		
		NetStats.calculateCosineSimilarity(offspring, mother);
		NetStats.calculateGraphLevelStats();
		
	}
	

	/******************************************************************************************************/
	/************************************ Simulation tools/methods ****************************************/
	/******************************************************************************************************/
	
	//This is used to remove nodes from the context and network
	public static void removeNode(){


		ArrayList<Node> toBeRemoved = new ArrayList<Node>();
		Network net = ModelSetup.getNetwork();
		//System.out.println("removing nodes");

		for (Node n:ModelSetup.allNodes){
			if(n.dead==1)toBeRemoved.add(n);
		}

		for(Node n:toBeRemoved){
			try{
				for(RepastEdge e : n.myEdges){
					ModelSetup.getContext().remove(e);
				}
			}catch(NullPointerException ee){
				//no edges to remove
			}

			ModelSetup.allNodes.remove(n);
			ModelSetup.getContext().remove(n);

		}

	}

}
