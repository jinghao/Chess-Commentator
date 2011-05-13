import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import com.supareno.pgnparser.PGNParser;
import com.supareno.pgnparser.jaxb.Game;
import com.supareno.pgnparser.jaxb.Games;
import com.supareno.pgnparser.jaxb.Hit;


public class Deduplicator {

	static class GamePair {
		public GamePair(String filename, int hash) {
			this.filename = filename;
			this.hash = hash;
		}
		public String filename;
		public int hash;
		public int hashCode() {
			return hash;
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof GamePair) {
				GamePair p = (GamePair)obj;
				Games gs1 = PGNParser.getGames(p.filename);
				Games gs2 = PGNParser.getGames(this.filename);
				return gs1.equals(gs2);
			}
			return false;
		}
	}
  /**
   * @param args
   * @throws NoSuchAlgorithmException 
   * @throws IOException 
   * @throws MalformedMoveException 
   * @throws PGNParseException 
   * @throws NullPointerException 
   */
  public static void main(String[] args) {
	
    HashSet<GamePair> doneGames = new HashSet<GamePair>();

    String initialFolder = "C:/Users/Darren/chess/chessok/";
    //"C:/Archives/Chess/Games";
    
    Stack<String> stack = new Stack<String>();
    
    stack.add(initialFolder);
    long numGames = 0, numFiles = 0;
    PGNParser parser = new PGNParser(true);
    
    while (!stack.isEmpty()) {
      String filename = stack.pop();
      File f = new File(filename);
      
      
      if (f.isDirectory()) {
        for (String file : f.list()) {
          stack.add(filename + "/" + file);
        }
      } else if (filename.toLowerCase().indexOf(".pgn") > 0) {
        int localGames = 0;
          System.out.printf("File #%d: %s\n", ++numFiles, filename);
          
          parser.smartParseFile(filename);
          
          while (parser.hasNext()) {
        	  numGames ++;
        	  Game g = parser.nextGame();
        	  GamePair p = new GamePair(g.getTmpPath(), g.hashCode());
        	  
        	  doneGames.add(p);
          }
      }
    }
    System.out.println("Number of games processed: " + numGames);
    System.out.println("Number of unique games: " + doneGames.size());
  }
}
