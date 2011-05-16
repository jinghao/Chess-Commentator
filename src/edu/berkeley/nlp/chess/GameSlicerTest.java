package edu.berkeley.nlp.chess;

import java.io.IOException;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.base.Joiner;

public class GameSlicerTest {
	public static void main (String[] args) throws IOException, PGNSyntaxError {
		PGNReader reader = new PGNReader
			("/home/aa/users/tarski/Chess-Commentator/themes/backrankWeakness/backrankWeakness.pgn-fixed");
		
		Game game = reader.parseGame();
		
		while (game != null) {
			GameSlicer slicer = new GameSlicer(Games.flatten(game), 3);			
			for (List<PositionWithMoves> slice : slicer) {
				System.out.println(Joiner.on(", ").join(slice)); 
			}
			game = reader.parseGame();
		}
	} 
}
