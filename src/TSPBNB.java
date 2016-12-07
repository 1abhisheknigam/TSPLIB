
import java.awt.*;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

public class TSPBNB{
	
	int numRows;
	int numCols;
	public double bestTour = Double.MAX_VALUE / 4;
	public City bestNode;
	public static Cost c;
	public static double startTime;
	public static double endTime;
	public static double runTime;
	public static double RUNTIMELIMIT;
	public static boolean runTimeExceed = false;
	public static ArrayList newEdge = new ArrayList();
	private int newNodeCount = 0;
	private int numberPrunedNodes = 0;
	PrintWriter output_trace;
	DecimalFormat twos_precision =  new DecimalFormat("#0.00");
	
	public TSPBNB (long [][] costMatrix, int size,PrintWriter output_trace, double cutoff) {
		numRows = numCols = size;
		c = new Cost(numRows, numCols);
		this.output_trace = output_trace;
		this.RUNTIMELIMIT = cutoff*1000;//cut off is in seconds, Runtimelimit is in millseconds
		for (int row = 1; row <= size; row++) {
			for (int col = 1; col <= size; col++) {
				c.matrix[row][col] =  costMatrix[row][col];
			}
		}
	}
	
	public void calculate () {
		Point pt;
		for (int row = 1; row <= numRows; row++) {
			for (int col = row + 1; col <= numCols; col++) {
				pt = new Point(row, col);
				newEdge.add(pt);
				pt = new Point(-row, -col);
				newEdge.add(pt);
			}
		}
		// Create root node
		City root = new City(numRows, numCols);
		newNodeCount++;
		root.computeLowerBound();
	
		
		// Apply the branch and bound algorithm
		startTime = System.nanoTime();
		branchAndBound(root, -1);
	}
	
	private void branchAndBound (City node, int edgeIndex) {
		endTime = System.nanoTime();
		runTime = (endTime - startTime)/(double)(1000000);
		if(runTime > RUNTIMELIMIT ) { 
			runTimeExceed = true;
			return;
		}
		
		if (node != null && edgeIndex < newEdge.size()) {
			City leftChild, rightChild;
			int leftEdgeIndex = 0, rightEdgeIndex = 0;
			
			if (node.isTour()) {
				node.setTour();
				if (node.tourCost < bestTour) {
					bestTour = node.tourCost;
					bestNode = node;
					double betterRouteFoundTime = (System.nanoTime() - startTime)/(double)1000000;
					output_trace.println(twos_precision.format(betterRouteFoundTime/1000) + ", " + (int)bestTour);
				}
			}
			else {
				if (node.lowerBound() < 2 * bestTour) {
					leftChild = new City(numRows, numCols);
					newNodeCount++;
					leftChild.setConstraint(copy(node.constraint()));
					if (edgeIndex != -1 && ((Point) newEdge.get(edgeIndex)).getX() > 0) {
						edgeIndex += 2;
					} else {
						edgeIndex++;
					}
					if (edgeIndex >= newEdge.size()) {
						return;
					}
					
					
					Point p = (Point) newEdge.get(edgeIndex);
					leftEdgeIndex = leftChild.addPoint(p, edgeIndex);
					leftChild.addDisallowedEdges();
					leftChild.addRequiredEdges();
					leftChild.addDisallowedEdges();
					leftChild.addRequiredEdges();
					leftChild.computeLowerBound();
					
					if (leftChild.lowerBound() >= 2 * bestTour) {
						leftChild = null;
						numberPrunedNodes++;
					}
					
					// Create right child node
					rightChild = new City(numRows, numCols);
					newNodeCount++;
					rightChild.setConstraint(copy(node.constraint()));
					if (leftEdgeIndex >= newEdge.size()) {
						return;
					}
					p = (Point) newEdge.get(leftEdgeIndex + 1);
					rightEdgeIndex = rightChild.addPoint(p, leftEdgeIndex + 1);
					rightChild.addDisallowedEdges();
					rightChild.addRequiredEdges();
					rightChild.addDisallowedEdges();
					rightChild.addRequiredEdges();
					rightChild.computeLowerBound();
					
						if (rightChild.lowerBound() > 2 * bestTour) {
							rightChild = null;
							numberPrunedNodes++;
						}
						if (leftChild != null && rightChild == null) {
							branchAndBound(leftChild, leftEdgeIndex);
						} else if (leftChild == null && rightChild != null) {
							branchAndBound(rightChild, rightEdgeIndex);
						} else if (leftChild != null && rightChild != null && leftChild.lowerBound() <= rightChild.lowerBound()) {
								if (leftChild.lowerBound() < 2 * bestTour) {
									branchAndBound(leftChild,leftEdgeIndex);
								} else {
									leftChild = null;
									numberPrunedNodes++;
								}
								if (rightChild.lowerBound() < 2 * bestTour) {
										branchAndBound(rightChild, rightEdgeIndex);
								} else {
											rightChild = null;
											numberPrunedNodes++;
								}
						} else if (rightChild != null) {
								if (rightChild.lowerBound() < 2 * bestTour) {
									branchAndBound(rightChild, rightEdgeIndex);
								} else {
									rightChild = null;
									numberPrunedNodes++;
								}
								if (leftChild.lowerBound() < 2 * bestTour) {
									branchAndBound(leftChild, leftEdgeIndex);
								} else {
									leftChild = null;
									numberPrunedNodes++;
								}
						}
						
						
					}
			
			
				}
			}
	}
	private byte [][] copy (byte [][] constraint) {
		byte [][] toReturn = new byte[numRows + 1][numCols + 1];
		for (int row = 1; row <= numRows; row++) {
			for (int col = 1; col <= numCols; col++) {
				toReturn[row][col] = constraint[row][col];
			}
		}
		return toReturn;
		}
	
}

