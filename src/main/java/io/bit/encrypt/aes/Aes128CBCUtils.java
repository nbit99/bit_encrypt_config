package io.bit.encrypt.aes;


import io.bit.encrypt.utils.Base64;

public class Aes128CBCUtils {
    public static String decryptString(String content,String secret) throws Exception{
        OpenSSLAesDecrypter d = new OpenSSLAesDecrypter(128);

        byte[] deciphers = d.decipher(secret.getBytes(), Base64.decode2(content));

        if(deciphers == null){
            throw new Exception("decrypt failed");
        }
        String r = new String(d.decipher(secret.getBytes(),
                Base64.decode2(content)));

        return r;
    }
}

