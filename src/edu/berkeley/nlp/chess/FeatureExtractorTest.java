package edu.berkeley.nlp.chess;

import java.io.IOException;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;

public class FeatureExtractorTest {
	public static void main(String[] args) throws PGNSyntaxError, IOException,
			IllegalMoveException {
		PGNReader reader = new PGNReader(
				"/home/aa/users/tarski/Chess-Commentator/themes/backrankWeakness/backrankWeakness.pgn-fixed");

		Game game = reader.parseGame();
		Featurizer<List<PositionWithMoves>> featurizer = 
			new Featurizers.ConcatenatingPositionWithMove(new FeatureExtractor());
		while (game != null) {
			GameSlicer slicer = new GameSlicer(Games.flatten(game), 3);
			for (List<PositionWithMoves> slice : slicer) {
				double[] features = featurizer.getFeaturesFor(slice);
			}
			game = reader.parseGame();
			System.out.println("Done with one game");
		}
		System.out.println("Finished!");
	}
}
