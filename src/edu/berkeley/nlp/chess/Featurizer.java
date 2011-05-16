package edu.berkeley.nlp.chess;

public interface Featurizer<T> {
	public double[] getFeaturesFor(T input);

}
