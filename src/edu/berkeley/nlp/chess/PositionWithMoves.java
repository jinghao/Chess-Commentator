package edu.berkeley.nlp.chess;

import java.io.Serializable;
import java.util.List;

import chesspresso.move.Move;
import chesspresso.position.Position;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.inject.Inject;

import edu.berkeley.nlp.classification.Featurizer;

public class PositionWithMoves implements Serializable {
	private static final long serialVersionUID = 6669577379907352858L;
	
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
		return "(" + previousMove + " -> " + position + " -> " + nextMove + ")";
	}
	
	@Override
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
	
	static public List<PositionWithMoves> getPrefix(List<PositionWithMoves> input, int limit) {
		return Lists.newArrayList(input.subList(0, limit));/*
		List<PositionWithMoves> result = Lists.newArrayList();
		int numUsed = 0;
		for (PositionWithMoves pwm : input) {
			if (numUsed < limit) {
				result.add(pwm);
				if (pwm.nextMove == null) numUsed += 1;
				else numUsed += 2;
			} else {
				result.add(new PositionWithMoves(pwm.previousMove, pwm.position, null));
				++numUsed;
				break;
			}
		}
		Preconditions.checkState(numUsed == limit);
		return result;*/
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
