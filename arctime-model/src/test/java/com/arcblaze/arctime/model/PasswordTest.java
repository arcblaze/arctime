package com.arcblaze.arctime.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Perform testing on the password class.
 */
public class PasswordTest {
	/**
	 * Test the {@link Password#hash(String)} method.
	 */
	@Test
	public void testHash() {
		final String password = "pass";
		final String hashedPass = Password.hash(password);
		final String correct = "5b722b307fce6c944905d132691d5e4a2214b7fe92b"
				+ "738920eb3fce3a90420a19511c3010a0e7712b054daef5b57bad59ec"
				+ "bd93b3280f210578f547f4aed4d25";

		assertEquals(correct, hashedPass);
	}
}
