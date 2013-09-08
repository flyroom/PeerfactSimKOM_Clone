/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.util.toolkits;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * This is a helper class to be able to reuse the SHA1 hash generation of a
 * string.
 * 
 * The idea was taken from the implementation of chord.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HashToolkit {

	/**
	 * Logger for the class.
	 */
	private static Logger log = SimLogger.getLogger(HashToolkit.class);

	/**
	 * The default id length is set to 160
	 */
	public static final int DEFAULT_ID_LENGTH = 160;

	/**
	 * Constant for 2 as BigInteger.
	 */
	private static final BigInteger TWO = new BigInteger("2");

	/**
	 * Generates a hashed number from the net id of a host.
	 * 
	 * @param transInfo
	 *            the information about the host
	 * @return the generated has value
	 */
	public static BigInteger getSHA1Hash(TransInfo transInfo, int numberOfBits) {
		return getSHA1Hash(transInfo.getNetId().toString(), numberOfBits);
	}

	/**
	 * Generates a hash value.
	 * 
	 * @param stringToHash
	 *            a string to hash
	 * @param numberOfBits
	 *            the number of bits
	 * @return the BigInteger interpretation of the SHA1-Hash
	 */
	public static BigInteger getSHA1Hash(String stringToHash, int numberOfBits) {
		MessageDigest md;
		byte[] sha1hash = new byte[numberOfBits];
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(stringToHash.getBytes("iso-8859-1"), 0,
					stringToHash.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			log.error("NoSuchAlgorithmException", e);
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException", e);
		}
		BigInteger value = new BigInteger(1, sha1hash);

		// Make sure the value does not have more than numberOfBits bits
		value = value.mod(TWO.pow(numberOfBits));

		return value;
	}

	/**
	 * Generates hash value with default length.
	 * 
	 * @param stringToHash
	 *            a string to hash
	 * @return the BigInteger interpretation of the SHA1-Hash
	 */
	public static BigInteger getSHA1Hash(String stringToHash) {
		return getSHA1Hash(stringToHash, DEFAULT_ID_LENGTH);
	}

	/**
	 * Generates a hash value with MD5.
	 * 
	 * @param stringToHash
	 *            a string to hash
	 * @return the String interpretation of the MD5-Hash
	 */
	public static String getMD5Hash(String stringToHash) {
		MessageDigest md5;
		byte[] result = new byte[0];
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(stringToHash.getBytes());
			result = md5.digest();

		} catch (NoSuchAlgorithmException e) {
			log.error("NoSuchAlgorithmException", e);
		}

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			hexString.append(Integer.toHexString(0xFF & result[i]));
		}
		return hexString.toString();
	}

}
