package goserver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CaptureRule implements GameRule {
	int whitePrisoners=0;
	int blackPrisoners=0;
	private Move forbiddenByKo= new Move(-100,-100,0);
	
	static int directions[][] = { {0, 1}, {1, 0}, {0, -1}, {-1, 0}};

	static private boolean fieldInBounds(int x, int y, int boardSize) {
		return (x >= 0 && x < boardSize && y >= 0 && y < boardSize); //check if the field is inside the board (is not invalid)
	}
	private boolean koRule(int x, int y,int captured){
		if(forbiddenByKo.getX()==x && forbiddenByKo.getY()==y && captured==1){
			return true;
		}
		return false;
	}
	public void dismissKo(){
		forbiddenByKo=new Move(-100,-100,0);
	}
	public int getScore(){
		return blackPrisoners-whitePrisoners;
	}
	
	public int getWhitePrisoners() {
		return whitePrisoners;
	}
	
	public int getBlackPrisoners() {
		return blackPrisoners;
	}
	
	
	@Override
	public int verifyMove(int x, int y, int[][] gameBoard, int color) {
		int boardSize = gameBoard[0].length;
		
		boolean visited[][] = new boolean[boardSize][boardSize];	//whether or not we visited a given piece, default is false
		int groupOf[][] = new int[boardSize][boardSize];				//group of a piece, if it's unimportant it will remain as -1
		
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				groupOf[i][j] = -1;
			}
		}
		
		groupOf[x][y] = 0;					//the number 0 group is reserved for the most recently placed piece
		visited[x][y] = true;
		int currentGroup = 0;

		List<Integer> breathsOfGroup = new ArrayList<Integer>();
		breathsOfGroup.add(0);		//group (number 0) of pawns to which a pawn has been just added
		LinkedList<Integer> xQueue = new LinkedList<Integer>();
		LinkedList<Integer> yQueue = new LinkedList<Integer>();
		
		for(int[] dir : directions) {	//looking at each intersection neighboring the most recently placed piece
			int newX = x + dir[0];
			int newY = y + dir[1];
			if(fieldInBounds(newX, newY, boardSize) && visited[newX][newY] == false) {
				int workingGroup = -1;
				int workingColor = -100;
				
				if(gameBoard[newX][newY] == color) {	//this is here to check if the player commited suicide (I'm not sure wether this is allowed)
					workingGroup = 0;															//set the working group to the active player group
					workingColor = color;
				} else if (gameBoard[newX][newY] == -1 * color) { //there's an opponent's piece on the intersection
					currentGroup++;																			//this is a non-visited piece, so we're in a new group of pieces
					breathsOfGroup.add(0);																//we need to add a number of breaths entry for the group
					workingGroup = currentGroup;
					workingColor = -1 * color;
				} else if (gameBoard[newX][newY] == 0) {						//the active player's group has one more breath thanks to this free intersection
					breathsOfGroup.set(0, breathsOfGroup.get(0) + 1);
					System.out.println("Adding breaths to group 0 " + breathsOfGroup.get(0));
					continue;
				}
				int breaths=PawnGroupAlgorithm.getBreathsOfThisGroup(newX,newY,visited,groupOf,gameBoard,workingGroup,workingColor,0);
				breathsOfGroup.set(workingGroup, breathsOfGroup.get(workingGroup)+breaths);
				/*
				xQueue.push(newX);
				yQueue.push(newY);
				while(xQueue.isEmpty() == false) {								//keep adding pieces until we visited the whole group
					int curX = xQueue.pop();											//take a queued piece
					int curY = yQueue.pop();
					visited[curX][curY] = true;
					groupOf[curX][curY] = workingGroup;					//set its group
					for(int dir2[] : directions) {											//try to queue each of its neighbors
						newX = curX + dir2[0];
						newY = curY + dir2[1];
						if(fieldInBounds(newX, newY, boardSize) && visited[newX][newY] == false) {
							if(gameBoard[newX][newY] == 0){					//if it's a free field, add a breath to this group
								breathsOfGroup.set(workingGroup, breathsOfGroup.get(workingGroup) + 1);
							} else if(gameBoard[newX][newY]  == workingColor) {	//it's an allied piece, queue it
								xQueue.push(newX);
								yQueue.push(newY);
							}
						}
					}
				}
				*/
			}
		}
		
		
		int captured = 0;
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(groupOf[i][j] >0 && breathsOfGroup.get(groupOf[i][j]) == 0) {		//do not count suicidal captures
					captured++;
					if(captured>1)			//not important 
						break;
				}
			}
		}
		
		if(captured==0 && breathsOfGroup.get(0) == 0) {
			System.out.println("Suicide rule");
			return -1;
		}
		if(koRule(x,y,captured)){
			System.out.println("Ko Rule");
			return -1;
		}
		dismissKo();
		
		for(int i = 0; i < boardSize; i++) {	
			for(int j = 0; j < boardSize; j++) {
				if(groupOf[j][i] != -1) {					
					System.out.print(groupOf[j][i]);
				} else {
					System.out.print(".");
				}
			}
			System.out.print('\n');
		}
		
		
		int boardChanged = 0;
		captured=0;
		//check the board for groups with no breaths;
		System.out.println("[SERVR] Checking for groups with no breaths");
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(groupOf[i][j] != -1  && breathsOfGroup.get(groupOf[i][j]) == 0) {
					System.out.println("[SERVER]Removing piece group " + groupOf[i][j]);
					if(gameBoard[i][j] == Move.WHITE_NUMBER){						
						whitePrisoners++;
					}
					else if (gameBoard[i][j] == Move.BLACK_NUMBER){						
						blackPrisoners++;
					} else {
						System.out.println("[SERVER] Unexpected board piece in CaptureRule");
					}
					captured++;
					gameBoard[i][j] = 0;
					if(groupOf[i][j] != 0)
						forbiddenByKo=new Move(i,j,0);			//remember captured territory 
															//so it can't be reused for capture in the next move
				}
			}
		}
		if(captured==1 || ( captured==2 && breathsOfGroup.get(0)==0)) {			
			System.out.println("Ko rule active");
		} else {			
			dismissKo();
		}
		
		boardChanged = (captured > 0 ? 1 : 0) ;
		return boardChanged;
	}
}
