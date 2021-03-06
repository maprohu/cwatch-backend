package org.cwatch.backend;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.backend.store.MemoryPositionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MemoryIdentityStoreConfiguration.class, StoreConfiguration.class})
public class MemoryStoreConfiguration {
	
	@Bean
	DefaultPositionStore<Integer, AisPosition> aisPositionStore() {
		return new MemoryPositionStore<Integer, AisPosition>();
	}

	@Bean
	DefaultPositionStore<Integer, LritPosition> lritPositionStore() {
		return new MemoryPositionStore<Integer, LritPosition>();
	}

	@Bean
	DefaultPositionStore<String, VmsPosition> vmsPositionStore() {
		return new MemoryPositionStore<String, VmsPosition>();
	}

}
