package goclient;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class GoGameWindow extends JFrame {
	
	GamePanel gamePanel;
	StatusPanel statusPanel;
	
	
	public Move getUserMove() {
		Move move = new Move(-10, -10);
		statusPanel.waitForMove(move);
		gamePanel.waitForMove(move);
		
		try {			
			while(move.getX() == -10) {
				Thread.sleep(10);
			}
		} catch (InterruptedException ie) {
			System.out.println("Interrupted");
		}
		
		statusPanel.stopWaitingForMove();
		gamePanel.stopWaitingForMove();
		return move;
		
	}
	public void setBoard(int[][] board){
		gamePanel.setBoard(board);
	}
	public void setField(Move move,int color){
		gamePanel.makeMove(move.getX(),move.getY(),color);
	}
	
	
	private void initUI(){
		setLayout(new BorderLayout());
		this.add(statusPanel, BorderLayout.NORTH);
		this.add(gamePanel, BorderLayout.SOUTH);
		this.setResizable(false);
	}
	
	public GoGameWindow(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.gamePanel = new GamePanel();
		this.statusPanel = new StatusPanel();
		initUI();
		this.setVisible(true);
		this.pack();
	}

}
