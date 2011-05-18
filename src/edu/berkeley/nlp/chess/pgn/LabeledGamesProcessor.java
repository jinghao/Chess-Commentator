package edu.berkeley.nlp.chess.pgn;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.math.random.RandomDataImpl;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import edu.berkeley.nlp.chess.Games;
import edu.berkeley.nlp.chess.PositionWithMoves;
import edu.berkeley.nlp.chess.util.FileIterator;
import edu.berkeley.nlp.classification.Featurizer;
import edu.berkeley.nlp.classification.Featurizers;
import edu.berkeley.nlp.util.CommandLineUtils;

public class LabeledGamesProcessor {
	public static String getTag(String filename) {
		return filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'));
	}
	
	public static void main(String[] args) throws IOException {
		assert getTag("themes/t/deflection.pgn-fixed").equals("deflection") : "getTag doesn't work";

		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {})
					.to(Featurizers.ConcatenatingPositionWithMove.class);
				bind(new TypeLiteral<Featurizer<PositionWithMoves>>() {})
					.to(PositionWithMoves.PositionFeaturizer.class);
				bind(new TypeLiteral<Featurizer<Position>>() {})
					.to(Featurizers.TwoColorBoard.class);
			}
		});
		
		Featurizer<List<PositionWithMoves>> featurizer =
			injector.getInstance(Key.get(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {}));
		
		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		int maxLength = Integer.parseInt(argMap.get("-maxLength"));
		String prefix = argMap.get("-prefix");
		String outputPath = argMap.get("-output");
		int sliceLength = Integer.parseInt(argMap.get("-sliceLength"));
		String extraFeatures = argMap.get("-extra-features"); 
		
		long games = 0, largeGames = 0;
		Multimap<List<PositionWithMoves>, String> tags = HashMultimap.create();
		int[] counts = new int[10];
		
		Set<String> tagsSet = new HashSet<String>();
		
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
					
//					for (int i = 1; i <= sliceLength && i <= boards.size(); ++i) {
					if (boards.size() >= sliceLength) {
						String tag = getTag(filename);
						tags.put(PositionWithMoves.getPrefix(boards, sliceLength), tag);
						tagsSet.add(tag);
					}

					++games;
				} catch (PGNSyntaxError e) {
					System.out.printf("\n\tPGNSyntaxError (line %d of good game; error line %d): %s", lineNumber, reader.getLineNumber(), e.getMessage());
				} catch (RuntimeException e) {
					e.printStackTrace(System.out);
				}
			}
		}
		
		// Take a random subset from the list of tags
		int numTestValidationExamples = (int) (tags.keySet().size() * 0.1);
		Object[] testValidationSamples = new RandomDataImpl().nextSample(tags.keySet(), numTestValidationExamples);
		
		Multimap<List<PositionWithMoves>, String> validationSet = HashMultimap.create();
		Multimap<List<PositionWithMoves>, String> testSet = HashMultimap.create();
		
		for (int i = 0; i < testValidationSamples.length / 2; ++i) {
			@SuppressWarnings("unchecked")
			List<PositionWithMoves> key = (List<PositionWithMoves>) testValidationSamples[i];  
			validationSet.putAll(key, tags.removeAll(key));
		}
		
		for (int i = testValidationSamples.length / 2; i < testValidationSamples.length; ++i) {
			@SuppressWarnings("unchecked")
			List<PositionWithMoves> key = (List<PositionWithMoves>) testValidationSamples[i];  
			testSet.putAll(key, tags.removeAll(key));
		}
		
		// Invert the map
		Multimap<String, List<PositionWithMoves>> negativeSlicesByTag = HashMultimap.create();
		for (List<PositionWithMoves> key : tags.keySet()) {
			for (String tag : tagsSet) {
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
				games, tags.size(), tagsSet.size(), largeGames);
		
		ObjectOutputStream oos = new ObjectOutputStream(
				new GZIPOutputStream(
						Files.newOutputStreamSupplier(new File(outputPath + ".labeledgames.gz")).getOutput()));
		oos.writeObject(tags);
		oos.writeObject(positiveSlicesByTag);
		oos.writeObject(negativeSlicesByTag);
		oos.writeObject(validationSet);
		oos.writeObject(testSet);
		oos.close();
		
		oos = new ObjectOutputStream(
				new GZIPOutputStream(
						Files.newOutputStreamSupplier(new File(outputPath + ".features.gz")).getOutput()));
		for (List<PositionWithMoves> lpwm : tags.keySet()) {
			oos.writeObject(featurizer.getFeaturesFor(lpwm));
			oos.reset();
		}
		for (List<PositionWithMoves> lpwm : validationSet.keySet()) {
			oos.writeObject(featurizer.getFeaturesFor(lpwm));
			oos.reset();
		}
		for (List<PositionWithMoves> lpwm : testSet.keySet()) {
			oos.writeObject(featurizer.getFeaturesFor(lpwm));
			oos.reset();
		}	
		oos.close();
		System.out.println("Saved to " + outputPath + ".labeledgames.gz and " + outputPath + ".features.gz.");
	}
}
