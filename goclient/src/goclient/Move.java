package goclient;

public class Move {
	public static final int WHITE_NUMBER = 1;
	public static final int BLACK_NUMER = -1;
	private int x;
	private int y;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Move(int x, int y, int color){
		this.x = x;
		this.y = y;
	}
}
