package edu.berkeley.nlp.classification;


public class LabeledFeatureVector<T> extends FeatureVector<T> {
	public int label;
	
	LabeledFeatureVector(double[] features, int label) {
		super(features);
		this.label = label;
	}
	
	LabeledFeatureVector(Featurizer<T> featurizer, T input, int label) {
		super(featurizer, input);
		this.label = label;
	}
}
