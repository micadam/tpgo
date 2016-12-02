package goserver;

import java.net.Socket;

public class ConnectedPlayer implements Player {
	private Socket socket;
	
	public ConnectedPlayer(Socket socket) {
		this.socket = socket;
	}

}
