package goclient;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class GoGameWindow extends JFrame {
	
	
	
	GamePanel gamePanel;
	StatusPanel statusPanel;
	
	
	public Move getUserMove(int color) {
		Move move = new Move(-10, -10, color);
		statusPanel.waitForMove(move);
		gamePanel.waitForMove(move);
		
		try {			
			while(move.getX() == -10) {
				Thread.sleep(10);
			}
		} catch (InterruptedException ie) {
			System.out.println("Interrupted");
		}
		
		System.out.println("Starting the waiting in game window");
		statusPanel.stopWaitingForMove();
		gamePanel.stopWaitingForMove();
		return move;
		
	}
	public void setTerritoriesMode(boolean mode){
		gamePanel.setTerritoriesMode(mode);
		statusPanel.setTerritoriesmode(mode);
	}
	public void setStatusMessage(String message) {
		statusPanel.setStatusMessage(message);
	}
	public void setBoard(int[][] board){
		gamePanel.setBoard(board);
	}
	public void setField(Move move){
		gamePanel.makeMove(move);
	}
	
	public void setWhitePrisoners(int prisoners) {
		statusPanel.setWhitePrisoners(prisoners);
	}
	public void setBlackPrisoners(int prisoners) {		
		statusPanel.setBlackPrisoners(prisoners);
	}
	
	private void initUI(){
		setLayout(new BorderLayout());
		this.add(statusPanel, BorderLayout.NORTH);
		this.add(gamePanel, BorderLayout.SOUTH);
		this.setResizable(false);
	}
	
	public GoGameWindow(int boardSize){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.gamePanel = new GamePanel(boardSize);
		this.statusPanel = new StatusPanel();
		initUI();
		this.setVisible(true);
		this.pack();
	}

}
