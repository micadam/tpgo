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
		int answer=0;
		Move move,response;
		while(!exit){			//no exit handling
			move=goGameWindow.getUserMove();
			answer=goGameManager.makeMove(move.getX(),move.getY());
			System.out.println("answer from gameManager : "+ answer);
			if(answer==-1){
				goGameWindow.setBoard(goGameManager.getBoard());
			}else{
				response=goGameManager.getResponse();
				if(!(response.getX()==-1 && response.getY()==-1))	//pass handling
					goGameWindow.setField(response, -1);
			}
		}
	}
	public static void main(String[] args){
		GoClient goClient = new GoClient (new GoGameManagerRaw(), new GoGameWindow());
		goClient.run();
	}
}
