package edu.berkeley.nlp.chess.pgn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Stack;

import com.google.common.io.Files;

import chesspresso.game.Game;
import chesspresso.game.GameModel;
import chesspresso.pgn.PGN;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.pgn.PGNWriter;

public class Deduplicator {

	public static String sha1(byte[] b) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.reset();
		md.update(b);

		byte[] t = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < t.length; i++) {
			int curr_byte = 0xFF & t[i];

			hexString.append((curr_byte < 0x10 ? "0" : "")
					+ Integer.toHexString(curr_byte));
		}

		return hexString.toString();
	}

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws MalformedMoveException
	 * @throws PGNParseException
	 * @throws NullPointerException
	 */
	public static void main(String[] args) {
		String initialFolder = args[0];
		String destFolder = args[1];

		HashSet<String> seenGames = new HashSet<String>();
		Stack<String> stack = new Stack<String>();

		stack.add(initialFolder);
		
		long numGames = 0, numFiles = 0, written = 0, bytes = 0;
		NumberFormat nf = NumberFormat.getInstance();
		
		while (!stack.isEmpty()) {
			String filename = stack.pop();
			File f = new File(filename);

			if (f.isDirectory()) {
				for (String file : f.list()) {
					stack.add(filename + "/" + file);
				}
			} else if (filename.endsWith("-fixed")) {
				int localGames = 0, added = 0;
				
				bytes += f.length();
				
				System.out.printf("File #%d (%s bytes): %s. ", 
						++numFiles, 
						nf.format(f.length()),
						filename);

				try {
					PGNReader reader = new PGNReader(filename);
					Game g;
					
					int linenumber = 0;

					while (true) {
						try {
							g = reader.parseGame();

							if (g == null) {
								break;
							}
							
							linenumber = reader.getLineNumber();

							GameModel game = g.getModel();

							numGames++;
							localGames++;

							String sha = sha1(game.getBytes());

							if (seenGames.add(sha)) {
								++added;
								String outfile = destFolder + "/"
										+ sha.substring(0, 2) + "/"
										+ sha.substring(2) + ".pgn";
								try {
									if (new File(outfile).exists()) {
										continue;
									}
									FileWriter fw = new FileWriter(outfile);
									PGNWriter p = new PGNWriter(fw);
									p.write(game);
									fw.close();
									++written;
								} catch (IOException e) {
									System.err.printf(
											"\t\tIOException (%s): %s\n",
											outfile, e.getMessage());
								}
							}
						} catch (PGNSyntaxError e) {
							System.out.printf("\n\tPGNSyntaxError (line %d of good game; error line %d): %s", linenumber, reader.getLineNumber(), e.getMessage());
							// e.printStackTrace(System.out);
						} catch (RuntimeException e) {
							// System.out.printf("\n\tRuntime (line %d of good game; error line %d): %s", linenumber, reader.getLineNumber(), e.getMessage());
							// e.printStackTrace(System.out);
						} catch (NoSuchAlgorithmException e) {
							System.out.println("\n\tWTF");
						}
					}

					reader.close();
				} catch (IOException e) {
					System.err.println("IOError: " + e.getMessage());
				}

				System.out.printf(
						"Added %d/%d games (total: %s bytes read, %d/%d = %f, %d written)\n",
						added, localGames, 
						nf.format(bytes), seenGames.size(), numGames, (double) seenGames.size() / numGames,
						written);
			}
		}
		System.out.println("Number of games processed: " + numGames);
		System.out.println("Number of unique games: " + seenGames.size());
	}
}
