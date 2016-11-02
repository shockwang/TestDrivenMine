package test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import mine.Cell;
import mine.MineUtil;
import mine.Cell.CellStatus;
import mine.ai.AiUtil;
import mine.exception.ExplodeException;
import mine.exception.GameClearException;
import mine.gui.GameBoard;

import org.junit.Test;

public class AiTest {

	@Test
	public void declareMineTest() {
		Set<Point> mineSet = new HashSet<Point>();
		mineSet.add(new Point(0, 0));
		mineSet.add(new Point(4, 2));
		mineSet.add(new Point(3, 3));
		mineSet.add(new Point(4, 4));
		Cell[][] cellArray = MineUtil.genMap(mineSet, 5, 5);
		AiUtil.initAiCellArray(cellArray);
		
		try {
			cellArray = MineUtil.openCell(cellArray, 3, 0);
			AiUtil.updateAiCell(cellArray);
			cellArray = AiUtil.takeNextStep(cellArray);
			cellArray = AiUtil.takeNextStep(cellArray);
			cellArray = AiUtil.takeNextStep(cellArray);
			cellArray = AiUtil.takeNextStep(cellArray);
			cellArray = AiUtil.takeNextStep(cellArray);
			assertEquals(CellStatus.FLAGGED, cellArray[0][0].getStatus());
			assertEquals(CellStatus.FLAGGED, cellArray[4][2].getStatus());
			assertEquals(CellStatus.FLAGGED, cellArray[3][3].getStatus());
			assertEquals(CellStatus.FLAGGED, cellArray[4][4].getStatus());
		} catch (ExplodeException e) {
			fail();
		} catch (GameClearException e) {
			System.out.println("Game Clear");
			assertTrue(true);
		}
	}
	
	@Test
	public void inferenceTest() {
		Set<Point> mineSet = new HashSet<Point>();
		mineSet.add(new Point(1, 1));
		mineSet.add(new Point(2, 3));
		mineSet.add(new Point(3, 1));
		Cell[][] cellArray = MineUtil.genMap(mineSet, 5, 5);
		AiUtil.initAiCellArray(cellArray);
		
		try {
			cellArray = MineUtil.openCell(cellArray, 0, 3);
			AiUtil.updateAiCell(cellArray);
			
			cellArray = AiUtil.takeNextStep(cellArray);
			assertEquals(CellStatus.OPENED, cellArray[3][3].getStatus());
		} catch (ExplodeException | GameClearException e) {
			fail();
		} 
	}
	
	@Test
	public void randomTest() {
		int mapSizeX = 16;
		int mapSizeY = 16;
		int mineNum = 40;
		
		int gameCount = 0;
		int win = 0;
		int lose = 0;
		int loseAtBeginning = 0;
		int stepCount = 0;
		
		while (gameCount < 1000) {
			Set<Point> mineSet = MineUtil.genMine(mineNum, mapSizeX, mapSizeY);
			/*for (Point p : mineSet) {
				System.out.print(String.format("(%d, %d), ", p.x, p.y));
			}
			System.out.println();*/
			Cell[][] cellArray = MineUtil.genMap(mineSet, mapSizeX, mapSizeY);
			AiUtil.initAiCellArray(cellArray);
			
			try {
				stepCount = 0;
				while (true) {
					AiUtil.takeNextStep(cellArray);
					stepCount++;
				}
			} catch (ExplodeException e) {
				if (stepCount < 3) {
					loseAtBeginning++;
				}
				lose++;
			} catch (GameClearException e) {
				win++;
			}
			gameCount++;
			
			/*cellArray = MineUtil.genMap(mineSet, 8, 8);
			new GameBoard(cellArray);
			try {
				Thread.sleep(86400000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		System.out.println("win: " + win + ", lose: " + lose);
		System.out.println("lose at the beginning: " + loseAtBeginning);
		int winRatio = win * 100 / (1000 - loseAtBeginning);
		System.out.println("win ratio except die at beginning: " + winRatio);
	}
}
