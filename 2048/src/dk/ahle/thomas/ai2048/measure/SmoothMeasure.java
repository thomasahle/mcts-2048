package dk.ahle.thomas.ai2048.measure;

import dk.ahle.thomas.ai2048.Board;

public class SmoothMeasure implements Measure {
	String kernel;
	public SmoothMeasure(String kernel) {
		this.kernel = kernel;
	}
	
	@Override
	public double score(Board board) {
		int res = 0;
		for (int m = 0; m <= 1; m++) {
			int dir = Board.dirs[m];
			for (int p : Board.orders[m]) {
				res += kernel(p, p+dir);
			}
		}
		return res;
	}
	
	int kernel(int a, int b) {
		if (kernel.equals("id"))
			return Math.abs(a-b);
		if (kernel.equals("pow"))
			return Math.abs((1<<a)-(1<<b));
		throw new Error("Av");
	}
}
