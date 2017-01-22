package models.msgs;

public class Sync {
	
	final int[][] board;
	public int[][] getBoard(){
		return board;
	}
	public Sync(int[][] board){
		this.board = board;
	}
}
