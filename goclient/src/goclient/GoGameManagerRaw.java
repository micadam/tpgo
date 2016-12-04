package goclient;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

public class GoGameManagerRaw implements GoGameManager {
	private int gameBoard[][];
	private int fieldsLeft;
	private static int BOARD_SIZE = 19;
	int currentColor = -1;
	boolean isBotGame=false;
	
	public GoGameManagerRaw(){
		gameBoard=new int[BOARD_SIZE][BOARD_SIZE];
		fieldsLeft=BOARD_SIZE*BOARD_SIZE;
		currentColor = Move.BLACK_NUMBER;
		AuthWindowRaw awr = new AuthWindowRaw(this);
	}
	

	public int[][] getBoard() {
		int copy[][]=new int[BOARD_SIZE][BOARD_SIZE];
		for(int x=0;x<BOARD_SIZE;x++){
			for(int y=0;y<BOARD_SIZE;y++)
				copy[x][y]=gameBoard[x][y];
		}
		return copy;
	}	
	public Move getResponse(){		//weak bot
		Move move=null;
		for(int x=0;x<BOARD_SIZE && move==null;x++){
			for(int y=0;y<BOARD_SIZE ;y++){
				if(gameBoard[x][y]==0){ 
					move=new Move(x,y, currentColor);
					makeMove(x, y);
					break;
				}
			}
		}
		return move;
	}
	
	public int getGameStatus() {
	//	System.out.println("Returning status: " + currentColor);
		if(isBotGame && currentColor==Move.BLACK_NUMBER)
			return 2;
		return currentColor;
	}
	
	public String getStatusMessage() {
		String color = (currentColor == Move.BLACK_NUMBER ? "Black" : "White");
		return "Make your move: " + color;
		
	}
	public int makeMove(int x, int y){
		if( x != -1 ){	  //check if player passed
			if(x < 0 || y < 0 || x >= BOARD_SIZE ||  y >= BOARD_SIZE || gameBoard[x][y] != 0) { //invalid move
				return -1;
			}
			
			fieldsLeft--;
			gameBoard[x][y] = currentColor;
		}
		if(fieldsLeft<2) {			//no more moves
			return 3;
		}
		
		currentColor *= -1;
		return 1;
	}
	public void setGameType(String type){
		System.out.println("Game type set to "+type);
		isBotGame=(type.compareTo("Singleplayer")==0);
	}


	@Override
	public Move getCancellingMove() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getBoardSize() {
		return BOARD_SIZE;
	}
}


class AuthWindowRaw extends JDialog{
	public AuthWindowRaw(GoGameManagerRaw ggmr){
		setModalityType(DEFAULT_MODALITY_TYPE);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
		Button multiplayer = new Button("Multiplayer");
		multiplayer.addActionListener(new AuthButtonListener(ggmr,this));
		Button singleplayer = new Button("Singleplayer");
		singleplayer.addActionListener(new AuthButtonListener(ggmr,this));
		add(multiplayer);
		add(singleplayer);
		
		pack();
		setVisible(true);
	}
}
class AuthButtonListener implements ActionListener{
	private GoGameManagerRaw goGameManagerRaw;
	private AuthWindowRaw parent;
	public AuthButtonListener(GoGameManagerRaw ggmr,AuthWindowRaw parent){
		this.goGameManagerRaw=ggmr;
		this.parent=parent;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		goGameManagerRaw.setGameType(e.getActionCommand());
		parent.dispose();
	}
	
}
