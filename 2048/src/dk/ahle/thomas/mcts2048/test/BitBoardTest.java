package dk.ahle.thomas.mcts2048.test;

import static org.junit.Assert.assertEquals;
import static dk.ahle.thomas.mcts2048.test.TestUtils.*;
import static dk.ahle.thomas.mcts2048.BitBoards.*;

import org.junit.Test;

import dk.ahle.thomas.mcts2048.Board;

public class BitBoardTest {

	@Test
	public void testReverse() {
		assertEquals(
						parseb("...1", "..3.", ".5..", "7..."),
				reverse(parseb("1...", ".3..", "..5.", "...7")));
	}
	
	@Test
	public void testFlip() {
		assertEquals(
					  parseb("1...", "2...", "3...", "4567"),
				trans(parseb("1234", "...5", "...6", "...7")));
	}
	
	@Test
	public void testMoves() {
		assertEquals(
				parseb("....", "....", "....", "..22"),
				move(parseb("....", "....", "....", "1111"),Board.RIGHT));
		assertEquals(
				parseb("1111", "....", "....", "...."),
				move(parseb("....", "....", "....", "1111"),Board.UP));
		assertEquals(
			 	parseb("....", "....", "....", "3..."),
			 	move(move(parseb("....", "....", "....", "1111"),Board.LEFT),Board.LEFT));
		assertEquals(
			 	parseb("....", "....", "....", "2..."),
			 	move(parseb("1...", "....", "1...", "...."),Board.DOWN));
		assertEquals(
			 	parseb("....", "....", "1...", "2..."),
			 	move(parseb("1...", "....", "2...", "...."),Board.DOWN));
		assertEquals(
				parseb("....", "51..", "242.", "4642"),
			 	move(move(move(move(parseb("2351", "4632", ".4..", ".11."),Board.UP),Board.RIGHT),Board.DOWN),Board.LEFT));
	}
}
