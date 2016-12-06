

public class Cost {
	int row;
	int col;
	long[][] matrix;
	Cost(int row, int col){
		this.row = row;
		this.col = col;
		matrix = new long[row+1][col+1];
	}
	//c.assignCost(costMatrix[row][col], row,col);
	public void assignCost(long val, int r, int c){
		matrix[r][c] = val;
	}
	public long cost(int from, int to){
		return (long) matrix[from][to];//Node中的TSP.c.cost
	}
}
