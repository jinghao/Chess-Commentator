package edu.berkeley.nlp.autoencoder;

public interface ActivationFunction {
	/** Computes function at x. */
	public double valueAt(double x);
	
	/** Given the value of f(x), return f'(x).
	 *  In other words, compute f'(f^-1(input)). */
	public double derivativeAt(double x);
	
	/** Multiply normalized initialization by
	 * this factor. */
	public double normalizedInitializationFactor();
	
	public static final ActivationFunction SIGMOID = new ActivationFunction() {
		@Override public double valueAt(double x) {
			return 1. / (1 + Math.exp(-x));
		}

		@Override public double derivativeAt(double x) {
			return x * (1 - x);
		}
		
		@Override public double normalizedInitializationFactor() {
			return 4.0;
		}
	};
	
	public static final ActivationFunction TANH = new ActivationFunction() {
		@Override public double valueAt(double x) {
			return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
		}

		@Override public double derivativeAt(double x) {
			return 1 - (x * x);
		}
		
		@Override public double normalizedInitializationFactor() {
			return 1.0;
		}
	};
	
	public static final ActivationFunction SOFTSIGN = new ActivationFunction() {
		@Override public double valueAt(double x) {
			return x / (1 + Math.abs(x));
		}

		@Override public double derivativeAt(double x) {
			double numerator;
			if (x >= 0) numerator = Math.sqrt((x*x) / ((x-1)*(x-1))) + 1; 
			else numerator = Math.sqrt((x * x) / ((x+1)*(x+1))) + 1;
			
			return 1. / (numerator * numerator);	
		}
		
		@Override public double normalizedInitializationFactor() {
			return 1.0;
		}
	};
}
