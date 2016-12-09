package goserver;

public interface Player {
	
	
	public Move getMove();
	public void sendAlert(int color);
	public void sendResponse(String response);
	public void sendOpponentsMove(Move move);
	public void sendBoard(String boardRaw, int boardSize);
	public void sendCancellingMove(Move move);
	public void endCommunication();
}
