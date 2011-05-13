/*
 * PGNParser.java
 * 
 * Copyright 2008-2010 supareno
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supareno.pgnparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;

import com.supareno.pgnparser.filters.PGNFileFilter;
import com.supareno.pgnparser.jaxb.Game;
import com.supareno.pgnparser.jaxb.Games;
import com.supareno.pgnparser.jaxb.Hit;
import com.supareno.pgnparser.jaxb.Hits;

/**
 * The {@code PGNParser} class is the class used to parse the PGN files.
 * @author supareno - Frank Breuel
 * @version 2.2
 */
public class PGNParser extends AbstractPGNParser {

	private final String TMP_DIRECTORY = "tmp/";
	
	// game separator used in the first parse.
	private static final String GAME_SEPARATOR="###";

	/** The PGNType of the Parser: set to {@link PGNType#PGN}. */
	public static final PGNType TYPE = PGNType.PGN;

	/**
	 * The String representation of the pattern used to match the attributes of
	 * the PGN file. The value of the pattern is set to {@literal \\[[^\\[]*\\]}
	 * @see #ATTRIBUTES_PATTERN
	 */
	public static final String ATTRIBUTES_STRING_PATTERN="\\[[^\\[]*\\]";

	/**
	 * The String representation of the pattern used to check a number validity.<br />
	 * The value is set to {@code [0-9]+}.
	 */
	public static final String NUMBER_VALIDITY_STRING_PATTERN="[0-9]+";

	/**
	 * The String representation of the pattern used to match a single hit.<br />
	 * This pattern matches hit value like {@code e5 e7}.<br />
	 * The value is set to as follow (divided in multilines to a better comprehension):
	 * <br />
	 * <pre>
	 * ([a-zA-Z]+[1-8]{1}[=][A-Z]{1}[\\+|#]?|           // promotion pattern
	 * [a-zA-Z]{1}[1-8]{1}[\\+]?|                       // one letter / one number pattern : simple hit
	 * [a-zA-Z]{1}[1-8]?[a-zA-Z]{1,3}?[1-8]{1}[\\+|#]?| // complex hit
	 * [O]+[\\-][O]+[\\-][O]+[\\+|#]?|                  // queenside castling hit
	 * [O]+[\\-][O]+[\\+|#]?|                           // kingside castling hit
	 * [a-z]{1}[\\-][a-z]{1}[\\+|#]?)                   // en passant hit
	 * </pre>
	 * @see #SINGLE_HIT_PATTERN
	 */
	public static final String SINGLE_HIT_STRING_PATTERN =
		"([a-zA-Z]+[1-8]{1}[=][A-Z]{1}[\\+|#]?|" + 					// promotion pattern
		"[a-zA-Z]{1}[1-8]{1}[\\+]?|" +								// one letter / one number pattern : simple hit
		"[a-zA-Z]{1}[1-8]?[a-zA-Z]{1,3}?[1-8]{1}[\\+|#]?|" +		// complex hit
		"[O]+[\\-][O]+[\\-][O]+[\\+|#]?|" +							// queenside castling hit
		"[O]+[\\-][O]+[\\+|#]?|" +									// kingside castling hit
		"[a-z]{1}[\\-][a-z]{1}[\\+|#]?)";							// en passant hit

	public static final String COMMENT_STRING_PATTERN = "(\\{[\\s\\S]*\\})?";
	public static final String COMMENT_REPLACEMENT_STRING_PATTERN = "(@[\\d]+)?";
	
	public static final String END_STRING_PATTERN = "[\\d]+[\\-][\\d]+";
	
	/**
	 * The String representation of the pattern used to match the hits of
	 * the PGN file.<br>
	 * This pattern is composed in two parts: <br>
	 * The first one is for the hit number<br>
	 * {@code #NUMBER_VALIDITY_STRING_PATTERN} concatened with {@code [.][ ]?}<br>
	 * The second one is for the hit (composed in two same part seperate with a space)<br>
	 * {@code #SINGLE_HIT_STRING_PATTERN}
	 * 
	 * @see #HITS_PATTERN
	 */
	public static final String HITS_STRING_PATTERN1 =
		NUMBER_VALIDITY_STRING_PATTERN +
		"[.]" +
		"[ ]?" +
		COMMENT_REPLACEMENT_STRING_PATTERN +
		"[ ]?" +
		SINGLE_HIT_STRING_PATTERN +
		"[ ]?" +
		COMMENT_REPLACEMENT_STRING_PATTERN +
		"[ ]?" +
		SINGLE_HIT_STRING_PATTERN + "[ ]?" + 
		COMMENT_REPLACEMENT_STRING_PATTERN + "[ ]?" +
		"((" + NUMBER_VALIDITY_STRING_PATTERN + "[.])|(" + END_STRING_PATTERN + "))";
	
	public static final String HITS_STRING_PATTERN =
		NUMBER_VALIDITY_STRING_PATTERN +
		"[.]" +
		"[ ]?" +
		SINGLE_HIT_STRING_PATTERN +
		"[ ]?" +
		SINGLE_HIT_STRING_PATTERN + "?";

	/**
	 * Pattern used to parse the attributes of the PGN file. It is compiled
	 * with the {@link PGNParser#ATTRIBUTES_STRING_PATTERN} pattern.
	 */
	public static final Pattern ATTRIBUTES_PATTERN=Pattern.compile(ATTRIBUTES_STRING_PATTERN);

	/**
	 * Pattern used to parse the hits of the PGN file. It is compiled
	 * with the {@link PGNParser#HITS_STRING_PATTERN} pattern.
	 */
	public static final Pattern HITS_PATTERN=Pattern.compile(HITS_STRING_PATTERN1);

	public static final Pattern COMMENT_PATTERN = Pattern.compile(COMMENT_STRING_PATTERN);
	
	public static final Pattern COMMENT_REPLACEMENT_PATTERN = Pattern.compile(COMMENT_REPLACEMENT_STRING_PATTERN);
	/**
	 * Pattern used to check the validity of a Number. It is compiled with the
	 * {@link PGNParser#NUMBER_VALIDITY_STRING_PATTERN} pattern.
	 */
	public static final Pattern NUMBER_VALIDITY_PATTERN=Pattern.compile(NUMBER_VALIDITY_STRING_PATTERN);

	/**
	 * Pattern used to parse one hit. It is compiled
	 * with the {@link PGNParser#SINGLE_HIT_STRING_PATTERN} pattern.
	 */
	public static final Pattern SINGLE_HIT_PATTERN = Pattern.compile(SINGLE_HIT_STRING_PATTERN);

	/** Empty constructor. */
	public PGNParser(){
		this(DEFAULT_LOGGER_LEVEL);
	}
	
	long numGames = 0;
	String directory;
	File tmp_folder;
	Queue<String> tmp_files;
	public PGNParser(boolean flag) {
		this(DEFAULT_LOGGER_LEVEL);
		tmp_folder = new File(TMP_DIRECTORY);
		if (!tmp_folder.mkdir()) {
			try {
				FileUtils.deleteDirectory(tmp_folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			tmp_folder.mkdir();
		}
		tmp_files = new LinkedList<String>();	
	}

	/**
	 * Creates a PGNParser with a Level for the Logger. This Logger will log nothing
	 * if the logger xml configuration file is not set.
	 * 
	 * @param loggerLevel the {@code Level} of the Logger. It could be:<br>
	 * <ul><li>{@link Level#ALL}</li>
	 * <li>{@link Level#DEBUG}</li>
	 * <li>{@link Level#ERROR}</li>
	 * <li>{@link Level#WARN}</li>
	 * <li>{@link Level#FATAL}</li>
	 * <li>{@link Level#INFO}</li>
	 * <li>{@link Level#OFF}</li></ul>
	 */
	public PGNParser(Level loggerLevel){
		this(loggerLevel,"");
	}

	/**
	 * Creates a PGNParser with a Level for the Logger and a configuration file
	 * for Log4j.
	 * 
	 * @param loggerLevel the {@code Level} of the Logger. It could be:<br>
	 * <ul><li>{@link Level#ALL}</li>
	 * <li>{@link Level#DEBUG}</li>
	 * <li>{@link Level#ERROR}</li>
	 * <li>{@link Level#WARN}</li>
	 * <li>{@link Level#FATAL}</li>
	 * <li>{@link Level#INFO}</li>
	 * <li>{@link Level#OFF}</li></ul>
	 * @param log4jXmlConfigFile the xml log4j configuration file
	 */
	public PGNParser(Level loggerLevel, String log4jXmlConfigFile){
		setLoggerLevel(loggerLevel);
		setLoggerConfiguratorFile(log4jXmlConfigFile);
	}

	/*
	 * (non-Javadoc)
	 * @see com.supareno.pgnparser.Parser#getExtension()
	 */
	public String getExtension(){ return TYPE.getExtension(); }

	/**
	 * Returns a List of List of PGNGames contained in the {@code folder}.
	 * <p>
	 * It will only treat the files ending with {@code PGNType.PGN.getExtension()}.
	 * </p>
	 * @param folder the folder that contains files to parse.
	 * @return a List of List of PGNGames contained in the {@code folder}.
	 */
	public List<Games> parseFolder(String folder){
		List<Games> gamesList=null;
		File file=new File(folder);
		if(file.exists()){// if the folder exists, we list it and parse each file
			File[] files=file.listFiles(new PGNFileFilter());
			if(files!=null && files.length>0){
				gamesList=new ArrayList<Games>();
				for(File f:files){
					gamesList.add(parseFile(f));
				}
			}else{
				log("empty folder :-(");
			}
		}
		return gamesList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.supareno.pgnparser.Parser#parseFile(java.lang.String)
	 */
	public Games parseFile(String file){
		return parseFile(new File(file));
	}

	/*
	 * (non-Javadoc)
	 * @see com.supareno.pgnparser.Parser#parseFile(java.io.File)
	 */
	public Games parseFile(File file){
		try {
			return parseFile(new FileReader(file));
		} catch (FileNotFoundException e) {
			log("FileNotFoundException: ",e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.supareno.pgnparser.Parser#parseURL(java.lang.String)
	 */
	public Games parseURL(String url){
		String games=null;
		URL urltmp=null;
		try {
			urltmp=new URL(url);
			games=formatPGNFile(new InputStreamReader(urltmp.openStream()));
			if(games!=null && games.length()>0){
				return parseContents(games);
			}
		} catch (MalformedURLException e) {
			log("MalformedURLException: ",e);
		} catch (IOException e) {
			log("IOException: ",e);
		}
		return null;
	}

	/**
	 * This method parses a PGN file and formats it to be easely parseable by games.
	 * @param reader the current Reader.
	 * @return a String representation of the content of the file.
	 */	
	private String formatPGNFile(Reader reader){
		StringBuffer contents = new StringBuffer();
		String lastLine="no";
		BufferedReader input = null;
		try {
			input = new BufferedReader(reader);
			String line = null;
			while (( line = input.readLine() ) != null){
				if(line.startsWith("[") && !lastLine.endsWith("]")){
					contents.append(GAME_SEPARATOR);
				}
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
				lastLine=line.trim();// adding trim() to remove with space
			}
		} catch (FileNotFoundException ex) {
			log("error in formatting the PGN file",ex);
		} catch (IOException ex){
			log("error in formatting the PGN file",ex);
		} finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying Reader
					input.close();
				}
			} catch (IOException ex) {
				log("error in formatting the PGN file",ex);
			}
		}
		return contents.toString();
	}
	
	private String writeToTmpFile(String t) {
		File f = new File(tmp_folder, numGames + ".pgn");
		FileWriter fstream;
		try {
			fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(t);
			out.close();
			fstream.close();
			//System.out.println("Wrote to tmp file: " + f.getPath());
			numGames ++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return f.getPath();
	}
	
	private List<String> formatPGNToTmpFiles(Reader reader){
		StringBuffer contents = new StringBuffer();
		String lastLine="no";
		BufferedReader input = null;
		List<String> files = new ArrayList<String>();
		try {
			input = new BufferedReader(reader);
			String line = null;
			while (( line = input.readLine() ) != null){
				if(line.startsWith("[") && !lastLine.endsWith("]") && !lastLine.equals("no")){
					files.add(writeToTmpFile(contents.toString()));
					contents = new StringBuffer();
				}
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
				lastLine=line.trim();// adding trim() to remove with space
			}
		} catch (FileNotFoundException ex) {
			log("error in formatting the PGN file",ex);
		} catch (IOException ex){
			log("error in formatting the PGN file",ex);
		} finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying Reader
					input.close();
				}
			} catch (IOException ex) {
				log("error in formatting the PGN file",ex);
			}
		}
		return files;
	}

	/**
	 * Parses a PGN content in two steps. First step it parses the attributes and the second step
	 * is to parse the hits. It returns a List of PGNGames.
	 * @param content the PGN game String representation to parse.
	 * @return a List of PGNGames.
	 */
	private Games parseContents(String content) {
		Games games=new Games();
		String[] gamesString=content.split(GAME_SEPARATOR);
		for(String s:gamesString){
			if ( s != null && s.trim().length() > 0) {
				String attributes = s.substring(0, s.lastIndexOf("]")+1);
				String hits=s.substring(s.lastIndexOf("]")+1, s.length()).trim();
				if(attributes.length()>0 && hits.length()>0){
					Game pgn=treatePGNString(attributes, hits);
					if(pgn!=null){
						games.addGame(pgn);
					}
				}
			}
		}
		return games;
	}

	/**
	 * Parses the {@code attributes} and the {@code hits} and returns a {@code PGNGame} filled with the datas.
	 * @param attributes the attributes of the PGN game.
	 * @param hits the hits of the PGN game.
	 * @return a PGNGame filled with the datas.
	 */
	private Game treatePGNString(String attributes, String hits){
		Game p=new Game();
		parseAttributes(p, attributes);
		parseHits(p, hits);
		return p;
	}

	/**
	 * Parses the PGN attributes. These attributes looks like this:<br>
	 * <pre>
	 * [Event "event_name"]
	 * [Site "site_name"]
	 * [Date "date"]
	 * [Round "round_number"]
	 * [White "player_name"]
	 * [Black "player_name"]
	 * [Result "result"]
	 * [WhiteElo "elo_number"]
	 * [BlackElo "elo_number"]
	 * [ECO "eco"]
	 * </pre>
	 * It uses the {@link PGNParser#ATTRIBUTES_PATTERN} Pattern to parse the attributes.
	 * 
	 * @param pgn the PGNGame to fill.
	 * @param attributes the String representation of the attributes to parse.
	 * 
	 * @return the PGNGame filled with the attributes found.
	 */
	private Game parseAttributes(Game pgn, String attributes){
		if(attributes == null || attributes.length() < 1) {
			return pgn;
		}
		Matcher matcher = ATTRIBUTES_PATTERN.matcher(attributes);
		while(matcher.find()){
			String[] str=matcher.group().split("\"*\"");
			String s1=str[0].substring(1, str[0].length()).trim();
			String s2=str[1].trim();
			setPGNGameAttributeAndValue(pgn, s1, s2);
		}
		return pgn;
	}

	/**
	 * Sets the Game attribute value according to the attribute.
	 * @param pgnGame the current Game.
	 * @param attribute the Game attribute.
	 * @param attrValue the attribute value.
	 */
	private void setPGNGameAttributeAndValue(Game pgnGame, String attribute,
			String attrValue) {
		if (attrValue == null || attrValue.length() == 0){
			attrValue = "?";
		}
		if(attribute.equalsIgnoreCase(PGNParserConstants.EVENT_ATTR)){
			pgnGame.setEvent(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.SITE_ATTR)) {
			pgnGame.setSite(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.DATE_ATTR)) {
			pgnGame.setDate(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.ROUND_ATTR)) {
			pgnGame.setRound(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.WHITE_ATTR)) {
			pgnGame.setWhite(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.BLACK_ATTR)) {
			pgnGame.setBlack(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.RESULT_ATTR)) {
			pgnGame.setResult(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.WHITE_ELO_ATTR)) {
			pgnGame.setWhiteElo(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.BLACK_ELO_ATTR)) {
			pgnGame.setBlackElo(attrValue);
		} else if(attribute.equalsIgnoreCase(PGNParserConstants.ECO_ATTR)) {
			pgnGame.setEco(attrValue);
		}
	}

	/**
	 * Parses the PGN hits. Uses the {@link #HITS_PATTERN} Pattern to parse the hits.
	 * @param pgn the {@code PGNGame} to fill
	 * @param hits the String representation of the hits to parse
	 * @return the {@code PGNGame} filled with the attributes found
	 * @see PGNParser#HITS_PATTERN
	 */
	private Game parseHits(Game pgn, String hits){
		StringBuilder newHit=new StringBuilder();
		String[] strings = hits.split(" \\n ");
		for(String s : strings){
			newHit.append(s + " ");
		}
		Hits list = new Hits();
		String newHits = newHit.toString();
		Matcher matcher = COMMENT_PATTERN.matcher(newHits);
		
		//System.out.println("newHits: " + newHits);
		
		Map<Integer, String> comments = new HashMap<Integer, String>();
		int comment_id = 0;
		while (matcher.find()) {
			String matcherGroup = matcher.group();
			if (matcherGroup.length() < 1)
				continue;
			String cleanComment = matcherGroup.replace("{", "").replace("}", "").trim();
			//System.out.println("matcherGroup: " + matcherGroup);
			//System.out.println("cleanComment: " + cleanComment);
			comments.put(comment_id, cleanComment);
			newHits = newHits.replace(matcherGroup, "@"+comment_id);
			comment_id ++;
		}
		
		//System.out.println("newHits: " + newHits);
		
		matcher = HITS_PATTERN.matcher(newHits);
		//System.out.println("HIT pattern: " + HITS_STRING_PATTERN1);
		while (matcher.find()) {
			String matchStr = matcher.group();
			//System.out.println("matchStr: " +  matchStr);
			String[] str = matchStr.split("\\.");
			if(str.length < 1){
				continue;
			}
			Hit hit = new Hit();
			hit.setNumber(str[0]);
			
			//System.out.println("str1: " + str[1]);
			
			String[] items = str[1].split("\\s");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < items.length; i ++) {
				if (COMMENT_REPLACEMENT_PATTERN.matcher(items[i]).matches()) {
					hit.addComment(comments.get(new Integer(items[i].substring(1))));
				} else {
					sb.append(items[i]).append(" ");
				}
			}
			
			hit.setContent(normalizeHit(sb.toString()));
			/*
			System.out.println("string in hit: " + hit.getContent());
			for (String c : hit.getComments()) {
				System.out.println("Comment: " + c);
			}
			*/
			list.addHit(hit);
		}
		pgn.setHits(list);
		return pgn;
	}

	/**
	 * Return the hit normalized.
	 * @param hitToNormalize the to normalized
	 * @return the hit normalized.
	 */
	private String normalizeHit(final String hitToNormalize) {
		StringBuilder sb = new StringBuilder();
		String[] hitSplitted = hitToNormalize.split(" ");
		for ( String str : hitSplitted ) {
			if ( str.trim().length() > 0 ) {
				if ( SINGLE_HIT_PATTERN.matcher(str).matches() ) {
					sb.append(str)
					.append(" ");
				}
			}
		}
		return sb.toString().trim();
	}

	/*
	 * (non-Javadoc)
	 * @see com.supareno.pgnparser.Parser#parseURL(java.net.URL)
	 */
	public Games parseURL(URL url) {
		Games games = null;
		try {
			games = parseFile(new InputStreamReader(url.openStream()));
		} catch (IOException e) {
			log("error in parseURL ", e);
		}
		return games;
	}

	/*
	 * (non-Javadoc)
	 * @see com.supareno.pgnparser.Parser#parseFile(java.io.Reader)
	 */
	public Games parseFile(Reader reader) {
		String games=null;
		games = formatPGNFile(reader);
		if(games!=null && games.length()>0){
			return parseContents(games);
		}
		return null;
	}
	
	public void smartParseFile(String filename) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(filename));
			tmp_files.addAll(formatPGNToTmpFiles(reader));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean hasNext() {
		return tmp_files.peek() != null;
	}
	
	public static Games getGames(String filename) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			while (( line = reader.readLine() ) != null){
				sb.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new PGNParser().parseContents(sb.toString());
	}
	public Game nextGame() {
		String filename = tmp_files.poll();
		if (filename == null)
			return null;
		Games gs = getGames(filename);
		if (gs.getGame().size() != 1) {
			System.out.println("ERROR!!!!!!! size: " + gs.getGame().size() + " in file: " + filename);
			return null;
		}
		Game g = gs.getGame().get(0);
		g.tmp_file = filename;
		return g;
	}

}
