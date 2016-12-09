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
	private Move lastOpponentsMove;
	private Move cancellingMove;
	private boolean connectionEstabilished=false;
	private int boardSize;
	private String currentStatusMessage = "DUMMYTEXT";
	
	@Override
	public int getGameStatus() {
		try{
			String status = in.readLine();
			System.out.println("[CLIENT] Response in geGameStatus(): "+ status);
			String[] statusTokens = status.split("\\s+");
			if(statusTokens[0].equals("GO")) {
				int color = Integer.parseInt(statusTokens[1]);
				String colorString = (color == Move.WHITE_NUMBER ? "White" : "Black");
				currentStatusMessage = "Your move: " + colorString;
				return color;
			} else if (statusTokens[0].equals("OPPONENT")) {
				int x = Integer.parseInt(statusTokens[1]);
				int y = Integer.parseInt(statusTokens[2]);
				int color = Integer.parseInt(statusTokens[3]);
				lastOpponentsMove = new Move(x, y, color);
				return 2;
			} else if(statusTokens[0].equals("SYNC")) {
				return 3;
			} else if (statusTokens[0].equals("REDO")) {
				int x = Integer.parseInt(statusTokens[1]);
				int y = Integer.parseInt(statusTokens[2]);
				int color = Integer.parseInt(statusTokens[3]);
				cancellingMove = new Move(x, y, color);
				return 4;
			}else {
				System.out.println("[CLIENT] Unknown response in getGameStatus(): " + status);
				return -100;
			}
		} catch (IOException ioe) {
			System.out.println("[CLIENT] IOException in getGameStatus();");
			return -100;
		}	
	}
	
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
			System.out.println("[CLIENT] Response in makeMove(): " + response);
			if(response.equals("OK")){
				currentStatusMessage = "Wait for the opponent's move";
				return 1;
			} else if(response.equals("NO")) {
				currentStatusMessage= "Wrong move, try again";
				return -1;
			} else if(response.equals("SYNC")) {			
				return 300;
			}else {
				System.out.println("[CLIENT] Unknown response in makeMove: " + response);
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
		return lastOpponentsMove;
	}
	
	public int getBoardSize() {
		return boardSize;
	}


	@Override
	public String getStatusMessage() {
		return currentStatusMessage;
	}
	@Override 
	public Move getCancellingMove(){
		return cancellingMove;
	}
	
	public GoGameManagerConnected() throws IllegalStateException {
		connectionEstabilished=false;
		AuthWindow aw = new AuthWindow(this);
		if(!connectionEstabilished){
			throw new IllegalStateException("Connection not initialized");
		}
	}
	
	
	public String establishConnection(String type,String host,int port,String keyCode, int boardSize, boolean botSetting){
		try {
			socket=new Socket(host,port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			type=type.toUpperCase();
			this.boardSize = boardSize;
			String botString = (botSetting == true ? "BOT" : "NOBOT");
			out.println(type+" "+keyCode + " " + boardSize + " " + botString);
			String message=in.readLine();
			System.out.println("[Client] Received message: "+message);
			if(message.equals("NO")){				
				return "Wrong game keyCode";
			}
			if(message.equals("OK")){		//TODO? in.readline()?
				if(type.equals("JOIN")) {
					this.boardSize = Integer.parseInt(in.readLine());
					System.out.println("[CLIENT] Remote game board size: " + boardSize);
				}
			} else {				
				return "A connection error occured";
			}
		} catch (IOException e) {
			// TODO comment printStackTrace after tests 
			e.printStackTrace();
			return "Connection failed";
		}	
		connectionEstabilished=true;
		return null;
	}
	

}

