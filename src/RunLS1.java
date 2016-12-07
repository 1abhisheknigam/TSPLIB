//package hillclimb;

import java.io.*;
import java.util.*;

public class RunLS1 {
	//File path string
	static private final String PATH_TO_TSP_DATA= "/src/data/";
	static private final String PATH_TO_RESULTS_SOL= "/src/results/LS1/sol/";
	static private final String PATH_TO_RESULTS_TRACE= "/src/results/LS1/trace/";
	static private final String METHOD_FILE_PREFIX= "_LS1_";

	static private int init_nodes;
	static private int init_edges;
	static private double TSPcost=0;
	
	public static void run(String input_file, double cutoff,int seed) throws FileNotFoundException, UnsupportedEncodingException{
		String workingDir  = System.getProperty("user.dir");
		//String graph_file = workingDir + PATH_TO_TSP_DATA + input_file;
		
		/* Solution File */
		//< instance > < method > < cutoff > < randSeed > ∗.sol
		//Atlanta_MSTapprox_300.sol
		String city = input_file.split("\\.")[0];
		String output_base_sol = city + METHOD_FILE_PREFIX + cutoff+"_"+seed + ".sol";
		String output_file_sol = workingDir + PATH_TO_RESULTS_SOL + output_base_sol;
		
		PrintWriter output_sol = new PrintWriter(output_file_sol, "UTF-8");
		
		/* Trace File */
		//< instance > < method > < cutoff > < randSeed > ∗.trace
		//Atlanta_MSTapprox_300.trace
		String output_base_trace = city + METHOD_FILE_PREFIX + cutoff +"_" +seed + ".trace";
		String output_file_trace = workingDir + PATH_TO_RESULTS_TRACE + output_base_trace;
		PrintWriter output_trace = new PrintWriter(output_file_trace, "UTF-8");
		
		String fileName = workingDir + PATH_TO_TSP_DATA + input_file;
  		String line = null;
    		int dimension = 0;
   		double [][] input;
    
		
        //read the file 
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count = 0;
            while((line = bufferedReader.readLine()) != null) {
            	count++;
                if(count == 3){
            		dimension = (int)Double.parseDouble(line.split(" ")[1]);
            		count = 0;
            		break;
            	}
            }
            input = new double[dimension][3];
            
            for(int i = 0; i < 2; i++){
            	line = bufferedReader.readLine();

            }
            
            while( !(line = bufferedReader.readLine()).equals("EOF") ) {
                double id = Double.parseDouble(line.split(" ")[0]);
                double x =  Double.parseDouble(line.split(" ")[1]);
                double y =  Double.parseDouble(line.split(" ")[2]);
                input[count][0] = id;
                input[count][1] = x;
                input[count][2] = y;
                count++;
            }
             bufferedReader.close();
             
             //calculate the distance for every two point and store in the matrix
             double [][] distance = new double[dimension+1][dimension+1];
             for(int i = 1; i <= dimension; i++){
            	 for(int j = 1; j <= dimension; j++){
            		 double pow2X = (input[i-1][1] - input[j-1][1])*(input[i-1][1] - input[j-1][1]);
            		 double pow2Y = (input[i-1][2] - input[j-1][2])*(input[i-1][2] - input[j-1][2]);
            		 
            		 distance[i][j] = Math.sqrt(pow2X+pow2Y);
            		 
            	 }
             }
		
 	     //calculate the total time 
             double startTime = System.nanoTime();
             ls1 tsp = new ls1(distance, dimension,seed,cutoff, output_trace);	
             
             double endTime   = System.nanoTime();
             double totalTime = endTime - startTime;
        	 
        	 int bestcost=(int)tsp.getBestCost(); 
        	 int[] bestroute=tsp.getRoute();

        	 output_sol.println(bestcost);
            	 for(int i = 0; i < bestroute.length-1; i++){
            		 int startNode = bestroute[i]-1;
            		 int endNode = bestroute[i+1]-1;
			 // print the solution file
            		 output_sol.print(startNode + " " + endNode + " "+ (int)distance[startNode+1][endNode+1]);
            		 output_sol.println();
            	 }
		 //print the last point and the first point and the distance between them
            	 int n1 = bestroute[bestroute.length-1]-1;
            	 int n2 =bestroute[0]-1; 
            	 output_sol.print(n1 + " " + n2 + " "+ (int)distance[n1+1][n2+1]);
            		 output_sol.println();
          
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
