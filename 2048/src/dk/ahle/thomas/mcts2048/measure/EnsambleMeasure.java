package dk.ahle.thomas.mcts2048.measure;

import java.util.ArrayList;
import java.util.List;

import dk.ahle.thomas.mcts2048.Board;

public class EnsambleMeasure implements Measure {
	private List<Measure> measures = new ArrayList<>();
	private List<Double> weights = new ArrayList<>();

	public EnsambleMeasure addMeasure(double weight, Measure measure) {
		weights.add(weight);
		measures.add(measure);
		return this;
	}

	@Override
	public double score(Board board) {
		double score = 0;
		for (int i = 0; i < measures.size(); i++) {
			score += weights.get(i) * measures.get(i).score(board);
		}
		return score;
	}
}
