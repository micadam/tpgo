package goserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class GoServer {
	
	private List<GameInstance> games;
	public static final int NET_PORT = 47615;
	
	private GameInstance getFreeGameInstance() {
		for(GameInstance gi : games) {
			if(gi.getState() == GameInstance.STATE_IDLE) {
				return gi;
			}
			
		}
		
		GameInstance gi = new GameInstance();
		games.add(gi);
		return gi;
	}
	
	public void startListening() throws IOException{
		ServerSocket serverSocket = null;
		Socket playerSocket = null;
		try {			
			serverSocket = new ServerSocket(NET_PORT);
			
			while(true) {
				playerSocket = serverSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
				String message = in.readLine();
				String messageTokens[] = message.split("\\s+");
				if(messageTokens[0].equals("HOST")) {
					
				} else if (messageTokens[0].equals("JOIN")) {
					
				} else {
					System.out.println("I just don't understand :(");
				}
			}
		} finally {
			if(serverSocket != null) {	
					serverSocket.close();
				}
		}
	}
	
	
	public static void main(String[] args) {
		GoServer goServer = new GoServer();
		try {			
			goServer.startListening();
		} catch (IOException ioe) {
			System.out.println("IOException while listening");
			ioe.printStackTrace();
		}
	}

}
