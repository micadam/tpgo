package goserver;

public class GameInstance {
	
	public final static int STATE_IDLE = 1;
	
	private int currentState;
	private String keyCode;
	
	public int getState() {
		return currentState;
	}
	public String getKeyCode() {
		return keyCode;
	}

}
