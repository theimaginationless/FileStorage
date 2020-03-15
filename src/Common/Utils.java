package Common;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static java.lang.Math.abs;

public class Utils {
    public static String getSHA256(@NotNull String filePath) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStreamEx(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] hash = digest.digest();
            BigInteger bigInt = new BigInteger(1, hash);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for SHA256", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String toHexString(@NotNull String hash) {
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
