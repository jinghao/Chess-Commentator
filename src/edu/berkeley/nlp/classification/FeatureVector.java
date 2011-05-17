package edu.berkeley.nlp.classification;

public class FeatureVector {
	public double[] features;
	
	FeatureVector(double[] features) {
		this.features = features;
	}
	
	/**
	 * Returns the linear norm factor of this vector's values (i.e., the sum of
	 * it's values).
	 */
	public double getL1Norm() {
		double sum = 0.0;
	    for (int i = 0; i < features.length; i++) {
	    	sum += features[i];
	    }
	    return sum;
	}

	/**
	 * Returns the L2 norm factor of this vector's values.
	 */
	public double getL2Norm() {
		double square_sum = 0.0;
	    for (int i = 0; i < features.length; i++) {
	    	square_sum += (features[i] * features[i]);
	    }
	    return Math.sqrt(square_sum);
	}

	public double getValueAt(int index) {
		return features[index];
	}

	/**
	 * Performs a linear normalization to the value 1.
	 */
	public void normalizeL1() {
	    normalizeL1(getL1Norm());
	}

	/**
	 * Performs a linear normalization to the given norm value.
	 */
	public void normalizeL1(double norm) {
	    for (int i = 0; i < features.length; i++) {
	    	if (features[i] > 0) {
	    		features[i] /= norm;
	    	}
	    }
	}

	/**
	 * Performs an L2 normalization to the value 1.
	 */
	public void normalizeL2() {
	    double norm = Math.pow(getL2Norm(), 2);
	    for (int i = 0; i < features.length; i++) {
	    	features[i] = Math.pow(features[i], 2) / norm;
	    }
	}
}
