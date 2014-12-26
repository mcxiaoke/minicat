/**
 *
 */
package com.mcxiaoke.minicat.util;

import com.mcxiaoke.minicat.AppContext;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author mcxiaoke
 * @version 1.2 2011.12.02
 */
public class StringHelper {

    // Regex that matches characters that have special meaning in HTML. '<',
    // '>', '&' and
    // multiple continuous spaces.
    private static final Pattern PLAIN_TEXT_TO_ESCAPE = Pattern
            .compile("[<>&]| {2,}|\r?\n");

    /**
     * Escape some special character as HTML escape sequence.
     *
     * @param text Text to be displayed using WebView.
     * @return Text correctly escaped.
     */
    public static String escapeCharacterToDisplay(String text) {
        Pattern pattern = PLAIN_TEXT_TO_ESCAPE;
        Matcher match = pattern.matcher(text);

        if (match.find()) {
            StringBuilder out = new StringBuilder();
            int end = 0;
            do {
                int start = match.start();
                out.append(text.substring(end, start));
                end = match.end();
                int c = text.codePointAt(start);
                if (c == ' ') {
                    // Escape successive spaces into series of "&nbsp;".
                    for (int i = 1, n = end - start; i < n; ++i) {
                        out.append("&nbsp;");
                    }
                    out.append(' ');
                } else if (c == '\r' || c == '\n') {
                    out.append("<br>");
                } else if (c == '<') {
                    out.append("&lt;");
                } else if (c == '>') {
                    out.append("&gt;");
                } else if (c == '&') {
                    out.append("&amp;");
                }
            } while (match.find());
            out.append(text.substring(end));
            text = out.toString();
        }
        return text;
    }

    public static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String getStackMessageString(Throwable e) {
        StringBuffer message = new StringBuffer();
        StackTraceElement[] stack = e.getStackTrace();
        StackTraceElement stackLine = stack[stack.length - 1];
        message.append(stackLine.getFileName());
        message.append(":");
        message.append(stackLine.getLineNumber());
        message.append(":");
        message.append(stackLine.getMethodName());
        message.append(" ");
        message.append(e.getMessage());
        return message.toString();
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    public static String toString(List<String> array) {
        if (array == null || array.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    public static String toString(String[] array) {

        if (array == null || array.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append(")");
        return sb.toString();

    }

    public static String join(Collection<?> items, String delimiter) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        final Iterator<?> iter = items.iterator();
        final StringBuilder buffer = new StringBuilder(iter.next().toString());

        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }

        return buffer.toString();
    }

    /**
     * @param s 原始消息字符串
     * @return 自动截断超过140个字符的消息，取前面133个字符，并添加...，预留转发的字符位置
     */
    public static String cut(String s) {
        String str = s.trim();
        StringBuilder sb = new StringBuilder();
        if (str.length() > 140) {
            return sb.append(str.substring(0, 135)).append("...").toString();
        } else {
            return str;
        }
    }

    public static String join(String separator, String[] strings) {
        if (strings == null) {
            return null;
        }
        if (strings.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(strings[0]);
        for (int i = 1, length = strings.length; i < length; i++) {
            builder.append(separator);
            builder.append(strings[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, Integer[] integers) {
        if (integers == null) {
            return null;
        }
        if (integers.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(integers[0].toString());
        for (int i = 1, length = integers.length; i < length; i++) {
            builder.append(separator);
            builder.append(integers[i]);
        }
        return builder.toString();
    }

    /**
     * @param s 原始字符串
     * @return 判断字符串是否为空
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static String md5(String s) {
        StringBuffer result = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte digest[] = md.digest();
            for (int i = 0; i < digest.length; i++) {
                result.append(Integer.toHexString(0xFF & digest[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return (result.toString());
    }

    /**
     * MD5加密函数
     *
     * @param str 要加密的字符串
     * @return 加密后的字符串
     */
    public static String md5old(String str) {
        if ((str == null) || ("".equals(str.trim()))) {
            return str;
        } else {
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.reset();
                messageDigest.update(str.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            byte[] byteArray = messageDigest.digest();

            StringBuffer md5StrBuff = new StringBuffer();

            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & byteArray[i]));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
            return md5StrBuff.toString();
        }
    }

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        // 16位加密，从第9位到25位
        return md5StrBuff.substring(8, 24).toString().toUpperCase();
    }

    public static String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    /**
     * 字符串转化为数字
     *
     * @param s 字符串参数
     * @return 字符串代表的数字，如果无法转换，返回0
     */
    public static int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * @param original
     * @return null if fails
     */
    public static String urlencode(String original) {
        try {
            // return URLEncoder.encode(original, "utf-8");
            // fixed: to comply with RFC-3986
            return URLEncoder.encode(original, "utf-8").replace("+", "%20")
                    .replace("*", "%2A").replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * @param encoded
     * @return null if fails
     */
    public static String urldecode(String encoded) {
        try {
            return URLDecoder.decode(encoded, "utf-8");
        } catch (UnsupportedEncodingException e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * @param original
     * @param key
     * @return null if fails
     */
    public static String hmacSha1Digest(String original, String key) {
        return hmacSha1Digest(original.getBytes(), key.getBytes());
    }

    /**
     * @param original
     * @param key
     * @return null if fails
     */
    public static String hmacSha1Digest(byte[] original, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] rawHmac = mac.doFinal(original);
            return new String(Base64.encodeBytes(rawHmac));
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * @param original
     * @return null if fails
     */
    public static String md5sum(byte[] original) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(original, 0, original.length);
            StringBuffer md5sum = new StringBuffer(new BigInteger(1,
                    md.digest()).toString(16));
            while (md5sum.length() < 32)
                md5sum.insert(0, "0");
            return md5sum.toString();
        } catch (NoSuchAlgorithmException e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * @param original
     * @return null if fails
     */
    public static String md5sum(String original) {
        return md5sum(original.getBytes());
    }

    /**
     * AES encrypt function
     *
     * @param original
     * @param key      16, 24, 32 bytes available
     * @param iv       initial vector (16 bytes) - if null: ECB mode, otherwise: CBC
     *                 mode
     * @return
     */
    public static byte[] aesEncrypt(byte[] original, byte[] key, byte[] iv) {
        if (key == null
                || (key.length != 16 && key.length != 24 && key.length != 32)) {

            return null;
        }
        if (iv != null && iv.length != 16) {

            return null;
        }

        try {
            SecretKeySpec keySpec = null;
            Cipher cipher = null;
            if (iv != null) {
                keySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(
                        iv));
            } else // if(iv == null)
            {
                keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
                cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            }

            return cipher.doFinal(original);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * AES decrypt function
     *
     * @param encrypted
     * @param key       16, 24, 32 bytes available
     * @param iv        initial vector (16 bytes) - if null: ECB mode, otherwise: CBC
     *                  mode
     * @return
     */
    public static byte[] aesDecrypt(byte[] encrypted, byte[] key, byte[] iv) {
        if (key == null
                || (key.length != 16 && key.length != 24 && key.length != 32)) {

            return null;
        }
        if (iv != null && iv.length != 16) {

            return null;
        }

        try {
            SecretKeySpec keySpec = null;
            Cipher cipher = null;
            if (iv != null) {
                keySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(
                        iv));
            } else // if(iv == null)
            {
                keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
                cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
            }

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * generates RSA key pair
     *
     * @param keySize
     * @param publicExponent public exponent value (can be RSAKeyGenParameterSpec.F0 or F4)
     * @return
     */
    public static KeyPair generateRsaKeyPair(int keySize,
                                             BigInteger publicExponent) {
        KeyPair keys = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(keySize,
                    publicExponent);
            keyGen.initialize(spec);
            keys = keyGen.generateKeyPair();
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return keys;
    }

    /**
     * generates a RSA public key with given modulus and public exponent
     *
     * @param modulus        (must be positive? don't know exactly)
     * @param publicExponent
     * @return
     */
    public static PublicKey generateRsaPublicKey(BigInteger modulus,
                                                 BigInteger publicExponent) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(
                    new RSAPublicKeySpec(modulus, publicExponent));
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * generates a RSA private key with given modulus and private exponent
     *
     * @param modulus         (must be positive? don't know exactly)
     * @param privateExponent
     * @return
     */
    public static PrivateKey generateRsaPrivateKey(BigInteger modulus,
                                                   BigInteger privateExponent) {
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(
                    new RSAPrivateKeySpec(modulus, privateExponent));
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA encrypt function (RSA / ECB / PKCS1-Padding)
     *
     * @param original
     * @param key
     * @return
     */
    public static byte[] rsaEncrypt(byte[] original, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(original);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA decrypt function (RSA / ECB / PKCS1-Padding)
     *
     * @param encrypted
     * @param key
     * @return
     */
    public static byte[] rsaDecrypt(byte[] encrypted, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * converts given byte array to a hex string
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10)
                buffer.append("0");
            buffer.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return buffer.toString();
    }

    /**
     * converts given hex string to a byte array (ex: "0D0A" => {0x0D, 0x0A,})
     *
     * @param str
     * @return
     */
    public static final byte[] hexStringToByteArray(String str) {
        int i = 0;
        byte[] results = new byte[str.length() / 2];
        for (int k = 0; k < str.length(); ) {
            results[i] = (byte) (Character.digit(str.charAt(k++), 16) << 4);
            results[i] += (byte) (Character.digit(str.charAt(k++), 16));
            i++;
        }
        return results;
    }

    public static String urlencode_rfc3986(String text) {
        final String encoded = StringUtils.replace(URLEncoder.encode(text).replace("+", "%20"), "%7E", "~");
        return encoded;
    }

    /**
     * Tweet Regex Pattern
     *
     * public static final Pattern a = Pattern.compile(
     * "\\b(([\\w-]+://?|www[.])[^\\s()<>]+(?:\\([\\w\\d]+\\)|([^[:punct:]\\s]|/)))"
     * ); public static final Pattern b = Pattern.compile(
     * "\\b(([hH][tT][tT][pP][sS]?://?)[^\\s()<>]+(?:\\([\\w\\d]+\\)|([^[:punct:]\\s]|/)))"
     * ); public static final Pattern c = Pattern.compile("[@#]{1}\\w+"); public
     * static final Pattern d = Pattern.compile("((\\s\\s+)|\\n|\\r)"); public
     * static final Pattern e =
     * Pattern.compile("^https?://twitter\\.com(/#!)?/\\w+/status/\\d+$");
     * public static final Pattern f = Pattern.compile("\\A@?\\w+\\z");
     *
     */

}
