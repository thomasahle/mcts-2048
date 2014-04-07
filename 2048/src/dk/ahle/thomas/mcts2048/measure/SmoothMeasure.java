package dk.ahle.thomas.mcts2048.measure;

import java.security.InvalidParameterException;

import dk.ahle.thomas.mcts2048.Board;

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
				int s = kernel(board.grid()[p], board.grid()[p + dir]);
				res += s;
			}
		}
		return res;
	}

	int kernel(int a, int b) {
		switch (kernel) {
		case "id":
			return Math.abs(a - b);
		case "pow":
			return Math.abs((a == 0 ? 0 : 1 << a) - (b == 0 ? 0 : 1 << b));
		default:
			throw new InvalidParameterException("No kernel: " + kernel);
		}
	}
}
