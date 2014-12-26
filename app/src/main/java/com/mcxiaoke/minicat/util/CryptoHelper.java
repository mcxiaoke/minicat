package com.mcxiaoke.minicat.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import org.apache.http.protocol.HTTP;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;


/**
 * @author mcxiaoke
 * @version 1.0 2011.12.09
 */
public final class CryptoHelper {
    private static final String TAG = CryptoHelper.class.getSimpleName();
    private static final String EncodeAlgorithm = "DES";
    private static final String HEX = "0123456789ABCDEF";
    private static final String SECURE_KEY = "g$#Tdg%$^mc[54jxiaoke";
    private static CryptoHelper instance = null;
    private SecretKey key = null;

    public static CryptoHelper getInstance() {
        if (instance == null) {
            instance = new CryptoHelper();
            if (!instance.init()) {
                instance = null;
            }
        }
        return instance;
    }

    public static String encrypt(String seed, String cleartext)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String seed, String encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = encrypted.getBytes();
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    public static String rot13(String text) {
        if (text == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        // plaintext flag (do not convert)
        boolean plaintext = false;

        final int length = text.length();
        int c;
        int capitalized;
        for (int index = 0; index < length; index++) {
            c = text.charAt(index);
            if (c == '[') {
                plaintext = true;
            } else if (c == ']') {
                plaintext = false;
            } else if (!plaintext) {
                capitalized = c & 32;
                c &= ~capitalized;
                c = ((c >= 'A') && (c <= 'Z') ? ((c - 'A' + 13) % 26 + 'A') : c)
                        | capitalized;
            }
            result.append((char) c);
        }
        return result.toString();
    }

    public static String md5(String text) {
        String hashed = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes(HTTP.UTF_8), 0, text.length());
            hashed = new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            Log.e(TAG, "cgBase.md5: " + e.toString());
        }
        return hashed;
    }

    public static String sha1(String text) {
        String hashed = "";

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(text.getBytes(), 0, text.length());
            hashed = new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            Log.e(TAG, "cgBase.sha1: " + e.toString());
        }

        return hashed;
    }

    public static byte[] hashHmac(String text, String salt) {
        byte[] macBytes = {};

        try {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(salt.getBytes(), "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            macBytes = mac.doFinal(text.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "cgBase.hashHmac: " + e.toString());
        }

        return macBytes;
    }

    public static CharSequence rot13(final Spannable span) {
        // I needed to re-implement the rot13(String) encryption here because we must work on
        // a SpannableStringBuilder instead of the pure text and we must replace each character inline.
        // Otherwise we loose all the images, colors and so on...
        final SpannableStringBuilder buffer = new SpannableStringBuilder(span);
        boolean plaintext = false;

        final int length = span.length();
        int c;
        int capitalized;
        for (int index = 0; index < length; index++) {
            c = span.charAt(index);
            if (c == '[') {
                plaintext = true;
            } else if (c == ']') {
                plaintext = false;
            } else if (!plaintext) {
                capitalized = c & 32;
                c &= ~capitalized;
                c = ((c >= 'A') && (c <= 'Z') ? ((c - 'A' + 13) % 26 + 'A') : c)
                        | capitalized;
            }
            buffer.replace(index, index + 1, String.valueOf((char) c));
        }
        return buffer;
    }

    public static String convertToGcBase31(final String gccode) {
        final String alphabet = "0123456789ABCDEFGHJKMNPQRTVWXYZ";

        if (null == gccode) {
            return "";
        }

        char[] characters = gccode.toUpperCase().toCharArray();

        if (characters.length <= 2) {
            return "";
        }

        final int base = (characters.length <= 5 || (characters.length == 6 && alphabet.indexOf(characters[2]) < 16)) ? 16 : 31;
        int result = 0;

        for (int i = 2; i < characters.length; i++) {
            result *= base;
            result += alphabet.indexOf(characters[i]);
        }

        if (31 == base) {
            result += Math.pow(16, 4) - 16 * Math.pow(31, 3);
        }

        return Integer.toString(result);
    }

    private boolean init() {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(SECURE_KEY.getBytes());
            SecretKeyFactory skf = SecretKeyFactory
                    .getInstance(EncodeAlgorithm);
            key = skf.generateSecret(desKeySpec);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return key != null;
    }

    @SuppressWarnings("unused")
    private boolean init2() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(EncodeAlgorithm);
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            key = keygen.generateKey();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return key != null;
    }

    private Cipher getCipher(int mode) {
        try {
            Cipher cipher = Cipher.getInstance(EncodeAlgorithm);
            cipher.init(mode, key);
            return cipher;
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    private Cipher getEncodeCipher() {
        return this.getCipher(Cipher.ENCRYPT_MODE);
    }

    private Cipher getDecodeCipher() {
        return this.getCipher(Cipher.DECRYPT_MODE);
    }

    /**
     * 解密，若输入为null或加/解密过程出现异常，则输出为null <br/>
     * 作者：wallimn　时间：2009-8-12　上午08:09:44<br/>
     * 博客：http://wallimn.iteye.com<br/>
     * 参数：<br/>
     *
     * @param str
     * @return
     */
    public String decode(String str) {
        if (str == null)
            return null;
        Cipher cipher = getDecodeCipher();
        StringBuffer sb = new StringBuffer();
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] src = stringToBytes(str);
        byte[] outBytes = new byte[outputSize];
        int i = 0;
        try {
            for (; i <= src.length - blockSize; i = i + blockSize) {
                int outLength = cipher.update(src, i, blockSize, outBytes);
                sb.append(new String(outBytes, 0, outLength));
            }
            if (i == src.length)
                outBytes = cipher.doFinal();
            else {
                outBytes = cipher.doFinal(src, i, src.length - i);
            }
            sb.append(new String(outBytes));
            return sb.toString();
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密，若输入为null或加/解密过程出现异常，则输出为null <br/>
     * 作者：wallimn　时间：2009-8-12　上午08:09:59<br/>
     * 博客：http://wallimn.iteye.com<br/>
     * 参数：<br/>
     *
     * @param str
     * @return
     */
    public String encode(String str) {
        if (str == null)
            return null;
        Cipher cipher = getEncodeCipher();
        StringBuffer sb = new StringBuffer();
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] src = str.getBytes();
        byte[] outBytes = new byte[outputSize];
        int i = 0;
        try {
            for (; i <= src.length - blockSize; i = i + blockSize) {
                int outLength = cipher.update(src, i, blockSize, outBytes);
                sb.append(bytesToString(outBytes, outLength));
            }
            if (i == src.length)
                outBytes = cipher.doFinal();
            else {
                outBytes = cipher.doFinal(src, i, src.length - i);
            }
            sb.append(bytesToString(outBytes));
            return sb.toString();
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    private String bytesToString(byte[] bs) {
        if (bs == null || bs.length == 0)
            return "";
        return bytesToString(bs, bs.length);
    }

    private String bytesToString(byte[] bs, int len) {
        if (bs == null || bs.length == 0)
            return "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            // System.out.println(bs[i]+":"+String.format("%02X", bs[i]));
            sb.append(String.format("%02X", bs[i]));
        }
        return sb.toString();
    }

    private byte[] stringToBytes(String str) {
        if (str == null || str.length() < 2 || str.length() % 2 != 0)
            return new byte[0];
        int len = str.length();
        byte[] bs = new byte[len / 2];
        for (int i = 0; i * 2 < len; i++) {
            bs[i] = (byte) (Integer.parseInt(str.substring(i * 2, i * 2 + 2),
                    16) & 0xFF);
            // System.out.println(str.substring(i * 2, i * 2 + 2)+":"+bs[i]);
        }
        return bs;
    }

}
