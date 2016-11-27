package goclient;

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
	
	
	private void initUI(){
		this.add(statusPanel);
		this.add(gamePanel);
		this.pack();
		this.setVisible(true);
	}
	
	public GoGameWindow(){
		this.gamePanel = new GamePanel();
		this.statusPanel = new StatusPanel();
		
		initUI();
	}

}
