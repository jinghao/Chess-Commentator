package edu.berkeley.nlp.classification;


public class LabelledFeatureVector<T> extends FeatureVector<T> {
	public int label;
	
	LabelledFeatureVector(double[] features, int label) {
		super(features);
		this.label = label;
	}
	
	LabelledFeatureVector(Featurizer<T> featurizer, T input, int label) {
		super(featurizer, input);
		this.label = label;
	}
}
