package dk.ahle.thomas.mcts2048.test;

import static dk.ahle.thomas.mcts2048.test.TestUtils.parse;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dk.ahle.thomas.mcts2048.measure.FreesMeasure;
import dk.ahle.thomas.mcts2048.measure.Measure;
import dk.ahle.thomas.mcts2048.measure.SmoothMeasure;
import dk.ahle.thomas.mcts2048.measure.SumMeasure;

public class MeasureTest {
	@Test
	public void testSmoothPow() {
		Measure m = new SmoothMeasure("pow");
		assertEquals(4, m.score(parse("1...", "....", "....", "....")), 1e-6);
		assertEquals(30+14, m.score(parse("1234", "....", "....", "....")), 1e-6);
		assertEquals(4*14, m.score(parse("1234", "1234", "1234", "1234")), 1e-6);
	}
	
	@Test
	public void testSmoothId() {
		Measure m = new SmoothMeasure("id");
		assertEquals(2, m.score(parse("1...", "....", "....", "....")), 1e-6);
		assertEquals(10+3, m.score(parse("1234", "....", "....", "....")), 1e-6);
		assertEquals(4*3, m.score(parse("1234", "1234", "1234", "1234")), 1e-6);
	}
	
	@Test
	public void testSum() {
		Measure m = new SumMeasure();
		assertEquals(2, m.score(parse("1...", "....", "....", "....")), 1e-6);
		assertEquals(30, m.score(parse("1234", "....", "....", "....")), 1e-6);
		assertEquals(4*30, m.score(parse("1234", "1234", "1234", "1234")), 1e-6);
	}
	
	@Test
	public void testFrees() {
		Measure m = new FreesMeasure();
		assertEquals(15, m.score(parse("1...", "....", "....", "....")), 1e-6);
		assertEquals(12, m.score(parse("1234", "....", "....", "....")), 1e-6);
		assertEquals(0, m.score(parse("1234", "1234", "1234", "1234")), 1e-6);
	}
}
