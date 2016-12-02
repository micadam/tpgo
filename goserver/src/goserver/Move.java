package goserver;

public class Move {

	int x;
	int y;
	int color;
	
	public static final int WHITE_NUMBER = 1;
	public static final int BLACK_NUMBER = -1;
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getColor(){
		return color;
	}
	
	
	public Move(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
}
