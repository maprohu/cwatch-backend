package org.cwatch.backend;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.backend.store.MemoryIdentityStore;
import org.cwatch.backend.store.MemoryPositionStore;
import org.cwatch.backend.store.VesselId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
	
	@Bean
	DefaultIdentityStore<VesselId, Integer> mmsiIdentityStore() {
		return new MemoryIdentityStore<VesselId, Integer>();
	}

	@Bean
	DefaultIdentityStore<VesselId, Integer> imoIdentityStore() {
		return new MemoryIdentityStore<VesselId, Integer>();
	}

	@Bean
	DefaultIdentityStore<VesselId, String> irIdentityStore() {
		return new MemoryIdentityStore<VesselId, String>();
	}

}
