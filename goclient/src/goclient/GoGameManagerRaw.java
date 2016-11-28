package goclient;

public class GoGameManagerRaw implements GoGameManager {
	private int gameBoard[][];
	private int fieldsLeft;
	private static final int BOARD_SIZE = 19;
	int currentColor = -1;
	public GoGameManagerRaw(){
		gameBoard=new int[BOARD_SIZE][BOARD_SIZE];
		fieldsLeft=BOARD_SIZE*BOARD_SIZE;
		currentColor = Move.BLACK_NUMER;
	}
	
	public int[][] getBoard() {
		int copy[][]=new int[BOARD_SIZE][BOARD_SIZE];
		for(int x=0;x<BOARD_SIZE;x++){
			for(int y=0;y<BOARD_SIZE;y++)
				copy[x][y]=gameBoard[x][y];
		}
		return copy;
	}	
	public Move getResponse(){		//weak bot
		Move move=null;
		for(int x=0;x<BOARD_SIZE && move==null;x++){
			for(int y=0;y<BOARD_SIZE ;y++){
				if(gameBoard[x][y]==0){ 
					makeMove(x, y);
					move=new Move(x,y, currentColor);
					break;
				}
			}
		}
		return move;
	}
	
	public int getGameStatus() {
	//	System.out.println("Returning status: " + currentColor);
		return currentColor;
	}
	
	public String getStatusMessage() {
		return "DUMMY"; //TODO
	}
	public int makeMove(int x, int y){
		if( x != -1 ){	  //check if player passed
			if(x < 0 || y < 0 || x >= BOARD_SIZE ||  y >= BOARD_SIZE || gameBoard[x][y] != 0) { //invalid move
				return -1;
			}
			
			fieldsLeft--;
			gameBoard[x][y] = currentColor;
		}
		if(fieldsLeft<2) {			//no more moves
			return 3;
		}
		
		currentColor *= -1;
		return 1;
	}
}
