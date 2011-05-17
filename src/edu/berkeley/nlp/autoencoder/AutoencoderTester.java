package edu.berkeley.nlp.autoencoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.primitives.Doubles;

import edu.berkeley.nlp.chess.util.SerializedVectorCollection;
import edu.berkeley.nlp.math.LBFGSMinimizer.IterationCallbackFunction;
import edu.berkeley.nlp.util.CommandLineUtils;
import edu.berkeley.nlp.util.Pair;

public class AutoencoderTester {

	public static void main(String[] args) {
		double sparsity = 0.05;
		double sparsityWeight = 1e-2;
		double weightDecay = 1e-3;
		
		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		final String prefix = argMap.get("-prefix");
		
		int size;
		Collection<double[]> examples;
		
		
		examples = new SerializedVectorCollection(new InputSupplier<ObjectInputStream>() {
			private InputSupplier<FileInputStream> fiss = Files.newInputStreamSupplier(new File(prefix + ".features.gz"));
			@Override public ObjectInputStream getInput() throws IOException {
				return new ObjectInputStream(new GZIPInputStream(fiss.getInput()));
			}
		});
		size = examples.iterator().next().length;

		
		String serializePath = prefix + "-serialized";
		
		if (argMap.containsKey("-sparsity")) sparsity = Double.parseDouble(argMap.get("-sparsity"));
		if (argMap.containsKey("-sparsityWeight")) sparsityWeight = Double.parseDouble(argMap.get("-sparsityWeight"));
		if (argMap.containsKey("-weightDecay")) weightDecay = Double.parseDouble(argMap.get("-weightDecay"));
		
		System.out.printf("Training with %d input units and %d hidden units.\n", size, size * 2);
		System.out.printf("sparsity: %f, sparsity weight: %f, weight decay: %f\n", sparsity, sparsityWeight, weightDecay);
		System.out.printf("%d examples loaded.\n", examples.size());
		
		NeuralNetwork nn = NeuralNetwork.train(
				new int[] { size, size * 2, size }, 
				weightDecay, 
				sparsity, 
				sparsityWeight, 
				selfZip(examples),
				new IterationCallbackFunction() {

					@Override
					public void iterationDone(double[] curGuess, int iter) {
						// TODO Auto-generated method stub
					}
					
				});
		
		System.out.println("Hidden outputs: " + Doubles.join(" ", nn.getHiddenOutput(examples.iterator().next())));
		System.out.println("Original: " + Doubles.join(" ", examples.iterator().next()));
		System.out.println("Restored: " + Doubles.join(" ", nn.getOutput(examples.iterator().next())));
		
		if (serializePath != null) {
			try {
				new ObjectOutputStream(Files.newOutputStreamSupplier(new File(serializePath)).getOutput()).writeObject(nn);
				System.out.println("Saved to " + serializePath);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	}
	
	private static List<double[]> readData(File file, final int size) {
		try {
			return Files.readLines(file, Charset.defaultCharset(),
				new LineProcessor<List<double[]>>() {
					private List<double[]> result = Lists.newArrayList();
					@Override public boolean processLine(String line) throws IOException {
						double[] row = new double[size];
						for (String item : line.split("\\s+")) {
							String[] f = item.split(":");
							row[Integer.parseInt(f[0])] = Double.parseDouble(f[1]);
						}
						result.add(row);
						return true;
					}

					@Override public List<double[]> getResult() {
						return result;
					}
				});
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static <T> Collection<Pair<T, T>> selfZip(Collection<T> input) {
		return Collections2.transform(input,
				new Function<T, Pair<T, T>>() {
			@Override public Pair<T, T> apply(T input) {
				return Pair.makePair(input, input);
			}
		});
	}
	
	private static Collection<Pair<double[], double[]>> makeMaskingNoiseDataset(Collection<double[]> input, double defaultValue, double fractionCorrupted) {
		return Collections2.transform(input,
				new Function<double[], Pair<double[], double[]>>() {
			@Override public Pair<double[], double[]> apply(double[] input) {
				double[] output = Arrays.copyOf(input, input.length);
				return Pair.makePair(input, input);
			}
		});
	}
}
