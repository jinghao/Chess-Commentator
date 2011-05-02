package pgnparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class PGNSourceIterator implements Iterator {
  BufferedReader br;
  String line;
  
  public PGNSourceIterator(File file) throws IOException {
    this(new FileInputStream(file));
  }
  
  public PGNSourceIterator(InputStream inputStream) throws IOException {
    this.br = new BufferedReader(new InputStreamReader(inputStream));
    this.line = br.readLine();
  }
  
  public boolean hasNext() {
    return line != null;
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
      e.printStackTrace();
    }
    
    return buffer.toString();
  }
  
  public void remove() {
    next();
  }
}