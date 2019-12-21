package Common;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static java.lang.Math.abs;

public class Utils {
    public static byte[] getSHA256(InputStream is) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        DigestInputStream dis = new DigestInputStream(is, md);
        return dis.getMessageDigest().digest();
    }

    public static String toHexString(byte[] hash) {
        BigInteger numericData = new BigInteger(hash);
        StringBuilder stringBuilder = new StringBuilder(numericData.toString(16));
        return stringBuilder.toString();
    }

    public static String getRandomString(int len) {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder randomString = new StringBuilder();
        Random rand = new Random();
        for(int idx = 0; idx < len; ++idx) {
            randomString.append(CHARS.charAt(abs(rand.nextInt()) % CHARS.length()));
        }
        return randomString.toString();
    }
}
