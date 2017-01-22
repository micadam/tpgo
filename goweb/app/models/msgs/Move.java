package models.msgs;

public class Move {
	
	public static int WHITE =1;
	public static int BLACK = -1;
	public int x;
	public int y;
	public int color;
	
	public Move(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
}
