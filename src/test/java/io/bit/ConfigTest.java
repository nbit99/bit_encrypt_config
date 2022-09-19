package io.bit;

import io.bit.encrypt.config.CommonConfig;
import org.junit.Test;

import java.util.Properties;

public class ConfigTest {

    @Test
    public void testNoEncryptConfigFile(){
        Properties properties = null;
        try {
            properties = CommonConfig.getProperties("test.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key1 = "test.key1";
        String kv1 = properties.getProperty(key1);

        System.out.println(key1 + ":" + kv1);
    }

    @Test
    public void testEncryptConfigFile(){
        //test2.properties 如果不存在，则读取test2.encrypt
        //openssl aes-128-cbc -k 123qwe -base64 -md md5 -in c.json > c.encrypt
        Properties properties = null;
        try {
            properties = CommonConfig.getProperties("test2.properties", "12345678qwertyui");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key1 = "test.key3";
        String kv1 = properties.getProperty(key1);

        System.out.println(key1 + ":" + kv1);
    }
}
