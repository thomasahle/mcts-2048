package dk.ahle.thomas.mcts2048;

import dk.ahle.thomas.mcts2048.measure.EnsambleMeasure;
import dk.ahle.thomas.mcts2048.measure.FreesMeasure;
import dk.ahle.thomas.mcts2048.measure.NegativeMeasure;
import dk.ahle.thomas.mcts2048.measure.SmoothMeasure;
import dk.ahle.thomas.mcts2048.measure.SumMeasure;
import dk.ahle.thomas.mcts2048.measure.ZeroMeasure;
import dk.ahle.thomas.mcts2048.strategy.CyclicStrategy;
import dk.ahle.thomas.mcts2048.strategy.GreedyStrategy;
import dk.ahle.thomas.mcts2048.strategy.MarkovStrategy;
import dk.ahle.thomas.mcts2048.strategy.MarkovStrategy2;
import dk.ahle.thomas.mcts2048.strategy.RandomStrategy;
import dk.ahle.thomas.mcts2048.strategy.Strategy;

public class Main {
	public static void main(String[] args) {
		test(new MarkovStrategy2(100, false), 10);
		test(new MarkovStrategy(100, false), 10);
	}
	
	static void compareStrategies() {
		test(new CyclicStrategy(Board.DOWN, Board.LEFT, Board.DOWN, Board.RIGHT), 1000);
		test(new RandomStrategy(), 1000);
		test(new GreedyStrategy(new ZeroMeasure()), 1000);
		test(new GreedyStrategy(new SumMeasure()), 1000);
		test(new GreedyStrategy(new FreesMeasure()), 1000);
		test(new GreedyStrategy(new NegativeMeasure(new SmoothMeasure("pow"))), 1000);
		test(new GreedyStrategy(new NegativeMeasure(new SmoothMeasure("id"))), 1000);
		test(new GreedyStrategy(new EnsambleMeasure()
				.addMeasure(-1, new SmoothMeasure("pow"))
				.addMeasure(1, new FreesMeasure())), 1000);
	}

	static void test(Strategy strat, int runs) {
		long time = System.currentTimeMillis();
		Board board = new Board();
		board.unsafe_spawn();
		board.unsafe_spawn();
		int sum = 0;
		for (int i = 0; i < runs; i++) {
			Board result = strat.play(board);
			sum += new SumMeasure().score(result);
		}
		System.out.println(sum / (double) runs);
		System.out.println(System.currentTimeMillis() - time);
	}
}
