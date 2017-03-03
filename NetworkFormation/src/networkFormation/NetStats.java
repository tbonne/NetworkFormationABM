package networkFormation;

import java.util.ArrayList;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import repast.simphony.space.graph.Network;

public class NetStats {
	
	
	/************************** cosine similarity ******************************************/

	public static void calculateCosineSimilarity(RConnection c,Node offspring, Node mother){

		ArrayList<Double> offspring_partners = new ArrayList<Double>();
		ArrayList<Double> mother_partners = new ArrayList<Double>();
		
		ArrayList<Integer> offspring_partners_binary = new ArrayList<Integer>();
		ArrayList<Integer> mother_not_partners = new ArrayList<Integer>();

		for(Node node: ModelSetup.getNodes()){

			if(node!=offspring && node!=mother){

				//for offspring
				if(offspring.myNeigh.contains(node)){
					double weightN = ModelSetup.getNetwork().getEdge(offspring, node).getWeight();
					offspring_partners.add(weightN);
					offspring_partners_binary.add(1);
				} else {
					offspring_partners.add(0.0);
					offspring_partners_binary.add(0);
				}

				//for mother
				if(mother.myNeigh.contains(node)){
					double weightN = ModelSetup.getNetwork().getEdge(mother, node).getWeight();
					mother_partners.add(weightN);
					mother_not_partners.add(0);
				} else {
					mother_partners.add(0.0);
					mother_not_partners.add(1);
				}
			}
		}

		//convert arrayList to array
		double[] offspringArray = new double[offspring_partners.size()];
		double[] motherArray = new double[mother_partners.size()];
		
		int[] offspringArrayBinary = new int[offspring_partners_binary.size()];
		int[] notMotherArray = new int[mother_not_partners.size()];
		
		for(int j=0;j<offspring_partners.size();j++){
			offspringArray[j]=offspring_partners.get(j);
			offspringArrayBinary[j]=offspring_partners_binary.get(j);
		}
		for(int j=0;j<mother_partners.size();j++){
			motherArray[j]=mother_partners.get(j);
			notMotherArray[j]=mother_not_partners.get(j);
		}


		try {
			//Use R to estimate network measures
			REXP cosineX;
			RList l;
			c.eval("library(igraph)");
			c.eval("library(lsa)");

			//Estimate of Pn from grooming interactions
			c.assign("offP", offspringArray);
			c.assign("motherP", motherArray);
			c.eval("cosSim <- cosine(offP,motherP)");
			cosineX = c.eval("cosSim");
			double cos = cosineX.asDouble();
			if(cos>0){
				Executor.setCosineSimilarity(cos);
				//System.out.println("cosine similarity = "+ cos);
				Executor.addToCosineArray(cos);
			} else {
				//System.out.println("cosine similarity = "+ 0);
				Executor.addToCosineArray(0);
			}
			
			//Estimate of Pr from grooming interactions
			c.assign("offP", offspringArrayBinary);
			c.assign("motherP", notMotherArray);
			c.eval("cosSim <- cosine(offP,motherP)");
			cosineX = c.eval("cosSim");
			double cosPr = cosineX.asDouble();
			if(cosPr>0){
				Executor.setCosineSimilarityPr(cosPr);
				//System.out.println("cosine similarity = "+ cos);
				Executor.addToCosinePrArray(cosPr);
			} else {
				//System.out.println("cosine similarity = "+ 0);
				Executor.addToCosinePrArray(0);
			}

		} catch (RserveException rs){
			rs.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: Rserve");
		} catch (REngineException re){
			re.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: Rengine");
		} catch (REXPMismatchException rm){
			rm.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: REXP Mismatch");

		}
	}
	
	/************************** Graph level statistics ******************************************/
	
	public static void calculateGraphLevelStats(RConnection c){
		
		double[][] asso = getAssociationMatrix();
		
		getGraphStatsFromR(c,asso);
		
	}
	
	public static void calculateGraphLevelStats(RConnection c, boolean startingValues){
		
		double[][] asso = getAssociationMatrix();
		
		getStartingGraphStatsFromR(c,asso);
		
	}
	
	private static double[][] getAssociationMatrix(){
		
		ArrayList<Node> nodes = ModelSetup.getNodes();
		Network net = ModelSetup.getNetwork();
		double[][] associationM = new double[nodes.size()][nodes.size()];
		
		for(int i =0; i<nodes.size(); i++){
			
			for(int j = 0;j<nodes.size();j++){
				
				if(net.isAdjacent(nodes.get(i), nodes.get(j))){
					associationM[i][j]=net.getEdge(nodes.get(i), nodes.get(j)).getWeight()+0;
				} else {
					associationM[i][j]=0;
				}
			}
		}
		return associationM;
	}
	
	
	private static void getGraphStatsFromR(RConnection c, double[][] asso){
		
		try {
			//System.out.println("Attempting to use R");
			REXP clusterC, betweenness, mod, strength;
			
			c.eval("library(igraph)");

			//caculate in R the distance D from the observed distributions
			assignAsRMatrix(c,asso, "a");
			c.eval("a.m <- as.matrix(a)");
			c.eval("gg <- graph_from_adjacency_matrix(a.m)");
			c.eval("gg.cluster <- transitivity(gg)");
			c.eval("gg.betweenness <- mean(betweenness(gg))");
			c.eval("gg.strength <- mean(strength(gg))");
			c.eval("gg.commu <- cluster_walktrap(gg)");
			c.eval("gg.mod <- modularity(gg.commu)");

			//clustering coef
			clusterC = c.eval("gg.cluster");
			double cluster = clusterC.asDouble();
			Executor.setClusteringCoef(cluster);
			Executor.addToClusteringCoefArray(cluster);

			//betweenness
			betweenness = c.eval("gg.betweenness");
			double bet = betweenness.asDouble();
			Executor.setBetweennessCoef(bet);
			Executor.addToBetweennessCoefArray(bet);
			
			//modularity
			mod = c.eval("gg.mod");
			double modularity = mod.asDouble();
			Executor.setModularity(modularity);
			Executor.addToModularityArray(modularity);
			
			//strength
			strength = c.eval("gg.strength");
			double stre = strength.asDouble();
			Executor.setStrength(stre);
			Executor.addToDegreeArray(stre);

		} catch (RserveException rs){
			rs.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: Rserve");
		} catch (REngineException re){
			re.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: Rengine");
		} catch (REXPMismatchException rm){
			rm.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: REXP Mismatch");

		}
		
		
	}
	
	
	private static void getStartingGraphStatsFromR(RConnection c, double[][] asso){
		
		try {
			//System.out.println("Attempting to use R");
			REXP clusterC, betweenness, mod, degree;
			
			c.eval("library(igraph)");
			c.eval("library(tnet)");

			//caculate in R the distance D from the observed distributions
			assignAsRMatrix(c,asso, "a");
			c.eval("a.m <- as.matrix(a)");
			c.eval("gg <- graph_from_adjacency_matrix(a.m, mode=c('undirected'), weighted = T)");
			c.eval("net.t <- as.tnet(a.m, type='weighted one-mode tnet')");
			c.eval("gg.cluster <- clustering_w(net.t, measure = c('gm','bi'))");
			c.eval("gg.betweenness <- mean(betweenness_w(net.t,alpha = 0.5)[,2])");
			c.eval("gg.degree <- mean(degree_w(net.t,alpha=0.5,measure=c('alpha'))[,2])");
			c.eval("gg.commu <- cluster_walktrap(gg)");
			c.eval("gg.mod <- modularity(gg.commu)");

			//clustering coef
			clusterC = c.eval("gg.cluster");
			double cluster = clusterC.asDouble();
			Executor.setStartingClusteringCoef(cluster);

			//betweenness
			betweenness = c.eval("gg.betweenness");
			double bet = betweenness.asDouble();
			Executor.setStartingBetweennessCoef(bet);
			
			//modularity
			mod = c.eval("gg.mod");
			double modularity = mod.asDouble();
			Executor.setStartingModularity(modularity);
			
			//strength
			degree = c.eval("gg.degree");
			double deg = degree.asDouble();
			Executor.setStartingDegree(deg);

		} catch (RserveException rs){
			rs.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: Rserve");
		} catch (REngineException re){
			re.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: Rengine");
		} catch (REXPMismatchException rm){
			rm.printStackTrace();
			System.out.println("Failed to estimate cosine similarity: REXP Mismatch");

		}
		
		
	}
	
    /**
     * Creates and assigns a matrix object in R from 2D table of double
     *
     * @param rEngine        the  R instance used
     * @param sourceArray    the 2D table of double
     *                       the matrix must have always the same column number on every row
     * @param nameToAssignOn the R object name
     * @return R matrix instance or null if R return an error
     * @throws REngineException 
     */
    public static REXP assignAsRMatrix(RConnection c, double[][] sourceArray, String nameToAssignOn) throws REngineException {
        if (sourceArray.length == 0) {
            return null;
        }

        c.assign(nameToAssignOn, sourceArray[0]);
        REXP resultMatrix = c.eval(nameToAssignOn + " <- matrix( " + nameToAssignOn + " ,nr=1)");
        for (int i = 1; i < sourceArray.length; i++) {
        	c.assign("temp", sourceArray[i]);
            resultMatrix = c.eval(nameToAssignOn + " <- rbind(" + nameToAssignOn + ",matrix(temp,nr=1))");
        }

        return resultMatrix;
    }
    
    
}
