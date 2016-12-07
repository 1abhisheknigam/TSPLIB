/*
CSE6140 Project
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/** {@link RunMSTApprox}
* 
* The execution code to create a TSP Tour using the MST Approximation Method
* 
* An MST is created using Prim's Algorithm
* 
* The TSP Tour is then created from this MST using a Depth First Search (DFS)
* 
* @author Abhishek Nigam
* @since  Dec 6, 2016
*/

public class RunMSTApprox{


/*
 * static path variables to input and output
 */
	static private final String PATH_TO_TSP_DATA= "/src/data/";
	static private final String PATH_TO_RESULTS_SOL= "/src/results/MSTApprox/sol/";
	static private final String PATH_TO_RESULTS_TRACE= "/src/results/MSTApprox/trace/";
	static private final String METHOD_FILE_PREFIX= "_MSTapprox_";

	static private int init_nodes;
	static private int init_edges;
	static private double TSPcost=0;
	
	public static void run(String input_file, double cutoff) throws NumberFormatException, IOException{

		String workingDir  = System.getProperty("user.dir");
		String graph_file = workingDir + PATH_TO_TSP_DATA + input_file;
		
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
		
		DecimalFormat twos_precision = new DecimalFormat("#0.00");

		euc_2dnode nodes[] = euc_2dnode.parse(graph_file);
		init_nodes = nodes.length;
		graph init = graph.createGraphFromEUCNodes(nodes);
		init_edges = init.size();
		
		graph mst = GenerateMST(init);
		
		/*The following commented lines can be used to see the actual MST in a ".mst" file*/
		//String output_file_mst = PATH_TO_RESULTS;
		//output_file_mst += args[0].split("\\.")[0] + ".mst";
		//PrintWriter output_mst = new PrintWriter(output_file_mst, "UTF-8");
		//double MSTweight = mst.getSumWeight();// Find weight of MST
		//output_mst.println(Double.toString(MSTweight) + " " + Double.toString(MSTtotal) + "ms");
		//displayMST(mst, output, MSTtotal);
		//output_mst.close();
		
		double cutoff_in_mills = cutoff * 1000;
		double elaps_time_millis = 0;
		int root = 0;
		int bestRoot=0;

		LinkedList<edge> BestTour = new LinkedList<>();
		
		long startTSP = System.nanoTime();	
		LinkedList<Integer> tour = createIntegerTourFromMST(mst, root);
		BestTour = convertIndexTourToEdgeTour(tour, init);
		long tour_finished_time = System.nanoTime();
		elaps_time_millis = (tour_finished_time - startTSP)/(double)1000000;
		
		output_trace.println(twos_precision.format(elaps_time_millis/1000) +", "+ (int)TSPcost);
		double bestCost = TSPcost;
		double total_costs = TSPcost;
		root++;// 0 -> 1
		
		while(elaps_time_millis < cutoff_in_mills && root < init_nodes){
			tour = createIntegerTourFromMST(mst, root);
			LinkedList<edge> new_tour = convertIndexTourToEdgeTour(tour, init);
			tour_finished_time = System.nanoTime();
			
			elaps_time_millis = (tour_finished_time - startTSP)/(double)1000000;
			
			if(TSPcost < bestCost){
				output_trace.println(twos_precision.format(elaps_time_millis/1000) +", "+ (int)TSPcost);
				BestTour = new_tour;
				bestCost = TSPcost;
				bestRoot = root;
			}
			total_costs+=TSPcost;
			root++;
		}
		elaps_time_millis = (System.nanoTime() - startTSP)/(double)1000000;//time to check all tours so far
		//if(elaps_time_millis>cutoff_in_mills && cutoff_in_mills!=0) elaps_time_millis = cutoff_in_mills;
		
		displayTour(BestTour, output_sol);
		output_sol.close();
		output_trace.close();
		//Console output for debugging and quick glance
		System.out.println("INITIAL Nodes: " + init_nodes + ", Edges: " + init_edges);
		System.out.println("Cutoff: " + twos_precision.format(cutoff) + "s");
		System.out.println("Average cost: " + (int)(total_costs/(root)));
		System.out.println("Average time: " + twos_precision.format(elaps_time_millis/(double)(root)) + "ms");
		System.out.println("BEST TSP Cost: " + twos_precision.format(bestCost) + " using location " + bestRoot + " as ROOT");
		System.out.println("FILES written: " + output_base_sol + ", " + output_base_trace);
		String elapsed;
		if(elaps_time_millis > 1000)elapsed = twos_precision.format(elaps_time_millis/1000) + "s";
		else elapsed = twos_precision.format(elaps_time_millis) + "ms";
		System.out.println("TOTAL: Checked " + root + "/" + init_nodes + " locations in " + city  + " in " + elapsed);
		System.out.println("Done");

	}
	

/*
 * Creates the MST from the given initial graph and returns it in a new Graph object which can be reused and viewed
 * 
 * The MST is created using Prim's Algorithm
 */
	private static graph GenerateMST(graph init){//without from
		graph mst = new graph();
		HashSet<Integer>            visited = new HashSet<Integer>();
		PriorityQueue<node>       unvisited = new PriorityQueue<node>(new nodeComparator());
		HashMap<Integer,edge>    ndistances = new HashMap<>();
		for(int i = 0; i < init.getNum_nodes(); i++){
			edge e = new edge(0, i, Integer.MAX_VALUE);
			ndistances.put(i, e);
			unvisited.add(new node(i,Integer.MAX_VALUE));
		}
//		int iter = 1;//DEBUG
		while(!unvisited.isEmpty()){
			node cnode = unvisited.poll();
			int cindex = cnode.getIndex();
			if(visited.contains(cindex))continue;
			if(!visited.isEmpty()){
				edge e = ndistances.get(cindex);
				mst.add(e);
				mst.addCost((int)e.getWeight());
				init.remove(e);
			}
			visited.add(cindex);
			mst.addnumnode();//increments by 1
			for(edge e: init){
				if(e.getU() == cindex){
					int dest = e.getV();
					double destdist = e.getWeight();
					double ndist = ndistances.get(dest).getWeight();
					if(destdist < ndist){
						ndistances.replace(dest, e);
						unvisited.add(new node(dest, destdist));
					}
				}
				else if(e.getV() == cindex){
					int dest = e.getU();
					double destdist = e.getWeight();
					double ndist = ndistances.get(dest).getWeight();
					if(destdist<ndist){
						ndistances.replace(dest, new edge(cindex, dest,destdist));
						unvisited.add(new node(dest, destdist));
					}
				}
			}
//			System.out.println("Iteration:" + iter +"\n"+ unvisited.toString());//DEBUG
//			System.out.println(mst + "\n");//DEBUG
//			iter++;//DEBUG
		}
		return mst;
	}
		

/*
 * Unused code for commented blocks which can display the MST in an aesthetically pleasing format
 */
	@SuppressWarnings("unused")
	private static void displayMST(graph mst, PrintWriter output, double MSTtotal){
		output.println("{Graph n=" +  mst.getNum_nodes() + ",e=" + mst.size() + "}:");
		DecimalFormat twos_precision = new DecimalFormat("#00");// Store sums and times proper precision
		DecimalFormat sixes_precision = new DecimalFormat("#000000.00");// Store sums and times proper precision
		for(edge e : mst){
			output.println(
					twos_precision.format(e.getU()) + " ----" + //change here if want zero or one indexing (add +1 for 1 indexing)
					sixes_precision.format(e.getWeight()) + "----- " + 
					twos_precision.format(e.getV()));
		}
		output.println("--------------------");
		output.println("Initial Nodes: " + init_nodes + ", Edges: " + init_edges);
		output.println("Static Time: " + Double.toString(MSTtotal) + " ms");
		output.println("--------------------");
	}
	

/*
 * Method to create TSP Tour from a given MST, and a given root.
 * 
 * Using the specified root, a DFS is performed and then a Euler Tour is created using shortcutting
 * 
 * This tour only consists of the indices
 */
	private static LinkedList<Integer> createIntegerTourFromMST(graph mst, int root) {
		LinkedList<Integer> tour = new LinkedList<>();
		LinkedList<Integer> seen = new LinkedList<>();
		
		graph mst_copy = mst.copy();
		edge first = new edge(-1, root, 0);
		edge current = first;
		tour.add(root);
		
		DFS_MST_indices(current, seen, mst_copy, tour);
		tour.add(root);
		tour.removeFirst();
		
		return tour;
	}
	

	/*
	 * Method to create TSP Tour from a given MST, and a given root.
	 * 
	 * Using the specified root, a DFS is performed and then a Euler Tour is created using shortcutting
	 */
	@SuppressWarnings("unused")
	private static LinkedList<edge> createTourFromMST(graph mst, graph init, int root) {
		LinkedList<edge> dfs_mst = new LinkedList<>();
		LinkedList<Integer> seen = new LinkedList<>();
		
		graph mst_copy = mst.copy();
		//edge current = (edge) mst_copy.toArray()[root];
		edge first = new edge(-1, root, 0);
		edge current = first;
		
		DFS_MST(current, seen, mst_copy, dfs_mst);
		
		
		//add the last edge to root
		for(edge e : init)
			if(e.getV() == root && e.getU() == dfs_mst.getLast().getV()){
				dfs_mst.add(e);
				TSPcost+= e.getWeight();
			}
		
		dfs_mst.remove(first);
		return dfs_mst;
	}
	
	/*
	 * The actual method performing the DFS
	 * 
	 * This tour only consists of the indices
	 * 
	 * The DFS is performed recursively with the current edge, seen array, the original graph, and the subtour so far
	 */
	private static void DFS_MST_indices(edge current, LinkedList<Integer> seen, graph orig, LinkedList<Integer> tour ){
		seen.add(current.getV());
		tour.add(current.getV());
		TSPcost+=current.getWeight();
		for(edge e : orig){
			if(e.getU() == current.getV()){
				if(!seen.contains(e.getV()))
					DFS_MST_indices(e, seen, orig, tour);
			}
			else if(e.getV() == current.getV()){
				if(!seen.contains(e.getU()))
					DFS_MST_indices(e.flip(), seen, orig, tour);
			}
		}
		
	}
	
	/*
	 * The actual method performing the DFS
	 * 
	 * The DFS is performed recursively with the current edge, seen array, the original graph, and the subtour so far
	 */
	private static void DFS_MST(edge current, LinkedList<Integer> seen, graph orig, LinkedList<edge> dfs_mst ){
		seen.add(current.getV());
		dfs_mst.add(current);
		TSPcost+=current.getWeight();
		for(edge e : orig){
			if(e.getU() == current.getV()){
				if(!seen.contains(e.getV()))
					DFS_MST(e, seen, orig, dfs_mst);
			}
			else if(e.getV() == current.getV()){
				if(!seen.contains(e.getU()))
					DFS_MST(e.flip(), seen, orig, dfs_mst);
			}
		}
		
	}
	/*
	 * The function to print the tour to the solution file
	 */
	private static void displayTour(LinkedList<edge> TSPtour, PrintWriter output_sol) {
		output_sol.println(/*"Cost: " +*/ (int)TSPcost);
		for(edge e : TSPtour)output_sol.println(e.toIntString());
	}
	
	/*
	 * This method takes the MST Index tour and converts it into a TSP Tour with weights
	 */
	private static LinkedList<edge> convertIndexTourToEdgeTour(LinkedList<Integer> indexTour, graph init){
		LinkedList<edge> tour = new LinkedList<>();
		TSPcost = 0;
		for(int i =0; i< indexTour.size()-1;i++){
			edge toAdd = new edge(indexTour.get(i), indexTour.get(i+1),0);
			for(edge e : init)
				if(e.sameSpan(toAdd)){tour.add(e);TSPcost+=e.getWeight();break;}
				else if(e.flip().sameSpan(toAdd)){tour.add(e.flip());TSPcost+=e.getWeight();break;}
			
		}
		return tour;
	}

	//Unused functions from assignment 1
	@SuppressWarnings("unused")
	private static int checkChanges(graph mst, PrintWriter output, String change_file, double MSTtotal)

			throws IOException{
		//Iterate through changes file
		BufferedReader br = new BufferedReader(new FileReader(change_file));
		String line = br.readLine();
		String[] split = line.split(" ");
		int num_changes = Integer.parseInt(split[0]);
		//int u, v, weight;
		double total=0;
		while ((line = br.readLine()) != null) {
			split = line.split(" ");
			edge e = new edge(
					Integer.parseInt(split[0]),
					Integer.parseInt(split[1]),
					Integer.parseInt(split[2])
			);
			mst.add(e);
			//Run your recomputeMST function to recalculate the new weight of the MST given the addition of this new edge
			//Note: you are responsible for maintaining the MST in order to update the cost without recalculating the entire MST
			long start_newMST = System.nanoTime();
			mst = GenerateMST(mst);
			double newMST_weight = mst.getSumWeight();
			long finish_newMST = System.nanoTime();
	
			double newMST_total = (finish_newMST - start_newMST)/1000000;
	
			//Write new MST weight and time to output file
			output.println(Double.toString(newMST_weight) + " " + Double.toString(newMST_total));
			total+= newMST_total;
	
		}
		output.println("--------------------");
		output.println("Initial Nodes: " + init_nodes + ", Edges: " + init_edges);
		output.println("Static Time: " + Double.toString(MSTtotal) + " ms");
		output.println("Changes: " + num_changes);
		output.println("Dynamic Time: " + Double.toString(total) + " ms");
		output.println("--------------------");
		br.close();
		return num_changes;
	}
	@SuppressWarnings("unused")
	private static graph oldparseEdges(String graph_file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(graph_file));
		String[] line1 = br.readLine().split(" ");
		init_nodes = Integer.valueOf(line1[0]);
		init_edges = Integer.valueOf(line1[1]);
		graph all = new graph(init_edges, init_nodes);
		String currentLine;
		while((currentLine = br.readLine()) != null){
			String[] line = currentLine.split(" ");
			all.add(new edge(line[0], line[1], line[2]));
		}
//		System.out.println(all);//DEBUG
		br.close();
		return all;
	}
}