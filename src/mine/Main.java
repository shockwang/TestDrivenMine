package mine;

import mine.gui.GameBoard;

public class Main {
	public static void main(String[] args) {
		Cell[][] cellArray = MineUtil.genNewGame();
		new GameBoard(cellArray);
	}
}
