import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import com.codethesis.pgnparse.MalformedMoveException;
import com.codethesis.pgnparse.PGNGame;
import com.codethesis.pgnparse.PGNParseException;
import com.codethesis.pgnparse.PGNParser;
import com.codethesis.pgnparse.PGNSource;


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
    // TODO Auto-generated method stub
    HashSet<String> strings = new HashSet<String>();

    String initialFolder = "C:/Archives/Chess/Games";
    
    Stack<String> stack = new Stack<String>();
    
    stack.add(initialFolder);
    long numGames = 0, numDupes = 0;
    
    while (!stack.isEmpty()) {
      System.out.printf("stack.size = %d, strings.size = %d\n", stack.size(), strings.size());
      String filename = stack.pop();
      File f = new File(filename);
      
      if (f.isDirectory()) {
        for (String file : f.list()) {
          stack.add(filename + "/" + file);
        }
      } else if (filename.toLowerCase().indexOf(".pgn") > 0) {
        // System.out.println("File " + f.getAbsolutePath());
        
        try {   
          PGNSource.PGNSourceIterator source = new PGNSource.PGNSourceIterator(f);
          
          while (source.hasNext()) {
            PGNGame game = PGNParser.parsePGNGame(source.next());
            
            if (numGames % 100 == 0) {
              System.out.println("Game #" + numGames);
            }
            
            ++numGames;
            
            if (!strings.add(game.sha1())) {
              ++numDupes;
              System.out.printf("Dupe %s. %d/%d\n", f.getAbsolutePath(), numDupes, numGames);
            }
          }
        } catch (PGNParseException e) {
          System.err.println("WTF exception PGNParseException " + f.getAbsolutePath());
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
