package dk.ahle.thomas.mcts2048.measure;

import dk.ahle.thomas.mcts2048.Board;

public class SumMeasure implements Measure {
	/**
	 * Returns the negative, exponential score. 
	 */
	@Override
	public double score(Board board) {
		int score = 0;
		for (int s : board.grid()) {
			if (s > 0)
				score += 1 << s;
		}
		return score;
	}
}
