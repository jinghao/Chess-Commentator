package edu.berkeley.nlp.chess;

import chesspresso.move.Move;
import chesspresso.position.Position;

import com.google.common.primitives.Doubles;
import com.google.inject.Inject;

import edu.berkeley.nlp.classification.Featurizer;

public class PositionWithMoves {
	public final Move previousMove;
	public final Position position;
	public final Move nextMove;
	public final int hashCode;
	
	public PositionWithMoves(Move previousMove, Position position, Move nextMove) {
		this.previousMove = previousMove;
		this.position = position;
		this.nextMove = nextMove;
		this.hashCode = getHashCode();
	}
	
	@Override
	public String toString() {
		return "(" + position + ", " + nextMove + ")";
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof PositionWithMoves)) return false;
		
		PositionWithMoves p = (PositionWithMoves)o;
		try {
			return (p.previousMove == previousMove || p.previousMove.equals(previousMove))
				&& (p.position == position || p.position.equals(position))
				&& (p.nextMove == nextMove || p.nextMove.equals(nextMove));
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public int getHashCode() {
		long result = 17;
		result = result * 37 + position.getHashCode();
		result = result * 37 + (previousMove == null ? 0 : previousMove.hashCode());
		result = result * 37 + (nextMove == null ? 0 : nextMove.hashCode());
		return (int) (result ^ (result >>> 32));
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	public static class ConcatenatingFeaturizer implements Featurizer<PositionWithMoves> {
		private final Featurizer<Position> positionFeaturizer;
		private final Featurizer<Move> moveFeaturizer;
		
		@Inject
		public ConcatenatingFeaturizer(Featurizer<Position> positionFeaturizer, Featurizer<Move> moveFeaturizer) {
			this.positionFeaturizer = positionFeaturizer;
			this.moveFeaturizer = moveFeaturizer;
		}
		
		@Override 
		public double[] getFeaturesFor(PositionWithMoves input) {
			return Doubles.concat(positionFeaturizer.getFeaturesFor(input.position),
						   moveFeaturizer.getFeaturesFor(input.nextMove));
		}
	};
	
	/**
	 * Featurizer that ignores moves.
	 */
	public static class PositionFeaturizer implements Featurizer<PositionWithMoves> {
		private final Featurizer<Position> positionFeaturizer;
		
		@Inject
		public PositionFeaturizer(Featurizer<Position> positionFeaturizer) {
			this.positionFeaturizer = positionFeaturizer;
		}
		
		@Override 
		public double[] getFeaturesFor(PositionWithMoves input) {
			return positionFeaturizer.getFeaturesFor(input.position);
		}
	};
}
