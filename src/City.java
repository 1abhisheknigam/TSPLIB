
import java.util.*;
import java.awt.*;

public class City {
	int lowerBound;
	int numRows, numCols;
	int [] nodeCosts;
	int edges;
	long tourCost;
	byte [] trip;
	String nodeAsString;
	boolean isLoop = false;
	byte [][] constraint;
	static BitSet b; 
	public City (int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
		nodeCosts = new int[numCols + 1]; 
		constraint = new byte[numRows + 1][numCols + 1];
		trip = new byte[numRows + 1];
	}
	
	public int addPoint (Point p, int edgeIndex) {
		Point pt = p;
		while (edgeIndex < TSPBNB.newEdge.size() && constraint[(int) Math.abs(pt.getX())][(int) Math.abs(pt.getY())]!= 0) {
			edgeIndex++;
			if (edgeIndex < TSPBNB.newEdge.size()) {
				pt = (Point) TSPBNB.newEdge.get(edgeIndex);
			}
		}
		if (edgeIndex < TSPBNB.newEdge.size()) {
			int tmpRow = (int) Math.abs(pt.getX());
			int tmpCol =  (int) Math.abs(pt.getY());
			if (pt.getX() < 0) {
				constraint[tmpRow][tmpCol] = -1;
				constraint[tmpCol][tmpRow] = -1;
			} else {
				constraint[tmpRow][tmpCol] = 1;
				constraint[tmpCol][tmpRow] = 1;
			}
		}
		return edgeIndex;
	}
	
		public void setConstraint (byte [][] constraint) {
			this.constraint = constraint;
		}
		public void addDisallowedEdges () {
			for (int row = 1; row <= numRows; row++) {
			
				int count = 0;
				for (int col = 1; col <= numCols; col++) {
					if (row != col && constraint[row][col] == 1) {
						count++;
					}
				}
				if (count >= 2) {
						for (int col = 1; col <= numCols; col++) {
							if (row != col && constraint[row][col] == 0) {
								constraint[row][col] = -1;
								constraint[col][row] = -1;
							}
						}
				}
			}
			
					for (int row = 1; row <= numRows; row++) {
						for (int col = 1; col <= numCols; col++) {
							if (row != col && isCycle(row, col) && numCities(b) < numRows) {
								if (constraint[row][col] == 0) {
									constraint[row][col] = -1;
									constraint[col][row] = -1;
								}
							}
						}
					}
			}
		
		
				public void addRequiredEdges () {
					for (int row = 1; row <= numRows; row++) {
						int count = 0;
						for (int col = 1; col <= numCols; col++) {
							if (row != col && constraint[row][col] == -1) {
								count++;
							}
						}
						if (count >= numRows - 3) {
							for (int col = 1; col <= numCols; col++) {
								if (row != col && constraint[row][col] == 0) {
									constraint[row][col] = 1;
									constraint[col][row] = 1;
								}
							}
						}
					}
				}
					
			public void computeLowerBound () {
					int lowB = 0;
					for (int row = 1; row <= numRows; row++) {
						for (int col = 1; col <= numCols; col++) {
							nodeCosts[col] = (int)TSPBNB.c.cost(row, col);
						}
					nodeCosts[row] = Short.MAX_VALUE;	
					for (int col = 1; col <= numCols; col++) {
						if (constraint[row][col] == -1) {
							nodeCosts[col] = Short.MAX_VALUE; 
						}
					}
					int [] required = new int[numCols - 1]; 
					int numRequired = 0;
					for (int col = 1; col <= numCols; col++) {
						if (constraint[row][col] == 1) {
							numRequired++;
							required[numRequired] = nodeCosts[col];
							nodeCosts[col] = Short.MAX_VALUE; // Taken out of contention
						}
					}
					int smallest = 0, nextSmallest = 0;
					if (numRequired == 0) {
						smallest = smallest();
						nextSmallest = nextSmallest();
					} else if (numRequired == 1) {
						smallest = required[1];
						nextSmallest = smallest();
					} else if (numRequired == 2) {
						smallest = required[1];
						nextSmallest = required[2];
					}
					if (smallest == Short.MAX_VALUE) {
						smallest = 0;
					}
					if (nextSmallest == Short.MAX_VALUE) {
						nextSmallest = 0;
					}
						lowB += smallest + nextSmallest;//calculate lower bound of every row and add them together
					}
					
					
					
						lowerBound = lowB; // This is twice the actual lower bound
					}
					
					
					public boolean isTour () {
						int path = 0;
						for (int col = 2; col <= numCols; col++) {
							if (constraint[1][col] == 1) {
							path = col;
							break;
							}
						}
						if (path > 0) {
							boolean cycle = isCycle(1, path);
							return cycle && numCities(b) == numRows;
						} else {
							return false;
						}
					}
						
					public boolean isCycle (int row, int col) {
						    b = new BitSet(numRows + 1);
							for (int i = 0; i < numRows + 1; i++) {
								b.clear(i);
								}
								b.set(row);
								b.set(col);
								int from = row;
								int pos = col;
								int edges = 1;
								boolean quit = false;
								while (pos != row && edges <= numCols &&!quit) {
									quit = true;
									for (int column = 1; column <= numCols; column++) {
										if (column != from && constraint[pos][column] == 1) {
											edges++;
											from = pos;
											pos = column;
											b.set(pos);
											quit = false;
											break;
										}
									}
								}
								return pos == row || edges >= numCols;
					}
					
					
								public String tour () {
									return nodeAsString;
								}
								public String toString () {
									String returnString = "";
									for (int row = 1; row <= numRows; row++) {
										for (int col = row + 1; col <= numCols; col++) {
											if (constraint[row][col] == 1) {
												returnString += "" + row + col + " ";
											} else if (constraint[row][col] == -1) {
												returnString += "*" + row + col + " ";
											}
										}
									}
									return returnString;
								}
								
								private int smallest () {
										int s = nodeCosts[1];
										int index = 1;
										for (int i = 2; i <= numCols; i++) {
											if (nodeCosts[i] < s) {
												s = nodeCosts[i];
												index = i;
											}
										}
										int temp = nodeCosts[1];
										nodeCosts[1] = nodeCosts[index];
										nodeCosts[index] = temp;
										return nodeCosts[1];
								}
								
								private int nextSmallest () {
									int ns = nodeCosts[2];
									int index = 2;
									for (int i = 2; i <= numCols; i++) {
										if (nodeCosts[i] < ns) {
											ns = nodeCosts[i];
											index = i;
										}
									}
									int temp = nodeCosts[2];
									nodeCosts[2] = nodeCosts[index];
									nodeCosts[index] = temp;
									return nodeCosts[2];
								}
								
								private int numCities (BitSet b) {
									int num = 0;
									for (int i = 1; i <= numRows; i++) {
										if (b.get(i)) {
											num++;
										}
									}
									return num;
								}
								
								public void setTour () {
									byte path = 0;
									for (int col = 2; col <= numCols; col++) {
										if (constraint[1][col] == 1) {
											path = (byte) col;
											break;
										}
									}
									tourCost = TSPBNB.c.cost(1, path);
									trip[1] = path;
									int row = 1;
									int col = path;
									int from = row;
									byte pos = path;
									nodeAsString = "" + row + " " + col;
									while (pos != row) {
										for (byte column = 1; column <= numCols; column++) {
											if (column != from && constraint[pos][column] == 1) {
												from = pos;
												pos = column;
												nodeAsString += " " + pos;
												tourCost += TSPBNB.c.cost(from, pos);
												trip[from] = pos;
												break;
											}
										}
									}
								}
					public byte [] trip () {
						return trip;
					}
					public byte constraint (int row, int col) {
						return constraint[row][col];
					}
					public byte [][] constraint () {
						return constraint;
					}
					public int lowerBound(){
						return lowerBound;
					}
}
