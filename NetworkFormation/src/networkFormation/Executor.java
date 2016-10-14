package networkFormation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Executor {

	public static void step (){

		//System.out.println("removing one");
		removeOneIndividual();
		//System.out.println("removed one");

		addNewIndividual();
		//System.out.println("added one, now vis");

	}

	public static void removeOneIndividual(){
		Node dead = ModelSetup.getRandNode();
		ModelSetup.removeNode(dead);

		Network net = ModelSetup.getNetwork();
		Iterable<RepastEdge> ed = net.getEdges(dead);

		List<RepastEdge> edges = new ArrayList<RepastEdge>();
		for (RepastEdge edge : ed) {
			edges.add(edge);
		}
		for(RepastEdge e : edges){
			net.removeEdge(e);
		}


		ModelSetup.getContext().remove(dead);
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
		List<Node> motherPartners = new ArrayList<Node>();
		if(net.getDegree(mother)>0){
			Iterable<Node> motherP = net.getAdjacent(mother);
			motherPartners = IteratorUtils.toList(motherP.iterator());
		}

		//loop though and create links based on Pn, Pr, and Pb
		for(Node node:ModelSetup.getNodes()){

			//If node is the mother
			if(node.equals(offspring)==false){
				if(node.equals(mother)){
					//this is Pb
					if(RandomHelper.nextDouble()<Parameter_set.Pn){
						net.addEdge(offspring, mother);	
					}
				} else {
					
					//
					if(motherPartners.contains(node)){
						//this is Pn
						if(RandomHelper.nextDouble()<Parameter_set.Pn){
							net.addEdge(offspring, node);
						}
					} else {
						//this is Pr
						if(RandomHelper.nextDouble()<Parameter_set.Pr){
							net.addEdge(offspring, node);
						}
					}
				}
			}
		}
	}


	public static void age(){
		//nothing now
	}

}
