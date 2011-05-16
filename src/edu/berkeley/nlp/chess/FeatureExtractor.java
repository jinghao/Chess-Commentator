package edu.berkeley.nlp.chess;

import java.util.List;

import chesspresso.Chess;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class FeatureExtractor implements Featurizer<Position> {
	public double[] getFeatures(List<Position> positions) {
		double[] features = new double[4034*positions.size()];
		int index = 0;
		for (Position p : positions) {
			System.arraycopy(getFeaturesFor(p), 0, features, index, 4034);
			index += 4034;
		}
		return features;
	}
	
	@Override
	public double[] getFeaturesFor(Position position) {
//		12*64 + 3*(12*64 + 64*5) + 2
		double[] features = new double[4034];
		int index = 0;
		for (; index < Chess.NUM_OF_SQUARES; index++) {
			features[index] = position.getStone(index);
		}
		
		short[] capturingMoves = position.getAllCapturingMoves();
		
		for (short move : capturingMoves) {
			int from = Move.getFromSqi(move);
			int to = Move.getToSqi(move);
			int isCapturing = Move.isCapturing(move) ? 1 : 0;
			int isEP = Move.isEPMove(move) ? 1 : 0;
			int isLongCastle = Move.isLongCastle(move) ? 1 : 0;
			int isShortCastle = Move.isShortCastle(move) ? 1 : 0;
			int piece = position.getStone(from)-1;
			if (piece < 0)
				piece = 4 - piece;
			
			features[index + piece*64 + to*5] = 1;
			features[index + piece*64 + to*5 + 1] = isCapturing;
			features[index + piece*64 + to*5 + 2] = isEP;
			features[index + piece*64 + to*5 + 3] = isLongCastle;
			features[index + piece*64 + to*5 + 4] = isShortCastle;
		}
		
		index += 12*64 + 64*5;
		
		short[] noncapturingMoves = position.getAllNonCapturingMoves();
		for (short move : noncapturingMoves) {
			int from = Move.getFromSqi(move);
			int to = Move.getToSqi(move);
			int isCapturing = Move.isCapturing(move) ? 1 : 0;
			int isEP = Move.isEPMove(move) ? 1 : 0;
			int isLongCastle = Move.isLongCastle(move) ? 1 : 0;
			int isShortCastle = Move.isShortCastle(move) ? 1 : 0;
			int piece = position.getStone(from)-1;
			if (piece < 0)
				piece = 4 - piece;
			
			features[index + piece*64 + to*5] = 1;
			features[index + piece*64 + to*5 + 1] = isCapturing;
			features[index + piece*64 + to*5 + 2] = isEP;
			features[index + piece*64 + to*5 + 3] = isLongCastle;
			features[index + piece*64 + to*5 + 4] = isShortCastle;
		}
		
		index += 12*64 + 64*5;
		
		short[] recapturingMoves = position.getAllReCapturingMoves(position.getLastShortMove());
		for (short move : recapturingMoves) {
			int from = Move.getFromSqi(move);
			int to = Move.getToSqi(move);
			int isCapturing = Move.isCapturing(move) ? 1 : 0;
			int isEP = Move.isEPMove(move) ? 1 : 0;
			int isLongCastle = Move.isLongCastle(move) ? 1 : 0;
			int isShortCastle = Move.isShortCastle(move) ? 1 : 0;
			int piece = position.getStone(from)-1;
			if (piece < 0)
				piece = 4 - piece;
			
			features[index + piece*64 + to*5] = 1;
			features[index + piece*64 + to*5 + 1] = isCapturing;
			features[index + piece*64 + to*5 + 2] = isEP;
			features[index + piece*64 + to*5 + 3] = isLongCastle;
			features[index + piece*64 + to*5 + 4] = isShortCastle;
		}
		
		index += 12*64 + 64*5;
		
		features[index++] = position.getMaterial();
		features[index++] = position.getDomination();
		
		return features;
	}
}
