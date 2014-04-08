package dk.ahle.thomas.mcts2048.strategy;

import dk.ahle.thomas.mcts2048.Board;

public class SmoothStrategy implements Strategy {

	String kernel;
	public SmoothStrategy(String kernel) {
		this.kernel = kernel;
	}
	
	@Override
	public Board play(Board board) {
		while (true) {
			Board board1 = pickMove(board);
			if (board1 == null)
				break;
			board = board1;
			board.unsafe_spawn();
//			board.print();
		}
		return board;
	}
	
	Board pickMove(Board board) {
		Board best = null;
		int bestS = Integer.MAX_VALUE;
		for (int move : Board.moves) {
			Board board1 = board.move(move);
			if (board1.changed) {
				int s = smoothness(board1);
				if (s <= bestS) {
					best = board1;
					bestS = s;
				}
			}
		}
		return best;
	}
	
	int smoothness(Board board) {
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
