package edu.berkeley.nlp.classification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import chesspresso.position.Position;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import edu.berkeley.nlp.autoencoder.NeuralNetwork;
import edu.berkeley.nlp.chess.PositionFeatureExtractor;
import edu.berkeley.nlp.chess.PositionWithMoves;
import edu.berkeley.nlp.chess.util.GzipFiles;
import edu.berkeley.nlp.util.CommandLineUtils;

public class ClassifyLabeledData {
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, ParseException, URISyntaxException,
			InterruptedException {
		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		String dataPath = argMap.get("-data");
		String autoEncoderPath = argMap.get("-autoencoder");

		Featurizer<List<PositionWithMoves>> featurizer;
		
		if (autoEncoderPath != null) {
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
	
			ObjectInputStream nnois = GzipFiles.newObjectInputStreamSupplier(new File(autoEncoderPath)).getInput();
			@SuppressWarnings("unchecked")
			NeuralNetwork nn = (NeuralNetwork) nnois.readObject();
		
			featurizer = new Featurizers.NeuralNetworkFeaturizer<List<PositionWithMoves>>(
					nn, 
					injector.getInstance(Key.get(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {}))); 
		} else {
			Injector injector = Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {})
					.to(Featurizers.ConcatenatingPositionWithMove.class);
					bind(new TypeLiteral<Featurizer<PositionWithMoves>>() {})
					.to(PositionFeatureExtractor.class);
				}
			});
			featurizer = injector.getInstance(Key.get(new TypeLiteral<Featurizer<List<PositionWithMoves>>>() {}));
		}
		// int numRounds = Integer.parseInt(argMap.get("-numRounds"));
		
		featurizer = new Featurizers.Normalizer<List<PositionWithMoves>>(featurizer, 1.0);

		ObjectInputStream ois = GzipFiles.newObjectInputStreamSupplier(
				new File(dataPath)).getInput();

		@SuppressWarnings("unchecked")
		Multimap<List<PositionWithMoves>, String> tags = (Multimap<List<PositionWithMoves>, String>) ois
				.readObject();

		@SuppressWarnings("unchecked")
		Multimap<String, List<PositionWithMoves>> allPositiveExamples = (Multimap<String, List<PositionWithMoves>>) ois
				.readObject();

		@SuppressWarnings("unchecked")
		Multimap<String, List<PositionWithMoves>> allNegativeExamples = (Multimap<String, List<PositionWithMoves>>) ois
				.readObject();

		@SuppressWarnings("unchecked")
		Multimap<List<PositionWithMoves>, String> validationSet = (Multimap<List<PositionWithMoves>, String>) ois
				.readObject();

		@SuppressWarnings("unchecked")
		Multimap<List<PositionWithMoves>, String> testSet = (Multimap<List<PositionWithMoves>, String>) ois
				.readObject();
		
		Set<String> tags_set = allPositiveExamples.keySet();

		Map<List<PositionWithMoves>, double[]> featureArrays = Maps
				.newHashMap();
		for (List<PositionWithMoves> lpwm : tags.keySet())
			featureArrays.put(lpwm, featurizer.getFeaturesFor(lpwm));
		for (List<PositionWithMoves> lpwm : validationSet.keySet())
			featureArrays.put(lpwm, featurizer.getFeaturesFor(lpwm));
		for (List<PositionWithMoves> lpwm : testSet.keySet())
			featureArrays.put(lpwm, featurizer.getFeaturesFor(lpwm));

		// Make models
		Map<String, File> models = Maps.newHashMap();
		Map<String, File> testFiles = Maps.newHashMap();
		for (String tag : tags_set) {
			System.out.print("Training model for " + tag);
			Collection<List<PositionWithMoves>> positiveExamples = allPositiveExamples
					.get(tag);
			Collection<List<PositionWithMoves>> negativeExamples = allNegativeExamples
					.get(tag);

			File input = File.createTempFile("input", ".dat");
			BufferedWriter writerToInput = new BufferedWriter(new FileWriter(
					input), 10000);

			File test = File.createTempFile("test", ".dat");
			BufferedWriter writerToTest = new BufferedWriter(new FileWriter(
					test), 10000);

			File model = File.createTempFile("model", "");

			for (List<PositionWithMoves> example : positiveExamples) {
				double[] vector = featureArrays.get(example);

				WritableFeatureVector lfv = new WritableFeatureVector(1.0, range(
						1, vector.length + 1), vector);

				lfv.write(writerToInput);
			}
			System.out.print('.');

			for (List<PositionWithMoves> example : negativeExamples) {
				double[] vector = featureArrays.get(example);

				WritableFeatureVector lfv = new WritableFeatureVector(-1.0,
						range(1, vector.length + 1), vector);
				lfv.write(writerToInput);
			}
			System.out.print('.');

			for (List<PositionWithMoves> example : testSet.keySet()) {
				double[] vector = featureArrays.get(example);

				new WritableFeatureVector(testSet.containsEntry(example, tag) ? 1.0 : -1.0,
						range(1, vector.length + 1), 
						vector)
				.write(writerToTest);
			}

			writerToInput.close();
			writerToTest.close();

			String command = String.format("lib/svm/svm_light/svm_learn %s %s",
					input.getAbsolutePath(), model.getAbsolutePath());
			Process p = Runtime.getRuntime().exec(command);

			System.out.print('.');
			p.waitFor();
			System.out.print('.');

			models.put(tag, model);
			testFiles.put(tag, test);
			System.out.println(" Done");
		}
		
		printResults(predict(models, testFiles, testSet));
	}
	
	public static int[] predict(Map<String, File> models, 
			Map<String, File> tests,
			Multimap<List<PositionWithMoves>, String> testSet) throws IOException {
		int tp = 0, fp = 0, tn = 0, fn = 0;

		File predictions = File.createTempFile("predictions", "");

		for (String tag : models.keySet()) {
			File model = models.get(tag);
			File test = tests.get(tag);
			Process p = Runtime.getRuntime().exec(
					String.format(
							"lib/svm/svm_light/svm_classify %s %s %s", 
							test.getAbsolutePath(), 
							model.getAbsolutePath(),
							predictions.getAbsolutePath()));

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.err.println("WTF");
				return null;
			}
			
			Scanner predictionScanner = new Scanner(predictions);
			
			for (List<PositionWithMoves> sample : testSet.keySet()) {
				double prediction = predictionScanner.nextDouble();				
				boolean trueValue = testSet.containsEntry(sample, tag);

				if (trueValue) {
					if (prediction >= 0) {
						++tp;
					} else {
						++fn;
					}
				} else {
					if (prediction < 0) {
						++tn;
					} else {
						++fp;
					}
				}
			}
		}
		
		return new int[] {tp, fp, tn, fn};
	}
	
	public static void printResults(int[] stats) {
		printResults(stats[0], stats[1], stats[2], stats[3]);
	}

	public static void printResults(int tp, int fp, int tn, int fn) {
		int total = tn + tp + fn + fp;
		
		double precision = (tp + fp > 0) ? ((double)tp / (tp + fp)) : Double.NaN;
		double recall = (tp + fn > 0) ? ((double)tp / (tp + fn)) : Double.NaN;
		
		System.out.printf("True positive: %d (%f)\n", tp, (double) tp
				/ total);
		System.out.printf("False positive: %d (%f)\n", fp, (double) fp
				/ total);
		System.out.printf("True negative: %d (%f)\n", tn, (double) tn
				/ total);
		System.out.printf("False negative: %d (%f)\n", fn, (double) fn
				/ total);
		System.out.printf("Precision/Recall: %f, %f\n", precision, recall);
		System.out.printf("F1 Score: %f\n", 2*precision*recall/(precision+recall));
	}

	private static int[] range(int from, int to) {
		Preconditions.checkArgument(from <= to);
		int[] result = new int[to - from];
		for (int i = 0; i < result.length; ++i)
			result[i] = from++;
		return result;
	}
}
