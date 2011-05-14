package edu.berkeley.nlp.chess;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

import com.google.common.collect.Lists;

public class GameSlicer {
	private LinkedList<Position> positions = Lists.newLinkedList();
	private final int sliceSize;
	
	public GameSlicer (String filename, int sliceSize) throws IOException, PGNSyntaxError {
		PGNReader reader = new PGNReader(filename);
		Game game = reader.parseGame();
		this.sliceSize = sliceSize;
		do { 
			positions.add(game.getPosition());
		} while (game.goForward());	
	}
	
    /**
     * Gets slice [start, start+sliceSize] of this game. This is a view of the
     * internal list of positions.
     **/

	public List<Position> getSlice(int start) {
		if (start >= positions.size())
			throw new IndexOutOfBoundsException();
		return positions.subList(start, Math.min(start+sliceSize, positions.size()));
	}
	
	/** Returns number of positions in this game **/
	public int numPositions() {
		return positions.size();
	}
}
