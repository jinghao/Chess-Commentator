package edu.berkeley.nlp.chess;

import java.util.Iterator;
import java.util.List;

public class GameSlicer implements Iterable<List<PositionWithMoves>> {
	private final List<PositionWithMoves> game;
	private final int sliceSize;
	
	public GameSlicer(List<PositionWithMoves> game, int sliceSize) {
		this.game = game;
		this.sliceSize = sliceSize;
	}

	@Override
	public Iterator<List<PositionWithMoves>> iterator() {
		return new Iterator<List<PositionWithMoves>>() {
			private int now = 0;

			@Override
			public boolean hasNext() {
				return now <= (game.size() - sliceSize);
			}

			@Override
			public List<PositionWithMoves> next() {
				List<PositionWithMoves> result = game.subList(now, now + sliceSize);
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
