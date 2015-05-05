package org.cwatch.backend;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.backend.store.MemoryAisPosition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Range;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class MemoryTest {
	
	@Produce(uri="direct:AIS_POSITION")
	ProducerTemplate aisPosition;
	
	@Autowired
	DefaultPositionStore<Integer, AisPosition> aisPositionStore;
	
	@Test
	public void test() {
		int count = 1000;
		
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		IntStream.rangeClosed(1, count)
		.forEach(i -> aisPosition.sendBody(new MemoryAisPosition(
				rnd.nextInt(), 
				new Date(rnd.nextLong()), 
				rnd.nextDouble(), 
				rnd.nextDouble())
		));
		
		Assert.assertEquals(count, aisPositionStore.queryArea(
				Range.<Date>all(), 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY 
		).count());
	}
	
	@Configuration
	@EnableAutoConfiguration
	@Import({
		MemoryStoreConfiguration.class,
		RouteConfiguration.class
	})
	public static class Context {
	}

}
