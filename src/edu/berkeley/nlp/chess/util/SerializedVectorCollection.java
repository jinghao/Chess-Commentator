package edu.berkeley.nlp.chess.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.google.common.io.InputSupplier;

public class SerializedVectorCollection extends AbstractCollection<double[]> {
	private int size = 0;
	private InputSupplier<ObjectInputStream> oiss;
	
	public SerializedVectorCollection(InputSupplier<ObjectInputStream> oiss) {
		this.oiss = oiss;
		ObjectInputStream ois = null;
		boolean threw = true;
		try {
			ois = oiss.getInput();
			while (true) {
				ois.readObject();
				++size;
			}
		} catch (EOFException e) {
			threw = false;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		} catch (ClassNotFoundException e) {
			throw Throwables.propagate(e);
		} finally {
			try {
				Closeables.close(ois, threw);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	}

	@Override
	public Iterator<double[]> iterator() {
		final ObjectInputStream ois;
		try {
			ois = oiss.getInput();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		
		return new Iterator<double[]>() {
			private double[] next = null;
			private boolean hasNext = true;
			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public double[] next() {
				double[] toReturn;
				if (next != null)
					toReturn = next;
				else
					try {
						toReturn = (double[]) ois.readObject();
					} catch (EOFException e) {
						throw new NoSuchElementException();
					} catch (IOException e) {
						throw Throwables.propagate(e);
					} catch (ClassNotFoundException e) {
						throw Throwables.propagate(e);
					}
				
				try {
					next = (double[]) ois.readObject();
				} catch (EOFException e) {
					hasNext = false;
				} catch (IOException e) {
					throw Throwables.propagate(e);
				} catch (ClassNotFoundException e) {
					throw Throwables.propagate(e);
				}
				
				return toReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		return size;
	}

}