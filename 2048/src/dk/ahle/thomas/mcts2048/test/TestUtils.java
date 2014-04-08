package dk.ahle.thomas.mcts2048.test;

import dk.ahle.thomas.mcts2048.Board;

public class TestUtils {

	public static Board parse(String... ss) {
		String r = "";
		for (String s : ss)
			r += s;
		return parse(r);
	}

	private static Board parse(String s) {
		Board board = new Board();
		for (int i = 0; i < 16; i++) {
			char c = s.charAt(i);
			if (c != '.')
				board.grid()[Board.all[i]] = c - '0';
		}
		return board;
	}
	
	public static long parseb(String... ss) {
		Board board = parse(ss);
		long res = 0;
		for (int i = 15; i >= 0; i--) {
			res <<= 4;
			res |= board.grid()[Board.all[i]];
		}
		return res;
	}
}
