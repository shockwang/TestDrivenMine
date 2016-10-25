package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import mine.Cell;
import mine.MineUtil;
import mine.Cell.CellStatus;
import mine.exception.ExplodeException;
import mine.exception.GameClearException;

import org.junit.Test;

public class GameBoardTest {
	
	@Test
	public void generateMineTest() {
		Set<Point> mineSet = MineUtil.genMine(10, 9, 9);
		assertEquals(mineSet.size(), 10);
		for (Point p : mineSet) {
			System.out.println("x=" + p.x + ", " + "y=" + p.y);
			assertTrue(p.x < 9);
			assertTrue(p.y < 9);
		}
		System.out.println();
		
		mineSet = MineUtil.genMine(15, 13, 12);
		assertEquals(mineSet.size(), 15);
		for (Point p : mineSet) {
			System.out.println("x=" + p.x + ", " + "y=" + p.y);
			assertTrue(p.x < 13);
			assertTrue(p.y < 12);
		}
		System.out.println();
		
		mineSet = MineUtil.genMine(50, 25, 30);
		assertEquals(mineSet.size(), 50);
		for (Point p : mineSet) {
			System.out.println("x=" + p.x + ", " + "y=" + p.y);
			assertTrue(p.x < 25);
			assertTrue(p.y < 30);
		}
		System.out.println();
	}
	
	@Test
	public void calculateHintTest() {
		int[][] board = {
				{-1, 1, 0},
				{2, 2, 1},
				{1, -1, 1}
		};
		Set<Point> mineSet = new HashSet<Point>();
		mineSet.add(new Point(0, 0));
		mineSet.add(new Point(1, 2));
		Cell[][] cellArray = MineUtil.genMap(mineSet, 3, 3);
		calculateHintTestHelper(cellArray, board);
		
		board = new int[][]{
				{-1, 2, 1},
				{2, -1, 2},
				{1, 2, -1}
		};
		mineSet.clear();
		mineSet.add(new Point(0, 0));
		mineSet.add(new Point(1, 1));
		mineSet.add(new Point(2, 2));
		cellArray = MineUtil.genMap(mineSet, 3, 3);
		calculateHintTestHelper(cellArray, board);
		
		board = new int[][]{
				{-1, -1, -1},
				{2, 3, 2},
				{0, 0, 0}
		};
		mineSet.clear();
		mineSet.add(new Point(0, 0));
		mineSet.add(new Point(1, 0));
		mineSet.add(new Point(2, 0));
		cellArray = MineUtil.genMap(mineSet, 3, 3);
		calculateHintTestHelper(cellArray, board);
		
		board = new int[][]{
				{1, 1, 2, 1, 1},
				{1, -1, 3, -1, 1},
				{2, 4, -1, 4, 2},
				{2, -1, -1, -1, 2},
				{2, -1, -1, -1, 2}
		};
		mineSet.clear();
		mineSet.add(new Point(1, 1));
		mineSet.add(new Point(3, 1));
		mineSet.add(new Point(2, 2));
		mineSet.add(new Point(1, 3));
		mineSet.add(new Point(2, 3));
		mineSet.add(new Point(3, 3));
		mineSet.add(new Point(1, 4));
		mineSet.add(new Point(2, 4));
		mineSet.add(new Point(3, 4));
		cellArray = MineUtil.genMap(mineSet, 5, 5);
		calculateHintTestHelper(cellArray, board);
		
		board = new int[][]{
				{1, 2, 3, 2, 1},
				{2, -1, -1, -1, 2},
				{3, -1, 8, -1, 3},
				{2, -1, -1, -1, 2},
				{1, 2, 3, 2, 1}
		};
		mineSet.clear();
		mineSet.add(new Point(1, 1));
		mineSet.add(new Point(2, 1));
		mineSet.add(new Point(3, 1));
		mineSet.add(new Point(1, 2));
		mineSet.add(new Point(3, 2));
		mineSet.add(new Point(1, 3));
		mineSet.add(new Point(2, 3));
		mineSet.add(new Point(3, 3));
		cellArray = MineUtil.genMap(mineSet, 5, 5);
		calculateHintTestHelper(cellArray, board);
	}
	
	private static void calculateHintTestHelper(Cell[][] cellArray, int[][] board) {
		int checkNumber;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[j][i] == -1) {
					checkNumber = 0;
				} else {
					checkNumber = board[j][i];
				}
				System.out.println("should be: " + checkNumber + ", actually: " + 
						cellArray[i + 1][j + 1].getNumber());
				assertEquals(cellArray[i + 1][j + 1].getNumber(), checkNumber);
			}
		}
	}
	
	@Test
	public void openCellTest() {
		Set<Point> mineSet = new HashSet<Point>();
		mineSet.add(new Point(0, 0));
		mineSet.add(new Point(1, 0));
		mineSet.add(new Point(2, 0));
		Cell[][] cellArray = MineUtil.genMap(mineSet, 3, 3);
		
		int[][] checkBoard = new int[][]{
				{0, 0, 0},
				{1, 1, 1},
				{1, 1, 1}
		};
		try {
			cellArray = MineUtil.openCell(cellArray, 0, 2);
		} catch (ExplodeException e) {
			fail();
		} catch (GameClearException e) {
			// do nothing
		}
		openCellTestHelper(cellArray, checkBoard);
		
		mineSet.clear();
		mineSet.add(new Point(4, 0));
		mineSet.add(new Point(4, 1));
		mineSet.add(new Point(4, 2));
		mineSet.add(new Point(4, 3));
		mineSet.add(new Point(4, 4));
		mineSet.add(new Point(3, 2));
		mineSet.add(new Point(2, 2));
		cellArray = MineUtil.genMap(mineSet, 5, 5);
		checkBoard = new int[][]{
				{1, 1, 1, 1, 0},
				{1, 1, 1, 1, 0},
				{1, 1, 0, 0, 0},
				{1, 1, 1, 1, 0},
				{1, 1, 1, 1, 0}
		};
		try {
			cellArray = MineUtil.openCell(cellArray, 0, 0);
		} catch (ExplodeException e) {
			fail();
		} catch (GameClearException e) {
			// do nothing
		}
		openCellTestHelper(cellArray, checkBoard);
		
		// click on a mine
		boolean exception = false;
		try {
			cellArray = MineUtil.openCell(cellArray, 4, 4);
		} catch (ExplodeException | GameClearException e) {
			exception = true;
		}
		assertEquals(true, exception);
		
		mineSet.clear();
		mineSet.add(new Point(0, 2));
		mineSet.add(new Point(1, 2));
		mineSet.add(new Point(2, 2));
		mineSet.add(new Point(3, 2));
		mineSet.add(new Point(4, 2));
		cellArray = MineUtil.genMap(mineSet, 5, 5);
		checkBoard = new int[][]{
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1}
		};
		try {
			cellArray = MineUtil.openCell(cellArray, 4, 4);
		} catch (ExplodeException | GameClearException e) {
			fail();
		}
		openCellTestHelper(cellArray, checkBoard);
	}
	
	private static void openCellTestHelper(Cell[][] cellArray, int[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				CellStatus status;
				if (board[j][i] == 0) {
					status = CellStatus.CLOSED;
				} else {
					status = CellStatus.OPENED;
				}
				System.out.println(String.format("x: %d, y: %d", i, j));
				assertEquals(status, cellArray[i + 1][j + 1].getStatus());
			}
		}
	}
	
	@Test
	public void checkGameClearTest() {
		Set<Point> mineSet = new HashSet<Point>();
		mineSet.add(new Point(0, 0));
		Cell[][] cellArray = MineUtil.genMap(mineSet, 2, 2);
		try {
			cellArray = MineUtil.openCell(cellArray, 1, 0);
			cellArray = MineUtil.openCell(cellArray, 0, 1);
		} catch (ExplodeException | GameClearException e) {
			fail();
		} 
		
		boolean done = false;
		try {
			MineUtil.checkGameClear(cellArray);
		} catch (GameClearException e) {
			done = true;
		}
		assertEquals(false, done);
		
		try {
			cellArray = MineUtil.openCell(cellArray, 1, 1);
		} catch (ExplodeException e1) {
			fail();
		} catch (GameClearException e) {
			// do nothing
		}
		
		try {
			MineUtil.checkGameClear(cellArray);
		} catch (GameClearException e) {
			done = true;
		}
		assertEquals(true, done);
	}
}
