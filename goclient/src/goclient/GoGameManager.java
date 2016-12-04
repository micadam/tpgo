package goclient;

public interface GoGameManager {
	
	public int makeMove(int x, int y);
	public int[][] getBoard();
	public int getBoardSize();
	public Move getResponse();
	public int getGameStatus();
	public String getStatusMessage();
	public Move getCancellingMove();
}
