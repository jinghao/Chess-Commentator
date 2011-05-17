package edu.berkeley.nlp.chess.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFunctions {
	static {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static MessageDigest md;
	
	public static String sha1(byte[] b) {
		md.reset();
		md.update(b);

		byte[] t = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < t.length; i++) {
			int curr_byte = 0xFF & t[i];

			hexString.append((curr_byte < 0x10 ? "0" : "")
					+ Integer.toHexString(curr_byte));
		}

		return hexString.toString();
	}
}
