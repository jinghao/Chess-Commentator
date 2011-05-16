package edu.berkeley.nlp.chess;

import java.util.Iterator;
import java.util.List;

public class GameSlicer implements Iterable<List<PositionWithMove>> {
	private final List<PositionWithMove> game;
	private final int sliceSize;
	
	public GameSlicer(List<PositionWithMove> game, int sliceSize) {
		this.game = game;
		this.sliceSize = sliceSize;
	}

	@Override
	public Iterator<List<PositionWithMove>> iterator() {
		return new Iterator<List<PositionWithMove>>() {
			private int now = 0;

			@Override
			public boolean hasNext() {
				return now <= (game.size() - sliceSize);
			}

			@Override
			public List<PositionWithMove> next() {
				List<PositionWithMove> result = game.subList(now, now + sliceSize);
				++now;
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
