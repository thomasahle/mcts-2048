package dk.ahle.thomas.mcts2048;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Board {
	public final static int orders[][] = {
		{13, 14, 15, 16,  19, 20, 21, 22,  25, 26, 27, 28},
		{9, 15, 21, 27,  8, 14, 20, 26,  7, 13, 19, 25},
		{19, 20, 21, 22,  13, 14, 15, 16,  7, 8, 9, 10},
		{8, 14, 20, 26,  9, 15, 21, 27,  10, 16, 22, 28},
	};
	public final static int all[] = {7, 8, 9, 10,  13, 14, 15, 16,  19, 20, 21, 22,  25, 26, 27, 28};
	public final static int dirs[] = {-6, 1, 6, -1};
	
	public final static int UP = 0;
	public final static int RIGHT = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int moves[] = {UP, RIGHT, DOWN, LEFT};
	
	private int[] grid = new int[] {
		-1, -1, -1, -1, -1, -1,
		-1,  0,  0,  0,  0, -1, 
		-1,  0,  0,  0,  0, -1, 
		-1,  0,  0,  0,  0, -1, 
		-1,  0,  0,  0,  0, -1, 
		-1, -1, -1, -1, -1, -1,
	};
	public boolean changed = false;

	private Random rand = ThreadLocalRandom.current();
	public void unsafe_spawn() {
		while (true) {
			int p = rand.nextInt(32);
			if (grid()[p] == 0) {
				grid()[p] = pickRandomly();
				break;
			}
		}
		// FIXME: Hangs if no spawn is available
	}
	
	public int pickRandomly() {
		return rand.nextInt(10) == 0 ? 2 : 1;
	}
	
	public Board spawn() {
		Board board1 = copy();
		board1.unsafe_spawn();
		return board1;
	}
	
	boolean merged[] = new boolean[36];
	public void unsafe_move(int move) {
		Arrays.fill(merged, false);
		changed = false;
		int dir = dirs[move];
		for (int src : orders[move]) {
			if (grid[src] == 0)
				continue;
			int dst = src + dir;
			// Move unto free squares
			while (grid[dst] == 0) {
				dst += dir;
			}
			// Merge
			if (grid[dst] == grid[src] && !merged[dst]) {
				grid[dst] += 1;
				merged[dst] = true;
				dst += dir;
			}
			// Normal termination
			else {
				grid[dst - dir] = grid[src];
			}
			// Move happened
			if (dst != src + dir) {
				grid[src] = 0;
				changed = true;
			}
		}
	}
	
	public Board move(int move) {
		Board board = copy();
		board.unsafe_move(move);
		return board;
	}
	
	public boolean isStuck() {
		for (int p : all) {
			if (grid()[p] == 0)
				return false;
			for (int dir : dirs) {
				if (grid()[p+dir] == grid()[p])
					return false;
			}
		}
		return true;
	}
	
	public boolean isFull() {
		for (int p : all) {
			if (grid()[p] == 0)
				return false;
		}
		return true;
	}
	
	public boolean canDirection(int move) {
		int dir = dirs[move];
		for (int p : orders[move]) {
			if (grid()[p+dir] == 0)
				return true;
			if (grid()[p+dir] == grid()[p])
				return true;
		}
		return false;
	}

	public void print() {
		for (int i = 0; i < 36; i++) {
			if (grid()[i] == 0)
				System.out.print(" .");
			if (grid()[i] > 0)
				System.out.print(" " + grid()[i]);
			if (i % 6 == 0 && i != 0 && i != 6)
				System.out.println();
		}
		System.out.println();
	}
	
	public Board copy() {
		Board copy = new Board();
		System.arraycopy(grid(), 0, copy.grid(), 0, 36);
		return copy;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(grid());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Board))
			return false;
		Board other = (Board) obj;
		if (!Arrays.equals(grid(), other.grid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String s = "";
		for (int p : all) {
			s += (char)(grid()[p]==0 ? '.' : grid()[p]+'0');
			if (p%6==4) s += " ";
		}
		return "Board [grid=" + s + "]";
	}

	public int[] grid() {
		return grid;
	}
}
