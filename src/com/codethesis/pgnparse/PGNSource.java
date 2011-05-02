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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Deyan Rizov
 *
 */
public class PGNSource {
  public static class PGNSourceIterator implements Iterator {
    BufferedReader br;
    String line;
    
    public PGNSourceIterator(File file) throws IOException {
      this(new FileInputStream(file));
    }
    
    public PGNSourceIterator(InputStream inputStream) throws IOException {
      this.br = new BufferedReader(new InputStreamReader(inputStream));
      this.line = br.readLine();
      
      if (line == null) {
        br = null;
      }
    }
    
    public boolean hasNext() {
      return br != null;
    }
    
    public String next() {
      if (!hasNext()) {
        return "";
      }
      
      StringBuilder buffer = new StringBuilder();
      boolean hasOne = false;
      
      try {
        do {
          line = line.trim();
          
          if (!line.isEmpty()) {
            buffer.append(line + "\r\n");
            
            if (line.endsWith("1-0") || line.endsWith("0-1") || line.endsWith("1/2-1/2") || line.endsWith("*")) {
              hasOne = true;
            }
          }
        } while ((line = br.readLine()) != null && !hasOne);
        
        if (line == null) {
          br.close();
          br = null;
        }        
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      return buffer.toString();
    }
    
    public void remove() {
      next();
    }
  }
  
	private String source;
	
	public PGNSource(String pgn) {
		if (pgn == null) {
			throw new NullPointerException("PGN data is null");
		}
		
		this.source = pgn;
	}
	
	public PGNSource(File file) throws IOException {
		this(new FileInputStream(file));
	}
	
	public PGNSource(URL url) throws IOException {
		this(url.openStream());
	}
	
	public PGNSource(InputStream inputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		StringBuilder buffer = new StringBuilder();
		
		while ((line = br.readLine()) != null) {
			buffer.append(line + "\r\n");
		}
		
		br.close();
		this.source = buffer.toString();
	}
	
	@Override
	public String toString() {
		return source;
	}
	
	public List<PGNGame> listGames() throws PGNParseException, IOException, NullPointerException, MalformedMoveException {
		return PGNParser.parse(source);
	}
	
	public List<PGNGame> listGames(boolean force) throws PGNParseException, IOException, NullPointerException, MalformedMoveException {
		return PGNParser.parse(source, force);
	}
	
}
