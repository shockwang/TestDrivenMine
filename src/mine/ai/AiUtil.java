package mine.ai;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import mine.Cell;
import mine.Cell.CellStatus;
import mine.MineUtil;
import mine.ai.AiCell.AiAction;
import mine.ai.AiCell.AiStatus;
import mine.exception.ExplodeException;
import mine.exception.GameClearException;

public class AiUtil {
	private static AiCell[][] aiCellArray = null;
	private static LinkedList<AiCell> actionList = new LinkedList<AiCell>();
	private static AiCell worthTry = null;
	
	public static void initAiCellArray(Cell[][] cellArray) {
		actionList.clear();
		aiCellArray = new AiCell[cellArray.length - 2][cellArray[0].length - 2];
		for (int i = 0; i < aiCellArray.length; i++) {
			for (int j = 0; j < aiCellArray[0].length; j++) {
				aiCellArray[i][j] = new AiCell(cellArray[i + 1][j + 1]);
			}
		}
	}
	
	public static Point takeNextStep(Cell[][] cellArray) throws ExplodeException, GameClearException {
		AiCell target = null;
		
		if (!actionList.isEmpty()) {
			target = actionList.pop();
			/*System.out.println("sure action: " + target.getAiAction());
			System.out.println(String.format("x: %d, y: %d", target.getRelatedCell().getX() - 1, 
					target.getRelatedCell().getY() - 1));*/
			doAiAction(cellArray, target);
			if (target == worthTry) {
				worthTry = null;
			}
		} else if (worthTry != null && worthTry.getMineProbability() < 20) {
			/*System.out.println("guess action");
			System.out.println(String.format("x: %d, y: %d", worthTry.getRelatedCell().getX() - 1, 
					worthTry.getRelatedCell().getY() - 1));*/
			worthTry.setAiAction(AiAction.OPEN);
			doAiAction(cellArray, worthTry);
			target = worthTry;
			worthTry = null;
		} else {
			target = clickOnCorner();
			if (target == null) {
				target = randomClick();
			}
			/*System.out.println("random click");
			System.out.println(String.format("x: %d, y: %d", target.getRelatedCell().getX() - 1, 
					target.getRelatedCell().getY() - 1));*/
			target.setAiAction(AiAction.OPEN);
			doAiAction(cellArray, target);
		}
		//System.out.println("updated");
		updateAiCell(cellArray);
		return new Point(target.getRelatedCell().getX() - 1, target.getRelatedCell().getY() - 1);
	}
	
	private static AiCell clickOnCorner() {
		AiCell target = null;
		if (aiCellArray[0][0].getRelatedCell().getStatus() == CellStatus.CLOSED) {
			target = aiCellArray[0][0];
		} else if (aiCellArray[0][aiCellArray[0].length - 1].getRelatedCell().getStatus() == CellStatus.CLOSED) {
			target = aiCellArray[0][aiCellArray[0].length - 1];
		} else if (aiCellArray[aiCellArray.length - 1][0].getRelatedCell().getStatus() == CellStatus.CLOSED) {
			target = aiCellArray[aiCellArray.length - 1][0];
		} else if (aiCellArray[aiCellArray.length - 1][aiCellArray[0].length - 1].getRelatedCell().getStatus() 
				== CellStatus.CLOSED) {
			target = aiCellArray[aiCellArray.length - 1][aiCellArray[0].length - 1];
		}
		
		return target;
	}
	
	private static AiCell randomClick() {
		Random rand = new Random();
		AiCell target = null;
		do {
			int x = rand.nextInt(aiCellArray.length);
			int y = rand.nextInt(aiCellArray[0].length);
			if (aiCellArray[x][y].getRelatedCell().getStatus() == CellStatus.CLOSED) {
				target = aiCellArray[x][y];
			}
		} while (target == null);
		return target;
	}
	
	public static void updateAiCell(Cell[][] cellArray) {
		for (int i = 1; i < cellArray.length - 1; i++) {
			for (int j = 1; j < cellArray[0].length - 1; j++) {
				if (cellArray[i][j].getStatus() == CellStatus.OPENED && 
						cellArray[i][j].getNumber() != 0 &&
						aiCellArray[i - 1][j - 1].getAiStatus() == AiStatus.UNDONE) {
					// calculate mine probability
					int knownMineNum = 0;
					
					Set<AiCell> unhandledAiCellSet = new HashSet<AiCell>();
					for (int k = 0; k < 3; k++) {
						for (int l = 0; l < 3; l++) {
							int x = i + k - 1;
							int y = j + l - 1;
							if (x < 1 || x > cellArray.length - 2) {
								continue;
							} else if (y < 1 || y > cellArray[0].length - 2) {
								continue;
							}
							
							if (cellArray[x][y].getStatus() == CellStatus.CLOSED) {
								unhandledAiCellSet.add(aiCellArray[x - 1][y - 1]);
							} else if (cellArray[x][y].getStatus() == CellStatus.FLAGGED) {
								knownMineNum++;
							}
						}
					}
					if (unhandledAiCellSet.isEmpty()) {
						aiCellArray[i - 1][j - 1].setAiStatus(AiStatus.DONE);
					} else {
						int possibleMineNum = cellArray[i][j].getNumber() - knownMineNum;
						assert(possibleMineNum >= 0);
						if (possibleMineNum == 0) {
							for (AiCell c : unhandledAiCellSet) {
								if (c.getAiAction() == null) {
									c.setAiAction(AiAction.OPEN);
									actionList.push(c);
								}
							}
						} else if (possibleMineNum == unhandledAiCellSet.size()) {
							for (AiCell c : unhandledAiCellSet) {
								if (c.getAiAction() == null) {
									c.setAiAction(AiAction.FLAG);
									actionList.push(c);
								}
							}
						} else {
							int mineProb = possibleMineNum * 100 / unhandledAiCellSet.size();
							for (AiCell c : unhandledAiCellSet) {
								if (mineProb < c.getMineProbability()) {
									c.setMineProbability(mineProb);
								}
								
								// update guess candidate
								if (worthTry == null || c.getMineProbability() < worthTry.getMineProbability()) {
									worthTry = c;
								} 
							}
							
							// set inference info
							if (aiCellArray[i - 1][j - 1].getInferenceInfo() == null) {
								aiCellArray[i - 1][j - 1].setInferenceInfo(
										new InferenceInfo(possibleMineNum, unhandledAiCellSet));
							} else {
								aiCellArray[i - 1][j - 1].getInferenceInfo().mineNum = possibleMineNum;
								aiCellArray[i - 1][j - 1].getInferenceInfo().aiCellSet = unhandledAiCellSet;
							}
						}
					}
				}
			}
		}
		
		if (actionList.isEmpty()) {
			// try inference
			for (int i = 1; i < cellArray.length - 1; i++) {
				for (int j = 1; j < cellArray[0].length - 1; j++) {
					if (cellArray[i][j].getStatus() == CellStatus.OPENED && 
							cellArray[i][j].getNumber() != 0 &&
							aiCellArray[i - 1][j - 1].getAiStatus() == AiStatus.UNDONE) {
						for (int k = 0; k < 5; k++) {
							for (int l = 0; l < 5; l++) {
								int x = i + k - 2;
								int y = j + l - 2;
								
								if (x < 1 || x > cellArray.length - 2) {
									continue;
								} else if (y < 1 || y > cellArray[0].length - 2) {
									continue;
								} else if (cellArray[x][y].getStatus() == CellStatus.CLOSED || 
										cellArray[x][y].getNumber() == 0 ||
										aiCellArray[x - 1][y - 1].getAiStatus() == AiStatus.DONE) {
									continue;
								}
								
								if (aiCellArray[i - 1][j - 1].getInferenceInfo().aiCellSet
										.containsAll(aiCellArray[x - 1][y - 1].getInferenceInfo().aiCellSet)) {
									int leftCellNum = aiCellArray[i - 1][j - 1].getInferenceInfo().aiCellSet.size()
											- aiCellArray[x - 1][y - 1].getInferenceInfo().aiCellSet.size();
									assert(leftCellNum >= 0);
									int leftMineNum = aiCellArray[i - 1][j - 1].getInferenceInfo().mineNum
											- aiCellArray[x - 1][y - 1].getInferenceInfo().mineNum;
									assert(leftMineNum >= 0);
									if (leftCellNum > 0) {
										Set<AiCell> srcSet = aiCellArray[i - 1][j - 1].getInferenceInfo().aiCellSet;
										Set<AiCell> compareSet = aiCellArray[x - 1][y - 1].getInferenceInfo().aiCellSet;
										
										Set<AiCell> diffSet = new HashSet<AiCell>(srcSet);
										diffSet.removeAll(compareSet);
										
										if (leftMineNum == 0) {
											for (AiCell c : diffSet) {
												if (c.getAiAction() == null) {
													c.setAiAction(AiAction.OPEN);
													actionList.push(c);
												}
											}
										} else if (leftCellNum == leftMineNum) {
											for (AiCell c : diffSet) {
												if (c.getAiAction() == null) {
													c.setAiAction(AiAction.FLAG);
													actionList.push(c);
												}
											}
										} else {
											int prob = leftMineNum * 100 / leftCellNum;
											for (AiCell c : diffSet) {
												if (c.getMineProbability() > prob) {
													c.setMineProbability(prob);
												}
												
												// update guess candidate
												if (worthTry == null 
														|| c.getMineProbability() < worthTry.getMineProbability()) {
													worthTry = c;
												} 
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static void doAiAction(Cell[][] cellArray, AiCell aiCell) throws ExplodeException, GameClearException {
		switch (aiCell.getAiAction()) {
		case OPEN:
			MineUtil.openCell(cellArray, aiCell.getRelatedCell().getX() - 1, aiCell.getRelatedCell().getY() - 1);
			break;
		case FLAG:
			aiCell.getRelatedCell().setStatus(CellStatus.FLAGGED);
			break;
		}
	}
}
