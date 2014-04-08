package dk.ahle.thomas.mcts2048;

import java.util.Random;

/**
 * Format:
 * [63-60][59-56][55-52][51-48]
 * [47-44][43-40][39-36][35-32]
 * [31-28][27-24][23-20][19-16]
 * [15-12][11- 8][ 7- 4][ 3- 0]
 */

public class BitBoards {
	private static Random rand = new Random();

	public final static int UP = 0;
	public final static int RIGHT = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int moves[] = { UP, RIGHT, DOWN, LEFT };

	private final static short[] move_table = new short[0x10000];
	
	static {
		Board board = new Board();
		for (int row = 0; row <= 0xffff; row++) {
			board.grid()[Board.all[0]] = row & 0xf;
			board.grid()[Board.all[1]] = row >>> 4 & 0xf;
			board.grid()[Board.all[2]] = row >>> 8 & 0xf;
			board.grid()[Board.all[3]] = row >>> 12 & 0xf;
			board = board.move(Board.RIGHT);
			short out = (short)board.grid()[Board.all[0]];
			out |= (short)board.grid()[Board.all[1]] << 4;
			out |= (short)board.grid()[Board.all[2]] << 8;
			out |= (short)board.grid()[Board.all[3]] << 12;
			move_table[row] = out;
		}
	}
	
	/**
	 * Transposes the board, one diagonal at the time.
	 */
	public static long trans(long board) {
		long res = 0;
		res |= board <<  4*9 & 0x000f000000000000L;
		res |= board <<  4*6 & 0x00f0000f00000000L;
		res |= board <<  4*3 & 0x0f0000f0000f0000L;
		res |= board         & 0xf0000f0000f0000fL;
		res |= board >>> 4*3 & 0x0000f0000f0000f0L;
		res |= board >>> 4*6 & 0x00000000f0000f00L;
		res |= board >>> 4*9 & 0x000000000000f000L;
		return res;
	}
	
	/**
	 * Reverses the board along the row axis.
	 */
	public static long reverse(long board) {
		board = (board << 8 & 0xff00ff00ff00ff00L)
				| (board >>> 8 & 0x00ff00ff00ff00ffL);
		board = (board << 4 & 0xf0f0f0f0f0f0f0f0L)
				| (board >>> 4 & 0x0f0f0f0f0f0f0f0fL);
		return board;
	}
	
	/**
	 * Isolates the row'th row, moves it with the move_table, and shifts it
	 * back.
	 */
	private static long move_row_right(long board, int row) {
		int b = (int)(board >>> 16*row) & 0xffff;
		// move_table is a short[], so to avoid sign issues, we need to and with 0xffff
		board = (long)move_table[b] & 0xffff;
		return board << 16*row;
	}
	
	private static long move_up(long board) {
		return trans(move_left(trans(board)));
	}
	
	private static long move_right(long board) {
		return move_row_right(board, 0)
				| move_row_right(board, 1)
				| move_row_right(board, 2)
				| move_row_right(board, 3);
	}
	
	private static long move_down(long board) {
		return trans(move_right(trans(board)));
	}
	
	private static long move_left(long board) {
		return reverse(move_right(reverse(board)));
	}
	
	public static long move(long board, int move) {
		switch (move) {
		case UP:
			return move_up(board);
		case RIGHT:
			return move_right(board);
		case DOWN:
			return move_down(board);
		case LEFT:
			return move_left(board);
		default:
			return 0;
		}
	}

	public static int frees(long b) {
		int free = 0;
		while (b != 0) {
			free += ~(b | b>>>1 | b>>>2 | b>>>3) & 1;
			b >>>= 4;
		}
		return free;
	}
	
	public static long spawn(long board) {
		// asserts there is a free spot 
		int p = rand.nextInt(frees(board));
		int i = 0;
		while (p != -1) {
			if ((board >>> i*4 & 0xf) == 0)
				p--;
			i++;
		}
		return board | pickRandomly() << 4*(i-1);
	}
	
	public static long pickRandomly() {
		return rand.nextInt(10) == 0 ? 2 : 1;
	}

	/**
	 * Not a very performant way of doing this.
	 */
	public static boolean isStuck(long board) {
		boolean res = false;
		for (int move = 0; move < 4; move++) {
			res |= canDirection(board, move);
		}
		return res;
	}

	public static boolean canDirection(long board, int move) {
		return move(board, move) != board;
	}

	public static void print(long board) {
		for (int i = 0; i < 16; i++) {
			long val = board >>> 4*i & 0xf;
			if (val == 0)
				System.out.print(" .");
			if (val > 0)
				System.out.print(" " + val);
			if ((i+1) % 4 == 0)
				System.out.println();
		}
		System.out.println();
	}
}
