package goclient;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class AuthWindow extends JDialog {
	JLabel addressLabel;
	JLabel portLabel;
	JLabel keyCodeLabel;
	JLabel messageLabel;
	JTextField addressField;
	JTextField portField;
	JTextField keyCodeField;
	
	JButton hostGame;
	JButton joinGame;
	private JCheckBox botSelectionBox;
	private JLabel hostSettingsLabel;
	private JSeparator separator;
	private JLabel basicSettingsLabel;
	private JComboBox boardSizeBox;
	private JLabel boardSizeLabel;
	
	private void prepareUI(GoGameManagerConnected goGameManagerConnected) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{17, 17, 7, 28, 54, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 18, 21, 28, 26, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		basicSettingsLabel = new JLabel("Basic settings");
		basicSettingsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gbc_basicSettingsLabel = new GridBagConstraints();
		gbc_basicSettingsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_basicSettingsLabel.gridx = 1;
		gbc_basicSettingsLabel.gridy = 1;
		getContentPane().add(basicSettingsLabel, gbc_basicSettingsLabel);
		addressLabel = new JLabel("Server address");
		addressLabel.setFont(new Font("Dialog", Font.BOLD, 13));
		GridBagConstraints gbc_addressLabel = new GridBagConstraints();
		gbc_addressLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_addressLabel.insets = new Insets(0, 0, 5, 5);
		gbc_addressLabel.gridx = 1;
		gbc_addressLabel.gridy = 2;
		getContentPane().add(addressLabel, gbc_addressLabel);
		portLabel = new JLabel("Server Port");
		portLabel.setFont(new Font("Dialog", Font.BOLD, 13));
		GridBagConstraints gbc_portLabel = new GridBagConstraints();
		gbc_portLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_portLabel.insets = new Insets(0, 0, 5, 5);
		gbc_portLabel.gridx = 2;
		gbc_portLabel.gridy = 2;
		getContentPane().add(portLabel, gbc_portLabel);
		keyCodeLabel = new JLabel("Game keycode");
		keyCodeLabel.setFont(new Font("Dialog", Font.BOLD, 13));
		GridBagConstraints gbc_keyCodeLabel = new GridBagConstraints();
		gbc_keyCodeLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_keyCodeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_keyCodeLabel.gridx = 3;
		gbc_keyCodeLabel.gridy = 2;
		getContentPane().add(keyCodeLabel, gbc_keyCodeLabel);
		addressField = new JTextField("localhost");
		GridBagConstraints gbc_addressField = new GridBagConstraints();
		gbc_addressField.fill = GridBagConstraints.HORIZONTAL;
		gbc_addressField.anchor = GridBagConstraints.NORTH;
		gbc_addressField.insets = new Insets(0, 0, 5, 5);
		gbc_addressField.gridx = 1;
		gbc_addressField.gridy = 3;
		getContentPane().add(addressField, gbc_addressField);
		portField = new JTextField("47615");
		GridBagConstraints gbc_portField = new GridBagConstraints();
		gbc_portField.fill = GridBagConstraints.HORIZONTAL;
		gbc_portField.anchor = GridBagConstraints.NORTH;
		gbc_portField.insets = new Insets(0, 0, 5, 5);
		gbc_portField.gridx = 2;
		gbc_portField.gridy = 3;
		getContentPane().add(portField, gbc_portField);
		keyCodeField= new JTextField(10);
		GridBagConstraints gbc_keyCodeField = new GridBagConstraints();
		gbc_keyCodeField.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyCodeField.anchor = GridBagConstraints.NORTH;
		gbc_keyCodeField.insets = new Insets(0, 0, 5, 5);
		gbc_keyCodeField.gridx = 3;
		gbc_keyCodeField.gridy = 3;
		getContentPane().add(keyCodeField, gbc_keyCodeField);
		joinGame = new JButton("Join game");
		joinGame.addActionListener(new AuthWindowListener(goGameManagerConnected,this));
		GridBagConstraints gbc_joinGame = new GridBagConstraints();
		gbc_joinGame.insets = new Insets(0, 0, 5, 5);
		gbc_joinGame.anchor = GridBagConstraints.NORTHWEST;
		gbc_joinGame.gridx = 1;
		gbc_joinGame.gridy = 4;
		getContentPane().add(joinGame, gbc_joinGame);
		
		hostGame = 	new JButton("Create game");
		hostGame.addActionListener(new AuthWindowListener(goGameManagerConnected,this));
		messageLabel=new JLabel();
		GridBagConstraints gbc_messageLabel = new GridBagConstraints();
		gbc_messageLabel.gridwidth = 3;
		gbc_messageLabel.anchor = GridBagConstraints.WEST;
		gbc_messageLabel.insets = new Insets(0, 0, 5, 5);
		gbc_messageLabel.gridx = 2;
		gbc_messageLabel.gridy = 4;
		getContentPane().add(messageLabel, gbc_messageLabel);
		
		separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 1;
		gbc_separator.gridy = 5;
		getContentPane().add(separator, gbc_separator);
		
		hostSettingsLabel = new JLabel("Game creation settings");
		hostSettingsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gbc_hostSettingsLabel = new GridBagConstraints();
		gbc_hostSettingsLabel.anchor = GridBagConstraints.WEST;
		gbc_hostSettingsLabel.gridwidth = 2;
		gbc_hostSettingsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_hostSettingsLabel.gridx = 1;
		gbc_hostSettingsLabel.gridy = 6;
		getContentPane().add(hostSettingsLabel, gbc_hostSettingsLabel);
		
		boardSizeLabel = new JLabel("Board size");
		GridBagConstraints gbc_boardSizeLabel = new GridBagConstraints();
		gbc_boardSizeLabel.anchor = GridBagConstraints.WEST;
		gbc_boardSizeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_boardSizeLabel.gridx = 1;
		gbc_boardSizeLabel.gridy = 7;
		getContentPane().add(boardSizeLabel, gbc_boardSizeLabel);
		
		boardSizeBox = new JComboBox();
		boardSizeBox.setModel(new DefaultComboBoxModel(new String[] {"9", "13", "19"}));
		boardSizeBox.setSelectedIndex(2);
		GridBagConstraints gbc_boardSizeBox = new GridBagConstraints();
		gbc_boardSizeBox.insets = new Insets(0, 0, 5, 5);
		gbc_boardSizeBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_boardSizeBox.gridx = 2;
		gbc_boardSizeBox.gridy = 7;
		getContentPane().add(boardSizeBox, gbc_boardSizeBox);
		
		botSelectionBox = new JCheckBox("Play against an AI player");
		GridBagConstraints gbc_botSelectionBox = new GridBagConstraints();
		gbc_botSelectionBox.gridwidth = 2;
		gbc_botSelectionBox.insets = new Insets(0, 0, 5, 5);
		gbc_botSelectionBox.gridx = 1;
		gbc_botSelectionBox.gridy = 8;
		getContentPane().add(botSelectionBox, gbc_botSelectionBox);
		GridBagConstraints gbc_hostGame = new GridBagConstraints();
		gbc_hostGame.anchor = GridBagConstraints.NORTHEAST;
		gbc_hostGame.insets = new Insets(0, 0, 5, 5);
		gbc_hostGame.gridx = 1;
		gbc_hostGame.gridy = 9;
		getContentPane().add(hostGame, gbc_hostGame);
	}
	public String getHost(){
		return addressField.getText();
	}
	public int getPort(){
		return Integer.parseInt(portField.getText());
	}
	public String getKeyCode(){
		return keyCodeField.getText();
	}
	public int getBoardSize() {
		return Integer.parseInt((String)boardSizeBox.getSelectedItem());
	}
	public boolean getBotSetting() {
		return botSelectionBox.isSelected();
	}
	public void setMessage(String message){
		messageLabel.setText(message);
	}
	
	public AuthWindow(GoGameManagerConnected goGameManagerConnected) {
		
		setModalityType(DEFAULT_MODALITY_TYPE);
		setSize(357, 318);
		prepareUI(goGameManagerConnected);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
	}
}

class AuthWindowListener implements ActionListener{
	private GoGameManagerConnected goGameManagerConnected;
	private AuthWindow parent;
	public AuthWindowListener(GoGameManagerConnected goGameManagerConnected,AuthWindow parent){
		this.goGameManagerConnected=goGameManagerConnected;
		this.parent=parent;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String type = e.getActionCommand().split("\\s+")[0];
		String message=goGameManagerConnected.establishConnection(type,parent.getHost(),parent.getPort(),parent.getKeyCode(), parent.getBoardSize(), parent.getBotSetting());
		if(message==null)
			parent.dispose();
		else
			parent.setMessage(message);
	}
	
}