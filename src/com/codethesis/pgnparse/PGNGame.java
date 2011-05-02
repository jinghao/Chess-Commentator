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
package com.codethesis.pgnparse;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Deyan Rizov
 *
 */
public class PGNGame {

	private Map<String, String> tags;
	
	private List<PGNMove> moves;
	
	private String pgn;
	
	PGNGame() {
		tags = new HashMap<String, String>();
		moves = new LinkedList<PGNMove>();
	}
	
	PGNGame(String pgn) {
		this();
		this.pgn = pgn;
	}
	
	@Override
	public String toString() {
		return pgn == null ? "" : pgn;
	}
	
	void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	void removeTag(String key) {
		tags.remove(key);
	}
	
	void addMove(PGNMove move) {
		moves.add(move);
	}
	
	void removeMove(PGNMove move) {
		moves.remove(move);
	}
	
	void removeMove(int index) {
		moves.remove(index);
	}
	
	public String getTag(String key) {
		return tags.get(key);
	}
	
	public Iterator<String> getTagKeysIterator() {
		return tags.keySet().iterator();
	}
	
	public boolean containsTagKey(String key) {
		return tags.containsKey(key);
	}
	
	public int getTagsCount() {
		return tags.size();
	}
	
	public PGNMove getMove(int index) {
		return moves.get(index);
	}
	
	public Iterator<PGNMove> getMovesIterator() {
		return moves.iterator();
	}
	
	public int getMovesCount() {
		return moves.size();
	}
	
	public int getMovePairsCount() {
		return moves.size() / 2;
	}
	
	public String sha1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
	  String s = canonicalRepresentation();
	  
    MessageDigest md = MessageDigest.getInstance("SHA-1");
	  md.update(s.getBytes("iso-8859-1"), 0, s.length());
	  
    byte[] t = md.digest();
    
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < t.length; i++) {
      hexString.append(Integer.toHexString(0xFF & t[i]));
    }
    
    return hexString.toString();
	}
	
	public String canonicalRepresentation() {
	  StringBuffer s = new StringBuffer();
	  
    for (PGNMove move : moves) {
      if (move.isEndGameMarked()) {
        s.append("(" + move.getMove() + ")");
      } else if (move.isKingSideCastle()) {
        s.append("[O-O] ");
      } else if (move.isQueenSideCastle()) {
        s.append("[O-O-O] ");
      } else {
        s.append("[" + move.getFromSquare() + "]->[" + move.getToSquare() + "] ");
      }
    }
    
    return s.toString();
	}
}
