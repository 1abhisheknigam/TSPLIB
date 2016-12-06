import java.text.DecimalFormat;

public class edge {
	private int u;
	private int v;
	private double weight;
	
	public edge(){
		this.u =0;
		this.v =0;
		this.weight = Float.MAX_VALUE;
	}
	
	public edge(int u1, int v1, double w1){
		this.u = u1;
		this.v = v1;
		this.weight = w1;
	}
	
	public edge(String u1, String v1, String w1){
		this.u = Integer.valueOf(u1);
		this.v = Integer.valueOf(v1);
		this.weight = Float.valueOf(w1);
	}

	public int getU() {
		return u;
	}

	public void setU(int u) {
		this.u = u;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public edge flip(){
		return new edge(v, u, weight);
	}
	
	public String toString(){
		return "(u=" + this.u + ",v=" + this.v + ", " + this.weight + ")";
	}
	
	public String toApproxString(){
		return "(u=" + this.u + ",v=" + this.v + ", " + 		new DecimalFormat("#0.00").format(this.weight) + ")";
	}
	public String toIntString(){
		return this.u + " " + this.v + " " + (int)this.weight;
	}

	public boolean sameSpan(edge other){
		if(other.getU() == u && other.getV() == v)return true;
		else return false;
	}
}
