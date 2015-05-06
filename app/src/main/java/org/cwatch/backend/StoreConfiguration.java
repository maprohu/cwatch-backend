package org.cwatch.backend;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.CompositeTrackStore;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.backend.store.SingleSourceTrackStore;
import org.cwatch.backend.store.VesselId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;

@Configuration
public class StoreConfiguration {

	@Bean
	SingleSourceTrackStore<VesselId, Integer, AisPosition> aisTrackStore(
			DefaultIdentityStore<VesselId, Integer> mmsiIdentityStore,
			DefaultPositionStore<Integer, AisPosition> aisPositionStore
	) {
		return new SingleSourceTrackStore<VesselId, Integer, AisPosition>(mmsiIdentityStore, aisPositionStore);
	}
	
	@Bean
	SingleSourceTrackStore<VesselId, Integer, LritPosition> lritTrackStore(
			DefaultIdentityStore<VesselId, Integer> imoIdentityStore,
			DefaultPositionStore<Integer, LritPosition> lritPositionStore
	) {
		return new SingleSourceTrackStore<VesselId, Integer, LritPosition>(imoIdentityStore, lritPositionStore);
	}
	
	@Bean
	SingleSourceTrackStore<VesselId, String, VmsPosition> vmsTrackStore(
			DefaultIdentityStore<VesselId, String> irIdentityStore,
			DefaultPositionStore<String, VmsPosition> vmsPositionStore
	) {
		return new SingleSourceTrackStore<VesselId, String, VmsPosition>(irIdentityStore, vmsPositionStore);
	}
	
	@Bean
	CompositeTrackStore<VesselId> trackStore(
			SingleSourceTrackStore<VesselId, Integer, AisPosition> aisTrackStore,
			SingleSourceTrackStore<VesselId, Integer, LritPosition> lritTrackStore,
			SingleSourceTrackStore<VesselId, String, VmsPosition> vmsTrackStore
	) {
		return new CompositeTrackStore<VesselId>(
				ImmutableList.of(
						aisTrackStore,
						lritTrackStore,
						vmsTrackStore
				)
		);
		
	}
	
	
}
