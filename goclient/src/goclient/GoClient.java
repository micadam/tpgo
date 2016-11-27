package goclient;

public class GoClient {
	GoGameWindow goGameWindow;
	GoGameManager goGameManager;
	
	public GoClient(GoGameManager goGameManager, GoGameWindow goGameWindow){		
		this.goGameWindow = goGameWindow;
		this.goGameManager = goGameManager;
		
		goGameWindow.setVisible(true);
		goGameWindow.pack();
	}
	
	public static void main(String[] args){
		GoClient goClient = new GoClient (new GoGameManagerRaw(), new GoGameWindow());
	}
}
