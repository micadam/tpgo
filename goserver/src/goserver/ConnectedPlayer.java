package goserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedPlayer implements Player {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public ConnectedPlayer(Socket socket) {
		this.socket = socket;
		try{			
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch(IOException ioe) {
			System.out.println("[SERVER] IOException in connectedPlayed");
		}
		
	}

	@Override
	public Move getMove() {
		try {
			String move = in.readLine();
			String[] moveTokens = move.split("\\s+");
			if(moveTokens[0] == "MOVE") {
				int x = Integer.parseInt(moveTokens[1]);
				int y = Integer.parseInt(moveTokens[2]);
				return new Move(x, y, 0);
			} else if(moveTokens[0] == "PASS") {
				return new Move(-1, -1, 0);
			} else {
				System.out.println("Unknown move: " + move);
				return new Move(-100, -100, 0);
			}
		} catch(IOException ioe) {
			System.out.println("[SERVER] IOException in geMove()");
			return new Move(-100, -100, 0);
		}
	}

	@Override
	public void sendResponse(String response) {
		out.println(response);
		
	}

}
