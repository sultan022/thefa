package com.thefa.audit.config;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.thefa.audit.PlayerAuditApplication;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:/application.test.properties")
@SpringBootTest(classes = { PlayerAuditApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public abstract class AbstractIntegrationTest {

    private static RedisContainer redis = RedisContainer.build();

    private static MySQLContainer mySQL = new MySQLContainer("mysql:5.6")
            .withDatabaseName("fa_player_audit")
            .withUsername("omer")
            .withPassword("password");

    private static GoogleCloudContainer datastore = GoogleCloudContainer.buildDatastore();

    private static GoogleCloudContainer pubsub = GoogleCloudContainer.buildPubsub();

    static {
        redis.start();
        mySQL.start();
        datastore.start();
        pubsub.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {

            String jdbcUrl = "jdbc:mysql://" + mySQL.getContainerIpAddress() + ":" + mySQL.getMappedPort(3306) +  "/fa_player_audit";

            TestPropertyValues values = TestPropertyValues.of(
                    "spring.redis.host=" + redis.getContainerIpAddress(),
                    "spring.redis.port=" + redis.getMappedPort(6379),
                    "spring.datasource.url=" + jdbcUrl,
                    "spring.datasource.username=omer",
                    "spring.datasource.password=password",
                    "spring.flyway.url=" + jdbcUrl,
                    "spring.flyway.user=omer",
                    "spring.flyway.password=password",
                    "pubsub.emulator.dev.host=" + pubsub.getContainerIpAddress(),
                    "pubsub.emulator.dev.port=" + pubsub.getMappedPort(8433)
            );
            values.applyTo(applicationContext);
        }
    }

    protected MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @TestConfiguration
    public static class TestConfig {

        @Primary @Bean
        public Datastore getTestDatastore() {
            return DatastoreOptions.newBuilder()
                    .setHost(datastore.getContainerIpAddress() + ":" + datastore.getMappedPort(8432))
                    .setProjectId("the-fa-api-dev")
                    .build()
                    .getService();
        }

    }
}
