package edu.berkeley.nlp.chess.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public class GzipFiles {
	public static InputSupplier<ObjectInputStream> newObjectInputStreamSupplier(final File f) {
		return new InputSupplier<ObjectInputStream>() {
			private InputSupplier<FileInputStream> fiss = Files.newInputStreamSupplier(f);
			@Override public ObjectInputStream getInput() throws IOException {
				return new ObjectInputStream(new GZIPInputStream(fiss.getInput()));
			}
		};
	}
	public static OutputSupplier<ObjectOutputStream> newObjectOutputStreamSupplier(final File f) {
		return new OutputSupplier<ObjectOutputStream>() {
			private OutputSupplier<FileOutputStream> fiss = Files.newOutputStreamSupplier(f);
			@Override public ObjectOutputStream getOutput() throws IOException {
				return new ObjectOutputStream(new GZIPOutputStream(fiss.getOutput()));
			}
		};
	}
}
