import java.util.*;
import java.io.*;

public class tictactoe {

	public static void main(String[] args) {
		Board game = new Board(5, 3);
		game.makeAIMove();
		game.printGame();
		System.out.println();
	}
}
