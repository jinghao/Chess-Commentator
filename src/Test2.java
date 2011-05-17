
public class Test2 {
	public static void main(String[] args) {
		int dataSize = 102475468;
		
		float multFactor = 0.5f;
		
		float[] input = new float[dataSize];
		float[] output = new float[dataSize];
		
		for (int i = 0; i < dataSize; ++i) {
			input[i] = (float)i;
		}
		long time = -System.nanoTime();
		for (int i = 0; i < dataSize; ++i) {
			output[i] = (input[i] * multFactor * input[i] / 2);
		}
		time += System.nanoTime();
		
		System.out.printf("Took %f seconds\n", (double)time/1000000000);
	}
}
