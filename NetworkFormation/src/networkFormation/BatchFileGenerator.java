package networkFormation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BatchFileGenerator {
	
	public static void main(String[] args){
		
		//convert to doubles
		Double[] r= new Double[args.length];
		for (int i=0; i< args.length;i++){
			r[i]=Double.parseDouble(args[i]);
		}
		
		//make file
		generateFile(r);
	}
	
	private static void generateFile(Double[] params){
		try{
			
			//create writer
			BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/t-work/Dropbox/People/PeterLou_lab/JohnJarrett/NetworkFormationResults/GenerateResults/scenario.rs/batch_params.xml",false));
			
			//header
			writer.append("<?xml version=\"1.0\"?>");
			writer.append("<sweep runs=\"1\">");
			
			//add parameters
			writer.append("<parameter name=\"randomSeed\" type=\"constant\" constant_type=\"int\" value=\"1234\"/>");
			
			writer.append("<parameter name=\"runTime\" type=\"constant\" constant_type=\"int\" value=\"2000\"/>");
			//writer.append(Long.toString(Math.round(params[0])));
			//writer.append("\"/>");
			
			writer.append("<parameter name=\"Pn\" type=\"constant\" constant_type=\"java.lang.Double\" value=\"");
			writer.append(params[0].toString());
			writer.append("\"/>");
			
			writer.append("<parameter name=\"Pr\" type=\"constant\" constant_type=\"java.lang.Double\" value=\"");
			writer.append(Double.toString(params[1]).toString());
			writer.append("\"/>");
			
			writer.append("<parameter name=\"Pb\" type=\"constant\" constant_type=\"java.lang.Double\" value=\"1.0\"/>");
			//writer.append(Double.toString(params[3]).toString());
			//writer.append("\"/>");
			
			//add closing tag
			writer.append("</sweep>");
			
			//write and close buffered writer
			writer.flush();
		    writer.close();
		
		} catch (IOException e){				
			e.printStackTrace();	
		}
	}

}
