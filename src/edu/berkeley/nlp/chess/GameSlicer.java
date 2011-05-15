package edu.berkeley.nlp.chess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.collect.Lists;

public class GameSlicer implements Iterable<List<Position>> {
	private List<Position> positions;
	private int sliceSize;
	
	public GameSlicer (Game game, int sliceSize) throws IOException, PGNSyntaxError {
		game.goBackToLineBegin();
		this.sliceSize = sliceSize;
		this.positions = new ArrayList<Position>();
		
		do {
			positions.add(new Position(game.getPosition()));
		} while (game.goForward());
	}

	@Override
	public Iterator<List<Position>> iterator() {
		return new Iterator<List<Position>>() {
			private int now = 0;

			@Override
			public boolean hasNext() {
				return now < positions.size();
			}

			@Override
			public List<Position> next() {
				now++;
				return positions.subList(now-1, Math.min(now-1+sliceSize, positions.size()));
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
