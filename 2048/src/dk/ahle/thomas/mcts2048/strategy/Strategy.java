package dk.ahle.thomas.mcts2048.strategy;

import dk.ahle.thomas.mcts2048.Board;

public interface Strategy {
	public Board play(Board board);
}
