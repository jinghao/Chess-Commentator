package edu.berkeley.nlp.chess;

import chesspresso.move.Move;
import chesspresso.position.Position;

import com.google.common.primitives.Doubles;
import com.google.inject.Inject;

public class PositionWithMoves {
	public final Move previousMove;
	public final Position position;
	public final Move nextMove;
	
	public PositionWithMoves(Move previousMove, Position position, Move nextMove) {
		this.previousMove = previousMove;
		this.position = position;
		this.nextMove = nextMove;
	}
	
	@Override
	public String toString() {
		return "(" + position + ", " + nextMove + ")";
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
