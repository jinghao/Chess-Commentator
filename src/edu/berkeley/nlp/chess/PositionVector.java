package edu.berkeley.nlp.chess;

import java.util.Arrays;

import chesspresso.Chess;
import chesspresso.position.Position;

import com.google.common.base.Function;

public interface PositionVector extends Function<Position, double[]> {
	public static final int
    KING = 5, PAWN = 4, QUEEN = 3, ROOK = 2, BISHOP = 1, KNIGHT = 0;
}

class Method1 implements PositionVector {

	@Override
	public double[] apply(Position position) {
		double[] ret = new double[64*12];
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
}

class Method2 implements PositionVector {
	@Override
	public double[] apply(Position position) {
		double[] ret = new double[64*12];
		
		for (int i = 0; i < Chess.NUM_OF_SQUARES; i++) {
			switch (position.getStone(i)) {
				case Chess.WHITE_BISHOP: ret[i * BISHOP] = 1;
				case Chess.WHITE_KING: ret[i * KING] = 1;
				case Chess.WHITE_KNIGHT: ret[i * KNIGHT] = 1;
				case Chess.WHITE_PAWN: ret[i * PAWN] = 1;
				case Chess.WHITE_QUEEN: ret[i * QUEEN] = 1;
				case Chess.WHITE_ROOK: ret[i * ROOK] = 1;
				case Chess.BLACK_BISHOP: ret[i * BISHOP] = 0.5;
				case Chess.BLACK_KING: ret[i * KING] = 0.5;
				case Chess.BLACK_KNIGHT: ret[i * KNIGHT] = 0.5;
				case Chess.BLACK_PAWN: ret[i * PAWN] = 0.5;
				case Chess.BLACK_QUEEN: ret[i * QUEEN] = 0.5;
				case Chess.BLACK_ROOK: ret[i * ROOK] = 0.5;
			}
		}
		return ret;
	}
}