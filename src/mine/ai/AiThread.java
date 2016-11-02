package mine.ai;

import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mine.Cell;
import mine.MineUtil;
import mine.exception.ExplodeException;
import mine.exception.GameClearException;
import mine.gui.GameBoard;

public class AiThread extends Thread {
	private Cell[][] cellArray;
	private JButton[][] buttonList;
	private JFrame frame;
	
	public AiThread(Cell[][] cellArray, JButton[][] buttonList, JFrame frame) {
		this.cellArray = cellArray;
		this.buttonList = buttonList;
		this.frame = frame;
	}
	
	@Override
	public void run() {
		// make button no use
		for (int i = 0; i < buttonList.length; i++) {
			for (int j = 0; j < buttonList[0].length; j++) {
				MouseListener[] listenerList = buttonList[i][j].getMouseListeners();
				for (MouseListener l : listenerList) {
					buttonList[i][j].removeMouseListener(l);
				}
			}
		}
		
		AiUtil.initAiCellArray(cellArray);
		AiUtil.updateAiCell(cellArray);
		
		while (true) {
			try {
				AiUtil.takeNextStep(cellArray);
				GameBoard.updateGameBoard(cellArray, buttonList);
				Thread.sleep(1000);
			} catch (ExplodeException e1) {
				JOptionPane.showMessageDialog(frame, "真不幸~AI輸了 = (");
				MineUtil.openAllCells(cellArray);
				break;
			} catch (GameClearException e1) {
				JOptionPane.showMessageDialog(frame, "AI成功破關了!");
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		GameBoard.updateGameBoard(cellArray, buttonList);
		GameBoard.disableAllButtons(this.buttonList);
	}
}
