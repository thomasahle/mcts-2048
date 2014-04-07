package dk.ahle.thomas.mcts2048.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dk.ahle.thomas.mcts2048.Board;

public class MarkovTest {

	@Test
	public void testEquals() {
		assertEquals(
				parse("1...", "....", "....", "...."),
				parse("1...", "....", "....", "...."));
	}
	
	@Test
	public void testMoves() {
		assertEquals(
				parse("1111", "....", "....", "...."),
				parse("....", "....", "....", "1111").move(Board.UP));
		assertEquals(
				parse("....", "....", "....", "..22"),
				parse("....", "....", "....", "1111").move(Board.RIGHT));
		assertEquals(
			 	parse("....", "....", "....", "3..."),
			 	parse("....", "....", "....", "1111").move(Board.LEFT).move(Board.LEFT));
		assertEquals(
			 	parse("....", "....", "....", "2..."),
			 	parse("1...", "....", "1...", "....").move(Board.DOWN));
		assertEquals(
			 	parse("....", "....", "1...", "2..."),
			 	parse("1...", "....", "2...", "....").move(Board.DOWN));
	}

	private Board parse(String... ss) {
		String r = "";
		for (String s : ss)
			r += s;
		return parse(r);
	}

	private Board parse(String s) {
		Board board = new Board();
		for (int i = 0; i < 16; i++) {
			char c = s.charAt(i);
			if (c != '.')
				board.grid()[Board.all[i]] = c - '0';
		}
		return board;
	}
}
