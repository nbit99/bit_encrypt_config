##对配置文件整体加密
###利用openssl加密配置文件
```shell
openssl aes-128-cbc -k 123qwe(密码) -base64 -md md5 -in xxx.propreties > xxx.encrypt
```
读取配置文件时会优先匹配存在的文件，如果找不到，则按文件名称查找.encrypt文件，仍然找不到则返回空的properties

###读取配置文件例子
```code
    @Test
    public void testEncryptConfigFile(){
        //test2.properties 如果不存在，则读取test2.encrypt
        //openssl aes-128-cbc -k 123qwe -base64 -md md5 -in c.json > c.encrypt
        Properties properties = CommonConfig.getProperties("test2.properties", "12345678qwertyui");
        String key1 = "test.key3";
        String kv1 = properties.getProperty(key1);

        System.out.println(key1 + ":" + kv1);
    }
```