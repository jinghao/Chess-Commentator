package edu.berkeley.nlp.chess;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.filefilter.FileFilterUtils;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import edu.berkeley.nlp.chess.util.FileIterator;
import edu.berkeley.nlp.classification.Featurizer;
import edu.berkeley.nlp.classification.Featurizers;
import edu.berkeley.nlp.util.CommandLineUtils;

public class GameSlicerFeaturizer {
	public static void main(String[] args) throws IOException {
		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		String inputPath = argMap.get("-input");
		String outputPath = argMap.get("-output");
		int maxGames = Integer.parseInt(argMap.get("-maxGames"));
		
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
		
		Featurizer<PositionWithMoves> featurizer =
			injector.getInstance(Key.get(new TypeLiteral<Featurizer<PositionWithMoves>>() {}));
		
		Iterable<File> pgnFiles = new FileIterator(new File(inputPath), FileFilterUtils.suffixFileFilter(".pgn"));
		
		ObjectOutputStream oos = new ObjectOutputStream(
			new GZIPOutputStream(
					Files.newOutputStreamSupplier(new File(outputPath + ".features.gz")).getOutput()));
		int numGames = 0;
		int numFeatures = 0;
		for (File pgnFile : pgnFiles) {
			try {
				if (numGames >= maxGames) break;
				PGNReader reader = new PGNReader(Files.newInputStreamSupplier(pgnFile).getInput(), 
												 pgnFile.getName());
				for (Game game = reader.parseGame(); game != null && numGames < maxGames; game = reader.parseGame(), ++numGames) {
					List<PositionWithMoves> pwms = Games.flatten(game);
					for (PositionWithMoves pwm : pwms) {
						oos.writeObject(featurizer.getFeaturesFor(pwm));
						++numFeatures;
					}
				}				
				reader.close();
				oos.reset();
				System.out.printf("Processed %s; %d games, %d features.\n", pgnFile.getPath(), numGames, numFeatures);
			} catch (IOException e) {
				System.err.println("IOException occured on " + pgnFile.getPath());
			} catch (PGNSyntaxError e) {
				System.err.println("PGNSyntaxError occured on " + pgnFile.getPath());
			} catch (RuntimeException e) {
				System.err.println("PRuntimeException occured on " + pgnFile.getPath());
			}
 		}
		
		System.out.printf("\n%d features collected.\n", numFeatures);
		oos.close();
		
		ObjectInputStream ois = new ObjectInputStream(
				new GZIPInputStream(
						Files.newInputStreamSupplier(new File(outputPath + ".features.gz")).getInput()));
		int numReadFeatures = 0;
		while (true) {
			try {
				ois.readObject();
				++numReadFeatures;
			} catch (IOException e) {
				break;
			} catch (ClassNotFoundException e) {
				throw Throwables.propagate(e);
			}
		}
		System.out.printf("%d features read back.\n", numReadFeatures);
			
		
	}
}
