package models.msgs;

public class ReadyState {
	private final boolean whiteReady;
	private final boolean blackReady;
	
	public ReadyState(boolean whiteReady, boolean blackReady) {
		this.whiteReady = whiteReady;
		this.blackReady = blackReady;
	}
	
	public boolean isWhiteReady() {
		return whiteReady;
	}
	public boolean isBlackReady() {
		return blackReady;
	}

}
