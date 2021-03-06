package goclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel {
	private final int fieldSize=50;
	private int boardSize=19;				//zrobi żeby dało się zmienia		
	private int size;
	private int pawnSize= 50;
	private Color boardColor;
	private int[][] board= new int[boardSize][boardSize];
	MoveListener moveListener;
	private boolean territoriesMode=false;
	private int[][] territories = new int[boardSize][boardSize];
	
	public void makeMove(Move move){
		System.out.println("Opponent's move received");
		if(territoriesMode){
			territories[move.getX()][move.getY()]=move.getColor();
		}else{
			board[move.getX()][move.getY()]=move.getColor();
		}
		repaint();
	}
	public void setBoard(int[][] board){
		if(territoriesMode){
			territories=board;
		}else
			this.board=board;
		repaint();
	}
	public boolean getTerritoriesMode(){
		return territoriesMode;
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(boardColor);			
		g.fillRect(0, 0, size + fieldSize, size + fieldSize);
		g.setColor(Color.BLACK);
		for(int i=1;i<=boardSize;i++){		//kreski
			g.drawLine(i*fieldSize, fieldSize, i*fieldSize, size-fieldSize);	//piopnowe
			g.drawLine(fieldSize, i*fieldSize, size-fieldSize, i*fieldSize);	//poziome
		}
		int dotSize=10;
		if(boardSize == 19) {			
			for(int i=4;i<boardSize;i+=6){		//4 bo jeszcze to puste
				for(int j=4;j<boardSize;j+=6)
					g.fillOval(i * fieldSize - dotSize/2,j * fieldSize - dotSize/2, 10, dotSize);
			}
		}
		for(int x=0; x<boardSize; x++){		//pionki
			for(int y=0; y<boardSize; y++){
				if(board[x][y]!=0){
					if(board[x][y] == Move.WHITE_NUMBER)
						g.setColor(Color.WHITE);
					else if(board[x][y] == Move.BLACK_NUMBER) {						
						g.setColor(Color.BLACK);
					} else {
						throw new IllegalArgumentException();
					}
					g.fillOval(fieldSize + x * fieldSize - pawnSize/2, fieldSize + y * fieldSize - pawnSize/2, pawnSize, pawnSize);
				}
			}
		}
		if(territoriesMode){
			for(int x=0; x<boardSize; x++){		//pionki
				for(int y=0; y<boardSize; y++){
					if(territories[x][y]!=0){
						if(territories[x][y] >0 )
							g.setColor(Color.WHITE);
						else if(territories[x][y] < 0) {						
							g.setColor(Color.BLACK);
						} else {
							throw new IllegalArgumentException();
						}
						g.fillRect(fieldSize + x * fieldSize - pawnSize/4, fieldSize + y * fieldSize - pawnSize/4, pawnSize/2, pawnSize/2);
					}
				}
			}
		}
		
		
	}
	public GamePanel(int boardSize){
		this.boardSize = boardSize;
		size=fieldSize*(boardSize+1);
		setPreferredSize(new Dimension(size,size));
		setSize(size,size);
		boardColor= new Color(219,178,92);		//Brown
		moveListener = new MoveListener(this);
		moveListener.setMove (new Move(-100, -100, 0));
	}
	
	public void waitForMove(Move move) {
		moveListener.setMove(move);
		this.addMouseListener(moveListener);
		
	}
	public void stopWaitingForMove() {
		System.out.println("Stopped waiting for move");
		this.removeMouseListener(moveListener);
	}
	
	public int getFieldSize() {
		return fieldSize;
	}
	public int getPawnSize() {
		return pawnSize;
	}
	public int getFieldCount() {
		return boardSize;
	}
	public void setTerritoriesMode(boolean mode) {
		this.territoriesMode=mode;
		if(mode==true){
			for(int i=0;i<boardSize;i++){
				for(int j=0;j<boardSize;j++){
					territories[i][j]=0;
				}
			}
		}
	}
	
}

class MoveListener extends MouseAdapter {
	private Move move;
	private int fieldSize;
	private int pawnSize;
	private int fieldCount;
	GamePanel gamePanel;
	
	@Override
	public void mouseClicked(MouseEvent me) {
		if( SwingUtilities.isLeftMouseButton(me) || (gamePanel.getTerritoriesMode() && SwingUtilities.isRightMouseButton(me))) {		
			int x = me.getX();
			int y = me.getY();
			
			int xDifference = x % fieldSize;
			int yDifference = y % fieldSize;
			
			if(x % fieldSize > fieldSize / 2) {
				xDifference -= fieldSize;
			}
			if(y % fieldSize > fieldSize / 2) {
				yDifference -= fieldSize;
			}

			int closestX = x - xDifference;
			int closestY = y - yDifference;
			
			if(closestX < fieldSize || closestY < fieldSize || closestX >= (fieldCount + 1)* fieldSize || closestY >= (fieldCount + 1) * fieldSize) {
				return;
			}
			if(Point2D.distance(x, y, closestX, closestY) < pawnSize / 2) {
				synchronized(move){			
					closestX = closestX / fieldSize - 1;
					closestY = closestY / fieldSize - 1;
					move.setX(closestX);
					move.setY(closestY);
					if(SwingUtilities.isRightMouseButton(me))
						move.setColor(2);
					gamePanel.makeMove(move);
					System.out.println("Placing piece on coordinates: " + closestX + ", " + closestY);
				}
			}
		}
	}
	
	public void setMove(Move move) {
		this.move = move;
	}
	
	public MoveListener(GamePanel gamePanel) {
		this.fieldSize = gamePanel.getFieldSize();
		this.pawnSize = gamePanel.getPawnSize();
		this.fieldCount = gamePanel.getFieldCount();
		this.gamePanel = gamePanel;
	}
	
	
}
