package mine.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mine.Cell;
import mine.Cell.CellStatus;
import mine.Cell.CellType;
import mine.MineUtil;
import mine.ai.AiThread;
import mine.exception.ExplodeException;
import mine.exception.GameClearException;

public class GameBoard {
	
	public GameBoard(final Cell[][] cellArray) {
		final JFrame frame = new JFrame();
		frame.setSize(50 * MineUtil.MAP_SIZE_X, 50 * MineUtil.MAP_SIZE_Y);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(MineUtil.WINDOW_LOCATION_X, MineUtil.WINDOW_LOCATION_Y);
		
		// set menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("功能");
		JMenuItem menuItem = new JMenuItem("另開新局");
		menuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame(frame);
			}
		});
		menu.add(menuItem);
		menuItem = new JMenuItem("設定遊戲變數");
		menuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFrame popUp = new JFrame();
				popUp.setSize(300,  200);
				popUp.setLocation(600, 300);
				popUp.setResizable(false);
				popUp.setLayout(new GridLayout(4, 1));
				
				JPanel pane = new JPanel();
				JLabel label = new JLabel("棋盤邊長 (x):");
				final JTextField mapSizeXInput = new JTextField(Integer.toString(MineUtil.MAP_SIZE_X));
				mapSizeXInput.setColumns(10);
				pane.add(label);
				pane.add(mapSizeXInput);
				popUp.add(pane);
				
				pane = new JPanel();
				label = new JLabel("棋盤邊長 (y):");
				final JTextField mapSizeYInput = new JTextField(Integer.toString(MineUtil.MAP_SIZE_Y));
				mapSizeYInput.setColumns(10);
				pane.add(label);
				pane.add(mapSizeYInput);
				popUp.add(pane);
				
				pane = new JPanel();
				label = new JLabel("地雷數量:");
				final JTextField mineNumInput = new JTextField(Integer.toString(MineUtil.MINE_NUM));
				mineNumInput.setColumns(10);
				pane.add(label);
				pane.add(mineNumInput);
				popUp.add(pane);
				
				pane = new JPanel();
				JButton button = new JButton("確定");
				button.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent event) {
						try {
							int newMapSizeX = Integer.parseInt(mapSizeXInput.getText());
							int newMapSizeY = Integer.parseInt(mapSizeYInput.getText());
							int newMineNum = Integer.parseInt(mineNumInput.getText());
							if (newMapSizeX <= 0 || newMapSizeY <= 0 || newMineNum <= 0) {
								JOptionPane.showMessageDialog(popUp, "請輸入大於0的數字.");
							} else if (newMineNum >= newMapSizeX * newMapSizeY) {
								JOptionPane.showMessageDialog(popUp, "地雷數量請勿大於棋盤大小.");
							} else if (newMapSizeX > 100 || newMapSizeY > 100) {
								JOptionPane.showMessageDialog(popUp, "地圖設定這麼大的話你也看不到字, 還是算了吧...");
							} else {
								MineUtil.MAP_SIZE_X = newMapSizeX;
								MineUtil.MAP_SIZE_Y = newMapSizeY;
								MineUtil.MINE_NUM = newMineNum;
								popUp.dispose();
								
								startNewGame(frame);
							}
						} catch (Exception e) {
							JOptionPane.showMessageDialog(popUp, "請輸入整數.");
							e.printStackTrace();
						}
					}
					
				});
				pane.add(button);
				button = new JButton("取消");
				button.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						popUp.dispose();
					}
					
				});
				pane.add(button);
				popUp.add(pane);
				
				popUp.setVisible(true);
			}
			
		});
		menu.add(menuItem);
		menuBar.add(menu);
		
		// declare here for AI access
		final JButton[][] buttonList = new JButton[MineUtil.MAP_SIZE_X][MineUtil.MAP_SIZE_Y];
		
		// AI menu
		menu = new JMenu("AI");
		menuItem = new JMenuItem("看AI玩");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AiThread(cellArray, buttonList, frame).start();
			}
			
		});
		menu.add(menuItem);
		menuBar.add(menu);
		
		frame.setJMenuBar(menuBar);
		
		// set game board
		JPanel arrayView = new JPanel();
		arrayView.setLayout(new GridLayout(MineUtil.MAP_SIZE_Y, MineUtil.MAP_SIZE_X));
		for (int i = 0; i < MineUtil.MAP_SIZE_Y; i++) {
			for (int j = 0; j < MineUtil.MAP_SIZE_X; j++) {
				final int x = j;
				final int y = i;
				final JButton b = new JButton();
				b.addMouseListener(new MouseListener(){

					@Override
					public void mouseClicked(MouseEvent event) {
						if (SwingUtilities.isLeftMouseButton(event)) {
							if (b.isEnabled() && cellArray[x + 1][y + 1].getStatus() != CellStatus.FLAGGED) {
								boolean done = false;
								try {
									MineUtil.openCell(cellArray, x, y);
								} catch (ExplodeException e) {
									JOptionPane.showMessageDialog(frame, "很不幸的, 你踩到地雷了! Game Over~");
									MineUtil.openAllCells(cellArray);
									done = true;
								} catch (GameClearException e) {
									JOptionPane.showMessageDialog(frame, "恭喜, 你贏了!");
									done = true;
								}
								b.setEnabled(false);
								if (done) {
									disableAllButtons(buttonList);
								}
							}
						} else if (SwingUtilities.isRightMouseButton(event)) {
							MineUtil.flagCell(cellArray, x, y);
						}
						updateGameBoard(cellArray, buttonList);
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// do nothing
					}
					
				});
				b.setFont(new Font("Courier New", Font.ITALIC, 24));
				b.setText("*");
				arrayView.add(b);
				buttonList[j][i] = b;
			}
		}
		JScrollPane scrollPane = new JScrollPane(arrayView);
		frame.add(scrollPane);
		
		frame.setVisible(true);
	}
	
	public static void updateGameBoard(Cell[][] cellArray, JButton[][] buttonList){
		for (int i = 0; i < buttonList[0].length; i++) {
			for (int j = 0; j < buttonList.length; j++) {
				if (cellArray[j + 1][i + 1].getStatus() == CellStatus.CLOSED) {
					buttonList[j][i].setText("*");
				} else if (cellArray[j + 1][i + 1].getStatus() == CellStatus.FLAGGED) {
					buttonList[j][i].setText("F");
				} else {
					buttonList[j][i].setEnabled(false);
					if (cellArray[j + 1][i + 1].getType() == CellType.MINE) {
						buttonList[j][i].setText("M");
					} else {
						if (cellArray[j + 1][i + 1].getNumber() == 0) {
							buttonList[j][i].setText("");
						} else {
							buttonList[j][i].setText(Integer.toString(cellArray[j + 1][i + 1].getNumber()));
						}
					}
				}
			}
		}
	}
	
	public static void disableAllButtons(JButton[][] buttonList){
		for (int i = 0; i < buttonList[0].length; i++) {
			for (int j = 0; j < buttonList.length; j++) {
				buttonList[j][i].setEnabled(false);
			}
		}
	}
	
	private static void startNewGame(JFrame nowFrame) {
		Cell[][] newCellArray = MineUtil.genNewGame();
		MineUtil.WINDOW_LOCATION_X = nowFrame.getX();
		MineUtil.WINDOW_LOCATION_Y = nowFrame.getY();
		nowFrame.dispose();
		new GameBoard(newCellArray);
	}
}
