package edu.berkeley.nlp.autoencoder;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.common.primitives.Doubles;

import edu.berkeley.nlp.chess.util.GzipFiles;
import edu.berkeley.nlp.chess.util.SerializedCollection;
import edu.berkeley.nlp.math.LBFGSMinimizer.IterationCallbackFunction;
import edu.berkeley.nlp.util.CommandLineUtils;
import edu.berkeley.nlp.util.Pair;

public class AutoencoderDriver {

	public static void main(String[] args) {
		double sparsity = 0.05;
		double sparsityWeight = 1e-2;
		double weightDecay = 1e-3;
		int numHiddenLayers = 3;
		
		Map<String, String> argMap = CommandLineUtils.simpleCommandLineParser(args);
		final String prefix = argMap.get("-prefix");
		
		Collection<double[]> examples = new SerializedCollection<double[]>(
			GzipFiles.newObjectInputStreamSupplier(new File(prefix + ".features.gz")));
		int vectorSize = examples.iterator().next().length;

		String serializePath = prefix + "-serialized";
		
		if (argMap.containsKey("-sparsity")) sparsity = Double.parseDouble(argMap.get("-sparsity"));
		if (argMap.containsKey("-sparsityWeight")) sparsityWeight = Double.parseDouble(argMap.get("-sparsityWeight"));
		if (argMap.containsKey("-weightDecay")) weightDecay = Double.parseDouble(argMap.get("-weightDecay"));
		if (argMap.containsKey("-numHiddenLayers")) numHiddenLayers = Integer.parseInt(argMap.get("-numHiddenLayers"));
		
		System.out.printf("Training with %d input units and %d hidden units.\n", vectorSize, vectorSize * 2);
		System.out.printf("sparsity: %f, sparsity weight: %f, weight decay: %f\n", sparsity, sparsityWeight, weightDecay);
		System.out.printf("%d examples loaded.\n\n", examples.size());
		
		for (int i = 0; i < numHiddenLayers; ++i) {
			System.out.printf("Training layer %d...\n", i);
			
			// See if the neural network already exists
			NeuralNetwork nn;
			File nnFile = new File(String.format("%s-layer%d-%f-%f-%f.autoencoder.gz", prefix, i, sparsity, sparsityWeight, weightDecay)); 
			if (nnFile.exists())
				try {
					nn = (NeuralNetwork) GzipFiles.newObjectInputStreamSupplier(nnFile).getInput().readObject();
					System.out.println("Serialized NN found.");
				} catch (IOException e) {
					throw Throwables.propagate(e);
				} catch (ClassNotFoundException e) {
					throw Throwables.propagate(e);
				}
			else {
				// See if partial results already exist
				final File partialNnFile = new File(String.format("%s-layer%d-%f-%f-%f.partial-autoencoder.gz", prefix, i, sparsity, sparsityWeight, weightDecay));
				double[] partialResults = null;
				if (partialNnFile.exists())
					try {
						partialResults = (double[]) GzipFiles.newObjectInputStreamSupplier(partialNnFile).getInput().readObject();
						System.out.println("Partial results found.");
					} catch (IOException e) {
						System.out.println("Partial restuls found but unusable.");
						partialResults = null;
					} catch (ClassNotFoundException e) {
						System.out.println("Partial restuls found but unusable.");
						partialResults = null;
					}
					
				nn = NeuralNetwork.train(
						new int[] { vectorSize, vectorSize, vectorSize }, 
						weightDecay, 
						sparsity, 
						sparsityWeight, 
						selfZip(examples),
						partialResults,
						new IterationCallbackFunction() {
							@Override public void iterationDone(double[] curGuess, int iter) {
								try {
									ObjectOutputStream oos = GzipFiles.newObjectOutputStreamSupplier(partialNnFile).getOutput();
									oos.writeObject(curGuess);
									oos.close();
								} catch (IOException e) {
									System.err.printf("Warning: saving to %s failed.", partialNnFile.getName());
								}
							}
						});
			}
			
			System.out.println("Hidden outputs: " + Doubles.join(" ", nn.getHiddenOutput(examples.iterator().next())));
			System.out.println("Original: " + Doubles.join(" ", examples.iterator().next()));
			System.out.println("Restored: " + Doubles.join(" ", nn.getOutput(examples.iterator().next())));

			try {
				ObjectOutputStream oos = GzipFiles.newObjectOutputStreamSupplier(nnFile).getOutput();
				oos.writeObject(nn);
				oos.close();
				System.out.println("Saved to " + serializePath);
				
				File partialNnFile = new File(String.format("%s-layer%d-%f-%f-%f.partial-autoencoder.gz", prefix, i, sparsity, sparsityWeight, weightDecay));
				if (partialNnFile.exists()) partialNnFile.delete();
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
			
			// Compute examples for next iteration
			try {
				File tempFile = File.createTempFile("288", ".features.gz");
				tempFile.deleteOnExit();
				ObjectOutputStream oos = GzipFiles.newObjectOutputStreamSupplier(tempFile).getOutput();
				for (double[] example : examples) {
					oos.writeObject(nn.getHiddenOutput(example));
					oos.reset();
				}
				oos.close();
				examples = new SerializedCollection<double[]>(GzipFiles.newObjectInputStreamSupplier(tempFile));
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
