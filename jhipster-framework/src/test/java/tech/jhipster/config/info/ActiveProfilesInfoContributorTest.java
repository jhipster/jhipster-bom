package tech.jhipster.config.info;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;

public class ActiveProfilesInfoContributorTest {

    @Test
    public void activeProfilesShouldBeSetWhenProfilesActivated() {
        ConfigurableEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");
        environment.setDefaultProfiles("dev", "api-docs");

        ActiveProfilesInfoContributor contributor = new ActiveProfilesInfoContributor(environment);

        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        Assertions.assertThat((List<String>) info.get("activeProfiles")).contains("prod");
    }

    @Test
    public void defaultProfilesShouldBeSetWhenNoProfilesActivated() {
        ConfigurableEnvironment environment = new MockEnvironment();
        environment.setDefaultProfiles("dev", "api-docs");

        ActiveProfilesInfoContributor contributor = new ActiveProfilesInfoContributor(environment);

        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        Assertions.assertThat((List<String>) info.get("activeProfiles")).contains("dev", "api-docs");
    }
}
