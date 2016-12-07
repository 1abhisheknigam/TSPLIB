

import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class SimulatedAnnealing {
	static private final String PATH_TO_TSP_DATA= "/src/DATA/";
	static private final String PATH_TO_RESULTS_SOL= "/src/results/LS2/sol/";
	static private final String PATH_TO_RESULTS_TRACE= "/src/results/LS2/trace/";
	static private final String METHOD_FILE_PREFIX= "_LS2_";

	static private int init_nodes;
	static private int init_edges;
	static private double TSPcost=0;
	public double [][] distance;
	
	public static List<Integer> solve(String fileName, double[][] distance2, double temp, double coolingRate, int seed, double cutoff_time) throws FileNotFoundException, UnsupportedEncodingException {
		
		// Generate Sol file
		String workingDir  = System.getProperty("user.dir");
		String city = fileName.split("\\.")[0];
		String output_base_sol = city + METHOD_FILE_PREFIX + cutoff_time + "_" + seed + ".sol";
		String output_file_sol =  workingDir + PATH_TO_RESULTS_SOL + output_base_sol;
		//System.out.println(output_file_sol);
		PrintWriter output_sol = new PrintWriter(output_file_sol, "UTF-8");
		
		// Generate Trace file
		String output_base_trace = city + METHOD_FILE_PREFIX + cutoff_time + "_" + seed + ".trace";
		String output_file_trace =  workingDir + PATH_TO_RESULTS_TRACE + output_base_trace;
		//System.out.println(output_file_trace);
		PrintWriter output_trace = new PrintWriter(output_file_trace, "UTF-8");
		DecimalFormat twos_precision = new DecimalFormat("#0.00");
		// Clock starts
		double startTime = System.currentTimeMillis();
		double endTime = 0;
		
		// Generate a random tour and calculate the distance
		List<Integer> solution = Random(distance2, 1);
		int totalNumber = distance2.length;
		Random rand = new Random(seed);
		double currentDistance = 0;
		for (int i = 0; i < solution.size() - 1; i++) {
			currentDistance += distance2[solution.get(i)][solution.get(i + 1)];
		}
		int iterations = 0;
		double totalTime = 0;
		
		// Optimal Solution for each city
		int optimal = 655454;
		int cur_error = 10;
		
		// Find the best tour
		while (temp > 1.0 || totalTime <= cutoff_time) {
			iterations += 1;
			if (iterations % 1000 == 0) {
//				System.out.println("Iteations: " + iterations);
//				System.out.println("Current Dis: " + currentDistance);
			}
			int firstPostion = rand.nextInt(totalNumber);
			int secondPostion = rand.nextInt(totalNumber);
			if (firstPostion == secondPostion) {
				secondPostion = (secondPostion + 1) % totalNumber;
			}

			// Swap two position
			int t = solution.get(firstPostion);
			solution.set(firstPostion, solution.get(secondPostion));
			solution.set(secondPostion, t);
			int newDistance = 0;
			for (int i = 0; i < solution.size() - 1; i++) {
				newDistance += distance2[solution.get(i)][solution.get(i + 1)];
			}
			newDistance+=distance2[solution.get(solution.size()-1)][solution.get(0)];
			double problem = problemlemCooling(newDistance, currentDistance, temp);
			double r = rand.nextDouble();
			temp = temp * (1.0 - coolingRate);
			
			
			if (problem < r) {
				t = solution.get(firstPostion);
				solution.set(firstPostion, solution.get(secondPostion));
				solution.set(secondPostion, t);
				continue;
			}
			//
			int old = (int)currentDistance;
			currentDistance = newDistance;
			cur_error = (newDistance - optimal) / optimal;
			endTime   = System.currentTimeMillis();;
	        totalTime = endTime - startTime;
	        // use the next command to generate data for QRTD 
	        //double Psolve = (currentDistance/655454-1)*100.0;
	        // if (iterations % 10 == 0 && Psolve <= 30){
		if (currentDistance != old && currentDistance < old){
		    output_trace.println(twos_precision.format(totalTime / 1000) + ", " + (int)currentDistance);	        	
	        }
		}
		output_trace.close();
		System.out.println(solution);
		System.out.println("Total Iteations: " + iterations);
		System.out.println("Total Dis: " + currentDistance);
    		System.out.println("Total Run Time: " + totalTime/1000+"s");
		output_sol.println((int)currentDistance);
		for (int i = 0; i < solution.size() - 1; i = i + 2){
			output_sol.print(solution.get(i) + " " + solution.get(i+1)+ " ");
			output_sol.println(Math.round(distance2[solution.get(i)][solution.get(i+1)]*100)/100);
		}
		output_sol.print(solution.get(solution.size()-1) + " " + solution.get(0)+ " ");
		output_sol.println(Math.round(distance2[solution.get(solution.size()-1)][solution.get(0)]*100)/100);
		output_sol.close();
		return solution;		
	}
	
	//Randomize the route using the random seed
	private static List<Integer> Random(double[][] graph, long seed) {
		int totalNumber = graph.length;
		List<Integer> solution = new ArrayList<Integer>(totalNumber);
		for(int i = 0; i < totalNumber; i++) solution.add(i);
		Random rand = new Random(seed);
		Collections.shuffle(solution, rand);
		return solution;
	}
	
	// Update distance using e^(current-new)/temp
	private static double problemlemCooling(int newDistance, double currentDistance, double temp) {
		if (newDistance < currentDistance) {
			return 1.0;
		}
		return Math.exp((currentDistance - newDistance) / temp);
	}
	


}
