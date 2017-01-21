package models.msgs;

public class Opponent {
	final int x;
	final int y;
	final int color;
	public Opponent ( int x, int y , int color){
		this.x = x;
		this.y = y;
		this.color = color;
	}
	public int getX(){
		return x;	
	}
	public int getY(){
		return y;
	}
	public int getColor(){
		return color;
	}
}
