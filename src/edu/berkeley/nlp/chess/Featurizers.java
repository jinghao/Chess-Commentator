package edu.berkeley.nlp.chess;

import java.util.Arrays;
import java.util.List;

import chesspresso.Chess;
import chesspresso.position.Position;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class Featurizers {
	public static class TwoColorBoard implements Featurizer<Position> {
		public static final int KING = 5, PAWN = 4, QUEEN = 3, ROOK = 2, BISHOP = 1, KNIGHT = 0;

		@Override
		public double[] getFeaturesFor(Position position) {
			double[] ret = new double[Chess.NUM_OF_SQUARES * 6];
			Arrays.fill(ret, 0.5);
			
			for (int i = 0; i < Chess.NUM_OF_SQUARES; i++) {
				switch (position.getStone(i)) {
					case Chess.WHITE_BISHOP: ret[i * BISHOP] = 1;
					case Chess.WHITE_KING: ret[i * KING] = 1;
					case Chess.WHITE_KNIGHT: ret[i * KNIGHT] = 1;
					case Chess.WHITE_PAWN: ret[i * PAWN] = 1;
					case Chess.WHITE_QUEEN: ret[i * QUEEN] = 1;
					case Chess.WHITE_ROOK: ret[i * ROOK] = 1;
					case Chess.BLACK_BISHOP: ret[i * BISHOP] = 0;
					case Chess.BLACK_KING: ret[i * KING] = 0;
					case Chess.BLACK_KNIGHT: ret[i * KNIGHT] = 0;
					case Chess.BLACK_PAWN: ret[i * PAWN] = 0;
					case Chess.BLACK_QUEEN: ret[i * QUEEN] = 0;
					case Chess.BLACK_ROOK: ret[i * ROOK] = 0;
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