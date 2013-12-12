package com.arcblaze.arctime.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * A utility class used to perform password operations.
 */
public class Password {
	/**
	 * The hash algorithm
	 */
	public final static String HASH_ALGORITHM = "SHA-512";

	/**
	 * @param password
	 *            the new password for which a hash will be generated
	 * 
	 * @return the hashed value in the form of a hex string
	 */
	public String hash(String password) {
		try {
			MessageDigest messageDigest = MessageDigest
					.getInstance(HASH_ALGORITHM);
			return toHexString(messageDigest.digest(password.getBytes()));
		} catch (NoSuchAlgorithmException badHashAlgorithm) {
			// Not expecting this to happen.
			return password;
		}
	}

	private String toHexString(byte[] bytes) {
		final char[] hex = "0123456789abcdef".toCharArray();
		StringBuilder sb = new StringBuilder(bytes.length << 1);

		for (int i = 0; i < bytes.length; ++i)
			sb.append(hex[(bytes[i] & 0xf0) >> 4]).append(
					hex[(bytes[i] & 0x0f)]);

		return sb.toString();
	}

	/**
	 * @return a randomly generated password
	 */
	public String random() {
		final String chars = "aeuAEU23456789bdghjmnpqrstvzBDGHJLMNPQRSTVWXZ";

		Random random = new Random();
		StringBuilder password = new StringBuilder();
		for (int i = 0; i < 14; i++)
			password.append(chars.charAt(random.nextInt(chars.length())));
		return password.toString();
	}
}
