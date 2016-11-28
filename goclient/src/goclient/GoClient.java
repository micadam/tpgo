package goclient;

public class GoClient {
	GoGameWindow goGameWindow;
	GoGameManager goGameManager;
	
	public GoClient(GoGameManager goGameManager, GoGameWindow goGameWindow){		
		this.goGameWindow = goGameWindow;
		this.goGameManager = goGameManager;
	}
	public void run(){
		boolean exit=false;
		while(!exit){			//TODO exit handling 
			int answer=-1;
			int gameStatus = 0;
			Move move,response;
			
			gameStatus = goGameManager.getGameStatus();
			//System.out.println("GameStatus from GameManager: " + gameStatus);
			if(gameStatus == Move.BLACK_NUMER || gameStatus == Move.WHITE_NUMBER) { 	//make a move
				while(answer == -1) {
					move=goGameWindow.getUserMove(gameStatus);
					answer=goGameManager.makeMove(move.getX(),move.getY());
					System.out.println("answer from gameManager : "+ answer);
					goGameWindow.setBoard(goGameManager.getBoard());				
				}
			} else if (gameStatus == 2) { //download new move
				response=goGameManager.getResponse();
				if(!(response.getX()==-1 && response.getY()==-1))	//pass handling
					goGameWindow.setField(response, -1);
				goGameWindow.setBoard(goGameManager.getBoard());				
			} else if (gameStatus == 0 ) { //wait
				try{
					Thread.sleep(500);
					continue;					
				}
				catch (InterruptedException ie) {
					System.out.println("Interrupted");
				}
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	public static void main(String[] args){
		GoClient goClient = new GoClient (new GoGameManagerRaw(), new GoGameWindow());
		goClient.run();
	}
}
