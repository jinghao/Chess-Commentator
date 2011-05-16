package edu.berkeley.nlp.chess.util.mapreduce;

import java.util.Iterator;

public class IterableSynchronizer<T> {
	private Iterable<T> iterable;
	private Iterator<T> iterator;

	public IterableSynchronizer(Iterable<T> iterable) {
		this.iterable = iterable;
		this.iterator = iterable.iterator();
	}
	
	public Iterable<T> get() {
		return new Iterable<T>() {
			@Override public Iterator<T> iterator() {
				return new Iterator<T>() {
					@Override public boolean hasNext() {
						synchronized(iterator) {
							return iterator.hasNext();
						}
					}
					@Override public T next() {
						synchronized(iterator) {
							try {
								return iterator.next();
							} catch (NullPointerException e) {
								return null;
							}
						}
 					}
					@Override public void remove() {
						synchronized(iterator) {
							iterator.remove();
						}
					}
					
				};
			}
		};
	}
	
	public void reset() {
		this.iterator = iterable.iterator();
	}
}
