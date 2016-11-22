package goclient;

public class GoGameManagerRaw implements GoGameManager {
	private int gameBoard[][];
	private static final int BOARD_SIZE = 19;
	private GoClient goClient;
	
	public GoGameManagerRaw(){

	}
	
	public void setGameClient(GoClient goClient){
		this.goClient = goClient;
	}
	public boolean makeMove(int x, int y){
		if(x < 0 || y < 0 || x > BOARD_SIZE ||  y > BOARD_SIZE) {
			return false;
		}
		gameBoard[x][y] = 1;
		return true;
	}
}
