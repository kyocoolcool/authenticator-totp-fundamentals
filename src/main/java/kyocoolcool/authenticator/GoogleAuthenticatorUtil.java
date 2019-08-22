package kyocoolcool.authenticator;

import com.google.zxing.WriterException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GoogleAuthenticatorUtil {

    public static final int SECRET_SIZE = 10;

    public static final String SEED = "g8GjEvTbW5oVSV7avLBdwIHqGlUYNzKFI7izOF8GwLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";

    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    int window_size = 3; // default 3 - max 17 (from google docs) 最多可偏移的時間

    public void setWindowSize(int s) {
        if (s >= 1 && s <= 17)
            window_size = s;
    }

   /**
    * @description: 驗證身份
    * @param codes 驗證碼
    * @param secret key
    * @return: java.lang.Boolean
    * @author: Chris Chen
    * @time: 2019-07-26 09:55
    */
    public static Boolean authCode(String codes, String secret) {
        long code = 0;
        try {
            code = Long.parseLong(codes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long t = System.currentTimeMillis();
        GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
        ga.setWindowSize(3); // should give 5 * 30 seconds of grace...
        boolean r = ga.check_code(secret, code, t);
        return r;
    }

    /**
     * @description: 獲取key
     * @return: java.lang.String
     * @author: Chris Chen
     * @time: 2019-07-26 09:56
     */
    public static String genSecret() {
        String secret = GoogleAuthenticatorUtil.generateSecretKey();
        return secret;
    }

    /**
     * @description: 產生key
     * @param
     * @return: java.lang.String
     * @author: Chris Chen
     * @time: 2019-07-26 10:00
     */
    private static String generateSecretKey() {
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            String encodedKey = new String(bEncodedKey);
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            // should never occur... configuration error
        }
        return null;
    }

    /**
     * @description: 產生二維碼
     * @param user 用戶
     * @param host domain
     * @param secret
     * @return: java.lang.String
     * @author: Chris Chen
     * @time: 2019-07-26 10:01
     */
    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s";
        return String.format(format, user, host, secret);
    }


    private boolean check_code(String secret, long code, long timeMsec) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        long t = (timeMsec / 1000L) / 30L;
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = verify_code(decodedKey, t + i);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            if (hash == code) {
                return true;
            }
        }
        return false;
    }

    private static int verify_code(byte[] key, long t)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }


    public static void main(String[] args) {

        String user = "chrischen";
        String host = "mitac";
        String secret = genSecret();
        System.out.println("secret:" + secret);
//        String url = getQRBarcodeURL(user, host, secret);
//        System.out.println("url:" + url);
        try {
            QRCodeUtil.createQRCode("otpauth://totp/"+user+"@"+host+"?"+"secret="+secret,"/opt/app/666.png", 300, 300);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        boolean result = authCode("867828", "3FUAD6RWKJ2WQ5GM");
//        System.out.println("result:" + result);
    }
}