package org.cwatch.backend;

import org.apache.camel.builder.RouteBuilder;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.TypedPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.route.Direct;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.backend.store.VesselId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class RouteConfiguration {

	@Component
	public static class Routes extends RouteBuilder {

		@Autowired
		DefaultPositionStore<Integer, AisPosition> aisPositionStore;
		
		@Autowired
		DefaultPositionStore<Integer, LritPosition> lritPositionStore;
		
		@Autowired
		DefaultPositionStore<String, VmsPosition> vmsPositionStore;
		
		@Autowired
		DefaultIdentityStore<VesselId, Integer> mmsiIdentityStore;
		
		@Autowired
		DefaultIdentityStore<VesselId, Integer> imoIdentityStore;
		
		@Autowired
		DefaultIdentityStore<VesselId, String> irIdentityStore;
		
		@Override
		public void configure() throws Exception {
			Direct.AIS_POSITION.from(this)
			.multicast().parallelProcessing()
			.bean(aisPositionStore)
			.process(ex -> latestPosition(ex.getIn().getBody(AisPosition.class), mmsiIdentityStore));

			Direct.LRIT_POSITION.from(this)
			.multicast().parallelProcessing()
			.bean(lritPositionStore)
			.process(ex -> latestPosition(ex.getIn().getBody(LritPosition.class), imoIdentityStore));

			Direct.VMS_POSITION.from(this)
			.multicast().parallelProcessing()
			.bean(vmsPositionStore)
			.process(ex -> latestPosition(ex.getIn().getBody(VmsPosition.class), irIdentityStore));
		}
		
		<I, P extends TypedPosition<I>> void latestPosition(P position, DefaultIdentityStore<VesselId, I> ids) {
			
		}

	}
	
}
