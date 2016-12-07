import java.util.Comparator;
import java.util.PriorityQueue;

/** graph.java
* 
* Represents a Graph, or specifically the MST, as a collection of edges
* 
* By being a PriorityQueue, it automatically sorts the edges to easily show the entire MST in order
* 
* int num_nodes: the number of nodes in the Graph
* double cost  : the cost of the edges in the Graph
*
* @author Abhishek Nigam
* @since  Dec 6, 2016
*/

public class graph extends PriorityQueue<edge>{
	private static final long serialVersionUID = 1L;
	
	private int num_nodes;
	private double cost;
	
	public int getNum_nodes() {
		return num_nodes;
	}
	public void setNum_nodes(int num_nodes) {
		this.num_nodes = num_nodes;
	}
	public graph(){
		super(new myComparator());
		this.num_nodes = 0;
		this.cost=0;
	}
	
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void addCost(double newcost){
		this.cost+= newcost;
	}
	public graph(int size){
		super(size, new myComparator());
	}
	
	public graph(int size, int num){
		super(size, new myComparator());
		this.num_nodes = num;
	}
	
	public double getSumWeight(){
		int sum=0;
		for(edge e: this)sum+=e.getWeight();
		return sum;
	}
	
	public void addnumnode(){
		this.num_nodes++;
	}
	
	public String toString(){
		String retString  = "{Graph n=" +  num_nodes + ",e=" + this.size() + "}:\n" ;
		for(edge e : this)
			retString+= e.toString()+ "\n";
		return retString;
		//return "{Graph n=" +  num_nodes + ",e=" + this.size() + "}:" + super.toString();
	}
	
	/*
	 * Creates a deep copy of this graph
	 */
	public graph copy(){
		graph copy = new graph(size(), num_nodes);
		for(edge e : this)
			copy.add(new edge(e.getU(), e.getV(), e.getWeight()));
		return copy;
	}
	
	/*
	 * Checks the graph from specifed nodes by creating edges between any two nodes in the array
	 * Creates duplicate edges i.e. for two nodes a, b, the Graph will contain both edges (a,b) and (b,a)
	 */
	public static graph createGraphFromEUCNodes(euc_2dnode[] nodes){
		int num_nodes = nodes.length;
		graph init = new graph(num_nodes * num_nodes, num_nodes);
		for(int i1 =0; i1<num_nodes; i1++){
			euc_2dnode n1 = nodes[i1];
			for(int j = 0; j< num_nodes; j++)
			{
				if(j != i1){
					euc_2dnode n2 = nodes[j];
					double d = euc_2dnode.calcDistance(n1, n2);
					init.add(new edge(n1.index-1, n2.index-1, d));//the MST maker is 0-start indexed
					init.add(new edge(n2.index-1, n1.index-1, d));
				}
			}
		}
		//System.out.println(init.toArray()[3]);
		return init;
	}

	/*
	 * Comparator for this PriorityQueue which sorts the edges in a readable manner
	 */
	private static class myComparator implements Comparator<edge>{//Used primarily for debug display
		@Override
		public int compare(edge arg0, edge arg1) {
			if(arg0.getU()<arg1.getU())return -1;
			else if(arg0.getU()>arg1.getU())return 1;
			else{
				if(arg0.getV()<arg1.getV())return -1;
				else if(arg0.getV()>arg1.getV())return 1;
				else{
					if(arg0.getWeight()<arg1.getWeight())return -1;
					else if(arg0.getWeight()>arg1.getWeight())return 1;
					else return 0;
				}
			}
		}
		
	}
}
