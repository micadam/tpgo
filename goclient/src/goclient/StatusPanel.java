package goclient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusPanel extends JPanel {
	
	JButton surrenderButton;
	JButton passButton;
	PassListener passListener;
	SurrenderListener surrenderListener;
	JButton territoriesButton;
	
	JLabel statusLabel;
	
	JLabel gameTime;

	
	public void waitForMove(Move move) {
		passListener.setMove(move);
		passButton.addActionListener(passListener);
		passButton.setEnabled(true);
		surrenderListener.setMove(move);
		surrenderButton.addActionListener(surrenderListener);
		surrenderButton.setEnabled(true);
	}
	
	public void stopWaitingForMove() {
		passButton.removeActionListener(passListener);
		passButton.setEnabled(false);
		surrenderButton.removeActionListener(surrenderListener);
		surrenderButton.setEnabled(false);
	}
	public void setStatusMessage(String status) {
		statusLabel.setText(status);
	}
	
	private void initUI() {
		
		this.setLayout(new FlowLayout());
		gameTime = new JLabel("DUMMYTEXT");
		this.add(gameTime);
		
		statusLabel = new JLabel("DUMMYTEXT");
		this.add(statusLabel);
		
		passButton = new JButton("Pass");
		this.add(passButton);
		passButton.setEnabled(false);
		territoriesButton = new JButton("Territories");
		this.add(territoriesButton);
		territoriesButton.setEnabled(false);
		surrenderButton = new JButton("Surrender");
		this.add(surrenderButton);
		surrenderButton.setEnabled(false);
		
//		this.setPreferredSize(new Dimension(600, 30));
		this.setVisible(true);
	}
	
	public StatusPanel() {
		passListener = new PassListener();
		surrenderListener=new SurrenderListener();
		initUI();
		
	}

}
class SurrenderListener implements ActionListener{
	Move move;
	@Override
	public void actionPerformed(ActionEvent ae) {
		synchronized(move) {					
			move.setX(-2);
			move.setY(-2);
		}
	}
	public void setMove(Move move) {
		this.move = move;
	}
}

class PassListener implements ActionListener{
	Move move;
	@Override
	public void actionPerformed(ActionEvent ae) {
		synchronized(move) {					
			move.setX(-1);
			move.setY(-1);
		}
	}
	public void setMove(Move move) {
		this.move = move;
	}
}
