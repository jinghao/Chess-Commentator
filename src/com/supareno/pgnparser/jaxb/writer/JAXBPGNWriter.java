/*
 * JAXBPGNWriter.java
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
package com.supareno.pgnparser.jaxb.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.supareno.pgnparser.AbstractPGNWriter;
import com.supareno.pgnparser.Writer;
import com.supareno.pgnparser.jaxb.Game;
import com.supareno.pgnparser.jaxb.Games;

/**
 * The {@code JAXBPGNWriter} is a concrete JAXB implementation of the {@link Writer}.
 * @author reno
 * @version 1.0
 */
public class JAXBPGNWriter extends AbstractPGNWriter {

	/* (non-Javadoc)
	 * @see com.supareno.pgnparser.Writer#getExtension()
	 */
	@Override
	public String getExtension() { return "xml"; }

	/* (non-Javadoc)
	 * @see com.supareno.pgnparser.Writer#writePGNGame(com.supareno.pgnparser.jaxb.Game)
	 */
	@Override
	public boolean writePGNGame(Game game) {
		if(game == null) {
			return false;
		}
		Games games = new Games();
		games.addGame(game);
		return writePGNGames(games);
	}

	/* (non-Javadoc)
	 * @see com.supareno.pgnparser.Writer#writePGNGames(com.supareno.pgnparser.jaxb.Games)
	 */
	@Override
	public boolean writePGNGames(Games games) {
		if(games == null) {
			return false;
		}
		boolean result = false;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Games.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(games, new FileOutputStream(getFileName() + "." + getExtension()));
			result = true;
		} catch (JAXBException e) {
			log("JAXBException in writePGNGames(Games)", e);
		} catch (FileNotFoundException e) {
			log("FileNotFoundException in writePGNGames(Games)", e);
		}
		return result;
	}

}
