import java.util.*;
import java.io.*;

public class tictactoe {

	public static void main(String[] args) {
		Board game = new Board(6, 4);
		/*
		game.makeMove(12);
		game.printGame();
		game.makeMove(13);
		game.printGame();
		System.out.println();
		*/
		game.makeAIMove();
		game.printGame();
		System.out.println();
		game.makeAIMove();
		game.printGame();
		System.out.println();
		game.makeAIMove();
		game.printGame();
		System.out.println();
		game.makeMove(6);
		game.printGame();
		System.out.println();
		game.makeAIMove();
		game.printGame();
		System.out.println();
	}
}
