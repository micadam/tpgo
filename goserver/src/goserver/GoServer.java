package goserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GoServer {
	
	private List<GameInstance> games;
	public static final int NET_PORT = 47615;
	
	public void startListening() throws IOException{
		games = new ArrayList<GameInstance>();
		ServerSocket serverSocket = null;
		Socket playerSocket = null;
		try {			
			serverSocket = new ServerSocket(NET_PORT);
			
			while(true) {
				playerSocket = serverSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
				PrintWriter out = new PrintWriter(playerSocket.getOutputStream(), true);
				String message = in.readLine();
				System.out.println("[SERVER] Received message. Its content: " + message);
				String messageTokens[] = message.split("\\s+");
				if(messageTokens.length < 2) {	//Not enough arguments
					out.println("NO");
					playerSocket.close();
					continue;
				}
				if(messageTokens[0].equals("HOST")) {  //HOST <keycode>, only if a game with such keycode doesn't already exist
					boolean okay = true;
					for(GameInstance gi : games) {
						if(gi.getKeyCode().equals(messageTokens[1])) {
							okay = false;
							System.out.println("[SERVER] Duplicate keycode: " + messageTokens[1]);
							out.println("NO");			//game already exists
							playerSocket.close();
							break;
						}
					}
					if(okay) {
						out.println("OK");
						GameInstance gi = new GameInstance(new ConnectedPlayer(playerSocket), messageTokens[1]);
						if(messageTokens.length > 2){
							if(messageTokens[2].equals("BOT"))
								gi.addPlayer(new Bot());
						}
						games.add(gi);
					}	
				} else if (messageTokens[0].equals("JOIN")) {	//JOIN <keycode>, only if a game with such keycode exists
					boolean added = false;
					for(GameInstance gi : games) {
						if(gi.getKeyCode().equals(messageTokens[1])) {
							added = gi.addPlayer(new ConnectedPlayer(playerSocket));
							if(added) {
								System.out.println("[SERVER] Added played to a game with keycode " + messageTokens[1]);
								out.println("OK");
							}
							else { 
								System.out.println("[SERVER] Could not add a player to a game as it was full. Keycode: " + messageTokens[1]);
								out.println("NO");		//game is full
								playerSocket.close();
							}
						}
					}
					if(added == false) {		
						System.out.println("[SERVER] Could not find a game with keycode " + messageTokens[1]);
						out.println("NO");		//game doesn't exist
						playerSocket.close();			
					}
				} else {		//???
					System.out.println("[SERVER] I just don't understand :(");
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
			System.out.println("[SERVER] Starting listening for clients...");
			goServer.startListening();
		} catch (IOException ioe) {
			System.out.println("IOException while listening");
			ioe.printStackTrace();
		}
	}

}
