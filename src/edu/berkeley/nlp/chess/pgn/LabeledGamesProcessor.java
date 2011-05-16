package edu.berkeley.nlp.chess.pgn;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;

import chesspresso.game.Game;
import chesspresso.game.GameModel;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.pgn.PGNWriter;

import edu.berkeley.nlp.chess.util.FileIterator;
import edu.berkeley.nlp.chess.util.Util;

public class LabeledGamesProcessor {
	public static void main(String[] args) {
		long games = 0;
		
		PrintWriter pw_out = new PrintWriter(System.out);
		HashMap<String, String> seenGames = new HashMap<String, String>();
		
		for (File f : new FileIterator(args[0])) {
			String filename = f.getPath();
			
			if (filename.endsWith("-fixed")) {
				try {
					PGNReader reader = new PGNReader(filename);
					Game g;
					PGNWriter pw = null;
	
					int linenumber = 0;
					System.out.printf("Parsing %s\n", filename);
					while (true) {
						try {
							g = reader.parseGame();
	
							if (g == null) {
								break;
							}
							
							linenumber = reader.getLineNumber();
	
							GameModel game = g.getModel();
	
							/*pw = new PGNWriter(pw_out);
							pw.write(game);
							pw.flush();
							*/
							//game.getMoveModel().write(System.out);

							byte[] bytes = g.getBytes();
							String sha = Util.sha1(bytes);

							if (seenGames.containsKey(sha)) {
								System.out.printf("\tDuplicate game (line %d). Previously found in %s\n", linenumber, seenGames.get(sha));
							} else {
								seenGames.put(sha, filename + " line " + linenumber);
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
		
		System.out.printf("Parsed %d games, and added %d\n", games, seenGames.size());
	}
}
