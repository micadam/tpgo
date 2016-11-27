package goclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
	private final int fieldSize=50;
	private final int fieldCount=19;				//zrobi żeby dało się zmienia		
	private int size;
	private int pawnSize=16;
	private Color boardColor;
	private int[][] board= new int[fieldCount][fieldCount];
	public void makeMove(int x,int y,int color){
		board[x][y]=color;
	}
	public void setBoard(int[][] board){
		this.board=board;
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.setColor(boardColor);			
		g.fillRect(0, 0, size + fieldSize, size + fieldSize);
		g.setColor(Color.BLACK);
		for(int i=1;i<=fieldCount;i++){		//kreski
			g.drawLine(i*fieldSize, fieldSize, i*fieldSize, size-fieldSize);	//piopnowe
			g.drawLine(fieldSize, i*fieldSize, size-fieldSize, i*fieldSize);	//poziome
		}
		for(int x=0;x<fieldCount;x++){		//pionki
			for(int y=0;y<fieldCount;y++){
				if(board[x][y]!=0){
					if(board[x][y]==1)
						g.setColor(Color.WHITE);
					else {						
						g.setColor(Color.BLACK);
					}
					g.fillOval(fieldSize + x * fieldSize - pawnSize/2, fieldSize + x * fieldSize - pawnSize/2, pawnSize, pawnSize);
				}
			}
		}
		
		
	}
	public GamePanel(){
		size=fieldSize*(fieldCount+1);
		setPreferredSize(new Dimension(size,size));
		setSize(size,size);
		boardColor= new Color(219,178,92);		//Brown
	}
	
	public void waitForMove(Move move) {
	//TODO wszystko	
	}
	public void stopWaitingForMove() {
		//TODO
	}
	
}
