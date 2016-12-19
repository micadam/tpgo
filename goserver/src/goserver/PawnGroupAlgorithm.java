package goserver;

import java.util.ArrayList;
import java.util.LinkedList;

public class PawnGroupAlgorithm {
	private static int[][] directions={{0,1},{0,-1},{1,0},{-1,0}};
	private static boolean fieldInBounds(int x,int y,int boardSize){
		return (x>=0 && y>=0 && x<boardSize && y<boardSize);
	}
	
	
	/**
	 * @param x starting point x
	 * @param y starting point y
	 * @param visited
	 * @param groupOf an array on which we will store our groups 
	 * @param gameBoard
	 * @param workingGroup new group id
	 * @param workingColor color of this group
	 * @param breathColor color of this groups breath, should be different than working color
	 * @return
	 */
	public static int getBreathsOfThisGroup(int x,int y,boolean[][] visited, int[][] groupOf, int[][] gameBoard, int workingGroup,int workingColor,int breathColor){
		int boardSize=gameBoard[0].length;
		LinkedList<Integer> xQueue= new LinkedList<Integer>();
		LinkedList<Integer> yQueue= new LinkedList<Integer>();
		xQueue.push(x);
		yQueue.push(y);
		int newX, newY;
		int breaths=0;
		while(xQueue.isEmpty() == false) {								//keep adding pieces until we visited the whole group
			int curX = xQueue.pop();											//take a queued piece
			int curY = yQueue.pop();
			visited[curX][curY] = true;
			groupOf[curX][curY] = workingGroup;					//set its group
			for(int dir2[] : directions) {											//try to queue each of its neighbors
				newX = curX + dir2[0];
				newY = curY + dir2[1];
				if(fieldInBounds(newX, newY, boardSize) && visited[newX][newY] == false) {
					if(gameBoard[newX][newY] == breathColor){					//if it's a free field, add a breath to this group
						breaths++;
					} else if(gameBoard[newX][newY]  == workingColor) {	//it's an allied piece, queue it
						xQueue.push(newX);
						yQueue.push(newY);
					}
				}
			}
		}
		return breaths;
	}

	public static void fillThisGroup(int x,int y,boolean[][] visited1, int[][] groupOf, int[][] gameBoard){
		if(gameBoard[x][y]!=0 )
			return;
		System.out.println("X Y " + x + " " + y);
		int boardSize=gameBoard.length;
		boolean visited[][] = new boolean[boardSize][boardSize];
		ArrayList<Integer> xQueue= new ArrayList<Integer>();
		ArrayList<Integer> yQueue= new ArrayList<Integer>();
		xQueue.add(x);
		yQueue.add(y);
		int newX, newY;
		int black=0,white=0;
		int index =0;
		while(index < xQueue.size()) {								//keep adding pieces until we visited the whole group
			int curX = xQueue.get(index);											//take a queued piece
			int curY = yQueue.get(index);
			
			for(int dir2[] : directions) {											//try to queue each of its neighbors
				newX = curX + dir2[0];
				newY = curY + dir2[1];
				if(fieldInBounds(newX, newY, boardSize) && visited[newX][newY] == false) {
					visited[newX][newY]=true;
					visited1[newX][newY]=true;
					if(gameBoard[newX][newY] == Move.WHITE_NUMBER){		
						
						white++;
						System.out.println("Białe : " + white);
					} else if( gameBoard[newX][newY]== Move.BLACK_NUMBER){
						black++;
						System.out.println("Czorne XD  : " +  black);
					}
					else {	//it's an empty  place, queue it
						xQueue.add(newX);
						yQueue.add(newY);
					} 
				}
			}
			index++;
		}
		int workingGroup=0;
		if(black == 0 ){
			workingGroup=Move.WHITE_NUMBER;
		}else if( white == 0 ) {
			workingGroup = Move.BLACK_NUMBER;
		}
		System.out.println("WORKING GRoup " + workingGroup);
		if(workingGroup!= 0 ){
			index--;
			while(index>=0){
				groupOf[xQueue.get(index)][yQueue.get(index)]= workingGroup;
				System.out.println("pop");
				index--;
			}
		}
		yQueue.clear();
		xQueue.clear();
	}
}
