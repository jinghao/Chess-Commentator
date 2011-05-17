package edu.berkeley.nlp.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jnisvmlight.FeatureVector;
import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;

import org.apache.commons.math.random.RandomDataImpl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import edu.berkeley.nlp.chess.PositionFeatureExtractor;
import edu.berkeley.nlp.chess.PositionWithMoves;
import edu.berkeley.nlp.chess.util.GzipFiles;
import edu.berkeley.nlp.util.CommandLineUtils;

public class ClassifyLabeledData {
	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException, URISyntaxException, InterruptedException {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {})
					.to(Featurizers.ConcatenatingPositionWithMove.class);
				bind(new TypeLiteral<Featurizer<PositionWithMoves>>() {})
					.to(PositionFeatureExtractor.class);
			}
		});
		Featurizer<List<PositionWithMoves>> featurizer = injector.getInstance(
				Key.get(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {}));
		
		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		String dataPath = argMap.get("-data");
		int numRounds = Integer.parseInt(argMap.get("-numRounds"));
		
		ObjectInputStream ois = GzipFiles.newObjectInputStreamSupplier(new File(dataPath)).getInput();

		@SuppressWarnings("unchecked")
		Multimap<List<PositionWithMoves>, String> tags = (Multimap<List<PositionWithMoves>, String>) ois.readObject();

		@SuppressWarnings("unchecked")
		Multimap<String, List<PositionWithMoves>> allPositiveExamples = (Multimap<String, List<PositionWithMoves>>)
		ois.readObject();

		@SuppressWarnings("unchecked")	
		Multimap<String, List<PositionWithMoves>> allNegativeExamples = (Multimap<String, List<PositionWithMoves>>)
		ois.readObject();
		
		Map<List<PositionWithMoves>, double[]> featureArrays = Maps.newHashMap();
		for (List<PositionWithMoves> lpwm : tags.keySet())
			featureArrays.put(lpwm, featurizer.getFeaturesFor(lpwm));
		
		Object[] samples = new RandomDataImpl().nextSample(tags.keySet(), numRounds); 
		for (int i = 0; i < numRounds; ++i) {
			@SuppressWarnings("unchecked")
			List<PositionWithMoves> sample = (List<PositionWithMoves>) samples[i];
			Map<String, URL> models = Maps.newHashMap();
			Map<String, URL> testFiles = Maps.newHashMap();
			for (String tag : allPositiveExamples.keySet()) {
				System.out.println("Training model for " + tag);
				Collection<List<PositionWithMoves>> positiveExamples = allPositiveExamples.get(tag);
				Collection<List<PositionWithMoves>> negativeExamples = allNegativeExamples.get(tag);

				File input = File.createTempFile("input", ".dat");
				BufferedWriter writerToInput = new BufferedWriter(new FileWriter(input), 10000);
				
				File test = File.createTempFile("test", ".dat");
				BufferedWriter writerToTest = new BufferedWriter(new FileWriter(test), 10000);
				
				File model = File.createTempFile("model", "");
				
				for (List<PositionWithMoves> positiveExample : positiveExamples) {
					
					double[] vector = featureArrays.get(positiveExample);
					JinghaoFeatureVector lfv = new JinghaoFeatureVector(1.0, range(1, vector.length + 1), vector);
					if (positiveExample.equals(sample))
						lfv.write(writerToTest);

					else
						lfv.write(writerToInput);
				}
				
				int k = 0;
				long featureTime = 0;
				long totalTime = 0;
				for (List<PositionWithMoves> negativeExample : negativeExamples) {
					double[] vector = featureArrays.get(negativeExample);

					JinghaoFeatureVector lfv = new JinghaoFeatureVector(-1.0, range(1, vector.length + 1), vector);
					if (negativeExample.equals(sample))
						lfv.write(writerToTest);
					else
						lfv.write(writerToInput);
				}
				
								
				writerToInput.close();
				writerToTest.close();
				
				Process p = Runtime.getRuntime().exec(
							String.format("lib/svm/svm_light/svm_learn %s %s", input.getAbsolutePath(), model.getAbsolutePath()));
				
				p.waitFor();
				
				models.put(tag, model.toURL());
				testFiles.put(tag, test.toURL());
				System.out.println("Trained model for " + tag);
			}

			System.out.println(sample + ": ");
			for (String tag : allPositiveExamples.keySet()) {
				File model = new File(models.get(tag).toURI());
				File test = new File(testFiles.get(tag).toURI());
				File predictions = File.createTempFile("predictions", "");
				
				Process p = Runtime.getRuntime().exec
				(String.format("lib/svm/svm_light/svm_classify %s %s %s", test.getAbsolutePath(), model.getAbsolutePath(), predictions.getAbsolutePath()));
								
				// Read from an input stream
				BufferedReader dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = dis.readLine()) != null)
					System.out.println(line);
			    System.out.flush();
			    
			    try {
			    	p.waitFor();
			    } catch (InterruptedException e) {
			    	System.err.println("WTF");
			    	return;
			    }
			}
		}
	}
	
	private static int[] range(int from, int to) {
		Preconditions.checkArgument(from <= to);
		int[] result = new int[to - from];
		for (int i = 0; i < result.length; ++i)
			result[i] = from++;
		return result;
	}
}
