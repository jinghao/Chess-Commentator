package edu.berkeley.nlp.chess.pgn;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import pgnparse.MalformedMoveException;
import pgnparse.PGNGame;
import pgnparse.PGNParseException;
import pgnparse.PGNParser;
import pgnparse.PGNSource;
import pgnparse.PGNSourceIterator;



public class Deduplicator {

  /**
   * @param args
   * @throws NoSuchAlgorithmException 
   * @throws IOException 
   * @throws MalformedMoveException 
   * @throws PGNParseException 
   * @throws NullPointerException 
   */
  public static void main(String[] args) throws NoSuchAlgorithmException, 
  IOException {
    HashSet<String> strings = new HashSet<String>();

    String initialFolder = "C:/Archives/Chess/Games";
    
    Stack<String> stack = new Stack<String>();
    
    stack.add(initialFolder);
    long numGames = 0, numDupes = 0, numFiles = 0;
    
    while (!stack.isEmpty()) {
      String filename = stack.pop();
      File f = new File(filename);
      
      if (f.isDirectory()) {
        for (String file : f.list()) {
          stack.add(filename + "/" + file);
        }
      } else if (filename.toLowerCase().indexOf(".pgn") > 0) {
        int localGames = 0;
        try {
          System.out.printf("File #%d\n", ++numFiles);
          PGNSourceIterator source = new PGNSourceIterator(f);
          
          while (source.hasNext()) {
            PGNGame game = PGNParser.parsePGNGame(source.next());
            
            ++numGames;
            ++localGames;
            
            if (!strings.add(game.sha1())) {
              ++numDupes;
              System.out.printf("Dupe %s. %d/%d (%f)\n", f.getAbsolutePath(), numDupes, numGames, (double)numDupes/numGames);
            }
          }
        } catch (PGNParseException e) {
          System.err.println("WTF exception PGNParseException " + f.getAbsolutePath() + " after " + localGames + " games");
        } catch (MalformedMoveException e) {
          System.err.println("WTF MalformedMoveException " + f.getAbsolutePath());
        } catch (NullPointerException e) {
          System.err.println("WTF NullPointedException " + f.getAbsolutePath());
        } catch (OutOfMemoryError e) {
          System.err.println("WTF OutOfMemoryError " + f.getAbsolutePath());
          System.exit(0);
        }
      }
    }
  }

}
