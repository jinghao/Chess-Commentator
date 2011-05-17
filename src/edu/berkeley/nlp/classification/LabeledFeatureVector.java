package edu.berkeley.nlp.classification;


public class LabeledFeatureVector extends FeatureVector {
	public int label;
	
	LabeledFeatureVector(double[] features, int label) {
		super(features);
		this.label = label;
	}
	
	@Override
	public String toString() {
		String ans = String.format("%d", label);
		for (int i = 0; i < features.length; i++) {
			if (features[i] > 1e-8)
				ans += String.format(" %d:%.10f", i+1, features[i]);
		}
		
		ans += String.format("\n");
		
		return ans;
	}
}
