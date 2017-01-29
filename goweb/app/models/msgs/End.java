package models.msgs;

public class End {
	private String winningColor;
	
	public End(String winningColor) {
		this.winningColor = winningColor;
	}
	
	public String getWinner() {
		return winningColor;
	}
}
	