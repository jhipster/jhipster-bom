package tech.jhipster.security.ssl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tech.jhipster.security.RandomUtil;

class RandomUtilTest {

    @Test
    void testGenerateRandomAlphanumericString() {
        String randomAlphaString = RandomUtil.generateRandomAlphanumericString();

        // Check if the generated string is not null and has the expected length
        assertNotNull(randomAlphaString);
        assertTrue(randomAlphaString.length() == 20);
    }

    @Test
    void testGeneratePassword() {
        String password = RandomUtil.generatePassword();

        // Check if the generated password is not null and has the expected length
        assertNotNull(password);
        assertTrue(password.length() == 20);
    }
}
