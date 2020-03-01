package Utilitaries;

public class Utilitaries {
	private static final String hexDigits = "0123456789ABCDEF";
	/**
	 * Converts the byte array into a hexadecimal representation.
	 * @param input - The array of bytes to be converted.
	 * @return A string with the hex representation of the array
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer buf = new StringBuffer();
	
		for (int i = 0; i < b.length; i++) {
			int j = ((int) b[i]) & 0xFF;
			buf.append(hexDigits.charAt(j / 16));//UpperHexDigit
			buf.append(hexDigits.charAt(j % 16));//LowerHexDigit
		}
		return buf.toString();
	}

	/**
	 * Converts a hex string in the corresponding array of bytes.
	 * @param Hex - The Hex String
	 * @return The array of bytes
	 * @throws IllegalArgumentException - If the String does not sej Sitio
	 * valid representation haxadecimal
	 */
	public static byte[] hexStringToByteArray(String hexString)
			throws IllegalArgumentException {
	
		// check if the string pair has a number of elements
		if (hexString.length() % 2 != 0) {
			throw new IllegalArgumentException("Fail Invalid HexString[" + hexString + "]");
		}
        //Create a byte array size of HexString.length/2	
		byte[] b = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i += 2) {
			if(((hexString.charAt(i) >= '0') && (hexString.charAt(i) <= '9')) ||
		    	((hexString.charAt(i) >= 'a') && (hexString.charAt(i) <= 'f'))||  
		    		((hexString.charAt(i) >= 'A') && (hexString.charAt(i) <= 'F'))) {
			    //Store HexByte into a byte array position 
				b[i / 2] = (byte) ((hexDigits.indexOf(Character.toUpperCase(hexString.charAt(i))) << 4) | (hexDigits
						.indexOf(Character.toUpperCase(hexString.charAt(i + 1)))));
			} else {
			    //Fail There are no HexString('0'to 'F') at current position 
				throw new IllegalArgumentException("Fail Invalid HexString[" + hexString + "] Wrong Digit[" + hexString.charAt(i) + "] at["+i+"]");
			}
		}
		return b;
	}	

}
