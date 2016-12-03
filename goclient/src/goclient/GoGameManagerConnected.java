package goclient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GoGameManagerConnected implements GoGameManager {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	@Override
	public int makeMove(int x, int y) {
		try{ 			
			if(x == -1) {
				out.println("PASS");
			}
			else {
				out.println("MOVE " + x + " " + y);
			}
			
			String response = in.readLine();
			if(response.equals("OK")){
				return 1;
			} else if(response.equals("NO")) {
				return -1;
			} else if(response.equals("SYNC")) {
				return 2;
			}else {
				return -100;
			}
		} catch (IOException ioe) {
			System.out.println("[CLIENT] IOException in makeMove()");
			return -100;
		}
	}

	@Override
	public int[][] getBoard() {
		try {	
			int boardSize = Integer.parseInt(in.readLine());
			int board[][] = new int[boardSize][boardSize];
			
			String boardRaw = in.readLine();
			for(int i = 0; i < boardSize; i++) {
				for(int j = 0; j < boardSize; j++) {
					char curField = boardRaw.charAt(i * boardSize + j);
					int fieldValue;
					if(curField == '2') {
						fieldValue = -1;
					} else {
						fieldValue = curField - '0';
					}
					board[i][j] = fieldValue;
				}
			}
			return board;
		} catch (IOException ioe) {
			System.out.println("[CLIENT] IOException in getBoard()");
			return null;
		}	
	}

	@Override
	public Move getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGameStatus() {
		try{
			String status = in.readLine();
			String[] statusTokens = status.split("\\s+");
			System.out.println("[CLIENT] Response in geGameStatus(): "+ status);
			if(statusTokens[0].equals("GO")) {
				return Integer.parseInt(statusTokens[1]);
			} else {
				return -100;
			}
		} catch (IOException ioe) {
			System.out.println("[CLIENT] IOException in getGameStatus();");
			return -100;
		}
		
	}

	@Override
	public String getStatusMessage() {
		//todo
		return "TODO";
	}
	
	public GoGameManagerConnected() {
		AuthWindow aw = new AuthWindow(this);
	}
	
	
	public String establishConnection(String type,String host,int port,String keyCode){
		try {
			socket=new Socket(host,port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			type=type.toUpperCase();
			out.println(type+" "+keyCode);
			String message=in.readLine();
			System.out.println("[Client] Received message: "+message);
			if(message.compareTo("NO")==0)
				return "Wrong game keyCode";
			if(message.compareTo("OK")!=0)		//TODO? in.readline()?
				return "A connection error occured";
		} catch (IOException e) {
			// TODO comment printStackTrace after tests 
			e.printStackTrace();
			return "Connection failed";
		}	
		return null;
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
		String message=goGameManagerConnected.establishConnection(type,parent.getHost(),parent.getPort(),parent.getKeyCode());
		if(message==null)
			parent.dispose();
		else
			parent.setMessage(message);
	}
	
}
class AuthWindow extends JDialog {
	JLabel addressLabel;
	JLabel portLabel;
	JLabel keyCodeLabel;
	JLabel messageLabel;
	JTextField addressField;
	JTextField portField;
	JTextField keyCodeField;
	
	JButton hostGame;
	JButton joinGame;
	
	private void prepareUI(GoGameManagerConnected goGameManagerConnected) {
		addressLabel = new JLabel("Server address");
		portLabel = new JLabel("Server Port");
		keyCodeLabel = new JLabel("Game keycode");
		messageLabel=new JLabel();
		addressField = new JTextField("localhost");
		portField = new JTextField("47615");
		keyCodeField= new JTextField(10);
		
		hostGame = new JButton("Host game");
		hostGame.addActionListener(new AuthWindowListener(goGameManagerConnected,this));
		joinGame = new JButton("Join game");
		joinGame.addActionListener(new AuthWindowListener(goGameManagerConnected,this));
		setLayout(new FlowLayout());
		add(addressLabel);
		add(portLabel);
		add(keyCodeLabel);
		add(addressField);
		add(portField);
		add(keyCodeField);
		add(messageLabel);
		add(hostGame);
		add(joinGame);
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
	public void setMessage(String message){
		messageLabel.setText(message);
	}
	
	public AuthWindow(GoGameManagerConnected goGameManagerConnected) {
		
		setModalityType(DEFAULT_MODALITY_TYPE);
		setSize(280, 200);
		prepareUI(goGameManagerConnected);
		setVisible(true);
		
	}
}
