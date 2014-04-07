package dk.ahle.thomas.mcts2048.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import dk.ahle.thomas.mcts2048.Board;
import dk.ahle.thomas.mcts2048.measure.SumMeasure;

public class MarkovStrategy implements Strategy {

	private int expands;

	public MarkovStrategy(int expands) {
		this.expands = expands;
	}

	@Override
	public Board play(Board board) {
		TreeNode node = new ChoiceNode(board);
		node.expand();
		while (node.children.size() != 0) {
			for (int i = 0; i < expands; i++) {
				node.expand();
			}
			for (TreeNode child : node.children) {
				System.out.println(child.totValue+"/"+child.nVisits);
			}
			node = node.selectBest();
			node = node.selectRandomly();
			node.expand();
					
			node.board.print();
			System.out.println();
		}
		return node.board;
	}

	static class ChoiceNode extends TreeNode {
		public ChoiceNode(Board board) {
			super(board);
			totValue = new SumMeasure().score(rolloutStrat.play(board));
		}

		public void expandLeaf() {
			children = new ArrayList<>();
			for (int move : Board.moves) {
				Board board1 = board.move(move);
				if (board1.changed) {
					children.add(new SpawnNode(board1));
				}
			}
		}

		@Override
		public void updateStats() {
			nVisits++;
			if (children.size() != 0) {
				totValue = 0;
				for (TreeNode node : children) {
					totValue = Math.max(totValue, node.totValue);
				}
			}
		}
	}

	static class SpawnNode extends TreeNode {
		public SpawnNode(Board board) {
			super(board);
			totValue = rollOut();
		}

		@Override
		public void expandLeaf() {
			children = new ArrayList<>();
			for (Board board1 : board.allSpawns()) {
				children.add(new ChoiceNode(board1));
			}
		}

		public double rollOut() {
			Board copy = board.copy();
			copy.spawn();
			return new SumMeasure().score(rolloutStrat.play(copy));
		}
		
		@Override
		public void updateStats() {
			nVisits++;
			if (children.size() != 0) {
				totValue = 0;
				for (TreeNode node : children) {
					totValue += node.totValue;
				}
				totValue /= children.size();
			}
		}
	}

	static abstract class TreeNode {
		static Random r = ThreadLocalRandom.current();
		static double epsilon = 1e-6;
//		 static Strategy rolloutStrat = new SmoothStrategy("pow");
		static Strategy rolloutStrat = new CyclicStrategy(new int[] { Board.DOWN, Board.RIGHT, Board.DOWN, Board.LEFT });

		Board board;
		List<TreeNode> children;
		double nVisits, totValue;

		public TreeNode(Board board) {
			this.board = board;
		}

		public void expand() {
			if (children == null) {
				expandLeaf();
			} else if (children.size() != 0) {
				select().expand();
			}
			updateStats();
		}
		
		public abstract void expandLeaf();

		public abstract void updateStats();
		
		private TreeNode select() {
			if (children == null)
				expand();
			if (children.size() == 0)
				throw new Error("select called with no children");
			TreeNode selected = null;
			double bestValue = Double.MIN_VALUE;
			for (TreeNode c : children) {
				double uctValue = c.totValue/(c.nVisits + 1);
				uctValue += Math.sqrt(Math.log(nVisits + 1)/(c.nVisits + 1));
				// small random number to break ties randomly in unexpanded nodes
				if (uctValue + r.nextDouble()*epsilon > bestValue) {
					selected = c;
					bestValue = uctValue;
				}
			}
			return selected;
		}

		public TreeNode selectBest() {
			if (children == null)
				expand();
			if (children.size() == 0)
				throw new Error("select called with no children");
			TreeNode selected = null;
			double bestValue = 0;
			for (TreeNode c : children) {
				if (c.totValue >= bestValue) {
					selected = c;
					bestValue = c.totValue;
				}
			}
			return selected;
		}

		public TreeNode selectRandomly() {
			if (children == null)
				expand();
			if (children.size() == 0)
				throw new Error("select called with no children");
			int n = children.size();
			return children.get(r.nextInt(n));
		}
	}

}

