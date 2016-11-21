package org.unhack.bip38decrypt;
/*
This part is taken from Bitseal broject
GNU License
*/

import android.util.Log;

import com.google.common.base.CharMatcher;

import net.bither.bitherj.exception.AddressFormatException;
import net.bither.bitherj.utils.Base58;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    private static final int BTC_ADDRESS_MAX_LENGTH = 35;

    public static byte[] copyOfRange(byte[] from, int start, int end)
    {
        int length = end - start;
        byte[] result = new byte[length];
        System.arraycopy(from, start, result, 0, length);
        return result;
    }

    public static byte[] concatenateByteArrays (byte[] a, byte[] b)
    {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] doubleDigest(byte[] input)
    {
        return doubleDigest(input, 0, input.length);
    }

    public static byte[] doubleDigest(byte[] input, int offset, int length)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(input, offset, length);
            byte[] first = digest.digest();
            return digest.digest(first);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("NoSuchAlgorithmException occurred in DigestSHA256.doubleDigest()", e);
        }
    }
    public static String encodePrivateKeyToWIF (byte[] privateKey)
    {
        // If first byte of the private encryption key generated is zero, remove it.
        if (privateKey[0] == 0)
        {
            privateKey = copyOfRange(privateKey, 1, privateKey.length);
        }
        byte[] valueToPrepend = new byte[1];
        valueToPrepend[0] = (byte) 128;
        byte[] privateKeyWithExtraByte = concatenateByteArrays(valueToPrepend, privateKey);
        byte[] hashOfPrivateKey = doubleDigest(privateKeyWithExtraByte);
        byte[] checksum = copyOfRange(hashOfPrivateKey, 0, 4);
        byte[] convertedPrivateKey = concatenateByteArrays(privateKeyWithExtraByte, checksum);
        String walletImportFormatPrivateKey = Base58.encode(convertedPrivateKey);
        return walletImportFormatPrivateKey;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getDif(String pattern) {
        if (pattern.isEmpty()){
            Log.d("getDif","Empty string");
            return null;
        }
        if (!isValidBTCAddressSubstring(pattern)){
            return null;
        }

        try {
            //Number of all possible addresses
            BigDecimal biggest_niumber = BigDecimal.valueOf(Math.pow(2,160));
            boolean zeroPrefix = false;
            BigDecimal pattern_prefix;
            String pattern_word = "";
            //setp1 count leading 1's
            int zero_count = 0;
            for (int i = 0; i< pattern.length(); i++){
                if (i == zero_count){
                    if (String.valueOf(pattern.charAt(i)).equals("1")) {
                        zero_count++;
                    }
                    else {
                        pattern_word = pattern_word + pattern.charAt(i);
                    }
                }
                else {
                    pattern_word = pattern_word + pattern.charAt(i);
                }
                if (zero_count>=19){
                    //we can not have so much 1's in the pattern
                    throw new AddressFormatException();
                }
            }
            //pattern - initial pattern
            //pattern_word - everything expect leading 1's
            //zero_count - number of leading 1's
            int bin_len = 200 - (8*zero_count);
            byte[] zero = Utils.hexStringToByteArray("00");
            try {
                pattern_prefix = new BigDecimal(new BigInteger(Base58.decode(pattern_word)));
            }
            catch (Exception e){
                Log.d("Zero?","yup, Zero");
                pattern_prefix = new BigDecimal(BigInteger.valueOf(0));
                zeroPrefix = true;
            }
            BigDecimal top = new BigDecimal(new BigInteger(zero));
            BigDecimal bottom = new BigDecimal(new BigInteger(zero));
            BigDecimal buf_top = new BigDecimal(new BigInteger(zero));
            BigDecimal buf_bottom = new BigDecimal(new BigInteger(zero));
            BigDecimal buffer = new BigDecimal(new BigInteger(zero));
            if (zeroPrefix){
                buffer = BigDecimal.valueOf(Math.pow(2,bin_len));
            }
            else {
                //set pow to 1
                int n = 1;
                while (top.toBigInteger().bitLength() <= (bin_len + 8)) {
                    buf_bottom = bottom;
                    buf_top = top;
                    bottom = pattern_prefix.multiply(BigDecimal.valueOf(Math.pow(58, n)));
                    top = bottom.add((BigDecimal.valueOf(Math.pow(58, n)))).subtract(BigDecimal.valueOf(1));
                    Log.d("Numbers", bottom.toString() + " " + top.toString());
                    if (buf_bottom.toBigInteger().bitLength() > (bin_len - 8)
                            && buf_bottom.toBigInteger().bitLength() <= bin_len
                            && buf_top.toBigInteger().bitLength() <= bin_len) {
                        int compareBottom = buf_bottom.compareTo(BigDecimal.valueOf(Math.pow(2, 192)));
                        int compareTop = buf_top.compareTo(BigDecimal.valueOf(Math.pow(2, 192)));
                        if (compareTop == -1 && compareBottom == -1) {
                            buffer = buffer.add(buf_top).subtract(buf_bottom);
                        } else if (compareTop == 1 && compareBottom == -1) {
                            buffer = buffer.add(BigDecimal.valueOf(Math.pow(2, 192))).subtract(buf_bottom);
                        }
                    }
                    n = n + 1;
                }
            }
            Log.d("BUF TOP",buf_top.toString());
            buffer = buffer.divide(BigDecimal.valueOf(Math.pow(256,4)),1,BigDecimal.ROUND_UP);
            try {
                String res = biggest_niumber.divide(buffer, 0, BigDecimal.ROUND_DOWN).toString();
                Log.d("DIFF", res);
                return res;
            }
            catch (ArithmeticException ae){
                ae.printStackTrace();
            }
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isValidBTCAddressSubstring(final String substring) {
        boolean validity = true;
        if (!CharMatcher.JAVA_LETTER_OR_DIGIT.matchesAllOf(substring) ||
                substring.length() > BTC_ADDRESS_MAX_LENGTH ||
                CharMatcher.anyOf("OIl0").matchesAnyOf(substring)) {
            validity = false;
        }
        return validity;
    }
}
