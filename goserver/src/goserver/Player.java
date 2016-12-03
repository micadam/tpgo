package goserver;

public interface Player {
	
	
	public Move getMove();
	public void sendAlert(int color);
	public void sendResponse(String response);
	public void sendOpponentsMove(Move move);

}
