package edu.berkeley.nlp.chess.util.mapreduce;

import java.util.Collection;

import com.google.common.base.Function;

public interface MapReduce<In, Out> {
	public Out run(Iterable<In> input, Function<Iterable<In>, Out> mapper, Function<Collection<Out>, Out> reducer);
}
