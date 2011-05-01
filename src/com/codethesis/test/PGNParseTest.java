/*
 * This file is part of PGNParse.
 *
 * PGNParse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PGNParse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PGNParse.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.codethesis.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.codethesis.pgnparse.MalformedMoveException;
import com.codethesis.pgnparse.PGNGame;
import com.codethesis.pgnparse.PGNMove;
import com.codethesis.pgnparse.PGNParseException;
import com.codethesis.pgnparse.PGNSource;

/**
 *
 * @author Deyan Rizov
 *
 */
public class PGNParseTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws PGNParseException 
	 * @throws MalformedMoveException 
	 * @throws NullPointerException 
	 */
	public static void main(String[] args) throws IOException, PGNParseException, NullPointerException, MalformedMoveException {
		if (args.length == 0) {
			System.out.println("Usage:");
			System.out.println("\tpgn_path");
		}
		
		File file = new File(args[0]);
		
		if (!file.exists()) {
			System.out.println("File does not exist!");
			return;
		}
		
		PGNSource source = new PGNSource(file);
		Iterator<PGNGame> i = source.listGames().iterator();
		
		while (i.hasNext()) {
			PGNGame game = i.next();
			
			System.out.println("############################");
			Iterator<String> tagsIterator = game.getTagKeysIterator();
			
			while (tagsIterator.hasNext()) {
				String key = tagsIterator.next();
				System.out.println(key + " {" + game.getTag(key) + "}");
			}
			
			System.out.println();
			Iterator<PGNMove> movesIterator = game.getMovesIterator();
			int num = 1;
			
			while (movesIterator.hasNext()) {
				PGNMove move = movesIterator.next();
				
				if (num % 2 == 1 && !move.isEndGameMarked()) {
					System.out.print((num + 1) / 2 + ". ");
				}
				
				num++;
				
				if (move.isEndGameMarked()) {
					System.out.print("(" + move.getMove() + ")");
				} else if (move.isKingSideCastle()) {
					System.out.print("[O-O] ");
				} else if (move.isQueenSideCastle()) {
					System.out.print("[O-O-O] ");
				} else {
					System.out.print("[" + move.getFromSquare() + "]->[" + move.getToSquare() + "] ");
				}
			}
			
			System.out.println();
		}
	}

}
