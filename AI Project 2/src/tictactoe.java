import java.util.*;
import java.io.*;

public class tictactoe {

	public static void main(String[] args) {
		Board game = new Board(5, 3);
		/*
		game.makeMove(12);
		game.printGame();
		game.makeMove(13);
		game.printGame();
		System.out.println();
		*/
//		while(true) {
//			game.makeAIMove();
//			game.printGame();
//			System.out.println();
//			if(game.checkWin() == 1) break;
//		}
		
		game.makeAIMove();
		game.printGame();
		System.out.println();
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String line = scanner.nextLine();
			int move = Integer.parseInt(line);
			game.makeMove(move);
			game.printGame();
			System.out.println();
			game.makeAIMove();
			game.printGame();
			System.out.println();
		}

	}
}
