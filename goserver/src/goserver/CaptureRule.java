package goserver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CaptureRule implements GameRule {
	int whitePrisoners;
	int blackPrisoners;
	
	static int directions[][] = { {0, 1}, {1, 0}, {0, -1}, {-1, 0}};

	private boolean fieldInBounds(int x, int y, int boardSize) {
		return (x >= 0 && x < boardSize && y >= 0 && y < boardSize); //check if the field is inside the board (is not invalid)
	}
	
	@Override
	public boolean verifyMove(int x, int y, int[][] gameBoard) {
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
				
				if(gameBoard[newX][newY] == gameBoard[x][y]) {	//this is here to check if the player commited suicide (I'm not sure wether this is allowed)
					workingGroup = 0;															//set the working group to the active player group
					workingColor = gameBoard[x][y];
				} else if (gameBoard[newX][newY] == -1 * gameBoard[x][y]) { //there's an opponent's piece on the intersection
					currentGroup++;																			//this is a non-visited piece, so we're in a new group of pieces
					breathsOfGroup.add(0);																//we need to add a number of breaths entry for the group
					workingGroup = currentGroup;
					workingColor = -1 * gameBoard[x][y];
				} else if (gameBoard[newX][newY] == 0) {						//the active player's group has one more breath thanks to this free intersection
					breathsOfGroup.set(0, breathsOfGroup.get(0) + 1);
					continue;
				}
				
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
			}
		}
		
		boolean boardChanged = false;
		//check the board for groups with no breaths;
		System.out.println(breathsOfGroup);
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(groupOf[i][j] != -1 && breathsOfGroup.get(groupOf[i][j]) == 0) {
					System.out.println("[SERVER]Removing piece group " + groupOf[i][j]);
					gameBoard[i][j] = 0;
					boardChanged = true;
				}
			}
		}
		return boardChanged;
	}
}
