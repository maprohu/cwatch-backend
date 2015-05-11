package org.cwatch.backend;

import org.cwatch.backend.process.MemoryProfileService;
import org.cwatch.backend.process.ProfileService;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.MemoryIdentityStore;
import org.cwatch.backend.store.VesselId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(StoreConfiguration.class)
public class MemoryIdentityStoreConfiguration {
	
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
	
	@Bean
	ProfileService<VesselId> profileService() {
		return new MemoryProfileService<>();
	}

}
