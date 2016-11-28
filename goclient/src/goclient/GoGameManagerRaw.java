package goclient;

public class GoGameManagerRaw implements GoGameManager {
	private int gameBoard[][];
	private int fieldsLeft;
	private static final int BOARD_SIZE = 19;
	public GoGameManagerRaw(){
		gameBoard=new int[BOARD_SIZE][BOARD_SIZE];
		fieldsLeft=BOARD_SIZE*BOARD_SIZE;
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
					fieldsLeft--;
					gameBoard[x][y]=-1;
					move=new Move(x,y);
					break;
				}
			}
		}
		return move;
	}
	public int makeMove(int x, int y){
		if(!(x==-1 && y==-1)){	//pass
			if(x < 0 || y < 0 || x > BOARD_SIZE ||  y > BOARD_SIZE || gameBoard[x][y] != 0) {
				return -1;
			}
			fieldsLeft--;
			gameBoard[x][y] = 1;
		}
		if(fieldsLeft<2)		//no more moves
			return 3;
		return 1;
	}
}
