package dk.ahle.thomas.mcts2048.measure;

import dk.ahle.thomas.mcts2048.Board;

public class BestMeasure implements Measure {
	@Override
	public double score(Board board) {
		int best = 0;
		for (int p : Board.all) {
			best = Math.max(best, board.grid()[p]);
		}
		return best == 0 ? 0 : 1 << best;
	}
}
