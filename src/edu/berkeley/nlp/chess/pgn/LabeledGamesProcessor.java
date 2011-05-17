package edu.berkeley.nlp.chess.pgn;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.filefilter.FileFilterUtils;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import edu.berkeley.nlp.chess.Games;
import edu.berkeley.nlp.chess.PositionWithMoves;
import edu.berkeley.nlp.chess.util.FileIterator;
import edu.berkeley.nlp.util.CommandLineUtils;

public class LabeledGamesProcessor {
	public static String getTag(String filename) {
		return filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'));
	}
	
	public static void main(String[] args) throws IOException {
		assert getTag("themes/t/deflection.pgn-fixed").equals("deflection") : "getTag doesn't work";

		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		int maxLength = Integer.parseInt(argMap.get("-maxLength"));
		String prefix = argMap.get("-prefix");
		String outputPath = argMap.get("-output");
		int sliceLength = Integer.parseInt(argMap.get("-sliceLength"));
		
		long games = 0, largeGames = 0;
		Multimap<List<PositionWithMoves>, String> tags = HashMultimap.create();
		int[] counts = new int[10];
		
		Set<String> tags_set = new HashSet<String>();
		
		for (File f : new FileIterator(new File(prefix), FileFilterUtils.suffixFileFilter(".pgn-fixed"))) {
			String filename = f.getPath();
			PGNReader reader = new PGNReader(filename);
			Game g;

			int lineNumber = 0;
			System.out.printf("Parsing %s\n", filename);
			while (true) {
				try {
					g = reader.parseGame();
					if (g == null) break;

					lineNumber = reader.getLineNumber();
					List<PositionWithMoves> boards = Games.flatten(g);
					if (boards.size() > maxLength) {
						++largeGames;
						continue;
					}
					counts[boards.size() - 1]++;
					for (int i = 1; i <= sliceLength && i <= boards.size(); ++i) {
						String tag = getTag(filename);
						tags.put(PositionWithMoves.getPrefix(boards, i), tag);
						tags_set.add(tag);
					}

					++games;
				} catch (PGNSyntaxError e) {
					System.out.printf("\n\tPGNSyntaxError (line %d of good game; error line %d): %s", lineNumber, reader.getLineNumber(), e.getMessage());
				} catch (RuntimeException e) {
					e.printStackTrace(System.out);
				}
			}
		}
		
		// Invert the map
		Multimap<String, List<PositionWithMoves>> negativeSlicesByTag = HashMultimap.create();
		for (List<PositionWithMoves> key : tags.keySet()) {
			for (String tag : tags_set) {
				negativeSlicesByTag.put(tag, key);
			}
		}
		
		Multimap<String, List<PositionWithMoves>> positiveSlicesByTag = HashMultimap.create();
		for (Entry<List<PositionWithMoves>, String> entry : tags.entries()) {
			String tag = entry.getValue();
			List<PositionWithMoves> board = entry.getKey();
			
			negativeSlicesByTag.remove(tag, board);
			positiveSlicesByTag.put(tag, board);
		}
		
		
		Preconditions.checkState(positiveSlicesByTag.size() == tags.size());
		System.out.printf("Parsed %d games\nAdded %d board-tag configurations (with %d unique tags), threw out %d large games\n", 
				games, tags.size(), tags_set.size(), largeGames);
		
		ObjectOutputStream oos = new ObjectOutputStream(
				new GZIPOutputStream(
						Files.newOutputStreamSupplier(new File(outputPath + ".labeledgames.gz")).getOutput()));
		oos.writeObject(tags);
		oos.writeObject(positiveSlicesByTag);
		oos.writeObject(negativeSlicesByTag);	
		oos.close();
		
		System.out.println("Saved to " + outputPath + ".labeledgames.gz.");
	}
}
