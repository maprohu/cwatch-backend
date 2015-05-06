package org.cwatch.backend;

import org.apache.camel.builder.RouteBuilder;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.route.Direct;
import org.cwatch.backend.store.DefaultPositionStore;
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
		
		@Override
		public void configure() throws Exception {
			Direct.AIS_POSITION.from(this)
			.bean(aisPositionStore);
			Direct.LRIT_POSITION.from(this)
			.bean(lritPositionStore);
			Direct.VMS_POSITION.from(this)
			.bean(vmsPositionStore);
		}

	}
	
}
