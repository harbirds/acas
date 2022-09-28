package org.apereo.cas.services;

import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CosmosDbServiceRegistryConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.util.junit.EnabledIfListeningOnPort;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link CosmosDbServiceRegistryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Tag("CosmosDb")
@SpringBootTest(classes = {
    CasCoreHttpConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasCoreWebConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreNotificationsConfiguration.class,
    CasCoreAuthenticationMetadataConfiguration.class,
    RefreshAutoConfiguration.class,
    CosmosDbServiceRegistryConfiguration.class
}, properties = {
    "cas.http-client.host-name-verifier=none",
    "cas.service-registry.cosmos-db.uri=${#environmentVariables['COSMOS_DB_URL']}",
    "cas.service-registry.cosmos-db.key=${#environmentVariables['COSMOS_DB_KEY']}",
    "cas.service-registry.cosmos-db.database=RegisteredServicesDb",
    "cas.service-registry.cosmos-db.database-throughput=1000",
    "cas.service-registry.cosmos-db.max-retry-attempts-on-throttled-requests=5",
    "cas.service-registry.cosmos-db.indexing-mode=CONSISTENT",
    "cas.service-registry.cosmos-db.create-container=true"
})
@ResourceLock("cosmosdb-service")
@Getter
@EnabledIfEnvironmentVariable(named = "COSMOS_DB_URL", matches = ".+")
@EnabledIfEnvironmentVariable(named = "COSMOS_DB_KEY", matches = ".+")
public class CosmosDbServiceRegistryTests extends AbstractServiceRegistryTests {
    @Autowired
    @Qualifier("cosmosDbServiceRegistry")
    private ServiceRegistry newServiceRegistry;

    @BeforeEach
    public void deleteAll() throws Exception {
        Thread.sleep(3000);
        newServiceRegistry.deleteAll();
        assertTrue(newServiceRegistry.load().isEmpty());
    }
}
