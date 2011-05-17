package edu.berkeley.nlp.chess.util;
import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

public class FileIterator implements Iterable<File> {
	private Stack<File> files = new Stack<File>();
	private FileFilter filter;
	
	public FileIterator(String f) {
		this(new File(f), (FileFilter) FileFilterUtils.trueFileFilter());
	}
	
	public FileIterator(File f, FileFilter filter) {
		files.push(f);
		this.filter = filter;
	}

	@Override
	public Iterator<File> iterator() {
		return new Iterator<File>() {
			public boolean hasNext() {
				return !files.isEmpty();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public File next() {
				while (hasNext()) {
					File f = files.pop();
					if (f.isDirectory()) {
						for (File file : f.listFiles((FileFilter)
								FileFilterUtils.or(DirectoryFileFilter.INSTANCE, 
										FileFilterUtils.asFileFilter(filter))))
							files.add(file);
					} else return f;
				}

				throw new NoSuchElementException("No file");
			}
		};
	}
	public static void main(String[] args) {
		for (File f : new FileIterator(new File("/home/aa/ugrad/jinghao/chess/Chess-Commentator/src"), FileFileFilter.FILE)) {
			System.out.println(f.getPath());
		}
	}
}
