package dk.ahle.thomas.ai2048.strategy;

import dk.ahle.thomas.ai2048.Board;
import dk.ahle.thomas.ai2048.measure.Measure;

public class GreedyStrategy implements Strategy {

	private Measure measure;
	public GreedyStrategy(Measure measure) {
		this.measure = measure;
	}
	
	@Override
	public Board play(Board board) {
		while (true) {
			Board board1 = pickMove(board);
			if (board1 == null)
				break;
			board = board1;
			board.spawn();
		}
		return board;
	}
	
	Board pickMove(Board board) {
		Board best = null;
		double bestS = Integer.MAX_VALUE;
		for (int move : Board.moves) {
			Board board1 = board.move(move);
			if (board1.changed) {
				double s = measure.score(board1);
				if (s <= bestS) {
					best = board1;
					bestS = s;
				}
			}
		}
		return best;
	}
}
