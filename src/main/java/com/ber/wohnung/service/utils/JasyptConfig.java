package com.ber.wohnung.service.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    private String getSecretKey() {
        String masterPassword;
        if (System.getenv("MASTER_PASSWORD") != null) {
            masterPassword = System.getenv("MASTER_PASSWORD");
        } else {
            if (System.getProperty("master.password") != null) masterPassword = System.getProperty("master.password");
            else throw new IllegalArgumentException("Master password property 'master.password' or system env 'MASTER_PASSWORD' not set!!!");
        }
        return masterPassword;
    }

    @Bean
    public StandardPBEStringEncryptor standardPBEStringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(getSecretKey()); // Secret key used for encryption/decryption
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256"); // Encryption algorithm
        config.setKeyObtentionIterations(1000);
        config.setPoolSize(1);
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}
