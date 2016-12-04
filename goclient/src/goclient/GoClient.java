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
			Move move;
			Move response;
			
			
			gameStatus = goGameManager.getGameStatus();
			goGameWindow.setStatusMessage(goGameManager.getStatusMessage());
			//System.out.println("GameStatus from GameManager: " + gameStatus);
			if(gameStatus == Move.BLACK_NUMBER || gameStatus == Move.WHITE_NUMBER) { 	//make a move
				move=goGameWindow.getUserMove(gameStatus);
				answer=goGameManager.makeMove(move.getX(),move.getY());
				System.out.println("answer from gameManager: "+ answer);
				goGameWindow.setStatusMessage(goGameManager.getStatusMessage());
			} else if (gameStatus == 2) { //OPPONENT
				response=goGameManager.getResponse();	
				if( response.getX() != -1 )	//pass handling
					goGameWindow.setField(response);
			} else if (gameStatus == 3 ) { //SYNC
				goGameWindow.setBoard(goGameManager.getBoard());
			} else if (gameStatus == 4 ) { //REDO
				goGameWindow.setField(goGameManager.getCancallingMove());
			} else {
				System.out.println("Unknown status: " + gameStatus);
				throw new IllegalArgumentException();
			}
		}
	}
	public static void main(String[] args){

		GoClient goClient = new GoClient (new GoGameManagerConnected(), new GoGameWindow());
		goClient.run();
	}
}
