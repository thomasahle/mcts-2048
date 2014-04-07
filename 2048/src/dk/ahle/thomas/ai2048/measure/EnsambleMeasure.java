package dk.ahle.thomas.ai2048.measure;

import java.security.InvalidParameterException;

import dk.ahle.thomas.ai2048.Board;

public class EnsambleMeasure implements Measure {
	private Measure[] measures;
	private double[] weights;
	private int size;

	public Measure[] getMeasures() {
		return measures;
	}

	public double[] getWeights() {
		return weights;
	}

	public EnsambleMeasure(Measure[] measures, double[] weights) {
		if (measures.length != weights.length)
			throw new InvalidParameterException();
		this.measures = measures;
		this.weights = weights;
		this.size = measures.length;
	}

	@Override
	public double score(Board board) {
		double score = 0;
		for (int i = 0; i < size; i++) {
			score += weights[i] * measures[i].score(board);
		}
		return score;
	}
}
