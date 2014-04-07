package dk.ahle.thomas.mcts2048.measure;

import dk.ahle.thomas.mcts2048.Board;

public class FreesMeasure implements Measure {
	String kernel;
	public FreesMeasure(String kernel) {
		this.kernel = kernel;
	}
	
	@Override
	public double score(Board board) {
		int frees = 0;
		for (int p : Board.all) {
			if (board.grid()[p] == 0)
				frees++;
		}
		return frees;
	}
}
