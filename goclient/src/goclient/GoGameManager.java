package goclient;

public interface GoGameManager {
	
	public int makeMove(Move move);
	public int[][] getBoard();
	public int getBoardSize();
	public Move getResponse();
	public int getGameStatus();
	public String getStatusMessage();
	public Move getCancellingMove();
	public int getWhitePrisoners();
	public int getBlackPrisoners();
}
