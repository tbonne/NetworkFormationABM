package networkFormation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Node {
	
	int age,dead, degree;
	List<RepastEdge> myEdges;
	List<Node> myNeigh;
	Network network;
	
	public Node() {	
		age=0;//RandomHelper.nextIntFromTo(0, Params.maxAge);
		dead=0;
		myEdges = null;
		myNeigh = new ArrayList<Node>();
		network = ModelSetup.getNetwork();
		degree = 0;
	}
	
	public void step(){
		age++;
		myEdges = IteratorUtils.toList(network.getEdges(this).iterator());
		myNeigh = IteratorUtils.toList(network.getAdjacent(this).iterator());
		degree = myEdges.size();
	}
	
	public void setDead(int i){
		myEdges = IteratorUtils.toList(network.getEdges(this).iterator());
		dead = i;
	}
	
	public int getDegree(){
		return degree;
	}
	
	public List<Node> getMyNeigh(){
		myNeigh = IteratorUtils.toList(network.getAdjacent(this).iterator());
		return myNeigh;
	}
	
	public double getMeanDegree(){
		return degree/(double)ModelSetup.getNodes().size();
	}
	
}
