package tech.jhipster.security.ssl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import tech.jhipster.security.RandomUtil;

class RandomUtilTest {

    @Test
    void testGenerateRandomAlphanumericString() {
        String randomAlphaString = RandomUtil.generateRandomAlphanumericString();

        // Check if the generated string is not null and has the expected length
        assertNotNull(randomAlphaString);
        assertEquals(20, randomAlphaString.length());
    }

    @Test
    void testGeneratePassword() {
        String password = RandomUtil.generatePassword();

        // Check if the generated password is not null and has the expected length
        assertNotNull(password);
        assertEquals(20, password.length());
    }
}
