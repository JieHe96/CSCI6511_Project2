import java.util.*;
import java.io.*;

public class tictactoe {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("input board size: ");//input the board size
		int size = scanner.nextInt();
		System.out.print("input win target: ");//input the number of consecutive pieces to win
		int target = scanner.nextInt();
		Board game = new Board(size, target);
		Scanner scanner1 = new Scanner(System.in);
		game.makeAIMove();//AI makes the first move
		game.printGame();
		System.out.println();
		while(true) {
			String line = scanner1.nextLine();//AI and the player take turns playing. The player
			int move = Integer.parseInt(line);//plays by inputting the position of the move
			game.makeMove(move);
			game.printGame();
			System.out.println();
			game.makeAIMove();
			game.printGame();
			System.out.println();
		}
	}
}
