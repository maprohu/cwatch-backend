package org.cwatch.backend.store;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.util.concurrent.SynchronousExecutorService;
import org.cwatch.backend.MapdbStoreConfiguration;
import org.cwatch.backend.RouteConfiguration;
import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.process.LatestPositionProcessor;
import org.cwatch.backend.test.IdentityGenerator;
import org.cwatch.backend.test.PositionGenerator;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
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

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class MapdbStoreTestTool {
	
	@Produce(uri="direct:AIS_POSITION")
	ProducerTemplate aisPosition;

	@Produce(uri="direct:LRIT_POSITION")
	ProducerTemplate lritPosition;

	@Produce(uri="direct:VMS_POSITION")
	ProducerTemplate vmsPosition;
	
	@Autowired
	MapdbPositionStore<Integer, AisPosition> aisPositionStore;
	
	@Autowired
	MapdbPositionStore<Integer, LritPosition> lritPositionStore;
	
	@Autowired
	MapdbPositionStore<String, VmsPosition> vmsPositionStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, Integer> mmsiIdentityStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, Integer> imoIdentityStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, String> irIdentityStore;
	
	@Autowired
	CompositeTrackStore<VesselId> trackStore;
	
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(MapdbStoreTestTool.class);
	
	@Before
	public void init() {
		LOG.info("cleaning...");
		aisPositionStore.clear();
		lritPositionStore.clear();
		vmsPositionStore.clear();
		LOG.info("cleaned.");
	}
	
	
	@Test
	public void test() {
		int count = 1000000;
		
		LOG.info("sending messages...");
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		IntStream.rangeClosed(1, count)
		.forEach(i -> {
			aisPosition.sendBody(new MemoryAisPosition(
				rnd.nextInt(100), 
				new Date(), 
				rnd.nextDouble(), 
				rnd.nextDouble(),
				rnd.nextDouble()
		    ));
			lritPosition.sendBody(new MemoryLritPosition(
					rnd.nextInt(100), 
					new Date(), 
					rnd.nextDouble(), 
					rnd.nextDouble(),
					rnd.nextDouble()
			));
			vmsPosition.sendBody(new MemoryVmsPosition(
					Integer.toString(rnd.nextInt(100)), 
					new Date(), 
					rnd.nextDouble(), 
					rnd.nextDouble(),
					rnd.nextDouble()
			));
		});
		LOG.info("sent.");
		
		LOG.info("checking count...");
		Assert.assertEquals(count, aisPositionStore.queryArea(
				Range.<Date>all(), 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY 
		).count());
		LOG.info("checked.");
	}
	
	@Test
	public void testIds() throws InterruptedException {
		int days = 1;
		int posPerDay = (int) (TimeUnit.MINUTES.convert(1, TimeUnit.DAYS) / 6);

		LOG.info("generating ids...");
		IdentityGenerator<VesselId> idg = IdentityGenerator.newInstance();
		idg.setFrom(DateTime.now().minusDays(days).toDate());
		idg.setVesselCount(100000);
		idg.setChangeCount(3);
		
		idg.generate(mmsiIdentityStore, (v, d) -> v.getId() * 1000 + ThreadLocalRandom.current().nextInt(100, 200));
		idg.generate(imoIdentityStore, (v, d) -> v.getId() * 1000 + ThreadLocalRandom.current().nextInt(200, 300));
		idg.generate(irIdentityStore, (v, d) -> Integer.toString(v.getId() * 1000 + ThreadLocalRandom.current().nextInt(300, 400)));
		LOG.info("generated.");
		
		
		LOG.info("sending messages...");
		//ExecutorService save = Executors.newFixedThreadPool(8);
		//ExecutorService save = Executors.newSingleThreadExecutor();
		ExecutorService save = new SynchronousExecutorService();
		
		
		PositionGenerator pg = new PositionGenerator(save);
		pg.setPositionCount(posPerDay*days);
		pg.setFrom(idg.getFrom());
		pg.setTo(idg.getTo());
		
		Stopwatch timer = Stopwatch.createStarted();
		
		idg.getVessels().forEach(v -> {
			pg.generate(pos -> {

				switch(ThreadLocalRandom.current().nextInt(3)) {
				case 0:
//					aisPosition.sendBody(new MemoryAisPosition(
					aisPositionStore.save(new MemoryAisPosition(
							mmsiIdentityStore.getIdentifier(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue(),
							pos.getDirection()
					));
					break;
				case 1:
//					lritPosition.sendBody(new MemoryLritPosition(
					lritPositionStore.save(new MemoryLritPosition(
							imoIdentityStore.getIdentifier(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue(),
							pos.getDirection() 
					));
					break;
				case 2:
//					vmsPosition.sendBody(new MemoryVmsPosition(
					vmsPositionStore.save(new MemoryVmsPosition(
							irIdentityStore.getIdentifier(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue(),
							pos.getDirection() 
					));
					break;
				}
				
				
			}); 
			
		});
		
		save.shutdown();
		save.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		aisPositionStore.flush();
		lritPositionStore.flush();
		vmsPositionStore.flush();

		timer.stop();

		LOG.info("sent.");
		
		int positionTotal = idg.getVesselCount() * pg.getPositionCount();
		
		System.out.println(String.format(
				"Count = %,d msgs; Elapsed = %s; Rate = %f msg/sec", 
				positionTotal, 
				timer.toString(), 
				positionTotal * 1000.0 / timer.elapsed(TimeUnit.MILLISECONDS)));
		
		
//		LOG.info("checking count...");
//		idg.getVessels().forEach(v -> {
//			Assert.assertEquals(pg.getPositionCount(), trackStore.queryTrack(v, Range.<Date>all()).count());
//		});
//		LOG.info("checked.");
		
		
		
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
