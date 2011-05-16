package edu.berkeley.nlp.chess;

import chesspresso.move.Move;
import chesspresso.position.Position;

import com.google.common.primitives.Doubles;
import com.google.inject.Inject;

public class PositionWithMove {
	public final Position position;
	public final Move move;
	
	public PositionWithMove(Position position, Move move) {
		this.position = position;
		this.move = move;
	}
	
	@Override
	public String toString() {
		return "(" + position + ", " + move + ")";
	}
	
	public static class ConcatenatingFeaturizer implements Featurizer<PositionWithMove> {
		private final Featurizer<Position> positionFeaturizer;
		private final Featurizer<Move> moveFeaturizer;
		
		@Inject
		public ConcatenatingFeaturizer(Featurizer<Position> positionFeaturizer, Featurizer<Move> moveFeaturizer) {
			this.positionFeaturizer = positionFeaturizer;
			this.moveFeaturizer = moveFeaturizer;
		}
		
		@Override 
		public double[] getFeaturesFor(PositionWithMove input) {
			return Doubles.concat(positionFeaturizer.getFeaturesFor(input.position),
						   moveFeaturizer.getFeaturesFor(input.move));
		}
	};
	
	public static class PositionFeaturizer implements Featurizer<PositionWithMove> {
		private final Featurizer<Position> positionFeaturizer;
		
		@Inject
		public PositionFeaturizer(Featurizer<Position> positionFeaturizer) {
			this.positionFeaturizer = positionFeaturizer;
		}
		
		@Override 
		public double[] getFeaturesFor(PositionWithMove input) {
			return positionFeaturizer.getFeaturesFor(input.position);
		}
	};
}
