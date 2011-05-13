/*
 * PGNFileFilter.java
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
package com.supareno.pgnparser.filters;

import java.io.File;
import java.io.FileFilter;

import com.supareno.pgnparser.PGNType;

/**
 * This class is a specific file filter for PGN files.
 * <p>
 * Return {@code true} if the file name ends with {@link PGNType#PGN#getExtension()}to lower case, {@code false}
 * otherwise.
 * </p>
 * @author reno
 */
public class PGNFileFilter implements FileFilter {

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pathname) {
		if(pathname == null) { return false; }
		return isEndWithPGN(pathname);
	}

	/**
	 * @param pathname
	 * @return
	 */
	private boolean isEndWithPGN(File pathname) {
		if(pathname.getName().toLowerCase().endsWith(PGNType.PGN.getExtension())){
			return isPGNExtension(pathname);
		}
		return false;
	}

	/**
	 * @param pathname
	 * @return
	 */
	private boolean isPGNExtension(File pathname) {
		int x = pathname.getName().indexOf(".");
		if (pathname.getName().substring(x + 1, x + 4).toLowerCase().equals((PGNType.PGN.getExtension()))){
			return true;
		}
		return false;
	}

}
