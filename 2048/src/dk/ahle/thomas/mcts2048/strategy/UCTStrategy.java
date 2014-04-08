package dk.ahle.thomas.mcts2048.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import dk.ahle.thomas.mcts2048.Board;
import dk.ahle.thomas.mcts2048.measure.Measure;

public class UCTStrategy implements Strategy {

	static Measure rolloutMeasure;
	static Strategy rolloutStrategy;
	
	private int expands;
	private boolean verbose;

	public UCTStrategy(int expands, boolean verbose, Measure rolloutMeasure, Strategy rolloutStrategy) {
		this.expands = expands;
		this.verbose = verbose;
		UCTStrategy.rolloutMeasure = rolloutMeasure;
		UCTStrategy.rolloutStrategy = rolloutStrategy;
	}

	@Override
	public Board play(Board board) {
		Node node = new ChoiceLeaf(board);
//		int k = 0;
		while (!node.board().isStuck()) {
			for (int i = 0; i < expands; i++) {
				node = node.expand();
			}
			if (verbose) {
				for (Node child : ((ChoiceNode)node).children) {
					System.out.println((child.value()/child.visits())+" / "+child.visits());
				}
			}
			
			// Play the best move
			node = node.select(false);
//			if (verbose) {
//				System.out.println(k++ +" "+node.value()/(node.visits()+1));
//			}
//			node.board().print();
			// Play a random move
			node = node.select(false);
			
			if (verbose) {
				node.board().print();
			}
		}
		return node.board();
	}
}

abstract class Node {
	/**
	 * Expands some path.
	 * 
	 * @return The expanded node
	 */
	abstract Node expand();

	/**
	 * Select a child for expanding or moving.
	 * 
	 * @param explore Whether an exploration bonus should be included.
	 * @return The most interesting child.
	 */
	abstract Node select(boolean explore);

	int visits = 0;
	double value = 0;
	Board board;

	Node(Board board) {
		this.board = board;
	}

	/**
	 * @return The number of calls to expand.
	 */
	int visits() {
		return visits;
	}

	/**
	 * @return The reward that has been generated yet by visiting this node.
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

class ChoiceNode extends Node {
	List<Node> children;
	private Random rand = ThreadLocalRandom.current();
	private final static double C = 1;
	private double leafValue;

	public ChoiceNode(Board board, double leafValue, List<Node> children) {
		super(board);
		this.children = children;
		assert !children.isEmpty();
		
		value = leafValue;
		for (Node child : children) {
			value += child.value();
		}
		visits = 1 + children.size();
		
		this.leafValue = leafValue;
	}

	@Override
	public Node expand() {
		Node node = select(true);
		double oldValue = node.value();
		
		Node node1 = node.expand();
		if (node != node1) {
			children.remove(node);
			children.add(node1);
		}
		
		visits += 1;
		value += node1.value() - oldValue;

		return this;
	}
	
	@Override
	public Node select(boolean explore) {
		Node selected = null;
		double bestValue = -Double.MAX_VALUE;
		for (Node c : children) {
			double uctValue = c.value()/(c.visits()+1);
			if (explore) {
				
				uctValue += C * leafValue * Math.sqrt(Math.log(visits() + 1)/(c.visits() + 1));
				uctValue += rand.nextDouble()*1e-6;
//				System.err.println(C * Math.sqrt(Math.log(visits() + 1)/(c.visits() + 1)));
			}
			if (uctValue >= bestValue) {
				selected = c;
				bestValue = uctValue;
			}
		}
		assert selected != null;
		return selected;
	}
}

class ExitNode extends Node {
	private double leafValue;
	public ExitNode(Board board, double leafValue) {
		super(board);
		visits = 1;
		value = leafValue;
		this.leafValue = leafValue;
	}

	@Override
	public Node expand() {
		visits += 1;
		value += leafValue;
		return this;
	}

	@Override
	public Node select(boolean explore) {
		throw new UnsupportedOperationException();
//		Alternatively, return null to signal no children
	}
}

class ChoiceLeaf extends Node {
	public ChoiceLeaf(Board board) {
		super(board);
		visits = 1;
		value = UCTStrategy.rolloutMeasure.score(
				UCTStrategy.rolloutStrategy.play(board));
	}

	@Override
	public Node expand() {
		List<Node> children = new ArrayList<>();
		for (int move : Board.moves) {
			Board board1 = board().move(move);
			if (board1.changed) {
				children.add(new SpawnNode(board1));
			}
		}
		if (children.isEmpty()) {
			return new ExitNode(board(), value());
		}
		return new ChoiceNode(board(), value(), children);
	}

	@Override
	public Node select(boolean explore) {
		throw new UnsupportedOperationException();
		// Perhaps just return a random action?
	}
}

class SpawnNode extends Node {
	private List<Node> children = new ArrayList<>();

	/**
	 * True iff the last call to select spawned a new child
	 */
	private boolean wasNew;

	public SpawnNode(Board board) {
		super(board);
		visits = 1;
		// We have to spawn before we play-out, as the Strategies expect to start with a choice
		value = UCTStrategy.rolloutMeasure.score(
				UCTStrategy.rolloutStrategy.play(board.spawn()));
	}

	@Override
	public Node expand() {
		visits += 1;
		Node child = select(true);
		if (wasNew) {
			value += child.value();
		} else {
			double oldValue = child.value();
			Node child1 = child.expand();
			if (child != child1) {
				children.remove(child);
				children.add(child1);
			}
			value += child1.value() - oldValue;
		}
		return this;
	}
	
	@Override
	Node select(boolean explore) {
		// TODO: What if there are no possible spawns?
		Board board1 = board.spawn();
		for (Node child : children) {
			if (child.board().equals(board)) {
				wasNew = false;
				return child;
			}
		}
		// If we didn't already have a node representing the board, expand a bit
		Node child = new ChoiceLeaf(board1);
		children.add(child);
		wasNew = true;
		return child;
	}
}
