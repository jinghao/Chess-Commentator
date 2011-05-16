package edu.berkeley.nlp.classification;


public class LabelledFeatureVector<T> {
	public double[] features;
	public int label;
	
	LabelledFeatureVector(double[] features, int label) {
		this.features = features;
		this.label = label;
	}
	
	LabelledFeatureVector(Featurizer<T> featurizer, T input, int label) {
		this.features = featurizer.getFeaturesFor(input);
		this.label = label;
	}
}
