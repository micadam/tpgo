package models.msgs;

public class Prisoners {
	final int white;
	final int black;
	public Prisoners(int white, int black){
		this.white = white;
		this.black = black;
	}
	public int getWhitePrisoners(){
		return white;
	}
	public int getBlackPrisoners(){
		return black;
	}
}
