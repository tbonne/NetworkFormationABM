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

		ArrayList<Integer> offspring_partners = new ArrayList<Integer>();
		ArrayList<Integer> mother_partners = new ArrayList<Integer>();

		for(Node node: ModelSetup.getNodes()){

			if(node!=offspring && node!=mother){

				//for offspring
				if(offspring.myNeigh.contains(node)){
					offspring_partners.add(1);
				} else {
					offspring_partners.add(0);
				}

				//for mother
				if(mother.myNeigh.contains(node)){
					mother_partners.add(1);
				} else {
					mother_partners.add(0);
				}
			}
		}

		//convert arrayList to array
		int[] offspringArray = new int[offspring_partners.size()];
		int[] motherArray = new int[mother_partners.size()];
		for(int j=0;j<offspring_partners.size();j++){
			offspringArray[j]=offspring_partners.get(j);
		}
		for(int j=0;j<mother_partners.size();j++){
			motherArray[j]=mother_partners.get(j);
		}


		try {
			//Use R to estimate network measures
			REXP cosineX;
			RList l;
			c.eval("library(igraph)");
			c.eval("library(lsa)");

			//caculate in R the distance D from the observed distributions
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
		
		int[][] asso = getAssociationMatrix();
		
		getGraphStatsFromR(c,asso);
		getMeanDegree();
		
	}
	
	private static int[][] getAssociationMatrix(){
		
		ArrayList<Node> nodes = ModelSetup.getNodes();
		Network net = ModelSetup.getNetwork();
		int[][] associationM = new int[nodes.size()][nodes.size()];
		
		for(int i =0; i<nodes.size(); i++){
			
			for(int j = 0;j<nodes.size();j++){
				
				if(net.isAdjacent(nodes.get(i), nodes.get(j))){
					associationM[i][j]=1;
				} else {
					associationM[i][j]=0;
				}
			}
		}
		
		return associationM;
		
		
	}
	
	private static void getAvgBetweenness(){
		
	}
	
	private static void getMeanDegree(){
		
		double degreeSUM = 0;
		for(Node i : ModelSetup.getNodes()){
			
			degreeSUM = degreeSUM + i.getDegree();
			
		}
		
		double meanDegree = degreeSUM/(double)ModelSetup.getNodes().size();
		Executor.addToMeanDegreeArray(meanDegree);
		
	}
	
	private static void getGraphStatsFromR(RConnection c, int[][] asso){
		
		try {
			//System.out.println("Attempting to use R");
			REXP clusterC, betweenness, mod;
			
			c.eval("library(igraph)");

			//caculate in R the distance D from the observed distributions
			assignAsRMatrix(c,asso, "a");
			c.eval("a.m <- as.matrix(a)");
			c.eval("gg <- graph_from_adjacency_matrix(a.m)");
			c.eval("gg.cluster <- transitivity(gg)");
			c.eval("gg.betweenness <- mean(betweenness(gg))");
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
    public static REXP assignAsRMatrix(RConnection c, int[][] sourceArray, String nameToAssignOn) throws REngineException {
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
