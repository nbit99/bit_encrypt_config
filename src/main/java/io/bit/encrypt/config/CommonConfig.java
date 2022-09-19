package io.bit.encrypt.config;

import io.bit.encrypt.aes.Aes128CBCUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


/***
 * 加密配置文件
 * openssl aes-128-cbc -k 123qwe -base64 -md md5 -in c.json > c.encrypt
 * openssl aes-128-cbc -d -k 123 -base64 -in c.encrypt
 * @author apple
 *
 */
public class CommonConfig {
	private static Logger log = LogManager.getLogger(CommonConfig.class);
	private static final String EXT_NAME = ".encrypt";

	private static InputStream getInputStream(String file){
		return CommonConfig.class.getClassLoader().getResourceAsStream(file);
	}

	private static String getEncryptFile(String file){
		int last = file.lastIndexOf(".");

		if(last >= 0){
			return file.substring(0, last) + EXT_NAME;
		}
		return null;
	}

	private static Properties readFile(String file, String encryptPassword) throws Exception{
		boolean encrypt = false;//是否是加密配置
		//read hidden encrypt file
		InputStream inputStream = getInputStream(file);

		if(inputStream == null){
			String encFile = getEncryptFile(file);
			//read encrypt file
			inputStream = getInputStream(encFile);

			if(inputStream != null){
				encrypt = true;
			}
		}

		BufferedReader reader = null;
		if(inputStream != null){
			StringBuilder lastString = new StringBuilder();
			try{
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				reader = new BufferedReader(inputStreamReader);
				String tempString = null;
				while((tempString = reader.readLine()) != null){
					tempString = tempString.trim();
					if(!tempString.startsWith("#") && !tempString.startsWith("//")){
						lastString.append(tempString).append("\n");
					}
				}
				reader.close();
			}catch(IOException e){
				throw e;
			}finally{
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if(encrypt){
				String CONFIG_DECRYPT_PWD = (encryptPassword != null && encryptPassword.length() > 0) ? encryptPassword : System.getenv("CONFIG_DECRYPT_PWD");

				if(CONFIG_DECRYPT_PWD == null || CONFIG_DECRYPT_PWD.length() <= 0){
					throw new RuntimeException("DECRYPT_PWD is null, please set env var 'CONFIG_DECRYPT_PWD' or set encrypt password");
				}

				lastString = new StringBuilder(Aes128CBCUtils.decryptString(lastString.toString(), CONFIG_DECRYPT_PWD));
			}

			// 使用properties对象加载输入流
			try {
				return load(lastString.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static Properties load(String propertiesString) throws IOException {
		Properties properties = new Properties();
		properties.load(new StringReader(propertiesString));
		return properties;
	}

	private final static Map<String, Properties> propertiesMap = new ConcurrentHashMap<>();

	public static Properties getProperties(String filePath) throws Exception{
		return getProperties(filePath, null);
	}

	public static Properties getProperties(String filePath, String encryptPassword) throws Exception{
		Properties properties = propertiesMap.get(filePath);

		if(properties == null){
			synchronized (CommonConfig.class){
				Properties readProp = readFile(filePath, encryptPassword);

				if(readProp == null){
					readProp = new Properties();
				}
				properties = readProp;
				propertiesMap.put(filePath, properties);
			}
		}
		return properties;
	}


	/**
	 * 从控制台获取密码
	 * @return 返回从控制台获取的密码
	 */
	public static String getPassword() {
		Console console = System.console();

		if(console == null){
			Scanner scanner = new Scanner(System.in);
			return scanner.nextLine();
		}

		char[] passwordCharArray = console.readPassword();
		return new String(passwordCharArray);
	}

}
