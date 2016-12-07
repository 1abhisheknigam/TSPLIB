import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** {@link euc_2dnode}
* 
* A representation of a location in 2D space
* 
* Used to create a TSP tour
* 
* int index : the location ID of this node
* double x  : the x-coordinate of this node
* double y  : the y-coordinate of this node
*
* @author Abhishek Nigam
* @since  Dec 6, 2016
*/

public class euc_2dnode {
	int index;
	double x;
	double y;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	public euc_2dnode(int i, double x1, double y1){
		index =i;
		x = x1;
		y = y1;
	}
	public euc_2dnode(){
		index = 0;
		x = 0;
		y = 0;
	}
	
	public String toString(){
		return "{" + index + ", " + x + "," + y + "}";
	}
	
	/*
	 * Calculates distance between two given nodes
	 */
	public static double calcDistance(euc_2dnode n1, euc_2dnode n2){
		double dist = 0;
		dist = Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
		//System.out.println("Distance between " + n1 + " and " + n2 + ": " + dist);
		return dist;
	}
	
	/*
	 * Create Array of Euclidean 2D nodes from input file	
	 */
	public static euc_2dnode[] parse(String graph_file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(graph_file));
		System.out.println(br.readLine());//"Name: Boston"
		System.out.println(br.readLine()); //"Comment"
		String[] line1 = br.readLine().split(" ");//["DIMENSION:", "40"]
		int init_nodes = Integer.valueOf(line1[1]);
		euc_2dnode nodes[] = new euc_2dnode[init_nodes];
		br.readLine();//"EDGE WEIGHT TYPE"
		br.readLine();//"NODE COORD SECTION"
		String currentLine = br.readLine();
		int i =0 ;
		while(!currentLine.equals("EOF")){
			String[] line = currentLine.split(" ");//["1", "42347356.000000", "-71124287.000000"]
			nodes[i] = (new euc_2dnode(Integer.valueOf(line[0]),
									 Double.valueOf(line[1]), 
									 Double.valueOf(line[2])));
			currentLine = br.readLine();
			i++;
		}
//		System.out.println(all);//DEBUG
		br.close();
//		System.out.println("nodes[" + init_nodes + "]:");
//		for(euc_2dnode e : nodes){
//			System.out.println(e);//DEBUG
//		}
		return nodes;
	}
}
