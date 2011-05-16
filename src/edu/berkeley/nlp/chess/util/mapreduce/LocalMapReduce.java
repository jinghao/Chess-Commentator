package edu.berkeley.nlp.chess.util.mapreduce;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class LocalMapReduce<In, Out> implements MapReduce<In, Out> {
	
	private int numThreads;
	
	public LocalMapReduce() { 
		numThreads = Runtime.getRuntime().availableProcessors(); 
	}
	
	public LocalMapReduce(int numThreads) {
		this.numThreads = numThreads;
	}
	
	@Override
	public Out run(Iterable<In> input, 
			final Function<Iterable<In>, Out> mapper,
			final Function<Collection<Out>, Out> reducer) {
		
		ExecutorService pool = Executors.newFixedThreadPool(numThreads);
		final IterableSynchronizer<In> inputSynchronizer = new IterableSynchronizer<In>(input);
		List<Future<Out>> resultFutures = Lists.newArrayList();
		
		for (int i = 0; i < numThreads; ++i)
			resultFutures.add(pool.submit(new Callable<Out>() {
				@Override public Out call() throws Exception {
					return mapper.apply(inputSynchronizer.get());
				}
			}));
		
		List<Out> results = Lists.transform(resultFutures, new Function<Future<Out>, Out>() {
			@Override public Out apply(Future<Out> input) {
				try {
					return input.get();
				} catch (InterruptedException e) {
					throw Throwables.propagate(e);
				} catch (ExecutionException e) {
					throw Throwables.propagate(e);
				}
			}
		});
		
		pool.shutdown();
		
		return reducer.apply(results);
	}

}
