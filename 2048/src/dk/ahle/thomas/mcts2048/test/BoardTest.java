package dk.ahle.thomas.mcts2048.test;

import static dk.ahle.thomas.mcts2048.test.TestUtils.parse;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dk.ahle.thomas.mcts2048.Board;


public class BoardTest {

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
}
