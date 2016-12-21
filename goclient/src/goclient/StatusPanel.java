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
	ButtonListener passListener;
	ButtonListener surrenderListener;
	ButtonListener disagreeListener;
	JButton disagreeButton;
	
	JLabel statusLabel;
	
	JLabel gameTime;
	private boolean territoriesMode=false;

	public void setTerritoriesmode(boolean mode){
		this.territoriesMode=mode;
		if(mode){
			surrenderButton.setText("Resume");
		}else{
			surrenderButton.setText("Surrender");
		}
	}
	public void waitForMove(Move move) {
		passListener.setMove(move);
		passButton.addActionListener(passListener);
		passButton.setEnabled(true);
		surrenderListener.setMove(move);
		surrenderButton.addActionListener(surrenderListener);
		surrenderButton.setEnabled(true);
		if(territoriesMode){
			disagreeListener.setMove(move);
			disagreeButton.addActionListener(disagreeListener);
			disagreeButton.setEnabled(true);
		}
	}
	
	public void stopWaitingForMove() {
		passButton.removeActionListener(passListener);
		passButton.setEnabled(false);
		surrenderButton.removeActionListener(surrenderListener);
		surrenderButton.setEnabled(false);
		if(territoriesMode){
			disagreeButton.removeActionListener(disagreeListener);
			disagreeButton.setEnabled(false);
		}
	}
	public void setStatusMessage(String status) {
		statusLabel.setText(status);
	}
	
	private void initUI() {
		
		this.setLayout(new FlowLayout());
		gameTime = new JLabel("DUMMYTEXT");
		this.add(gameTime);
		
		statusLabel = new JLabel("Waiting for second player");
		this.add(statusLabel);
		
		passButton = new JButton("Pass");
		this.add(passButton);
		passButton.setEnabled(false);
		disagreeButton = new JButton("Disagree");
		this.add(disagreeButton);
		disagreeButton.setEnabled(false);
		surrenderButton = new JButton("Surrender");
		this.add(surrenderButton);
		surrenderButton.setEnabled(false);
		
//		this.setPreferredSize(new Dimension(600, 30));
		this.setVisible(true);
	}
	
	public StatusPanel() {
		passListener = new ButtonListener(-1);
		surrenderListener=new ButtonListener(-2);
		disagreeListener= new ButtonListener(-3);
		initUI();
		
	}

}
class ButtonListener implements ActionListener{
	Move move;
	private int value;
	@Override
	public void actionPerformed(ActionEvent ae) {
		synchronized(move) {					
			move.setX(value);
			move.setY(value);
		}
	}
	public void setMove(Move move) {
		this.move = move;
	}
	public ButtonListener(int value){
		this.value=value;
	}
}
