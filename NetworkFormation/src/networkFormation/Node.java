package networkFormation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Node {
	
	String name;
	int age,dead;
	List<RepastEdge> myEdges;
	List<Node> myNeigh;
	Network network;
	
	public Node(String n) {	
		name=n;
		age=0;
		dead=0;
		myEdges = null;
		myNeigh = new ArrayList<Node>();
		network = ModelSetup.getNetwork();
	}
	
	public void step(){
		age++;
		//myEdges = IteratorUtils.toList(network.getEdges(this).iterator());
		myNeigh = IteratorUtils.toList(network.getAdjacent(this).iterator());
	}
	
	public void setDead(int i){
		myEdges = IteratorUtils.toList(network.getEdges(this).iterator());
		dead = i;
	}
	
	public List<Node> getMyNeigh(){
		myNeigh = IteratorUtils.toList(network.getAdjacent(this).iterator());
		return myNeigh;
	}
	
	public String getName(){
		return name;
	}
	
}
