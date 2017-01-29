package models.msgs;

public class Sync {
	
	final int[][] board;
	private final boolean isTerritoriesBoard;
	
	public int[][] getBoard(){
		return board;
	}
	public boolean isTerritories() {
		return isTerritoriesBoard;
	}
	public Sync(int[][] board, boolean isTerritories){
		isTerritoriesBoard = isTerritories;
		this.board = board;
	}
}
