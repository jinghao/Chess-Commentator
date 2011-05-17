package edu.berkeley.nlp.chess;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Games {
	public static List<PositionWithMoves> flatten(Game game) {
		game.goBackToLineBegin();
		List<PositionWithMoves> result = Lists.newArrayList();
		Move previousMove = null;
		do {
			Move nextMove = game.getNextMove();
			Position position = new Position(game.getPosition());
			position.setPlyNumber(0);
			position.setHalfMoveClock(0);
			result.add(new PositionWithMoves(previousMove, position, nextMove));
			previousMove = nextMove;
		} while (game.goForward());
		
		return result;
	}
	
	public static void main(String[] args) throws PGNSyntaxError, IOException {
		Game game = new PGNReader(Files.newInputStreamSupplier(new File(args[0])).getInput(), args[0]).parseGame();
		System.out.println(game.getEvent());
		List<PositionWithMoves> unrolledGame = flatten(game);
		System.out.println("" + unrolledGame.size());
		for (PositionWithMoves pwm: unrolledGame)
			System.out.println(pwm);
		
	}
}
