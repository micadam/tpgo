package models.msgs;

public class ReDo {
	final int x;
	final int y;
	final int color;
	public ReDo( int x, int y , int color){
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
