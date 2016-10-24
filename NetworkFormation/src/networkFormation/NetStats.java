package networkFormation;

import java.util.ArrayList;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RserveException;

public class NetStats {
	
	
	/************************** cosine similarity ******************************************/

	public static void calculateCosineSimilarity(Node offspring, Node mother){

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
			//System.out.println("Attempting to use R");
			REXP cosineX,x2;
			RList l;
			//System.out.println("Connection set");
			ModelSetup.getR().eval("library(igraph)");
			ModelSetup.getR().eval("library(lsa)");
			//System.out.println("Library loaded");


			//caculate in R the distance D from the observed distributions
			ModelSetup.getR().assign("offP", offspringArray);
			ModelSetup.getR().assign("motherP", motherArray);
			ModelSetup.getR().eval("cosSim <- cosine(offP,motherP)");
			cosineX = ModelSetup.getR().eval("cosSim");
			double cos = cosineX.asDouble();
			if(cos>0){
				ModelSetup.setCosineSimilarity(cos);
			}
			System.out.println("cosine similarity = "+ cos);


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
	
	public static void calculateGraphLevelStats(){
		
		getAssociationMatrix();
		
		
	}
	
	private static void getAssociationMatrix(){
		
		
	}
	
	private static void getAvgBetweenness(){
		
	}
	
	private static void getAvgClusteringCoef(){
		
	}
}
