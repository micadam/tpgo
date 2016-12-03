package goserver;

public interface GameRule {
	/**
	 * @return wether or not a game board sync is necessary after this method is finished
	 */
	boolean verifyMove(int x, int y, int[][] gameBoard);
}
