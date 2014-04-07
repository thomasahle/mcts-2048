package dk.ahle.thomas.ai2048.strategy;

import dk.ahle.thomas.ai2048.Board;

public class CyclicStrategy implements Strategy {
	private int[] cycle;

	public CyclicStrategy(int... moves) {
		cycle = moves;
	}

	@Override
	public Board play(Board board) {
		int lst = 0;
		for (int pos = 0; pos - lst <= cycle.length + Board.moves.length; pos++) {
			int move = pos - lst <= cycle.length
					? cycle[pos % cycle.length]
					: Board.moves[pos % Board.moves.length];
			board = board.move(move);
			if (board.changed) {
				board.spawn();
				lst = pos;
			}
		}
		return board;
	}
}
