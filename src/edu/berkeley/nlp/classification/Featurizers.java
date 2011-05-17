package edu.berkeley.nlp.classification;

import java.util.Arrays;
import java.util.List;

import chesspresso.Chess;
import chesspresso.position.Position;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import edu.berkeley.nlp.chess.PositionWithMoves;

public class Featurizers {
	public static class Normalizer<T> implements Featurizer<T> {
		private Featurizer<T> featurizer;
		private double norm;

		public Normalizer(Featurizer<T> featurizer, double norm) {
			Preconditions.checkArgument(norm != 0);
			this.featurizer = featurizer;
			this.norm = norm;
		}
		
		@Override
		public double[] getFeaturesFor(T input) {
			double[] features = featurizer.getFeaturesFor(input);
			double total = 0;
			for (int i = 0; i < features.length; i++)
				total += features[i]*features[i];
			
			total = Math.sqrt(total);
			
			if (total != 0)
				for (int i = 0; i < features.length; i++)
					features[i] *= norm/total;
			
			return features;
		}
		
	}
	
	public static class TwoColorBoard implements Featurizer<Position> {
		public static final int KING = 5, PAWN = 4, QUEEN = 3, ROOK = 2, BISHOP = 1, KNIGHT = 0;

		@Override
		public double[] getFeaturesFor(Position position) {
			double[] ret = new double[Chess.NUM_OF_SQUARES * 6];
			Arrays.fill(ret, 0.5);
			
			for (int i = 0; i < Chess.NUM_OF_SQUARES; i++) {
				switch (position.getStone(i)) {
					case Chess.WHITE_BISHOP: 
						ret[Chess.NUM_OF_SQUARES * BISHOP + i] = 1; break;
					case Chess.WHITE_KING: 
						ret[Chess.NUM_OF_SQUARES * KING   + i] = 1; break;
					case Chess.WHITE_KNIGHT: 
						ret[Chess.NUM_OF_SQUARES * KNIGHT + i] = 1; break;
					case Chess.WHITE_PAWN: 
						ret[Chess.NUM_OF_SQUARES * PAWN   + i] = 1; break;
					case Chess.WHITE_QUEEN: 
						ret[Chess.NUM_OF_SQUARES * QUEEN  + i] = 1; break;
					case Chess.WHITE_ROOK: 
						ret[Chess.NUM_OF_SQUARES * ROOK   + i] = 1; break;
					case Chess.BLACK_BISHOP: 
						ret[Chess.NUM_OF_SQUARES * BISHOP + i] = 0; break;
					case Chess.BLACK_KING: 
						ret[Chess.NUM_OF_SQUARES * KING   + i] = 0; break;
					case Chess.BLACK_KNIGHT: 
						ret[Chess.NUM_OF_SQUARES * KNIGHT + i] = 0; break;
					case Chess.BLACK_PAWN: 
						ret[Chess.NUM_OF_SQUARES * PAWN   + i] = 0; break;
					case Chess.BLACK_QUEEN: 
						ret[Chess.NUM_OF_SQUARES * QUEEN  + i] = 0; break;
					case Chess.BLACK_ROOK: 
						ret[Chess.NUM_OF_SQUARES * ROOK   + i] = 0; break;
				}
			}
			return ret;
		}
	};
	
	public static class ConcatenatingPositionWithMove implements Featurizer<List<PositionWithMoves>> {
		private final Featurizer<PositionWithMoves> featurizer;

		@Inject
		public ConcatenatingPositionWithMove(Featurizer<PositionWithMoves> featurizer) {
			this.featurizer = featurizer;
		}
		
		@Override
		public double[] getFeaturesFor(List<PositionWithMoves> input) {
			List<double[]> features = Lists.newArrayList();
			int totalArrayLength = 0;
			for (PositionWithMoves pwm : input) {
				double[] feature = featurizer.getFeaturesFor(pwm);
				features.add(feature);
				totalArrayLength += feature.length;
			}
			
			double[] result = new double[totalArrayLength];
			int index = 0;
			for (double[] feature : features) {
	            System.arraycopy(feature, 0, result, index, feature.length);
	            index += feature.length;
	        }
			return result;
		}

	}
}