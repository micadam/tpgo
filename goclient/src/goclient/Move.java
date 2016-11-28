package goclient;

public class Move {
	public static final int WHITE_NUMBER = 1;
	public static final int BLACK_NUMER = -1;
	private int x;
	private int y;
	private int color;
	
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
	public int getColor() {
		return color;
	}
	public void setColor() {
		this.color = color;
	}
	
	public Move(int x, int y, int color){
		this.x = x;
		this.y = y;
		this.color = color;
	}
}
