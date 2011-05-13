package edu.berkeley.nlp.autoencoder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;

import edu.berkeley.nlp.autoencoder.NeuralNetwork.LossFunction;
import edu.berkeley.nlp.util.Pair;

public class NeuralNetworkTest {
	private NeuralNetwork nn;

	List<Pair<double[], double[]>> irisExamples = ImmutableList.<Pair<double[], double[]>> builder()
	.add(Pair.makePair(new double[] {5.1, 3.5, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.9, 3.0, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.7, 3.2, 1.3, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.6, 3.1, 1.5, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.6, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.4, 3.9, 1.7, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.6, 3.4, 1.4, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.4, 1.5, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.4, 2.9, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.9, 3.1, 1.5, 0.1}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.4, 3.7, 1.5, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.8, 3.4, 1.6, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.8, 3.0, 1.4, 0.1}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.3, 3.0, 1.1, 0.1}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.8, 4.0, 1.2, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.7, 4.4, 1.5, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.4, 3.9, 1.3, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.5, 1.4, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.7, 3.8, 1.7, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.8, 1.5, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.4, 3.4, 1.7, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.7, 1.5, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.6, 3.6, 1.0, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.3, 1.7, 0.5}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.8, 3.4, 1.9, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.0, 1.6, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.4, 1.6, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.2, 3.5, 1.5, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.2, 3.4, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.7, 3.2, 1.6, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.8, 3.1, 1.6, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.4, 3.4, 1.5, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.2, 4.1, 1.5, 0.1}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.5, 4.2, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.9, 3.1, 1.5, 0.1}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.2, 1.2, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.5, 3.5, 1.3, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.9, 3.1, 1.5, 0.1}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.4, 3.0, 1.3, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.4, 1.5, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.5, 1.3, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.5, 2.3, 1.3, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.4, 3.2, 1.3, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.5, 1.6, 0.6}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.8, 1.9, 0.4}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.8, 3.0, 1.4, 0.3}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.1, 3.8, 1.6, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {4.6, 3.2, 1.4, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.3, 3.7, 1.5, 0.2}, new double[] {0, 1, 0, 1}))
	.add(Pair.makePair(new double[] {5.0, 3.3, 1.4, 0.2}, new double[] {0, 1, 0, 1}))

	.add(Pair.makePair(new double[] {7.0, 3.2, 4.7, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.4, 3.2, 4.5, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.9, 3.1, 4.9, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.5, 2.3, 4.0, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.5, 2.8, 4.6, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.7, 2.8, 4.5, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.3, 3.3, 4.7, 1.6}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {4.9, 2.4, 3.3, 1.0}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.6, 2.9, 4.6, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.2, 2.7, 3.9, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.0, 2.0, 3.5, 1.0}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.9, 3.0, 4.2, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.0, 2.2, 4.0, 1.0}, new double[] {1, 0, 1, 0}))
//	.add(Pair.makePair(new double[] {6.1, 2.9, 4.7, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.6, 2.9, 3.6, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.7, 3.1, 4.4, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.6, 3.0, 4.5, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.8, 2.7, 4.1, 1.0}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.2, 2.2, 4.5, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.6, 2.5, 3.9, 1.1}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.9, 3.2, 4.8, 1.8}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.1, 2.8, 4.0, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.3, 2.5, 4.9, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.1, 2.8, 4.7, 1.2}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.4, 2.9, 4.3, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.6, 3.0, 4.4, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.8, 2.8, 4.8, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.7, 3.0, 5.0, 1.7}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.0, 2.9, 4.5, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.7, 2.6, 3.5, 1.0}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.5, 2.4, 3.8, 1.1}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.5, 2.4, 3.7, 1.0}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.8, 2.7, 3.9, 1.2}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.0, 2.7, 5.1, 1.6}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.4, 3.0, 4.5, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.0, 3.4, 4.5, 1.6}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.7, 3.1, 4.7, 1.5}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.3, 2.3, 4.4, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.6, 3.0, 4.1, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.5, 2.5, 4.0, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.5, 2.6, 4.4, 1.2}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.1, 3.0, 4.6, 1.4}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.8, 2.6, 4.0, 1.2}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.0, 2.3, 3.3, 1.0}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.6, 2.7, 4.2, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.7, 3.0, 4.2, 1.2}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.7, 2.9, 4.2, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {6.2, 2.9, 4.3, 1.3}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.1, 2.5, 3.0, 1.1}, new double[] {1, 0, 1, 0}))
	.add(Pair.makePair(new double[] {5.7, 2.8, 4.1, 1.3}, new double[] {1, 0, 1, 0}))
	.build();

	/*
	 * @Test public void testGetActivations() { assertArrayEquals(
	 * nn.getActivations(new double[] {1, 2, 3, 4, 5}, new double[] {2, 0, 0, 0,
	 * 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1,
	 * 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
	 * 0, 0, 1, 1, 1, 1, 1, 1})[2], new double[] {0.87572705297833242,
	 * 0.87572705297833242, 0.87889566963661969, 0.88009257869295032,
	 * 0.88053722386948019}, 1e-6); }
	 */

	@Test
	public void testGetOutput() {
		nn = NeuralNetwork.train(new int[] { 4, 64, 64, 4 }, 1e-3, 0.05, 1e-2, irisExamples);
		
//		System.out.println(Doubles.join(", ", nn.parameters));
		System.out.println(Doubles.join("\n",
				nn.getHiddenOutput(new double[] {6.1, 2.9, 4.7, 1.4})));
		
		System.out.println(Doubles.join(", ",
				nn.getOutput(new double[] {6.1, 2.9, 4.7, 1.4})));
	}

	@Test
	public void testGradientCorrectness() {
		double epsilon = 1e-4;

		LossFunction l = new NeuralNetwork.LossFunction(new int[] {4, 4, 4}, 1e-4, 0.5, 1e-4, irisExamples);
		double[] initial = l.initial();
		double[] derivatives = l.derivativeAt(initial);
		for (int i = 0; i < initial.length; ++i) {
			double[] bigger = Arrays.copyOf(initial, initial.length);
			bigger[i] += epsilon;
			double[] smaller = Arrays.copyOf(initial, initial.length);
			smaller[i] -= epsilon;

			assertEquals(Integer.toString(i), (l.valueAt(bigger) - l.valueAt(smaller))
					/ (2 * epsilon), derivatives[i], epsilon);
		}
	}
	
}