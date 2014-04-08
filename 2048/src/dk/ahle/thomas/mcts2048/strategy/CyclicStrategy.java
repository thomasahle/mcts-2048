package dk.ahle.thomas.mcts2048.strategy;

import dk.ahle.thomas.mcts2048.Board;

public class CyclicStrategy implements Strategy {
	private int[] cycle;

	public CyclicStrategy(int... moves) {
		cycle = moves;
	}

	@Override
	public Board play(Board board_) {
		Board board = board_.copy();
		int lst = 0;
		for (int pos = 0; pos - lst <= cycle.length + Board.moves.length; pos++) {
			board.unsafe_move(pos - lst <= cycle.length
					? cycle[pos % cycle.length]
					: Board.moves[pos % Board.moves.length]);
			if (board.changed) {
				board.unsafe_spawn();
				lst = pos;
			}
		}
		return board;
	}
}
