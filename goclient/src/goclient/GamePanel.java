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
	private final int fieldCount=19;				//zrobi żeby dało się zmienia		
	private int size;
	private int pawnSize= 50;
	private Color boardColor;
	private int[][] board= new int[fieldCount][fieldCount];
	MoveListener moveListener;
	
	public void makeMove(Move move){
		board[move.getX()][move.getY()]=move.getColor();
		repaint();
	}
	public void setBoard(int[][] board){
		this.board=board;
		repaint();
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
		int dotSize=10;
		for(int i=4;i<fieldCount;i+=6){		//4 bo jeszcze to puste
			for(int j=4;j<fieldCount;j+=6)
				g.fillOval(i * fieldSize - dotSize/2,j * fieldSize - dotSize/2, 10, dotSize);
		}
		for(int x=0; x<fieldCount; x++){		//pionki
			for(int y=0; y<fieldCount; y++){
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
		
		
	}
	public GamePanel(){
		size=fieldSize*(fieldCount+1);
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
		return fieldCount;
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
		if( SwingUtilities.isLeftMouseButton(me) ) {		
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
