import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

/** {@link RunHeuristicFurthestInsertion}
* 
* The exeuction file for the Heuristic Algorithm
* 
* Responsible for generating the tour using the Furthest Insertion algorithm
* 
* Called by mainController
*
* @author Abhishek Nigam
* @since  Dec 6, 2016
*/

public class RunHeuristicFurthestInsertion {
	/*
	 * Static variables for output
	 */
	static private final String PATH_TO_TSP_DATA= "/src/data/";
	static private final String PATH_TO_RESULTS_SOL= "/src/results/Heur/sol/";
	static private final String PATH_TO_RESULTS_TRACE= "/src/results/Heur/trace/";
	static private final String METHOD_FILE_PREFIX= "_Heur_";

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

		double cutoff_in_mills = cutoff * 1000;
		int root = 0;
		//int bestRoot=0;

		LinkedList<edge> FinalTour = new LinkedList<edge>();
		double bestCost = Double.MAX_VALUE;
		int bestRoot = 0;
		
		long startTSP = System.nanoTime();	
		double elaps_time_millis = 0;
		double total_costs = 0;
		//find best tour for all roots
		while(root<init_nodes && elaps_time_millis <= cutoff_in_mills){
			boolean seen[] = initSeen();
			seen[root] = true;
			//printSeen(seen, true);
			
			LinkedList<edge> tour = new LinkedList<edge>();
			tour.add(getFarthestUnseen(init, seen));
			tour.add(getFarthestUnseen(init, seen));
			tour.add(getLastEdge(nodes, tour));
//			printSeen(seen, true);
//			printTour(tour);
			while(!allSeen(seen)){
				edge next = getFarthestUnseen(init, seen);
				euc_2dnode newnode = nodes[next.getV()];
				//System.out.println(next.toApproxString());
				
				edge toReplace = getNearestEdge(tour, nodes, newnode);
				//System.out.println(toReplace.toApproxString());
				
				replaceEdge(tour, toReplace, nodes, newnode.index-1);
				//printTour(tour);
			}
			long tour_finished_time = System.nanoTime();
			elaps_time_millis = (tour_finished_time - startTSP)/(double)1000000;
			TSPcost = getCost(tour);
			total_costs += TSPcost;
			//System.out.println("Root:"+ root + " Cost:" + (int)TSPcost);
			if(TSPcost<bestCost){
				output_trace.println(twos_precision.format(elaps_time_millis/1000) +", "+ (int)TSPcost);
				FinalTour = tour;
				bestCost = TSPcost;
				bestRoot = root;
			}
			root++;
		}
		elaps_time_millis = (System.nanoTime() - startTSP)/(double)1000000;//time to check all tours so far
		//if(elaps_time_millis>cutoff_in_mills && cutoff_in_mills!=0) elaps_time_millis = cutoff_in_mills;
		
		output_sol.println((int)bestCost);
		for(edge e : FinalTour)
			output_sol.println(e.toIntString());

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
	 * Unused functions used for commented out debug lines
	 */
	
	@SuppressWarnings("unused")
	private static void printSeen(boolean[] seen){
		String ret="";
		for(int i =0; i < seen.length;i++)
			ret += seenToString(seen, i) + "\n";
		System.out.print(ret);
	}
	
	@SuppressWarnings("unused")
	private static void printSeen(boolean[] seen, boolean flag){
		String ret;
		if(flag)ret = "Seen:[ ";
		else  ret = "Unseen:[ ";
		for(int i =0; i < seen.length;i++)
			if(seen[i]==flag){
				ret += i + " ";
			}
		ret+="]";
		System.out.println(ret);
	}
	
	private static String seenToString(boolean[] seen, int i){
		return "Seen[" + i + "] = " + seen[i];
	}
	
	/*
	 * Finds the  unseen node that is farthest away from the current tour and returns 
	 * the closest edge to this unseen node
	 */
	private static edge getFarthestUnseen(graph init, boolean[] seen){
		edge longest = new edge();
		for(edge e : init){
			if(seen[e.getU()] && !seen[e.getV()])
				if(e.getWeight() < longest.getWeight())
					longest = e;
		}
		seen[longest.getV()] = true;
		return longest;
	}
	
	@SuppressWarnings("unused")
	private static void printTour(LinkedList<edge> tour){
		System.out.print("[");
		Iterator<edge> iter = tour.iterator();
		while(iter.hasNext()){
			System.out.print(iter.next().toApproxString());
			if(iter.hasNext())System.out.print(",");
			else System.out.println("]");
		}
	}
	
	/*
	 * Completes the triangle tour
	 */
	private static edge getLastEdge(euc_2dnode[] nodes, LinkedList<edge> tour){
		int root = tour.getFirst().getU();
		int last = tour.getLast().getV();
		return new edge(last, root, euc_2dnode.calcDistance(nodes[last], nodes[root]));
	}
	
	/*
	 * Initializes the seen array
	 */
	private static boolean[] initSeen(){
		boolean seen[] = new boolean[init_nodes];
		for(int i =0; i < seen.length;i++)seen[i] = false;
		return seen;
	}
	
	/*
	 * Checks if all the locations have been seen
	 * 
	 * true  if yes
	 * false if no
	 */
	private static boolean allSeen(boolean[] seen){
		for(int i = 0; i < seen.length; i++)
			if(!seen[i])return false;
		return true;
	}
	
	/*
	 * Finds the closest edge in the current subtour to the specified new node
	 */
	private static edge getNearestEdge(LinkedList<edge> tour, euc_2dnode[] nodes, euc_2dnode newnode){
		edge ret = new edge();
		double closest=Double.MAX_VALUE;
		for(edge e : tour){
			double edgedist = euc_2dnode.calcDistance(nodes[e.getU()], newnode);
			edgedist += euc_2dnode.calcDistance(nodes[e.getV()], newnode);
			edgedist -= e.getWeight();
			if(edgedist < closest){
				ret = e;
				closest = edgedist;
			}
		}
		return ret;
	}
	
	/*
	 * For a given edge (a,c) and a given node b
	 * Replaces (a,c) with (a,b) and (b,c)
	 * Replaces this in the tour list so far by adding in the right place.
	 */
	private static void replaceEdge(LinkedList<edge> tour, edge toReplace, euc_2dnode[] nodes, int newnode){
		int index = tour.indexOf(toReplace);//replacing at this position
		tour.remove(toReplace);
		
		edge edge1 = new edge(toReplace.getU(), newnode, 
				euc_2dnode.calcDistance(nodes[toReplace.getU()], nodes[newnode]));
		edge edge2 = new edge(newnode, toReplace.getV(),
				euc_2dnode.calcDistance(nodes[toReplace.getV()], nodes[newnode]));
		
		tour.add(index, edge2);
		tour.add(index, edge1);
	}
	
	/*
	 * Finds the cost of the tour so far (used for improving the tour)
	 */
	private static double getCost(LinkedList<edge> tour){
		double ret = 0;
		for(edge e : tour)ret+=e.getWeight();
		return ret;
	}
}
