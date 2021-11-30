package cc.ejyf.jfly.crypto;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 目前只支持AES/RSA的加解密
 */
public class MixinCryptor {
    public static final int PUBKEY_MODE = 0;
    public static final int PRIKEY_MODE = 1;
    private Base64.Encoder encoder = Base64.getEncoder();
    private Base64.Decoder decoder = Base64.getDecoder();
    private SecureRandom rand = new SecureRandom();

    public String aesStr2StrEncrypt(String message, String aesKey) throws GeneralSecurityException {
        return aesStr2StrEncrypt(message, aesKey, null);
    }

    public String aesStr2StrEncrypt(String message, String aesKey, String provider) throws GeneralSecurityException {
        return encoder.encodeToString(
                specAESCrypto(
                        Cipher.ENCRYPT_MODE,
                        message.getBytes(StandardCharsets.UTF_8),
                        decoder.decode(aesKey),
                        provider
                )
        );
    }

    public String aesStr2StrDecrypt(String ciphertext, String aesKey) throws GeneralSecurityException {
        return aesStr2StrDecrypt(ciphertext, aesKey, null);
    }

    public String aesStr2StrDecrypt(String ciphertext, String aesKey, String provider) throws GeneralSecurityException {
        return new String(
                specAESCrypto(
                        Cipher.DECRYPT_MODE,
                        decoder.decode(ciphertext),
                        decoder.decode(aesKey),
                        provider
                ),
                StandardCharsets.UTF_8
        );
    }

    /**
     * AES加解密方法
     *
     * @param mode     {@linkplain Integer int}类型。取值范围：
     *                 <ul>
     *                 <li>{@link Cipher#ENCRYPT_MODE ENCRYPT_MODE}</li>
     *                 <li>{@link Cipher#DECRYPT_MODE DECRYPT_MODE}</li>
     *                 </ul>
     *                 分别代表加密模式和解密模式。含义同原生方法。
     * @param todo     {@linkplain Byte byte}[]类型。
     *                 <ul>
     *                 <li>处于加密模式时，此参数应为待加密字节数组，例如{@link String#getBytes(Charset) String.getBytes()}</li>
     *                 <li>处于解密模式时，此参数应为待解密的字节数组，例如{@link Base64.Decoder#decode(String)}</li>
     *                 </ul>
     * @param secret   {@linkplain Byte byte[]}类型。一般从{@link Base64.Decoder#decode(String)}解码而来。
     * @param provider {@link String}类型。当使用null时，使用java默认实现。可传递providerNameString来使用指定供应商（可能需要{@linkplain Security#addProvider(Provider) 额外的事先注册代码}）。
     * @return
     * @throws GeneralSecurityException
     */
    public byte[] specAESCrypto(int mode, byte[] todo, byte[] secret, String provider) throws GeneralSecurityException {
        var key = new SecretKeySpec(secret, "AES");
        return crypto(mode, todo, key, "AES", provider);
    }

    public String rsaStr2StrPubEncrypt(String message, String pubKey) throws GeneralSecurityException {
        return rsaStr2StrPubEncrypt(message, pubKey, null);
    }

    public String rsaStr2StrPubEncrypt(String message, String pubKey, String provider) throws GeneralSecurityException {
        return encoder.encodeToString(
                specRSACrypto(
                        Cipher.ENCRYPT_MODE,
                        message.getBytes(StandardCharsets.UTF_8),
                        decoder.decode(pubKey),
                        PUBKEY_MODE,
                        provider
                )
        );
    }

    public String rsaStr2StrPriEncrypt(String message, String priKey) throws GeneralSecurityException {
        return rsaStr2StrPriEncrypt(message, priKey, null);
    }

    public String rsaStr2StrPriEncrypt(String message, String priKey, String provider) throws GeneralSecurityException {
        return encoder.encodeToString(
                specRSACrypto(
                        Cipher.ENCRYPT_MODE,
                        message.getBytes(StandardCharsets.UTF_8),
                        decoder.decode(priKey),
                        PRIKEY_MODE,
                        provider
                )
        );
    }

    public String rsaStr2StrPubDecrypt(String ciphertext, String pubKey) throws GeneralSecurityException {
        return rsaStr2StrPubDecrypt(ciphertext, pubKey, null);
    }

    public String rsaStr2StrPubDecrypt(String ciphertext, String pubKey, String provider) throws GeneralSecurityException {
        return new String(
                specRSACrypto(
                        Cipher.DECRYPT_MODE,
                        decoder.decode(ciphertext),
                        decoder.decode(pubKey),
                        PUBKEY_MODE,
                        provider
                ),
                StandardCharsets.UTF_8
        );
    }

    public String rsaStr2StrPriDecrypt(String ciphertext, String priKey) throws GeneralSecurityException {
        return rsaStr2StrPriDecrypt(ciphertext, priKey, null);
    }

    public String rsaStr2StrPriDecrypt(String ciphertext, String priKey, String provider) throws GeneralSecurityException {
        return new String(
                specRSACrypto(
                        Cipher.DECRYPT_MODE,
                        decoder.decode(ciphertext),
                        decoder.decode(priKey),
                        PRIKEY_MODE,
                        provider
                ),
                StandardCharsets.UTF_8
        );
    }

    /**
     * RSA加解密方法
     *
     * @param mode     {@linkplain Integer int}类型。{@link Cipher#ENCRYPT_MODE},{@link Cipher#DECRYPT_MODE}两种，分别代表加密模式和解密模式。含义同原生方法。
     * @param todo     {@linkplain Byte byte}[]类型。<br/>处于加密模式时，此参数应为待加密字节数组，例如{@link String#getBytes(Charset)}<br/>处于解密模式时，此参数应为待解密的字节数组，例如{@link Base64.Decoder#decode(String)}。
     * @param secret   {@linkplain Byte byte}[]类型。一般从{@link Base64.Decoder#decode(String)}解码而来。
     * @param type     {@linkplain Integer int}类型。取值{@link MixinCryptor#PUBKEY_MODE}或{@link MixinCryptor#PRIKEY_MODE}。
     * @param provider @param provider {@link String}类型。当使用null时，使用java默认实现。可传递providerNameString来使用指定供应商（可能需要{@linkplain Security#addProvider(Provider) 额外的事先注册代码}）。
     * @return
     * @throws GeneralSecurityException
     */
    public byte[] specRSACrypto(int mode, byte[] todo, byte[] secret, int type, String provider) throws GeneralSecurityException {
        KeyFactory keyFactory = provider == null ? KeyFactory.getInstance("RSA") : KeyFactory.getInstance("RSA", provider);
        return crypto(
                mode,
                todo,
                PUBKEY_MODE == type ?
                        keyFactory.generatePublic(new X509EncodedKeySpec(secret)) :
                        keyFactory.generatePrivate(new PKCS8EncodedKeySpec(secret)),
                "RSA",
                provider
        );
    }

    /**
     * wrapped vanilla crypt function
     *
     * @throws GeneralSecurityException
     */
    public byte[] crypto(int mode, byte[] todo, Key secret, String algorithm, String provider) throws GeneralSecurityException {
        Cipher cipher = provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
        cipher.init(mode, secret);
        return cipher.doFinal(todo);
    }

    /**
     * 去除各种RSA先导格式，方便base64操作
     *
     * @param formattedString
     * @return
     */
    public String reformatRSAKeyString(String formattedString) {
        return Arrays.stream(formattedString.split("\n"))
                .dropWhile(s -> s.contains("-----"))
                .takeWhile(s -> !s.contains("-----"))
//                .filter(s->!s.contains("-----"))
                .collect(Collectors.joining());
    }

    public HashMap<String, String> generateRSA(int size) throws GeneralSecurityException {
        int realSize = Math.max(512, size);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(realSize, rand);
        KeyPair pair = generator.generateKeyPair();
        HashMap<String, String> map = new HashMap<>(2);
        map.put("public", encoder.encodeToString(pair.getPublic().getEncoded()));
        map.put("private", encoder.encodeToString(pair.getPrivate().getEncoded()));
        return map;
    }

    public String generateAES(int size) throws GeneralSecurityException {
        int realSize = Math.min(256, size);
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(realSize, rand);
        return encoder.encodeToString(generator.generateKey().getEncoded());
    }
}
