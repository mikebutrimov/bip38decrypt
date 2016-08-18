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

}
