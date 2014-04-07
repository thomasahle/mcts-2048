package dk.ahle.thomas.ai2048.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import dk.ahle.thomas.ai2048.Board;
import dk.ahle.thomas.ai2048.measure.Measure;
import dk.ahle.thomas.ai2048.measure.SumMeasure;

public class MarkovStrategy2 implements Strategy {

	static Measure rolloutMeasure = new SumMeasure();
	static Strategy rolloutStrategy = new CyclicStrategy(Board.DOWN,
			Board.LEFT, Board.DOWN, Board.RIGHT);

	private int expands;

	public MarkovStrategy2(int expands) {
		this.expands = expands;
	}

	@Override
	public Board play(Board board) {
		TreeNode node = new ChoiceLeaf(board);
		while (true) {
			for (int i = 0; i < expands; i++) {
				node = node.expand();
			}
			for (TreeNode child : ((ChoiceNode)node).children) {
				System.out.println(child.value()+" / "+child.visits());
			}
			
			// Play the best move
			node = node.select(false);
//			node.board().print();
			// Play a random move
			node = node.select(false);

			node.board().print();
			if (node.board().isStuck()) {
				break;
			}
		}
		return node.board();
	}
}

abstract class TreeNode {
	/**
	 * Expands some path.
	 * 
	 * @return The expanded node
	 */
	abstract TreeNode expand();

	/**
	 * Select a child for expanding or moving.
	 * 
	 * @param explore Whether an exploration bonus should be included.
	 * @return The most interesting child.
	 */
	abstract TreeNode select(boolean explore);

	int visits = 0;
	double value = 0;
	Board board;

	TreeNode(Board board) {
		this.board = board;
	}

	/**
	 * @return The number of times the node has been expanded.
	 */
	int visits() {
		return visits;
	}

	/**
	 * @return The current score estimation.
	 */
	double value() {
		return value;
	}

	/**
	 * @return The current board wrapped by the node.
	 */
	Board board() {
		return board;
	}
}

class ChoiceNode extends TreeNode {
	List<TreeNode> children;
	private Random rand = ThreadLocalRandom.current();

	public ChoiceNode(Board board, List<TreeNode> children) {
		super(board);
		this.children = children;
		updateStats();
	}

	@Override
	public TreeNode expand() {
		TreeNode node = select(true);
		TreeNode node1 = node.expand();
		if (node != node1) {
			children.remove(node);
			children.add(node1);
		}

		updateStats();

		return this;
	}

	private void updateStats() {
		visits++;
		value = 0;
		for (TreeNode child : children) {
			value = Math.max(value, child.value());
		}
		if (value == 0)
			throw new Error("How can it be?");
	}
	
	@Override
	public TreeNode select(boolean explore) {
		TreeNode selected = null;
		double bestValue = 0;
		for (TreeNode c : children) {
			double uctValue = c.value();
			if (explore) {
//				uctValue = 0;
				uctValue /= c.visits() + 1;
				uctValue += Math.sqrt(Math.log(visits() + 1) / (c.visits() + 1));
			}
			if (uctValue + rand.nextDouble()*1e-6 > bestValue) {
//				System.out.println(c.value()+" "+c.visits()+" "+Math.sqrt(Math.log(visits() + 1) / (c.visits() + 1)));
				selected = c;
				bestValue = uctValue;
			}
		}
		assert selected != null;
		if (selected == null)
			throw new Error("What?");
		return selected;
	}
}

class ExitNode extends TreeNode {
	public ExitNode(Board board, double value) {
		super(board);
		this.value = value;
		this.visits = Integer.MAX_VALUE/2;
	}

	@Override
	public TreeNode expand() {
//		Vil blive kaldt fordi SpawnNode.select ikke kigger pa visits.
//		throw new UnsupportedOperationException();
		return this;
	}

	@Override
	public TreeNode select(boolean explore) {
		throw new UnsupportedOperationException();
//		return this;
	}
}

class ChoiceLeaf extends TreeNode {
	public ChoiceLeaf(Board board) {
		super(board);
		value = MarkovStrategy2.rolloutMeasure
				.score(MarkovStrategy2.rolloutStrategy.play(board));
	}

	@Override
	public TreeNode expand() {
		List<TreeNode> children = new ArrayList<>();
		for (int move : Board.moves) {
			Board board1 = board().move(move);
			if (board1.changed) {
				children.add(new SpawnLeaf(board1));
			}
		}
		if (children.isEmpty()) {
			return new ExitNode(board(), value());
		}
		return new ChoiceNode(board(), children);
	}

	@Override
	public TreeNode select(boolean explore) {
		throw new UnsupportedOperationException();
//		return this;
	}
}

class SpawnLeaf extends TreeNode {
	public SpawnLeaf(Board board) {
		super(board);

		Board board1 = board.copy();
		board1.spawn();
		value = MarkovStrategy2.rolloutMeasure.score(
				MarkovStrategy2.rolloutStrategy.play(board1));
	}

	@Override
	public TreeNode expand() {
		List<TreeNode> children = new ArrayList<>();
		for (Board board1 : board().allSpawns()) {
			children.add(new ChoiceLeaf(board1));
		}
		// This shouldnt happen
		assert !children.isEmpty();
		if (children.isEmpty()) {
			return new ExitNode(board(), value());
		}
		return new SpawnNode(board(), children);
	}

	@Override
	public TreeNode select(boolean explore) {
		return this;
	}
}

class SpawnNode extends TreeNode {
	private List<TreeNode> children;
	private Random rand = ThreadLocalRandom.current();

	public SpawnNode(Board board, List<TreeNode> children) {
		super(board);
		this.children = children;
		updateStats();
	}

	@Override
	public TreeNode expand() {
		TreeNode node = select(true);
		TreeNode node1 = node.expand();
		if (node != node1) {
			children.remove(node);
			children.add(node1);
		}

		updateStats();

		return this;
	}
	
	private void updateStats() {
		visits++;
		value = 0;
		for (TreeNode child : children) {
			value += child.value();
		}
		value /= children.size();
	}

	@Override
	public TreeNode select(boolean explore) {
		TreeNode selected = null;
		double bestValue = 0;
		for (TreeNode c : children) {
			double uctValue = 0;
			if (explore) {
//				uctValue = c.value() / (c.visits() + 1);
//				uctValue += Math.sqrt(Math.log(visits() + 1) / (c.visits() + 1));
			}
			if (uctValue + rand.nextDouble() * 1e-6 >= bestValue) {
				selected = c;
				bestValue = uctValue;
			}
		}
		assert selected != null;
		return selected;
	}
}
