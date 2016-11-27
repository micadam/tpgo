package goclient;

public class GoGameManagerRaw implements GoGameManager {
	private int gameBoard[][];
	private static final int BOARD_SIZE = 19;
	
	public GoGameManagerRaw(){

	}
	
	public int makeMove(int x, int y){
		if(x < 0 || y < 0 || x > BOARD_SIZE ||  y > BOARD_SIZE || gameBoard[x][y] != 0) {
			return -1;
		}
		gameBoard[x][y] = 1;
		return 1;
	}
}
