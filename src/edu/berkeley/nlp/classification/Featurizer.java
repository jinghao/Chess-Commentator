package edu.berkeley.nlp.classification;

public interface Featurizer<T> {
	public double[] getFeaturesFor(T input);

}
