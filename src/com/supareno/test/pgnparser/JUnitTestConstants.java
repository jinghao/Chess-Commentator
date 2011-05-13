/*
 * JUnitTestConstants.java
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

package com.supareno.test.pgnparser;

import com.supareno.pgnparser.jaxb.Game;
import com.supareno.pgnparser.jaxb.Hit;
import com.supareno.pgnparser.jaxb.Hits;

/**
 * The {@code JUnitTestConstants} class
 * @author supareno
 *
 */
public final class JUnitTestConstants {

	/** log4j configuration xml file */
	static final public String LOG4J_XML="src/main/resources/log4j/external_log4j.xml";

	/**  pgn folder */
	static final public String PGN_FOLDER="src/main/resources/pgnfiles/";

	/**  pgn test folder */
	static final public String PGN_TEST_FOLDER="src/test/resources/";

	/**
	 * pgn test file: this file contains the same game as the xml test file
	 */
	static final public String PGN_FILE=PGN_FOLDER+"testFile.pgn";

	/**
	 * xml test file: this file contains the same game as the pgn test file
	 */
	static final public String XML_PGN_FILE="src/main/resources/xml/testFile_2.0.xml";

	/**
	 * REFERENCE_GAME_2_0: this object contains the same game as the pgn and the xml files. This is the reference pgn
	 * game used during all the tests.
	 */
	public static Game REFERENCE_GAME_2_0 = new Game();
	static {
		REFERENCE_GAME_2_0.setEvent("chp");
		REFERENCE_GAME_2_0.setDate("1956.??.??");
		REFERENCE_GAME_2_0.setBlack("Fischer, R.");
		REFERENCE_GAME_2_0.setBlackElo("?");
		REFERENCE_GAME_2_0.setWhite("Byrne, D.");
		REFERENCE_GAME_2_0.setWhiteElo("?");
		REFERENCE_GAME_2_0.setEco("?");
		REFERENCE_GAME_2_0.setRound("?");
		REFERENCE_GAME_2_0.setSite("USA");
		REFERENCE_GAME_2_0.setResult("0-1");
		Hit hit = new Hit();
		hit = new Hit(); hit.setNumber("1"); hit.setContent("Nf3 Nf6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("2"); hit.setContent("c4 g6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("3"); hit.setContent("Nc3 Bg7"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("4"); hit.setContent("d4 O-O"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("5"); hit.setContent("Bf4 d5"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("6"); hit.setContent("Qb3 dxc4"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("7"); hit.setContent("Qxc4 c6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("8"); hit.setContent("e4 Nbd7"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("9"); hit.setContent("Rd1 Nb6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("10"); hit.setContent("Qc5 Bg4"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("11"); hit.setContent("Bg5 Na4"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("12"); hit.setContent("Qa3 Nxc3"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("13"); hit.setContent("bxc3 Nxe4"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("14"); hit.setContent("Bxe7 Qb6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("15"); hit.setContent("Bc4 Nxc3"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("16"); hit.setContent("Bc5 Rfe8+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("17"); hit.setContent("Kf1 Be6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("18"); hit.setContent("Bxb6 Bxc4+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("19"); hit.setContent("Kg1 Ne2+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("20"); hit.setContent("Kf1 Nxd4+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("21"); hit.setContent("Kg1 Ne2+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("22"); hit.setContent("Kf1 Nc3+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("23"); hit.setContent("Kg1 axb6"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("24"); hit.setContent("Qb4 Ra4"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("25"); hit.setContent("Qxb6 Nxd1"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("26"); hit.setContent("h3 Rxa2"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("27"); hit.setContent("Kh2 Nxf2"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("28"); hit.setContent("Re1 Rxe1"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("29"); hit.setContent("Qd8+ Bf8"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("30"); hit.setContent("Nxe1 Bd5"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("31"); hit.setContent("Nf3 Ne4"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("32"); hit.setContent("Qb8 b5"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("33"); hit.setContent("h4 h5"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("34"); hit.setContent("Ne5 Kg7"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("35"); hit.setContent("Kg1 Bc5+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("36"); hit.setContent("Kf1 Ng3+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("37"); hit.setContent("Ke1 Bb4+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("38"); hit.setContent("Kd1 Bb3+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("39"); hit.setContent("Kc1 Ne2+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("40"); hit.setContent("Kb1 Nc3+"); REFERENCE_GAME_2_0.addHit(hit);
		hit = new Hit(); hit.setNumber("41"); hit.setContent("Kc1 Rc2+"); REFERENCE_GAME_2_0.addHit(hit);
	}

	/**
	 * Returns a Game object with predefined parameters.
	 * <p>
	 * The Game is initialized with the following values:
	 * </p>
	 * <pre>
	 *	Game game = new Game();
	 *	game.setEvent("event");
	 *	game.setDate("1975.04.11");
	 *	game.setBlack("black");
	 *	game.setBlackElo("2000");
	 *	game.setWhite("white");
	 *	game.setWhiteElo("2001");
	 *	game.setEco("eco");
	 *	game.setRound("1");
	 *	game.setSite("USA");
	 *	game.setResult("1-0");
	 * </pre>
	 * @return a Game object with predefined parameters.
	 */
	public static Game getFilledGame(){
		Game game = new Game();
		game.setEvent("event");
		game.setDate("1975.04.11");
		game.setBlack("black");
		game.setBlackElo("2000");
		game.setWhite("white");
		game.setWhiteElo("2001");
		game.setEco("eco");
		game.setRound("1");
		game.setSite("USA");
		game.setResult("1-0");
		return game;
	}

	/**
	 * Returns a Hits object that contains two Hit objects.
	 * @return a Hits object that contains two Hit objects.
	 */
	public static Hits getFilledHits(){
		Hits hits = new Hits();
		Hit hit1 = new Hit();
		hit1.setNumber("1");
		hit1.setContent("e4 e5");
		hits.addHit(hit1);
		Hit hit2 = new Hit();
		hit2.setNumber("2");
		hit2.setContent("Nf3 Nf6");
		hits.addHit(hit2);
		return hits;
	}
}
