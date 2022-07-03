package net.shyshkin.study.microservices.configserver;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class JasyptTest {

    @Test
    void testEncryption() {

        //given
        String password = "plain_jasypt_password_not_recommended";
        String secret = "springCloud_Pwd!";

        //when
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
        String encryptedSecret = encryptor.encrypt(secret);

        //then
        log.debug("Encrypted: `{}`", encryptedSecret);
        String decryptedSecret = encryptor.decrypt(encryptedSecret);
        log.debug("Decrypted: `{}`", decryptedSecret);
        assertThat(decryptedSecret).isEqualTo(secret);
    }
}