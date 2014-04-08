package dk.ahle.thomas.mcts2048.measure;

import dk.ahle.thomas.mcts2048.Board;

public class NegativeMeasure implements Measure {
	Measure wrapped;

	public NegativeMeasure(Measure wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public double score(Board board) {
		return -wrapped.score(board);
	}
}
