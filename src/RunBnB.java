

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunBnB {
	
	static private final String PATH_TO_TSP_DATA= "/src/data/";
	static private final String PATH_TO_RESULTS_SOL= "/src/results/BnB/sol/";
	static private final String PATH_TO_RESULTS_TRACE= "/src/results/BnB/trace/";
	static private final String METHOD_FILE_PREFIX= "_BnB_";

	static private int init_nodes;
	static private int init_edges;
	static private double TSPcost=0;
	
	public static void run(String input_file, double cutoff) throws NumberFormatException, IOException{

		String workingDir  = System.getProperty("user.dir");
		//String graph_file = workingDir + PATH_TO_TSP_DATA + input_file;
		
		/* Solution File */
		//< instance > < method > < cutoff > < randSeed > ∗.sol
		//Atlanta_MSTapprox_300.sol
		String city = input_file.split("\\.")[0];
		String output_base_sol = city + METHOD_FILE_PREFIX + cutoff + ".sol";
		String output_file_sol = workingDir + PATH_TO_RESULTS_SOL + output_base_sol;
		
		PrintWriter output_sol = new PrintWriter(output_file_sol, "UTF-8");
		
		/* Trace File */
		//< instance > < method > < cutoff > < randSeed > ∗.trace
		//Atlanta_MSTapprox_300.trace
		String output_base_trace = city + METHOD_FILE_PREFIX + cutoff + ".trace";
		String output_file_trace = workingDir + PATH_TO_RESULTS_TRACE + output_base_trace;
		PrintWriter output_trace = new PrintWriter(output_file_trace, "UTF-8");
		
	String fileName = workingDir + PATH_TO_TSP_DATA + input_file;
    	String line = null;
    	int dimension = 0;
    	double [][] input;
    
    try {
	/*
	Read file and generate cost matrix, which is distance between two cities
	*/
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int count = 0;
        while((line = bufferedReader.readLine()) != null) {
        	count++;
            System.out.println(line);
            if(count == 3){
        		dimension = (int)Double.parseDouble(line.split(" ")[1]);
        		count = 0;
        		break;
        	}
        }
        input = new double[dimension][3];
        
        for(int i = 0; i < 2; i++){
        	line = bufferedReader.readLine();
        	System.out.println(line);
        }
        
        while( !(line = bufferedReader.readLine()).equals("EOF") ) {
            System.out.println(line);
            double id = Double.parseDouble(line.split(" ")[0]);
            double x =  Double.parseDouble(line.split(" ")[1]);
            double y =  Double.parseDouble(line.split(" ")[2]);
            input[count][0] = id;
            input[count][1] = x;
            input[count][2] = y;
            count++;
        }
         bufferedReader.close();
         
         List<Integer> res = new ArrayList<>();
        
         
         long [][] distance = new long[dimension+1][dimension+1];
         for(int i = 1; i <= dimension; i++){
        	 for(int j = 1; j <= dimension; j++){
        		 double pow2X = (input[i-1][1] - input[j-1][1])*(input[i-1][1] - input[j-1][1]);
        		 double pow2Y = (input[i-1][2] - input[j-1][2])*(input[i-1][2] - input[j-1][2]);
        		 distance[i][j] = (long)Math.sqrt(pow2X+pow2Y);
        		 
        	 }
         }
         // pass solution file and trace file here into the tsp run file, tun branch and bound algorithm
         TSPBNB tsp = new TSPBNB(distance, dimension, output_trace, cutoff);	
			 tsp.calculate();

			 if(TSPBNB.runTimeExceed){
        	 		System.out.println("Exceed Time Limit: " + TSPBNB.RUNTIMELIMIT);
				 output_sol.println(tsp.bestNode.tourCost);
        	 		String Best_Tour_cost = tsp.bestNode.tour();
        	 		String[] routeNode = Best_Tour_cost.split(" ");
        	 		for(int i = 0; i < routeNode.length-1; i++){
					 int startNode = Integer.parseInt(routeNode[i])-1;
					 int endNode = Integer.parseInt(routeNode[i+1])-1;
        		 	output_sol.println(startNode + " " + endNode + " "+ (int)distance[startNode+1][endNode+1]);
        	 		}
         		 }
			 else{
				 output_sol.println(tsp.bestNode.tourCost);
				 String Best_Tour_cost = tsp.bestNode.tour();
				 String[] routeNode = Best_Tour_cost.split(" ");
				 for(int i = 0; i < routeNode.length-1; i++){
					 int startNode = Integer.parseInt(routeNode[i])-1;
					 int endNode = Integer.parseInt(routeNode[i+1])-1;
					 output_sol.println(startNode + " " + endNode + " "+ (int)distance[startNode+1][endNode+1]);
				 }
				 //output_sol.println(Best_Tour_cost);
			   }
         /* close the solution file and trace file */
	 		output_sol.close();
	 		output_trace.close();
         
    }
    
      catch(FileNotFoundException ex) {
        System.out.println(
            "Unable to open file '" +
            fileName + "'");
    	}
	    catch(IOException ex) {
		System.out.println(
		    "Error reading file '"
		    + fileName + "'");
	    }
    
	}   
	
}
