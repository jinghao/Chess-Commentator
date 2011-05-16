package edu.berkeley.nlp.chess;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Games {
	public static List<PositionWithMove> flatten(Game game) {
		game.goBackToLineBegin();
		List<PositionWithMove> result = Lists.newArrayList();
		do {
			result.add(new PositionWithMove(new Position(game.getPosition()), game.getNextMove()));
		} while (game.goForward());
		
		return result;
	}
	
	public static void main(String[] args) throws PGNSyntaxError, IOException {
		Game game = new PGNReader(Files.newInputStreamSupplier(new File(args[0])).getInput(), args[0]).parseGame();
		System.out.println(game.getEvent());
		List<PositionWithMove> unrolledGame = flatten(game);
		System.out.println("" + unrolledGame.size());
		for (PositionWithMove pwm: unrolledGame)
			System.out.println(pwm);
	}
}
