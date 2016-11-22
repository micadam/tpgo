package goclient;

public interface GoGameManager {
	
	public void setGameClient(GoClient goClient);
	public boolean makeMove(int x, int y);
}
