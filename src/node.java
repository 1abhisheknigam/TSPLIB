/** node.java
* 
* A custom node class for representing a single location in a city.
* 
* This is primarily used internally for creating the MST. It is used for sorting purposes
* to speed up the generation of the MST.
* 
* int index:       the location ID of this node
* int from:		   the location ID of the other node
* double distance: the distance to the node that has instance ID "from"
*
* @author Abhishek Nigam
* @since  Dec 6, 2016
*/
public class node {
	private int index;
	private double distance;
	private int from;
	
	public node(){}
	public node(int i){
		this.index = i;
		this.distance = Float.MAX_VALUE;
	}
	public node(int i, double d){
		this.index = i;
		this.distance = d;
	}
	
	public node(int i , double d, int f){
		this.index = i;
		this.distance = d;
		this.from = f;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public String toString(){
		//return "[Node " + this.index + ", " + this.distance  + "]"; 
		return "[Node " + this.index + ", " + this.distance + " from " + this.from + "]"; 
	}
}
