package AES;

/**
 * AES main algorithm
 */
public class AES {
	/* mix column multiplier, usage (mixColumnSbox * state), * = multiplication in galois field */
	private static int[][] mixColumnsSbox = {
			{0x2, 0x3, 0x1, 0x1},
			{0x1, 0x2, 0x3, 0x1},
			{0x1, 0x1, 0x2, 0x3},
			{0x3, 0x1, 0x1, 0x2}
		};

	/* inverse mix column multiplier, usage (invMixColumnSbox * state), * = multiplication in galois field */
	private static int[][] invMixColumnsSbox = {
		{0x0e, 0x0b, 0x0d, 0x09},
		{0x09, 0x0e, 0x0b, 0x0d},
		{0x0d, 0x09, 0x0e, 0x0b},
		{0x0b, 0x0d, 0x09, 0x0e}
	};

	/* AES SBox */
	private static int[][] sbox = {
		{0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76},
		{0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0},
		{0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15},
		{0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75},
		{0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84},
		{0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf},
		{0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8},
		{0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2},
		{0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
		{0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb},
		{0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
		{0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08},
		{0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
		{0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
		{0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
		{0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16}
	};

	/* AES inverse SBox */
	private static int[][] invSbox = {
		{0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
		{0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
		{0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
		{0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
		{0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
		{0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
		{0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
		{0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
		{0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
		{0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
		{0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
		{0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
		{0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
		{0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
		{0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
		{0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
	};

	/* rcon for key expansion */
	private static int[] rcon = {
		0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d
	};

	/**
	 * getNumberOfRound, get number of AES round (according to key length)
	 *
	 * @param key
	 * @return int, number of round
	 * @throws Exception, if key length is not one of the following {128, 192, 256}
	 */
	private static int getNumberOfRound(byte[] key) throws Exception {
		int len = key.length * 8;
		if (len == 128) {
			return 10;
		} else if (len == 192) {
			return 12;
		} else if (len == 256) {
			return 14;
		} else {
			throw new Exception("Wrong key length");
		}
	}

	/**
	 * subByte, subByte using AES SBox
	 * @param b, a byte from state block
	 * @return byte
	 */
	private static byte subByte(byte b) {
		int idx = b & 0xff;
		return (byte)sbox[idx >> 4][idx & 0x0f];
	}

	/**
	 * invSubByte, subByte using AES SBox
	 * @param b, a byte from state block
	 * @return byte
	 */
	private static byte invSubByte(byte b) {
		int idx = b & 0xff;
		return (byte)invSbox[idx >> 4][idx & 0x0f];
	}

	/**
	 * getState, convert data byte[16] to AES state byte[4][4]
	 * @param p, the data
	 * @return AES state byte[4][4]
	 * {d0, d1, d2, d3, d4, ....} =>
	 * 		{
	 * 		  	{d0, d4, d8, d12},
	 * 		  	{d1, d5, d9, d13},
	 * 		  	...
	 * 		}
	 */
	private static byte[][] getState(byte[] p) {
		byte[][] state = new byte[4][4];
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				state[i][j] = p[4 * j + i];
			}
		}
		return state;
	}

	/**
	 * getByte, convert data AES state byte[4][4] to byte[16]
	 * @param p, AES state
	 * @return byte data byte[16]
	 * 	{
	 * 	  	{d0, d4, d8, d12},
	 * 	  	{d1, d5, d9, d13},
	 * 	  	...
	 * 	}
	 * => {d0, d1, d2, d3, d4, ....}
	 */
	private static byte[] getByte(byte[][] state) {
		byte[] result = new byte[16];
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				result[4 * j + i] = state[i][j];
			}
		}
		return result;
	}

	/**
	 * subWord, do SubWord in AES key expansion, basically will substitute each word using SBox
	 * @param w
	 * @return
	 */
	private static byte[] subWord(byte[] w) {
		byte[] result = new byte[4];
		for (int i = 0; i < 4; i++) {
			result[i] = subByte(w[i]);
		}
		return result;
	}

	/**
	 * do RotWord in AES key expansion
	 * @param w
	 * @return
	 */
	private static byte[] rotWord(byte[] w) {
		byte[] result = new byte[4];
		result[0] = w[1];
		result[1] = w[2];
		result[2] = w[3];
		result[3] = w[0];
		return result;
	}

	/**
	 * xor 2 array of byte (byte[])
	 * @param a
	 * @param b
	 * @return byte[]
	 */
	private static byte[] xor(byte[] a, byte[] b) {
		byte[] result = new byte[4];
		for (int i = 0; i < 4; i++) {
			result[i] = (byte)((a[i] & 0xff) ^ (b[i] & 0xff));
		}
		return result;
	}

	/**
	 * generateKey using AES key expansion algorithm
	 * @param key, key for round[0]
	 * @return key[][], key[i] is 16 bytes of round ith key
	 * @throws Exception, when key length is invalid
	 */
	private static byte[][] generateKeys(byte[] key) throws Exception {
		int round = getNumberOfRound(key);
		byte[][] keys = new byte[round + 1][16];

		int nk = key.length * 8 / 32;
		byte[][] w = new byte[4 * (round + 1)][4];

		for (int i = 0; i < nk; i++) {
			for (int j = 0; j < 4; j++) {
				w[i][j] = key[4 * i + j];
			}
		}

		for (int i = nk; i < 4 * (round + 1); i++) {
			byte[] temp = w[i - 1];
			if (i % nk == 0) {
				byte[] rc = {(byte)rcon[i / nk], 0, 0, 0};
				temp = xor(subWord(rotWord(temp)), rc);
			} else if (nk > 6 && i % nk == 4) {
				temp = subWord(temp);
			}
			w[i] = xor(w[i - nk], temp);
		}

		for (int i = 0; i < round + 1; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					keys[i][4 * j + k] = w[4 * i + j][k];
				}
			}
		}

		return keys;
	}

	/**
	 * subByte, will substitute each byte using AES Sbox
	 * @param state
	 * @return byte[][]
	 */
	private static byte[][] subBytes(byte[][] state) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i][j] = subByte(state[i][j]);
			}
		}
		return result;
	}

	/**
	 * invSubByte, will substitute each byte using AES invSbox
	 * @param state
	 * @return
	 */
	private static byte[][] invSubBytes(byte[][] state) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i][j] = invSubByte(state[i][j]);
			}
		}
		return result;
	}

	/**
	 * shiftRows, do AES ShiftRows step
	 * @param state
	 * @return byte[][]
	 */
	private static byte[][] shiftRows(byte[][] state) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i][Math.floorMod(j - i, 4)] = state[i][j];
			}
		}
		return result;
	}

	/**
	 * invShiftRows, do AES InvShiftRows step
	 * @param state
	 * @return byte[][]
	 */
	private static byte[][] invShiftRows(byte[][] state) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i][j] = state[i][Math.floorMod(j - i, 4)];
			}
		}
		return result;
	}

	/**
	 * mix column, will do matrix multiplication on invMixColumnSBox and AES state block array
	 * @param state
	 * @return byte[][]
	 */
	private static byte[][] mixColumns(byte[][] state) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					byte mul = Utils.galoisMultiplication((byte)mixColumnsSbox[i][k], state[k][j]);
					result[i][j] = Utils.galoisAddition(result[i][j], mul);
				}
			}
		}
		return result;
	}

	/**
	 * inverse mix column, will do matrix multiplication on invMixColumnSBox and AES state block array
	 * @param state
	 * @return byte[][]
	 */
	private static byte[][] invMixColumns(byte[][] state) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					byte mul = Utils.galoisMultiplication((byte)invMixColumnsSbox[i][k], state[k][j]);
					result[i][j] = Utils.galoisAddition(result[i][j], mul);
				}
			}
		}
		return result;
	}

	/**
	 * add round key, will use addition in galois field (basically xor operation)
	 * @param state, byte[4][4]
	 * @param key, byte[4][4]
	 * @return byte[4][4]
	 */
	private static byte[][] addRoundKey(byte[][] state, byte[][] key) {
		byte[][] result = new byte[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i][j] = Utils.galoisAddition(state[i][j], key[i][j]);
			}
		}
		return result;
	}

	/**
	 * padding, use PKCS#7 to have padding up to 16 byte blocks
	 * @param b, byte[]
	 * @return result, byte[] data with padding
	 */
	private static byte[] padding(byte[] b) {
		int padding = 16 - (b.length % 16);
		byte[] result = new byte[b.length + padding];
		for (int i = 0; i < b.length; i++) {
			result[i] = b[i];
		}
		for (int i = 0; i < padding; i++) {
			result[b.length + i] = (byte)padding;
		}
		return result;
	}

	/**
	 * unpadding, use PKCS#7 to have padding up to 16 byte blocks
	 * @param b, byte[]
	 * @return byte[], data with no padding
	 * @throws Exception
	 */
	private static byte[] unpadding(byte[] b) throws Exception {
		int unpadding = b[b.length - 1] & 0xff;
		if (unpadding > 16) {
			throw new Exception("Decryption Failed");
		}
		byte[] result = new byte[b.length - unpadding];
		for (int i = 0; i < result.length; i++) {
			result[i] = b[i];
		}
		return result;
	}

	/**
	 * AES block encryption
	 * @param plaintext, byte[]
	 * @param key, byte[]
	 * @return byte[], ciphertext
	 * @throws Exception
	 */
	public static byte[] encryptAES(byte[] plaintext, byte[] key) throws Exception {
		int round = getNumberOfRound(key);
		byte[][] keys = generateKeys(key);
		byte[] ciphertext = new byte[plaintext.length];

		for (int i = 0; i < plaintext.length; i += 16) {
			byte[] curr = new byte[16];
			for (int j = 0; j < 16; j++) {
				curr[j] = plaintext[i + j];
			}
			byte[][] state = getState(curr);
			state = addRoundKey(state, getState(keys[0]));

			for (int r = 1; r < round; r++) {
				state = subBytes(state);
				state = shiftRows(state);
				state = mixColumns(state);
				state = addRoundKey(state, getState(keys[r]));
			}

			state = subBytes(state);
			state = shiftRows(state);
			state = addRoundKey(state, getState(keys[round]));

			byte[] result = getByte(state);
			for (int j = 0; j < 16; j++) {
				ciphertext[i + j] = result[j];
			}
		}

		return ciphertext;
	}

	/**
	 * AES block decryption
	 * @param ciphertext, byte[]
	 * @param key, byte[]
	 * @return byte[], plaintext
	 * @throws Exception
	 */
	public static byte[] decryptAES(byte[] ciphertext, byte[] key) throws Exception {
		int round = getNumberOfRound(key);
		byte[][] keys = generateKeys(key);
		byte[] plaintext = new byte[ciphertext.length];
		

		for (int i = 0; i < ciphertext.length; i += 16) {
			byte[] curr = new byte[16];
			for (int j = 0; j < 16; j++) {
				curr[j] = ciphertext[i + j];
			}
			byte[][] state = getState(curr);
			state = addRoundKey(state, getState(keys[round]));
			state = invShiftRows(state);
			state = invSubBytes(state);

			for (int r = round - 1; r >= 1; r--) {
				state = addRoundKey(state, getState(keys[r]));
				state = invMixColumns(state);
				state = invShiftRows(state);
				state = invSubBytes(state);
			}

			state = addRoundKey(state, getState(keys[0]));

			byte[] result = getByte(state);
			for (int j = 0; j < 16; j++) {
				plaintext[i + j] = result[j];
			}
		}

		return plaintext;
	}

	/**
	 * AES block encryption with PKCS#7 encryption and CTR mode
	 * @param plaintext, byte[]
	 * @param key, byte[]
	 * @param nonce, byte[]
	 * @return byte[] ciphertext
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] nonce) throws Exception {
		plaintext = padding(plaintext);
		byte[] ciphertext = new byte[plaintext.length];
		int round = plaintext.length / 16;
		for (int i = 0; i < round; i++) {
			byte[] nonceCipher = encryptAES(nonce, key);
			for (int j = 0; j < 16; j++) {
				ciphertext[16 * i + j] = (byte)((nonceCipher[j] & 0xff) ^ (plaintext[16 * i + j] & 0xff));
			}
			nonce = Utils.increment(nonce);
		}
		return ciphertext;
	}

	/**
	 * AES block decryption with PKCS#7 encryption and CTR mode
	 * @param ciphertext, byte[]
	 * @param key, byte[]
	 * @param nonce, byte[]
	 * @return byte[] plaintext
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] ciphertext, byte[] key, byte[] nonce) throws Exception {
		byte[] plaintext = new byte[ciphertext.length];
		int round = ciphertext.length / 16;
		for (int i = 0; i < round; i++) {
			byte[] nonceCipher = encryptAES(nonce, key);
			for (int j = 0; j < 16; j++) {
				plaintext[16 * i + j] = (byte)((nonceCipher[j] & 0xff) ^ (ciphertext[16 * i + j] & 0xff));
			}
			nonce = Utils.increment(nonce);
		}
		plaintext = unpadding(plaintext);
		return plaintext;
	}
}