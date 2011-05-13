/*
 * Game.java
 * 
 * This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
 * See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
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
package com.supareno.pgnparser.jaxb;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * The {@code Game} class is a object that represents a PGN game with all the attributes (event, site, date, ...)
 * and a List of Hits.
 * <p>
 * For more details about PGN format, check out this link:<br>
 * {@linkplain http://chess.about.com/library/weekly/aa101202a.htm}
 * </p>
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}event"/>
 *         &lt;element ref="{}site"/>
 *         &lt;element ref="{}date"/>
 *         &lt;element ref="{}round"/>
 *         &lt;element ref="{}white"/>
 *         &lt;element ref="{}whiteElo"/>
 *         &lt;element ref="{}black"/>
 *         &lt;element ref="{}blackElo"/>
 *         &lt;element ref="{}result"/>
 *         &lt;element ref="{}eco"/>
 *         &lt;element ref="{}hits"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author supareno
 * @version 1.0
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"event",
		"site",
		"date",
		"round",
		"white",
		"whiteElo",
		"black",
		"blackElo",
		"result",
		"eco",
		"hits"
})
@XmlRootElement(name = "game")
public class Game implements Comparable<Game>, Serializable {

	//
	private static final long serialVersionUID = 8633579268697585350L;

	@XmlElement(required = true, defaultValue = "?")
	protected String event = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String site = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String date = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String round = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String white = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String whiteElo = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String black = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String blackElo = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String result = "?";
	@XmlElement(required = true, defaultValue = "?")
	protected String eco = "?";
	@XmlElement(required = true)
	protected Hits hits;
	
	public String tmp_file;

	/**
	 * Returns the event name of the game.
	 * @return the event name of the game.
	 */
	public String getTmpPath() {
		return tmp_file;
	}
	
	public String getEvent() { return event; }

	/**
	 * Sets the event name of the game.
	 * @param value the event name of the game.
	 */
	public void setEvent(String value) { this.event = value; }

	/**
	 * Returns the site name where the game took place.
	 * @return the site name where the game took place.
	 */
	public String getSite() { return site; }

	/**
	 * Sets the site name where the game took place.
	 * @param value the site name where the game took place.
	 */
	public void setSite(String value) { this.site = value; }

	/**
	 * Returns the date of the game as String format.
	 * <p>
	 * The format of this date is: {@code YYYY.MM.DD}. If one of the element of the date is unknown, it could
	 * be replaced by {@code ??}.
	 * </p>
	 * @return the date of the game as string format.
	 */
	public String getDate() { return date; }

	/**
	 * Sets the date of the game as String format.
	 * <p>
	 * The format of this date is: {@code YYYY.MM.DD}. If one of the element of the date is unknown, it could
	 * be replaced by {@code ??}.
	 * </p>
	 * @param value the date of the game as String format.
	 */
	public void setDate(String value) { this.date = value; }

	/**
	 * Returns the round value of the game.
	 * @return the round value of the game.
	 */
	public String getRound() { return round; }

	/**
	 * Sets the round value of the game.
	 * @param value the round value of the game.
	 */
	public void setRound(String value) { this.round = value; }

	/**
	 * Returns the white player of the game.
	 * @return the white player of the game.
	 */
	public String getWhite() { return white; }

	/**
	 * Sets the white player of the game.
	 * @param value the white player of the game.
	 */
	public void setWhite(String value) { this.white = value; }

	/**
	 * Returns the ELO of the white player.
	 * @return the ELO of the white player.
	 */
	public String getWhiteElo() { return whiteElo; }

	/**
	 * Sets the ELO of the white player.
	 * @param value the ELO of the white player.
	 */
	public void setWhiteElo(String value) { this.whiteElo = value; }

	/**
	 * Returns the black player of the game.
	 * @return the black player of the game.
	 */
	public String getBlack() { return black; }

	/**
	 * Sets the black player of the game.
	 * @param value the black player of the game.
	 */
	public void setBlack(String value) { this.black = value; }

	/**
	 * Returns the ELO of the black player.
	 * @return the ELO of the black player.
	 */
	public String getBlackElo() { return blackElo; }

	/**
	 * Sets the ELO of the black player.
	 * @param value the ELO of the black player.
	 */
	public void setBlackElo(String value) { this.blackElo = value; }

	/**
	 * Returns the result of the game.
	 * <p>
	 * The result of the game could be:
	 * <ul>
	 * 	<li>1-0: White won the game</li>
	 * 	<li>0-1: Black won the game</li>
	 * 	<li>1/2-1/2: Draw game</li>
	 * 	<li>*: On going game</li>
	 * </ul>
	 * </p>
	 * @return the result of the game.
	 */
	public String getResult() { return result; }

	/**
	 * Sets the result of the game.
	 * @param value the result of the game.
	 */
	public void setResult(String value) { this.result = value; }

	/**
	 * Returns the ECO of the game.
	 * @return the ECO of the game.
	 */
	public String getEco() { return eco; }

	/**
	 * Sets the ECO of the game.
	 * @param value the ECO of the game.
	 */
	public void setEco(String value) { this.eco = value; }

	/**
	 * Returns the Hits object of the Game object.
	 * @return the Hits object of the Game object.
	 */
	public Hits getHits() { return hits; }

	/**
	 * Adds a Hits object to the Game object.
	 * <p>
	 * The {@code Hits} object is a simple POJO with a list of {@code Hit}s.
	 * </p>
	 * @param value the Hits object to add to the Game object.
	 */
	public void setHits(Hits value) { this.hits = value; }

	/**
	 * Adds an Hit object to the hits list of the Game object.
	 * @param value the Hit object to add to the Game object.
	 */
	public void addHit(Hit value){
		if(hits == null){
			hits = new Hits();
		}
		this.hits.addHit(value);
	}

	@Override
	public int compareTo(Game obj) { return CompareToBuilder.reflectionCompare(this, obj); }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Game) {
			Game g = (Game)obj;
			List<Hit> hits1 = g.getHits().getHit();
			List<Hit> hits2 = this.getHits().getHit();
			
			if (hits1.size() != hits2.size()) {
				return false;
			}
			for (int i = 0; i < hits1.size(); i ++) {
				if (!hits1.get(i).equals(hits2.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
		//return EqualsBuilder.reflectionEquals(this, obj); 
	}

	@Override
	public int hashCode() {
		String str = "";
		for (Hit h : this.getHits().getHit()) {
			str += h.getContent();
		}
		return str.hashCode();
		//return HashCodeBuilder.reflectionHashCode(this); 
	}

	@Override
	public String toString() { return ToStringBuilder.reflectionToString(this); }
}
