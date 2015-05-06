package org.cwatch.backend;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.CompositeTrackStore;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.MemoryAisPosition;
import org.cwatch.backend.store.MemoryLritPosition;
import org.cwatch.backend.store.MemoryPositionStore;
import org.cwatch.backend.store.MemoryVmsPosition;
import org.cwatch.backend.store.VesselId;
import org.cwatch.backend.test.IdentityGenerator;
import org.cwatch.backend.test.PositionGenerator;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class MemoryTest {
	
	@Produce(uri="direct:AIS_POSITION")
	ProducerTemplate aisPosition;

	@Produce(uri="direct:LRIT_POSITION")
	ProducerTemplate lritPosition;

	@Produce(uri="direct:VMS_POSITION")
	ProducerTemplate vmsPosition;
	
	@Autowired
	MemoryPositionStore<Integer, AisPosition> aisPositionStore;
	
	@Autowired
	MemoryPositionStore<Integer, LritPosition> lritPositionStore;
	
	@Autowired
	MemoryPositionStore<String, VmsPosition> vmsPositionStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, Integer> mmsiIdentityStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, Integer> imoIdentityStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, String> irIdentityStore;
	
	@Autowired
	CompositeTrackStore<VesselId> trackStore;
	
	@Before
	public void init() {
		aisPositionStore.clear();
		lritPositionStore.clear();
		vmsPositionStore.clear();
	}
	
	@Test
	public void test() {
		int count = 100000;
		
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
	
	@Test
	public void testIds() throws InterruptedException {
		int days = 100;
		int posPerDay = 10;

		IdentityGenerator<VesselId> idg = IdentityGenerator.newInstance();
		idg.setFrom(DateTime.now().minusDays(days).toDate());
		idg.setVesselCount(100);
		idg.setChangeCount(3);
		
		idg.generate(mmsiIdentityStore, (v, d) -> v.getId() * 1000 + ThreadLocalRandom.current().nextInt(100, 200));
		idg.generate(imoIdentityStore, (v, d) -> v.getId() * 1000 + ThreadLocalRandom.current().nextInt(200, 300));
		idg.generate(irIdentityStore, (v, d) -> Integer.toString(v.getId() * 1000 + ThreadLocalRandom.current().nextInt(300, 400)));
		
		
		ExecutorService save = Executors.newFixedThreadPool(8);
		//ExecutorService save = Executors.newSingleThreadExecutor();
		
		
		PositionGenerator pg = new PositionGenerator(save);
		pg.setPositionCount(posPerDay*days);
		pg.setFrom(idg.getFrom());
		pg.setTo(idg.getTo());
		
		Stopwatch timer = Stopwatch.createStarted();
		
		idg.getVessels().forEach(v -> {
			pg.generate(pos -> {

				switch(ThreadLocalRandom.current().nextInt(3)) {
				case 0:
					aisPosition.sendBody(new MemoryAisPosition(
							mmsiIdentityStore.get(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue() 
					));
					break;
				case 1:
					lritPosition.sendBody(new MemoryLritPosition(
							imoIdentityStore.get(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue() 
					));
					break;
				case 2:
					vmsPosition.sendBody(new MemoryVmsPosition(
							irIdentityStore.get(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue() 
					));
					break;
				}
				
				
			}); 
			
		});
		
		save.shutdown();
		save.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		timer.stop();

		
		
		idg.getVessels().forEach(v -> {
			Assert.assertEquals(pg.getPositionCount(), trackStore.queryTrack(v, Range.<Date>all()).count());
		});
		
		int positionTotal = idg.getVesselCount() * pg.getPositionCount();
		
		System.out.println(String.format(
				"Count = %,d msgs; Elapsed = %s; Rate = %f msg/sec", 
				positionTotal, 
				timer.toString(), 
				positionTotal * 1000.0 / timer.elapsed(TimeUnit.MILLISECONDS)));
		
		
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
