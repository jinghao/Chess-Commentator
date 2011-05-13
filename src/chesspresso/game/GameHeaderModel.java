/*
 * Copyright (C) Bernhard Seybold. All rights reserved.
 *
 * This software is published under the terms of the LGPL Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt
 * file.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *
 * $Id: GameHeaderModel.java,v 1.1 2002/12/08 13:27:34 BerniMan Exp $
 */

package chesspresso.game;

import chesspresso.*;
import chesspresso.pgn.*;
import java.util.*;
import java.io.*;


/**
 *
 * @author  Bernhard Seybold
 * @version $Revision: 1.1 $
 */
public class GameHeaderModel
{
    
    //TODO store standard tags in variables, not array of string (eg elo as short)
    //     check tagValue for consistency, throw IllegalTagValue if wrong
    //     in pgnreader, issue warning if value is incorrect
    //TODO fen as standard tag, most probably not
    
    private static final int
        INDEX_EVENT = 0,
        INDEX_SITE = 1,
        INDEX_DATE = 2,
        INDEX_ROUND = 3,
        INDEX_WHITE = 4,
        INDEX_BLACK = 5,
        INDEX_RESULT = 6,
        INDEX_WHITE_ELO = 7,
        INDEX_BLACK_ELO = 8,
        INDEX_EVENT_DATE = 9,
        INDEX_ECO = 10,
        NUM_OF_STANDARD_TAGS = 11;
    
    private static final String[] TAG_NAMES =
        {PGN.TAG_EVENT, PGN.TAG_SITE, PGN.TAG_DATE, PGN.TAG_ROUND,
         PGN.TAG_WHITE, PGN.TAG_BLACK, PGN.TAG_RESULT,
         PGN.TAG_WHITE_ELO, PGN.TAG_BLACK_ELO, PGN.TAG_EVENT_DATE, PGN.TAG_ECO};
    
    public static final int
        MODE_SEVEN_TAG_ROASTER = 0,  // need to be consecutive!
        MODE_STANDARD_TAGS     = 1,
        MODE_ALL_TAGS          = 2;
    
    /*================================================================================*/
    
    private String[] m_standardTags;
    private LinkedList m_otherTags;
    private LinkedList m_otherTagValues;
    private long m_long;
    
    /*================================================================================*/
    
    public GameHeaderModel()
    {
        m_standardTags = new String[NUM_OF_STANDARD_TAGS];
        m_otherTags = null;
        m_long = -1;
    }
    
    public GameHeaderModel(DataInput in, int mode) throws IOException
    {
        m_standardTags = new String[NUM_OF_STANDARD_TAGS];
        m_otherTags = null;
        m_long = -1;
        load(in, mode);
    }
    
    /*================================================================================*/
    // use to store game position in file
    
    public long getLong() {return m_long;}
    public void setLong(long l) {m_long = l;}
    
    /*================================================================================*/
    
    private int getStandardTagIndex(String tagName)
    {
        for (int i = 0; i < NUM_OF_STANDARD_TAGS; i++) {
            if (TAG_NAMES[i].equals(tagName)) return i;
        }
        return -1;
        
//        if      (PGN.TAG_EVENT.equals(tagName))      return INDEX_EVENT;
//        else if (PGN.TAG_SITE.equals(tagName))       return INDEX_SITE;
//        else if (PGN.TAG_DATE.equals(tagName))       return INDEX_DATE;
//        else if (PGN.TAG_ROUND.equals(tagName))      return INDEX_ROUND;
//        else if (PGN.TAG_WHITE.equals(tagName))      return INDEX_WHITE;
//        else if (PGN.TAG_BLACK.equals(tagName))      return INDEX_BLACK;
//        else if (PGN.TAG_RESULT.equals(tagName))     return INDEX_RESULT;
//        else if (PGN.TAG_WHITE_ELO.equals(tagName))  return INDEX_WHITE_ELO;
//        else if (PGN.TAG_BLACK_ELO.equals(tagName))  return INDEX_BLACK_ELO;
//        else if (PGN.TAG_EVENT_DATE.equals(tagName)) return INDEX_EVENT_DATE;
//        else if (PGN.TAG_ECO.equals(tagName))        return INDEX_ECO;
//        else                                           return -1;
    }
    
    public String getTag(String tagName)
    {
        int index = getStandardTagIndex(tagName);
        if (index != -1) {
            return m_standardTags[index];
        } else if (m_otherTags != null) {
            index = m_otherTags.indexOf(tagName);
            return (index == -1 ? null : (String)m_otherTagValues.get(index));
//            return (String)m_otherTags.get(tagName);
        } else {
            return null;
        }
    }
    
    public void setTag(String tagName, String tagValue)
    {
        int index = getStandardTagIndex(tagName);
        if (index != -1) {
            m_standardTags[index] = tagValue;
        } else if (!"PlyCount".equals(tagName)) {
//            System.out.println(tagName + "=" + tagValue);
            if (m_otherTags == null) {
                m_otherTags = new LinkedList(); m_otherTagValues = new LinkedList();
            }
            index = m_otherTags.indexOf(tagName);
            if (index == -1) {
                m_otherTags.addLast(tagName); m_otherTagValues.addLast(tagValue);  // append
            } else {
                m_otherTagValues.set(index, tagValue);  // replace
            }
        }
    }
    
    public String[] getTags()
    {
        int numOfTags = (m_otherTags == null ? 0 : m_otherTags.size());
        for (int i = 0; i < NUM_OF_STANDARD_TAGS; i++) if (m_standardTags[i] != null) numOfTags++;
        
        String[] tags = new String[numOfTags];
        int index = 0;
        for (int i = 0; i < NUM_OF_STANDARD_TAGS; i++) {
            if (m_standardTags[i] != null)
                tags[index++] = TAG_NAMES[i];
        }
        if (m_otherTags != null) {
            for (Iterator it = m_otherTags.iterator(); it.hasNext(); ) {
                tags[index++] = (String)it.next();
            }
        }
        return tags;
    }
    
    /*================================================================================*/
    // convenience methods for tags
    
    public String getEvent()        {return m_standardTags[INDEX_EVENT];}
    public String getSite()         {return m_standardTags[INDEX_SITE];}
    public String getDate()         {return m_standardTags[INDEX_DATE];}
    public String getRound()        {return m_standardTags[INDEX_ROUND];}
    public String getWhite()        {return m_standardTags[INDEX_WHITE];}
    public String getBlack()        {return m_standardTags[INDEX_BLACK];}
    public String getResultStr()    {return m_standardTags[INDEX_RESULT];}
    public String getWhiteEloStr()  {return m_standardTags[INDEX_WHITE_ELO];}
    public String getBlackEloStr()  {return m_standardTags[INDEX_BLACK_ELO];}
    public String getEventDate()    {return m_standardTags[INDEX_EVENT_DATE];}
    public String getECO()          {return m_standardTags[INDEX_ECO];}
    
    public int getResult()
    {
        // TODO combine with PGNReader.isResultString
        String result = getResultStr();
        if      ("1-0".equals(result))     return Chess.RES_WHITE_WINS;
        else if ("0-1".equals(result))     return Chess.RES_BLACK_WINS;
        else if ("1/2-1/2".equals(result)) return Chess.RES_DRAW;
        else if ("*".equals(result))       return Chess.RES_NOT_FINISHED;
        else                               return Chess.NO_RES;
    }
    
    public int getWhiteElo()
    {
        try {
            String whiteElo = getWhiteEloStr();
            if (whiteElo == null) return 0;  // =====>
            return Integer.parseInt(whiteElo);
        } catch (NumberFormatException ex) {
            return 0;  // =====>
        }
    }
    
    public int getBlackElo()
    {
        try {
            String blackElo = getBlackEloStr();
            if (blackElo == null) return 0;  // =====>
            return Integer.parseInt(blackElo);
        } catch (NumberFormatException ex) {
            return 0;  // =====>
        }
    }
        
    /*================================================================================*/
    
    private String readUTFNonNull(DataInput in) throws IOException
    {
        String s = in.readUTF();
        return s.equals("") ? null : s;
    }
    
    public void load(DataInput in, int mode) throws IOException
    {
        setTag(PGN.TAG_EVENT, readUTFNonNull(in));
        setTag(PGN.TAG_SITE, readUTFNonNull(in));
        setTag(PGN.TAG_DATE, readUTFNonNull(in));
        setTag(PGN.TAG_ROUND, readUTFNonNull(in));
        setTag(PGN.TAG_WHITE, readUTFNonNull(in));
        setTag(PGN.TAG_BLACK, readUTFNonNull(in));
        setTag(PGN.TAG_RESULT, readUTFNonNull(in));
        
        if (mode <= MODE_SEVEN_TAG_ROASTER) return;  // =====>
        
        setTag(PGN.TAG_WHITE_ELO, readUTFNonNull(in));
        setTag(PGN.TAG_BLACK_ELO, readUTFNonNull(in));
        setTag(PGN.TAG_EVENT_DATE, readUTFNonNull(in));
        setTag(PGN.TAG_ECO, readUTFNonNull(in));
        
        if (mode <= MODE_STANDARD_TAGS) return;  // =====>
        
        // NOT YET SUPPORTED
    }
    
    private void writeUTFNonNull(DataOutput out, String s) throws IOException
    {
        out.writeUTF(s == null ? "" : s);
    }
    
    public void save(DataOutput out, int mode) throws IOException
    {
        writeUTFNonNull(out, getEvent());
        writeUTFNonNull(out, getSite());
        writeUTFNonNull(out, getDate());
        writeUTFNonNull(out, getRound());
        writeUTFNonNull(out, getWhite());
        writeUTFNonNull(out, getBlack());
        writeUTFNonNull(out, getResultStr());
        
        if (mode <= MODE_SEVEN_TAG_ROASTER) return;  // =====>
        
        writeUTFNonNull(out, getWhiteEloStr());
        writeUTFNonNull(out, getBlackEloStr());
        writeUTFNonNull(out, getEventDate());
        writeUTFNonNull(out, getECO());
        
        if (mode <= MODE_STANDARD_TAGS) return;  // =====>
        
        // NOT YET SUPPORTED
    }
    
    /*================================================================================*/
    
    private static String getLastName(String name)
    {
        int index = name.indexOf(',');
        if (index != -1) {
            return name.substring(0, index).trim();
        } else {
            index = name.indexOf(' ');
            if (index != -1) {
                return name.substring(index + 1).trim();
            } else {
                return name.trim();
            }
        }
    }
    
    private static boolean isStringSimilar(String s1, String s2)
    {
        if (s1 == null) {
            return (s2 == null) ? true : false;
        } else if (s2 == null) {
            return false;
        } else {
            int hits = 0, total = 0;
            s2 = s2.toLowerCase();
            for (int i=0; i<s1.length(); i++) {
                char ch = Character.toLowerCase(s1.charAt(i));
                if (!Character.isWhitespace(ch)) {
                    total++;
                    int index = s2.indexOf(ch);
                    if (index != -1) {
                        hits++;
                        s2 = s2.substring(0, index) + s2.substring(index + 1);
                    }
                }
            }
            return (2 * hits >= total);
        }
    }
    
    public boolean isSimilar(GameHeaderModel headerModel)
    {
        return isStringSimilar(getWhite(), headerModel.getWhite()) &&
               isStringSimilar(getBlack(), headerModel.getBlack());
//        return isStringSimilar(getLastName(getWhite()), getLastName(headerModel.getWhite())) &&
//               isStringSimilar(getLastName(getBlack()), getLastName(headerModel.getBlack()));
    }
    
    /*================================================================================*/
    
    public String toString() {return getWhite() + " - " + getBlack() + " " + getResultStr() + " (" + getDate() + ")";}
}