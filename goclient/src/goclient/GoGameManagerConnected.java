package goclient;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
		AuthWindow aw = new AuthWindow();
	}

}


class AuthWindow extends JDialog {
	JLabel addressLabel;
	JLabel portLabel;
	JLabel keyCodeLabel;

	JTextField addressField;
	JTextField portField;
	JTextField keyCodeField;
	
	JButton hostGame;
	JButton joinGame;
	
	private void prepareUI() {
		addressLabel = new JLabel("Server address");
		portLabel = new JLabel("Server Port");
		keyCodeLabel = new JLabel("Game keycode");

		addressField = new JTextField("localhost");
		portField = new JTextField("47615");
		keyCodeField= new JTextField(10);
		
		hostGame = new JButton("Host game");
		joinGame = new JButton("Join game");
		
		setLayout(new FlowLayout());
		add(addressLabel);
		add(portLabel);
		add(keyCodeLabel);
		add(addressField);
		add(portField);
		add(keyCodeField);
		add(hostGame);
		add(joinGame);
	}
	public AuthWindow() {
		
		setModalityType(DEFAULT_MODALITY_TYPE);
		setSize(280, 120);
		prepareUI();
		setVisible(true);
		
	}
}
