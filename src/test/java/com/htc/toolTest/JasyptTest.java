package com.htc.toolTest;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {
    @Test
    public void testEncrypt(){
        StandardPBEStringEncryptor sPBESE=new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config=new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");    //加密的算法
        config.setPassword("hubert");   //加密的密钥，必须是ASCII码
        sPBESE.setConfig(config);

        String plainText="12345678";     //明文字符串
        String encryptedText=sPBESE.encrypt(plainText);
        System.out.println("加密后: ENC("+encryptedText+")");

        plainText=sPBESE.decrypt(encryptedText);
        System.out.println("解密后:"+plainText);
    }
    
    @Test
    public void testDecrypt(){
        StandardPBEStringEncryptor sPBESE=new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config=new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");    //加密的算法，这个是默认算法
        config.setPassword("hubert");   //加密的密钥，必须是ASCII码
        sPBESE.setConfig(config);

        String encryptedText="d966DCMihw3GW8kq/6W6Ow==";
        String plainText=sPBESE.decrypt(encryptedText);
        System.out.println(plainText);
    }
}
