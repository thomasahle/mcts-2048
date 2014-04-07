package dk.ahle.thomas.mcts2048.measure;

import dk.ahle.thomas.mcts2048.Board;

public class ZeroMeasure implements Measure {
	@Override
	public double score(Board board) {
		return 0;
	}
}
