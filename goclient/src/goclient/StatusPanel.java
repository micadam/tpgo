package goclient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSeparator;
import java.awt.Font;

public class StatusPanel extends JPanel {
	
	JButton surrenderButton;
	JButton passButton;
	ButtonListener passListener;
	ButtonListener surrenderListener;
	ButtonListener disagreeListener;
	JButton disagreeButton;
	
	JLabel whitePrisonersLabel;
	JLabel blackPrisonersLabel;
	
	JLabel statusLabel;
	
	private boolean territoriesMode=false;
	private JSeparator separator;

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
	
	public void setWhitePrisoners(int prisoners) {
		whitePrisonersLabel.setText("White prisoners: " + prisoners);
	}
	public void setBlackPrisoners(int prisoners) {
		blackPrisonersLabel.setText("Black prisoners: " + prisoners);
	}
	
	private void initUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{113, 96, 42, 55, 0, 57, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{28, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 4.9E-324, 4.9E-324};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		statusLabel = new JLabel("Waiting for second player");
		statusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusLabel.insets = new Insets(0, 0, 5, 5);
		gbc_statusLabel.anchor = GridBagConstraints.NORTH;
		gbc_statusLabel.gridx = 1;
		gbc_statusLabel.gridy = 0;
		this.add(statusLabel, gbc_statusLabel);
		blackPrisonersLabel = new JLabel("Black Prisoners: 0");
		GridBagConstraints gbc_blackPrisonersLabel = new GridBagConstraints();
		gbc_blackPrisonersLabel.anchor = GridBagConstraints.WEST;
		gbc_blackPrisonersLabel.insets = new Insets(0, 0, 5, 5);
		gbc_blackPrisonersLabel.gridx = 8;
		gbc_blackPrisonersLabel.gridy = 0;
		this.add(blackPrisonersLabel, gbc_blackPrisonersLabel);
		
		
		passButton = new JButton("Pass");
		GridBagConstraints gbc_passButton = new GridBagConstraints();
		gbc_passButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_passButton.anchor = GridBagConstraints.NORTH;
		gbc_passButton.insets = new Insets(0, 0, 5, 5);
		gbc_passButton.gridx = 4;
		gbc_passButton.gridy = 1;
		this.add(passButton, gbc_passButton);
		passButton.setEnabled(false);
		surrenderButton = new JButton("Surrender");
		GridBagConstraints gbc_surrenderButton = new GridBagConstraints();
		gbc_surrenderButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_surrenderButton.anchor = GridBagConstraints.NORTH;
		gbc_surrenderButton.insets = new Insets(0, 0, 5, 5);
		gbc_surrenderButton.gridx = 5;
		gbc_surrenderButton.gridy = 1;
		this.add(surrenderButton, gbc_surrenderButton);
		surrenderButton.setEnabled(false);
		disagreeButton = new JButton("Disagree");
		GridBagConstraints gbc_disagreeButton = new GridBagConstraints();
		gbc_disagreeButton.insets = new Insets(0, 0, 5, 5);
		gbc_disagreeButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_disagreeButton.gridx = 6;
		gbc_disagreeButton.gridy = 1;
		this.add(disagreeButton, gbc_disagreeButton);
		disagreeButton.setEnabled(false);
		
		
		whitePrisonersLabel = new JLabel("White prisoners: 0");
		GridBagConstraints gbc_whitePrisonersLabel = new GridBagConstraints();
		gbc_whitePrisonersLabel.insets = new Insets(0, 0, 5, 5);
		gbc_whitePrisonersLabel.anchor = GridBagConstraints.WEST;
		gbc_whitePrisonersLabel.gridwidth = 2;
		gbc_whitePrisonersLabel.gridx = 8;
		gbc_whitePrisonersLabel.gridy = 1;
		this.add(whitePrisonersLabel, gbc_whitePrisonersLabel);
		
		separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 11;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 2;
		add(separator, gbc_separator);
		
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
