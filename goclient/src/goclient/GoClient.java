package goclient;

public class GoClient {
	GoGameWindow goGameWindow;
	GoGameManager goGameManager;
	
	public GoClient(GoGameManager goGameManager){		
		goGameWindow = new GoGameWindow(this);
		this.goGameManager = goGameManager;
		goGameManager.setGameClient(this);
	}

	public static void main(String[] args){
		GoClient goClient = new GoClient (new GoGameManagerRaw());
	}
}
