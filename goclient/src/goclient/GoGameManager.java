package goclient;

public interface GoGameManager {
	
	public int makeMove(int x, int y);
	public int[][] getBoard();
	public Move getResponse();
	public int getGameStatus();
	public String getStatusMessage();
	public Move getCancellingMove();
}
