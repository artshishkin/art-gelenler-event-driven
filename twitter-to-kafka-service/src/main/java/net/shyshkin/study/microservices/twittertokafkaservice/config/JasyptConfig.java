package net.shyshkin.study.microservices.twittertokafkaservice.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class JasyptConfig {

    @Bean(name = "encryptorBean")
    StringEncryptor stringEncryptor(Environment environment) {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        String password = environment.getProperty("jasypt.encryptor.password");
//        log.debug("Password: `{}`", password);
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());

        return encryptor;
    }
}
