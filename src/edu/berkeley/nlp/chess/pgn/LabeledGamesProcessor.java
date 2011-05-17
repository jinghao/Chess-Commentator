package edu.berkeley.nlp.chess.pgn;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.pgn.PGNWriter;

import edu.berkeley.nlp.chess.Games;
import edu.berkeley.nlp.chess.PositionWithMoves;
import edu.berkeley.nlp.chess.util.FileIterator;

public class LabeledGamesProcessor {
	public static String getTag(String filename) {
		return filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'));
	}
	public static void main(String[] args) {
		long games = 0, largegames = 0;
		
		assert getTag("themes/t/deflection.pgn-fixed").equals("deflection") : "getTag doesn't work";
		
		PrintWriter pw_out = new PrintWriter(System.out);
		Multimap<List<PositionWithMoves>, String> tags = HashMultimap.create();

		int[] counts = new int[10];
		for (File f : new FileIterator(args[0])) {
			String filename = f.getPath();
			
			if (filename.endsWith("-fixed")) {
				try {
					PGNReader reader = new PGNReader(filename);
					Game g;
	
					int linenumber = 0;
					System.out.printf("Parsing %s\n", filename);
					while (true) {
						try {
							g = reader.parseGame();
	
							if (g == null) {
								break;
							}
							
							linenumber = reader.getLineNumber();

							List<PositionWithMoves> boards = Games.flatten(g);
							if (boards.size() > 10) {
								System.out.printf("\tThrew out large game (%d): %s line %d\n", boards.size(), filename, linenumber);
								++largegames;
								continue;
							}
							counts[boards.size() - 1]++;
							for (int i = 1; i < boards.size(); ++i) {
								tags.put(boards.subList(0, i), getTag(filename));
							}
	
							++games;
						} catch (PGNSyntaxError e) {
							System.out.printf("\n\tPGNSyntaxError (line %d of good game; error line %d): %s", linenumber, reader.getLineNumber(), e.getMessage());
							// e.printStackTrace(System.out);
						} catch (RuntimeException e) {
							// System.out.printf("\n\tRuntime (line %d of good game; error line %d): %s", linenumber, reader.getLineNumber(), e.getMessage());
							// e.printStackTrace(System.out);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pw_out.flush();
		}
		
		System.out.printf("Parsed %d games, added %d board-tag configurations, threw out %d large games\n", games, tags.size(), largegames);
		for (int i = 0; i < counts.length; ++i) {
			System.out.printf("%3d: %d\n", i, counts[i]);
		}
	}
}
