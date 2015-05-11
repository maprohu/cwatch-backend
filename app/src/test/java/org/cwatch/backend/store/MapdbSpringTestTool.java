package org.cwatch.backend.store;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.cwatch.backend.MapdbStoreConfiguration;
import org.cwatch.backend.RouteConfiguration;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.process.LatestPositionProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class MapdbSpringTestTool {
	
	@Autowired
	MapdbPositionStore<Integer, AisPosition> aisPositionStore;
	
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(MapdbSpringTestTool.class);
	
	@Test
	public void test() {
		final int POS_COUNT    = 5000000;

		ThreadLocalRandom rnd = ThreadLocalRandom.current();	
		IntStream.rangeClosed(1, POS_COUNT).forEach(i -> {
			aisPositionStore.save(new MemoryAisPosition(
					rnd.nextInt(100), 
					new Date(), 
					rnd.nextDouble(), 
					rnd.nextDouble(),
					rnd.nextDouble()
			    ));
		});
		
	}
	
	@Configuration
	@EnableAutoConfiguration
	@Import({
		MapdbStoreConfiguration.class,
		RouteConfiguration.class
	})
	public static class Context {
		
		@Bean
		LatestPositionProcessor latestPositionProcessor() {
			return new LatestPositionProcessor() {
				
				@Override
				public void processVms(VmsPosition position) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void processLrit(LritPosition position) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void processDefault(DefaultPosition position) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void processAis(AisPosition position) {
					// TODO Auto-generated method stub
					
				}
			};
		}
	}

}
