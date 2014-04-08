package dk.ahle.thomas.mcts2048;

import java.util.ArrayList;
import java.util.List;

import dk.ahle.thomas.mcts2048.measure.BestMeasure;
import dk.ahle.thomas.mcts2048.measure.EnsambleMeasure;
import dk.ahle.thomas.mcts2048.measure.FreesMeasure;
import dk.ahle.thomas.mcts2048.measure.NegativeMeasure;
import dk.ahle.thomas.mcts2048.measure.SmoothMeasure;
import dk.ahle.thomas.mcts2048.measure.SumMeasure;
import dk.ahle.thomas.mcts2048.measure.ZeroMeasure;
import dk.ahle.thomas.mcts2048.strategy.CyclicStrategy;
import dk.ahle.thomas.mcts2048.strategy.GreedyStrategy;
import dk.ahle.thomas.mcts2048.strategy.RandomStrategy;
import dk.ahle.thomas.mcts2048.strategy.Strategy;
import dk.ahle.thomas.mcts2048.strategy.UCTStrategy;

public class Main {
	public static void main(String[] args) {
		compareStrategies();
		test(new UCTStrategy(100, true), 1);
	}
	
	static void compareStrategies() {
		test(new CyclicStrategy(Board.DOWN, Board.LEFT, Board.DOWN, Board.RIGHT), 1000);
		test(new RandomStrategy(), 1000);
		test(new RandomStrategy(0, .25, .5, .25), 1000);
		test(new GreedyStrategy(new FreesMeasure()), 1000);
		test(new GreedyStrategy(new NegativeMeasure(new SmoothMeasure("pow"))), 1000);
		test(new GreedyStrategy(new NegativeMeasure(new SmoothMeasure("id"))), 1000);
		test(new GreedyStrategy(new EnsambleMeasure()
				.addMeasure(-1, new SmoothMeasure("pow"))
				.addMeasure(1, new FreesMeasure())), 1000);
		test(new UCTStrategy(100, false), 10);
	}

	static void test(Strategy strat, int runs) {
		long time = System.currentTimeMillis();
		Board board = new Board();
		board.unsafe_spawn();
		board.unsafe_spawn();
		
		double loBest = Double.MAX_VALUE;
		double hiBest = -Double.MAX_VALUE;
		List<Double> scores = new ArrayList<>();
		for (int i = 0; i < runs; i++) {
			Board result = strat.play(board);
			scores.add(new SumMeasure().score(result));
			loBest = Math.min(loBest, new BestMeasure().score(result));
			hiBest = Math.max(hiBest, new BestMeasure().score(result));
		}
		
		double avg = 0;
		for (double score : scores) {
			avg += score;
		}
		avg /= runs;
		double var = 0;
		for (double score : scores) {
			var += (score-avg)*(score-avg);
		}
		var = Math.sqrt(var/runs);
		
		long totTime = System.currentTimeMillis() - time;
		
		System.out.print(strat.getClass().getSimpleName() + " ");
		System.out.print("Avg: " + avg + ", ");
		System.out.print("StdVar: " + var + ", ");
		System.out.print("Min: " + loBest + ", ");
		System.out.print("Max: " + hiBest + ", ");
		System.out.print("Time: " + totTime);
		System.out.println();
	}
}
