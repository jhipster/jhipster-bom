package tech.jhipster.config.h2;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThatCode;

class H2ConfigurationHelperTest {

    @Test
    void initH2Console() {

        assertThatCode(() -> {
            H2ConfigurationHelper.initH2Console("src/test/resources");
            new URL("http://localhost:8092").openConnection().connect();
        })
            .doesNotThrowAnyException();
    }
}
