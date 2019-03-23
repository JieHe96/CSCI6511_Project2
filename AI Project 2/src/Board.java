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
	
	//initialize the board to size * size and target amount of consecutive pieces to win
	public Board(int size, int target) {
		this.board = new char[size][size];
		this.size = size;
		this.totalMoves = 0;
		this.target = target;
		this.moveList = new LinkedList<>();
		this.maxXList = new LinkedList<>();
		this.maxOList = new LinkedList<>();
	}
	
	//print the current state of the game
	public void printGame() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] != 'X' && board[row][col] != 'O') {
					System.out.print("_.");
				} else {
					System.out.print(board[row][col] + ".");
				}
			}
			System.out.println();
		}
	}
	
	//get a list of all possible moves on the board
	public List<Integer> getMoves() {
		List<Integer> moves = new LinkedList<>();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] != 'X' && board[row][col] != 'O') {
					moves.add(row * size + col);
				}
			}
		}
		return moves;
	}
	
	//make a move on the board for the player in turn
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
	
	//undo the previous move made
	public void undoMove() {
		int last = moveList.get(moveList.size()-1);
		board[last/size][last%size] = '\u0000';
		if (totalMoves % 2 == 0) {
			maxOList.remove(maxOList.size()-1);
			if (maxOList.size() != 0) {
				maxO = maxOList.get(maxOList.size()-1);
			} else {
				maxO = 0;
			}
		} else {
			maxXList.remove(maxXList.size()-1);
			if (maxXList.size() != 0) {
				maxX = maxXList.get(maxXList.size()-1);
			} else {
				maxX = 0;
			}
		}
		totalMoves--;
	}
	
	//after making a move or undoing a move, update the information related to current state of the board
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
	
	//check if a player has won
	public int checkWin() {
		if (maxX == target) {
			declareWin('X');
			return 1;
		} else if (maxO == target) {
			declareWin('O');
			return 1;
		} else if (totalMoves == size * size) {
			System.out.println("Tie game!");
			return 1;
		}
		return 0;
	}
	
	//declare the winner
	public void declareWin(char ch) {
		System.out.println("Game Over! " + ch + " has win!");
	}
	
	//use minimax and alpha beta pruning to decide the next move
	private int[] minimax(int depth, int alpha, int beta) {
		int score = 0;
		int bestMove = -1;
		List<Integer> nextMoves = getMoves();
		if (nextMoves.isEmpty() || depth == 0) {
			score = calculateScore(this.board, 1, 2);
			//System.out.println("Score: " + score);
			return new int[] {score, bestMove};
		}
		//score = 0;
		for (int n: nextMoves) {
			if ((totalMoves + depth) % 2 == 0) {
				board[n/size][n%size] = 'X';
				score = minimax(depth - 1, alpha, beta)[0];
				if (score > alpha) {
					alpha = score;
					bestMove = n;
				}
			} else {
				board[n/size][n%size] = 'O';
				score = minimax(depth - 1, alpha, beta)[0];
				if (score < beta) {
					beta = score;
					bestMove = n;
				}
			}
			board[n/size][n%size] = '\u0000';
			if (alpha >= beta) {
				break;
			}
		}
		return new int[] {score, bestMove};
	}
	
	//make a move based on the minimax function
	public void makeAIMove() {
		int depth = 4;
		makeMove(minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE)[1]);
	}
	

	//add up all the heuristic and get the score of a move
	private int calculateScore(char[][]board, int seed, int oppSeed) {
		int[][] intBoard = convertBoard(board);
		int rScore = evaluateRow(intBoard, seed);
		int cScore = evaluateCol(intBoard, seed);
		int dScore = evaluateDig(intBoard, seed);
		int orScore = evaluateRow(intBoard, oppSeed);
		int ocScore = evaluateCol(intBoard, oppSeed);
		int odScore = evaluateDig(intBoard, oppSeed);
		int cScore1 = evaluateCol2(intBoard, oppSeed, seed);
		int rScore1 = evaluateRow2(intBoard, oppSeed, seed);
		int dScore1 = evaluateDig2(intBoard, oppSeed, seed);
		int totalScore = rScore + cScore + dScore + cScore1 + rScore1 + dScore1;
		//int totalScore = rScore + cScore + dScore + cScore1 + rScore1 
		//				- orScore - ocScore - odScore;
		//int totalScore = 0;
		return totalScore;
	}
	
	//calculate row heuristic
	private int evaluateRow(int[][]board, int seed) {
		int max = 0;
		int score = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(board[i][j] == seed) {
					max++;
				}
				else {
					if(max > 1) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
				}
			}
			max = 0;
		}
		return score;
	}
	
	//calculate row heuristic where block opponent tik
	private int evaluateRow2(int[][]board, int oppSeed, int seed) {
		int max = 0;
		int score = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(board[i][j] == oppSeed) {
					max++;
				}
				else {
					if(max > 1 && board[i][j] == seed) {
						max++;
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
				}
			}
			max = 0;
		}
		return score;
	}
	
	//calculate column heuristic
	private int evaluateCol(int[][]board, int seed) {
		int max = 0;
		int score = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(board[j][i] == seed) {
					max++;
				}
				else {
					if(max > 1) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
					
				}
			}
			max = 0;
		}
		return score;
	}
	
	//calculate column heuristic where block opponent tik
	private int evaluateCol2(int[][]board, int oppSeed, int seed) {
		int max = 0;
		int score = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(board[j][i] == oppSeed) {
					max++;
				}
				else {
					if(max > 1 && board[j][i] == seed) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
					
				}
			}
			max = 0;
		}
		return score;
	}
	
	//calculate diagonal heuristic
	private int evaluateDig(int[][]board, int seed) {
		int max = 0;
		int score = 0;
		for(int i = 1; i < size; i++) {
			int j = 0;
			int m = i;
			while(m >= 0 && j < size) {
				if(board[m][j] == seed) {
					max++;
				}
				else {
					if(max > 1) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
				}
				m--;
				j++;
			}
			max = 0;
		}
		max = 0;
		for(int j = 1; j < size-1; j++) {
			int i = size-1;
			int n = j;
			while(i >= 0 && n < size) {
				if(board[i][n] == seed) {
					max++;
				}
				else {
					if(max > 1) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
				}
				i--;
				n++;
			}
			max = 0;
		}
		max = 0;
		for(int i = size-2; i >= 0; i--) {
			int j = 0;
			int m = i;
			while(m < size && j < size) {
				if(board[m][j] == seed) {
					max++;
				}
				else {
					if(max > 1) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
				}
				m++;
				j++;
			}
			max = 0;
		}
		max = 0;
		for(int j = 1; j < size-1; j++) {
			int i = 0;
			int n = j;
			while(i < size && n < size) {
				if(board[i][n] == seed) {
					max++;
				}
				else {
					if(max > 1) {
						int tmp = (int) Math.pow(10, max);
						score += tmp;
					}
					max = 0;
				}
				i++;
				n++;
			}
			max = 0;
		}
		return score;
	}
	
	//calculate diagonal heuristic where block opponent tik
	private int evaluateDig2(int[][]board, int oppSeed, int seed) {
		int max = 0;
		int score = 0;
		for(int i = 1; i < size; i++) {
			int j = 0;
			int m = i;
			while(m >= 0 && j < size) {
				if(board[m][j] == oppSeed) {
					max++;
				}
				else {
					if(max > 1 && board[m][j] == seed) {
						int tmp = (int) Math.pow(8, max);
						score += tmp;
					}
					max = 0;
				}
				m--;
				j++;
			}
			max = 0;
		}
		max = 0;
		for(int j = 1; j < size-1; j++) {
			int i = size-1;
			int n = j;
			while(i >= 0 && n < size) {
				if(board[i][n] == oppSeed) {
					max++;
				}
				else {
					if(max > 1 && board[i][n] == seed) {
						int tmp = (int) Math.pow(8, max);
						score += tmp;
					}
					max = 0;
				}
				i--;
				n++;
			}
			max = 0;
		}
		max = 0;
		for(int i = size-2; i >= 0; i--) {
			int j = 0;
			int m = i;
			while(m < size && j < size) {
				if(board[m][j] == oppSeed) {
					max++;
				}
				else {
					if(max > 1 && board[m][j] == seed) {
						int tmp = (int) Math.pow(8, max);
						score += tmp;
					}
					max = 0;
				}
				m++;
				j++;
			}
			max = 0;
		}
		max = 0;
		for(int j = 1; j < size-1; j++) {
			int i = 0;
			int n = j;
			while(i < size && n < size) {
				if(board[i][n] == oppSeed) {
					max++;
				}
				else {
					if(max > 1 && board[i][n] == seed) {
						int tmp = (int) Math.pow(8, max);
						score += tmp;
					}
					max = 0;
				}
				i++;
				n++;
			}
			max = 0;
		}
		return score;
	}
	
	//convert char board to int board for helper function
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
