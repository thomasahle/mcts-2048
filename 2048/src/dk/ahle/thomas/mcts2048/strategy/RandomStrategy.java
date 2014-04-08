package dk.ahle.thomas.mcts2048.strategy;

import java.security.InvalidParameterException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import dk.ahle.thomas.mcts2048.Board;

public class RandomStrategy implements Strategy {
	Random rand = ThreadLocalRandom.current();
	double[] chances;
	public RandomStrategy(double... chances) {
		if (chances.length != Board.moves.length)
			throw new InvalidParameterException();
		this.chances = chances;
	}
	public RandomStrategy() {
		this(.25, .25, .25, .25);
	}
	@Override
	public Board play(Board board) {
		while (!board.isStuck()) {
			board = board.move(pickMove());
			if (board.changed)
				board.unsafe_spawn();
		}
		return board;
	}
	private int pickMove() {
		double cul = 0;
		double r = rand.nextDouble();
		for (int i = 0; i < chances.length; i++) {
			cul += chances[i];
			if (r < cul)
				return i;
		}
		throw new InvalidParameterException("Check chances sum to 1");
	}
}
