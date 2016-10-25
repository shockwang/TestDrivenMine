package mine;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import mine.Cell.CellStatus;
import mine.Cell.CellType;
import mine.exception.ExplodeException;
import mine.exception.GameClearException;

public class MineUtil {
	public static int MINE_NUM = 10;
	public static int MAP_SIZE = 9;
	public static int WINDOW_LOCATION_X = 600;
	public static int WINDOW_LOCATION_Y = 300;
	
	public static Set<Point> genMine(int mineNum, int mapSize) {
		Set<Point> mineSet = new HashSet<Point>();
		Random rand = new Random();
		int count = 0;
		while (count < mineNum) {
			Point mineLocation = new Point(rand.nextInt(mapSize), rand.nextInt(mapSize));
			if (mineSet.add(mineLocation)) {
				// this mine is unique
				count++;
			}
		}
		return mineSet;
	}
	
	public static Cell[][] genMap(Set<Point> mineSet, int mapSize) {
		Cell[][] cellArray = new Cell[mapSize + 2][mapSize + 2];
		for (int i = 0; i < mapSize + 2; i++) {
			for (int j = 0; j < mapSize + 2; j++) {
				cellArray[i][j] = new Cell(i, j);
			}
		}
		
		// set mine
		for (Point p : mineSet) {
			cellArray[p.x + 1][p.y + 1].setType(CellType.MINE);
		}
		
		// calculate mine hint
		for (int i = 1; i < mapSize + 1; i++) {
			for (int j = 1; j < mapSize + 1; j++) {
				if (cellArray[i][j].getType() == CellType.NUMBER) {
					int total = 0;
					for (int k = 0; k < 3; k++) {
						for (int l = 0; l < 3; l++) {
							if (cellArray[i + k - 1][j + l - 1].getType() == CellType.MINE) {
								total++;
							}
						}
					}
					cellArray[i][j].setNumber(total);
				}
			}
		}
		
		return cellArray;
	}
	
	public static Cell[][] openCell(Cell[][] cellArray, int x, int y) 
			throws ExplodeException, GameClearException{
		// add x & y by 1 for mapping purpose
		x++;
		y++;
		if (cellArray[x][y].getStatus() == CellStatus.OPENED || 
				cellArray[x][y].getStatus() == CellStatus.FLAGGED) {
			// do nothing
		} else {
			cellArray[x][y].setStatus(CellStatus.OPENED);
			if (cellArray[x][y].getType() == CellType.MINE) {
				throw new ExplodeException();
			} else {
				if (cellArray[x][y].getNumber() == 0) {
					Set<Cell> openSet = new HashSet<Cell>();
					openSet.add(cellArray[x][y]);
					
					Set<Cell> cellToCheck = new HashSet<Cell>();
					while (openSet.size() > 0) {
						Iterator<Cell> cellItr = openSet.iterator();
						while (cellItr.hasNext()) {
							Cell c = cellItr.next();
							for (int i = 0; i < 3; i++) {
								for (int j = 0; j < 3; j++) {
									int checkX = c.getX() + i - 1;
									int checkY = c.getY() + j - 1;
									if (checkX < 1 || checkX >= cellArray.length - 1) {
										continue;
									}
									if (checkY < 1 || checkY >= cellArray.length - 1) {
										continue;
									}
									// System.out.println(String.format("check x: %d, y: %d", checkX, checkY));
									Cell toCheck = cellArray[checkX][checkY];
									if (toCheck.getStatus() == CellStatus.CLOSED &&
											toCheck.getType() == CellType.NUMBER) {
										toCheck.setStatus(CellStatus.OPENED);
										if (toCheck.getNumber() == 0) {
											cellToCheck.add(toCheck);
										}
									}
								}
							}
							cellItr.remove();
						}
						openSet.addAll(cellToCheck);
						cellToCheck.clear();
					}
				}
			}
		}
		checkGameClear(cellArray);
		return cellArray;
	}
	
	public static void checkGameClear(Cell[][] cellArray) throws GameClearException {
		for (int i = 1; i < cellArray.length - 1; i++) {
			for (int j = 1; j < cellArray.length - 1; j++) {
				Cell c = cellArray[i][j];
				if (c.getType() == CellType.NUMBER && c.getStatus() == CellStatus.CLOSED) {
					// not done yet
					return;
				}
			}
		}
		throw new GameClearException();
	}
	
	public static void openAllCells(Cell[][] cellArray) {
		for (int i = 1; i < cellArray.length - 1; i++) {
			for (int j = 1; j < cellArray.length - 1; j++) {
				cellArray[i][j].setStatus(CellStatus.OPENED);
			}
		}
	}
	
	public static Cell[][] flagCell(Cell[][] cellArray, int x, int y) {
		// add x & y by 1 for mapping purpose
		x++;
		y++;
		
		if (cellArray[x][y].getStatus() == CellStatus.CLOSED) {
			cellArray[x][y].setStatus(CellStatus.FLAGGED);
		} else if (cellArray[x][y].getStatus() == CellStatus.FLAGGED) {
			cellArray[x][y].setStatus(CellStatus.CLOSED);
		}
		return cellArray;
	}
	
	public static Cell[][] genNewGame() {
		Set<Point> mineSet = MineUtil.genMine(MineUtil.MINE_NUM, MineUtil.MAP_SIZE);
		Cell[][] cellArray = MineUtil.genMap(mineSet, MineUtil.MAP_SIZE);
		return cellArray;
	}
}
