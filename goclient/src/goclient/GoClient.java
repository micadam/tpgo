package goclient;

public class GoClient {
	GoGameWindow goGameWindow;
	GoGameManager goGameManager;
	
	public GoClient(GoGameManager goGameManager){		
		this.goGameManager = goGameManager;
		this.goGameWindow = new GoGameWindow(goGameManager.getBoardSize());
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
				answer=goGameManager.makeMove(move);
				System.out.println("answer from gameManager: "+ answer);
				goGameWindow.setStatusMessage(goGameManager.getStatusMessage());
			} else if (gameStatus == 2) { //OPPONENT
				response=goGameManager.getResponse();	
				if( response.getX() != -1 )	//pass handling
					goGameWindow.setField(response);
			} else if (gameStatus == 3 ) { //SYNC
				goGameWindow.setBoard(goGameManager.getBoard());
			} else if (gameStatus == 4 ) { //REDO
				goGameWindow.setField(goGameManager.getCancellingMove());
			} else if (gameStatus == 5 ) { //END
				exit=true;
			}else if (gameStatus == 6 ) { //TERRITORIES START
				goGameWindow.setTerritoriesMode(true);
			}else if (gameStatus == 7 ) { //TERRITORIES END
				goGameWindow.setTerritoriesMode(false);
			}else if(gameStatus == -100){
				System.out.println("Unknown status: " + gameStatus);
				throw new IllegalArgumentException();
			}else{
				System.out.println("Fatal error, game ended unexpectedly");
				exit=true;
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		
		try{
			GoGameManagerConnected goGameManagerConnected=new GoGameManagerConnected();
			GoClient goClient = new GoClient (goGameManagerConnected);
			goClient.run();
		}catch(IllegalStateException e){
			System.out.println("Client could not be initialized: "+e.getMessage());
		}
		System.exit(0);
	}
}
