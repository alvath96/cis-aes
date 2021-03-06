package GUI;

/**
 * Utilities function used for various purpose in GUI
 */
public class Utils {

    /**
     * Array of bytes to hexadecimal numbers in string
     * @param bytes
     * @return hexadecimal numbers
     */
    public static String byteToHexString(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    /**
     * Hexadecimal numbers in string to array of Byte
     * @param s
     * @return array of bytes
     */
    public static byte[] stringToByteArray(String s) {
        if (s.length() % 2 == 1) {
            s = "0" + s;
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


}
