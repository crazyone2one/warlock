package cn.master.horde.util;

import cn.master.horde.common.constants.RsaKey;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author : 11's papa
 * @since : 2026/1/30, 星期五
 **/
public class RsaUtils {
    public static final String CHARSET = StandardCharsets.UTF_8.name();
    public static final String RSA_ALGORITHM = "RSA";

    private static RsaKey rsaKey;

    public static RsaKey createKeys(int keySize) throws NoSuchAlgorithmException {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();

        // 得到公钥
        Key publicKey = keyPair.getPublic();

        String publicKeyStr = new String(Base64.encodeBase64(publicKey.getEncoded()));

        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = new String(Base64.encodeBase64(privateKey.getEncoded()));
        return new RsaKey(publicKeyStr, privateKeyStr);
    }

    public static RsaKey createKeys() throws NoSuchAlgorithmException {
        return createKeys(1024);
    }

    public static RsaKey getRsaKey() throws NoSuchAlgorithmException {
        if (rsaKey == null) {
            rsaKey = createKeys();
        }
        return rsaKey;
    }

    public static void setRsaKey(RsaKey rsaKey) {
        // 放到缓存里
        RsaUtils.rsaKey = rsaKey;
    }

    public static String privateDecrypt(String cipherText, String privateKey) throws NoSuchAlgorithmException {
        RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
        return privateDecrypt(cipherText, rsaPrivateKey);
    }

    private static String privateDecrypt(String cipherText, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String v = new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(cipherText), privateKey.getModulus().bitLength()), CHARSET);
            if (StringUtils.isBlank(v)) {
                return cipherText;
            }
            return v;
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + cipherText + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] data, int keySize) {
        int maxBlock;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            while (data.length > offSet) {
                if (data.length - offSet > maxBlock) {
                    buff = cipher.doFinal(data, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(data, offSet, data.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
    }

    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = null;
        try {
            key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (InvalidKeySpecException e) {
        }
        return key;
    }
}
