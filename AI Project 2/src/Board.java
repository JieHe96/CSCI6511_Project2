import java.util.*;

public class Board {
	private char[][] board;
	private int size;
	private int totalMoves;
	private int target;
	private int maxX;
	private int maxO;
	private List<Integer> moveList;
	private List<Integer> maxXList;
	private List<Integer> maxOList;
	private int mySeed;
	private int opponentSeed;
	
	
	public Board(int size, int target) {
		this.board = new char[size][size];
		this.size = size;
		this.totalMoves = 0;
		this.target = target;
		this.moveList = new LinkedList<>();
		this.maxXList = new LinkedList<>();
		this.maxOList = new LinkedList<>();
		this.mySeed = 1;
		this.opponentSeed = 2;
	}
	
	public void printGame() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] != 'X' && board[row][col] != 'O') {
					System.out.print(" ");
				} else {
					System.out.print(board[row][col]);
				}
				System.out.println();
			}
		}
	}
	
	public List<Integer> getMoves() {
		List<Integer> moves = new LinkedList<>();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] != 'X' || board[row][col] != 'O') {
					moves.add(row * 10 + col);
				}
			}
		}
		return moves;
	}
	
	public void makeMove(int n) {
		int i = n / size;
		int j = n % size;
		if (totalMoves % 2 == 0) {
			board[i][j] = 'X';
			moveList.add(n);
			update(i, j, 'X');
		} else {
			board[i][j] = 'O';
			moveList.add(n);
			update(i, j, 'O');
		}
		totalMoves++;
		checkWin();
	}
	
	public void undoMove() {
		int last = moveList.get(moveList.size()-1);
		board[last/size][last%size] = '\u0000';
		if (totalMoves % 2 == 0) {
			maxOList.remove(maxOList.size()-1);
			maxO = maxOList.get(maxOList.size()-1);
		} else {
			maxXList.remove(maxXList.size()-1);
			maxX = maxXList.get(maxXList.size()-1);
		}
		totalMoves--;
	}
	
	private void update(int i, int j, char ch) {
		int rowCount = 0;
		int colCount = 0;
		int ldgCount = 0;
		int rdgCount = 0;
		for (int m = 0; m < size; m++) {
			if (board[i][m] == ch) {
				rowCount++;
			} else {
				if (ch == 'X') {
					maxX = Math.max(maxX, rowCount);
				} else {
					maxO = Math.max(maxO, rowCount);
				}
				rowCount = 0;
			}
		}
		
		for (int m = 0; m < size; m++) {
			if (board[m][j] == ch) {
				colCount++;
			} else {
				if (ch == 'X') {
					maxX = Math.max(maxX, colCount);
				} else {
					maxO = Math.max(maxO, colCount);
				}
				colCount = 0;
			}
		}
		
		int x = i;
		int y = j;
		while (x >= 0 && y >= 0 && board[x][y] == ch) {
			ldgCount++;
			x--;
			y--;
		}
		x = i + 1;
		y = j + 1;
		while (x < size && y < size && board[x][y] == ch) {
			ldgCount++;
			x++;
			y++;
		}
		x = i;
		y = j;
		while (x < size && y >= 0 && board[x][y] == ch) {
			rdgCount++;
			x++;
			y--;
		}
		x = i - 1;
		y = j + 1;
		while (x >= 0 && y < size && board[x][y] == ch) {
			rdgCount++;
			x--;
			y++;
		}
		if (ch == 'X') {
			maxX = Math.max(maxX, Math.max(ldgCount, rdgCount));
			maxXList.add(maxX);
		} else {
			maxO = Math.max(maxO, Math.max(ldgCount, rdgCount));
			maxOList.add(maxO);
		}
	}
	
	public void checkWin() {
		if (maxX == target) {
			declareWin('X');
		} else if (maxO == target) {
			declareWin('O');
		} else if (totalMoves == size * size) {
			System.out.println("Tie game!");
		}
	}
	
	public void declareWin(char ch) {
		System.out.println("Game Over! " + ch + " has win!");
	}
	 
	private int[] minimax(int depth, int alpha, int beta) {
		int score;
		int bestMove = -1;
		List<Integer> nextMoves = getMoves();
		if (nextMoves.isEmpty() || depth == 0) {
			score = evaluate();
			return new int[] {score, bestMove};
		}
		for (int n: getMoves()) {
			if ((totalMoves + depth) % 2 == 0) {
				makeMove(n);
				score = minimax(depth - 1, alpha, beta)[0];
				if (score > alpha) {
					alpha = score;
					bestMove = n;
				}
			} else {
				makeMove(n);
				score = minimax(depth - 1, alpha, beta)[0];
				if (score < beta) {
					beta = score;
					bestMove = n;
				}
			}
			if (alpha >= beta) {
				break;
			}
			undoMove();
		}
		return new int[] {score, bestMove};
	}
	

	public void makeAIMove() {
		int depth = 4;
		makeMove(minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE)[1]);
	}
	
	
	/*
	private int evaluateRow(char ch) {
		int score = 0;
		for (int i = 0; i < size; i++) {
			int count = 0;
			for (int j = 0 ; j < size; i++) {
				if (board[i][j] == ch) {
					count++;
				} else {
					if (count > 1) {
						score += (int) Math.pow(10, count);
						count = 0;
					}
				}
				if (j == size - 1 && count > 1) {
					score += (int) Math.pow(10, count);
				}
			}
		}
		return score;
	}
	
	private int evaluateCol(char ch) {
		int score = 0;
		for (int i = 0; i < size; i++) {
			int count = 0;
			for (int j = 0 ; j < size; i++) {
				if (board[j][i] == ch) {
					count++;
				} else {
					if (count > 1) {
						score += (int) Math.pow(10, count);
						count = 0;
					}
				}
				if (j == size - 1 && count > 1) {
					score += (int) Math.pow(10, count);
				}
			}
		}
		return score;
	}
	*/
 	
	
	
	
	
	private int calculateScore(char[][]board, int move, int seed) {
		int[][] intBoard = convertBoard(board);
		int i = move / size;
		int j = move % size;
		intBoard[i][j] = seed;
		int hFactor = calculateRowScore(intBoard, seed, i, j);
		int hScore = hFactor * 50;
		int vFactor = calculateColScore(intBoard, seed, i , j);
		int vScore = vFactor * 50;
		int dFactor = calculateDiaScore(intBoard, seed, i , j);
		int dScore = dFactor * 50;
		return hScore + vScore + dScore;
	}
	
	private int calculateRowScore(int[][]board, int seed, int row, int col) {
		int hFactor = 0;
		for (int m = row; m < size; m++) {
			if(board[m][col] == seed) hFactor++;
			else break;
		}
		if(row > 0) {
			for (int m = row-1; m >= 0; m--) {
				if(board[m][col] == seed) hFactor++;
				else break;
			}
		}
		return hFactor;
	}
	
	private int calculateColScore(int[][]board, int seed, int row, int col) {
		int vFactor = 0;
		for (int m = col; m < size; m++) {
			if(board[row][m] == seed) vFactor++;
			else break;
		}
		if(row > 0) {
			for (int m = col-1; m >= 0; m--) {
				if(board[row][m] == seed) vFactor++;
				else break;
			}
		}
		return vFactor;
	}
	
	private int calculateDiaScore(int[][]board, int seed, int row, int col) {
		int dFactor = 0;
		int m = row;
		int n = col;
		while(m < size && n < size) {
			if(board[m][n] == seed) dFactor++;
			else break;
			m++;
			n++;
		}
		m = row;
		n = col;
		if (m > 0 && n > 0) {
			while(m-1 >=0 && n-1 >=0) {
				if(board[m][n] == seed) dFactor++;
				else break;
				m--;
				n--;
			}
		}
		return dFactor;
	}
	
	private int[][] convertBoard(char[][]board) {
		int[][] convertBoard = new int[size][size];
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(board[i][j] == 'X') {
					convertBoard[i][j] = 1;
				}
				else if(board[i][j] == 'O') {
					convertBoard[i][j] = 2;
				}
				else {
					convertBoard[i][j] = 0;
				}
			}
		}
		return convertBoard;
	}
}