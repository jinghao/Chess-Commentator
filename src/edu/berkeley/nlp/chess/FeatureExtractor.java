package edu.berkeley.nlp.chess;

import java.util.List;

import com.google.common.base.Throwables;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;


public class FeatureExtractor implements Featurizer<PositionWithMoves> {
	public static final int FEATURES_PER_MOVE = 10;
	public static final int FEATURE_LENGTH = 
		12*Chess.NUM_OF_SQUARES + 12*Chess.NUM_OF_SQUARES + 64*FEATURES_PER_MOVE + 2;
	
	public double[] getFeaturesFor(PositionWithMoves positionWithMove) {
		Position position = positionWithMove.position;
		double[] features = new double[FEATURE_LENGTH];
		int index = 0;
		for (; index < Chess.NUM_OF_SQUARES; index++) {
			features[index] = position.getStone(index);
		}
		
		short[] noncapturingMoves = position.getAllNonCapturingMoves();
		for (short move : noncapturingMoves) {
			try { 
				position.doMove(move);
			} catch (IllegalMoveException e) {
				throw Throwables.propagate(e);
			}
			int from = Move.getFromSqi(move);
			int to = Move.getToSqi(move);
			int isCapturing = Move.isCapturing(move) ? 1 : 0;
			int isEP = Move.isEPMove(move) ? 1 : 0;
			int isLongCastle = Move.isLongCastle(move) ? 1 : 0;
			int isShortCastle = Move.isShortCastle(move) ? 1 : 0;
			int isCheck = position.isCheck() ? 1 : 0;
			int isStalemate = position.isStaleMate() ? 1 : 0;
			int isMate = position.isMate() ? 1 : 0;
			int promotingPiece = Move.getPromotionPiece(move);
			
			int piece = position.getStone(from)-1;
			if (piece < 0)
				piece = 4 - piece;
			
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE] = 1;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 1] = isCapturing;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 2] = isEP;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 3] = isLongCastle;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 4] = isShortCastle;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 5] = isCheck;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 6] = isStalemate;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 7] = isMate;
			features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 8] = promotingPiece;
			position.undoMove();
		}
			
		Move m = positionWithMove.previousMove;
		
		if (m != null) {
			short[] recapturingMoves = position.getAllReCapturingMoves(m.getShortMoveDesc());
			for (short move : recapturingMoves) {
				int from = Move.getFromSqi(move);
				int to = Move.getToSqi(move);
				int piece = position.getStone(from)-1;
				if (piece < 0)
					piece = 4 - piece;
				
				features[index + piece*Chess.NUM_OF_SQUARES + to*FEATURES_PER_MOVE + 9] = 1; // is recapturing
			}
		}
		
		index += 12*Chess.NUM_OF_SQUARES + Chess.NUM_OF_SQUARES*FEATURES_PER_MOVE;
		
		features[index++] = position.getMaterial();
		features[index++] = position.getDomination();
		
		return features;
	}
}
