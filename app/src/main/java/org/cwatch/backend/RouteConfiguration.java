package org.cwatch.backend;

import org.apache.camel.builder.RouteBuilder;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.TypedPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.process.LatestPositionProcessor;
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
		private DefaultPositionStore<Integer, AisPosition> aisPositionStore;
		
		@Autowired
		private DefaultPositionStore<Integer, LritPosition> lritPositionStore;
		
		@Autowired
		private DefaultPositionStore<String, VmsPosition> vmsPositionStore;
		
		@Autowired
		private LatestPositionProcessor latestPositionProcessor;
		
		@Override
		public void configure() throws Exception {
			Direct.AIS_POSITION.from(this)
//			.multicast()//.parallelProcessing()
			.process(ex -> aisPositionStore.save(ex.getIn().getBody(AisPosition.class)))
			.process(ex -> latestPositionProcessor.processAis(ex.getIn().getBody(AisPosition.class)))
			;

			Direct.LRIT_POSITION.from(this)
//			.multicast()//.parallelProcessing()
			.process(ex -> lritPositionStore.save(ex.getIn().getBody(LritPosition.class)))
			.process(ex -> latestPositionProcessor.processLrit(ex.getIn().getBody(LritPosition.class)))
			;

			Direct.VMS_POSITION.from(this)
//			.multicast()//.parallelProcessing()
			.process(ex -> vmsPositionStore.save(ex.getIn().getBody(VmsPosition.class)))
			.process(ex -> latestPositionProcessor.processVms(ex.getIn().getBody(VmsPosition.class)))
			;
		}
		
		<I, P extends TypedPosition<I>> void latestPosition(P position, DefaultIdentityStore<VesselId, I> ids) {
			
		}

	}
	
}
