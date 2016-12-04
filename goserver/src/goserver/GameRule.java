package goserver;

public interface GameRule {
	/**
	 * @return wether or not a game board sync is necessary after this method is finished
	 */
	int verifyMove(int x, int y, int[][] gameBoard, int color);
}
