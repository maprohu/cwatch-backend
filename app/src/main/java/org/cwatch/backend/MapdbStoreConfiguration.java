package org.cwatch.backend;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.backend.store.MapdbPositionStore;
import org.mapdb.Serializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MemoryIdentityStoreConfiguration.class, StoreConfiguration.class})
@EnableConfigurationProperties(CwatchBackendProperties.class)
public class MapdbStoreConfiguration {
	
	@Bean
	DefaultPositionStore<Integer, AisPosition> aisPositionStore() {
		return new MapdbPositionStore<Integer, AisPosition>("ais", Serializer.INTEGER_PACKED);
	}

	@Bean
	DefaultPositionStore<Integer, LritPosition> lritPositionStore() {
		return new MapdbPositionStore<Integer, LritPosition>("lrit", Serializer.INTEGER_PACKED);
	}

	@Bean
	DefaultPositionStore<String, VmsPosition> vmsPositionStore() {
		return new MapdbPositionStore<String, VmsPosition>("vms", Serializer.STRING);
	}

}
