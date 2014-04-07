package dk.ahle.thomas.ai2048;

import dk.ahle.thomas.ai2048.measure.SumMeasure;
import dk.ahle.thomas.ai2048.strategy.MarkovStrategy2;
import dk.ahle.thomas.ai2048.strategy.Strategy;


public class Main {
	public static void main(String[] args) {
//		test(new CyclicStrategy(Board.DOWN,Board.LEFT,Board.DOWN,Board.RIGHT), 10000);
//		test(new SmartCyclicStrategy(), 100000);
//		test(new SmoothStrategy("id"), 1000);
//		test(new SmoothStrategy("pow"), 1);
		test(new MarkovStrategy2(100), 1);
	}
	
	static void test(Strategy strat, int runs) {
		long time = System.currentTimeMillis();
		Board board = new Board();
		board.spawn();
		board.spawn();
		int sum = 0;
		for (int i = 0; i < runs; i++) {
			Board result = strat.play(board);
			sum += new SumMeasure().score(result);
		}
		System.out.println(sum/(double)runs);
		System.out.println(System.currentTimeMillis()-time);
	}
}
