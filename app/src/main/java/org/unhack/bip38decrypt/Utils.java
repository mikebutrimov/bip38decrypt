package org.unhack.bip38decrypt;
/*
This part is taken from Bitseal broject
GNU License
*/

import net.bither.bitherj.utils.Base58;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
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

}
