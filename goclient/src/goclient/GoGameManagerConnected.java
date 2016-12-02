package goclient;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class GoGameManagerConnected implements GoGameManager {

	@Override
	public int makeMove(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getBoard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Move getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGameStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStatusMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public GoGameManagerConnected() {
		
	}

}


class AuthWindow extends JFrame {
	JTextField addressField;
	JTextField portField;
	JTextField keyCode;
	
	JButton hostGame;
	JButton joinGame;
	
	
	public AuthWindow() {
		
	}
}
