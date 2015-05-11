package org.cwatch.backend;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.cwatch.backend.store.MapdbPositionStore;
import org.cwatch.backend.store.MemoryAisPosition;
import org.cwatch.backend.store.MemoryPositionStore;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Range;

public class MapdbConcurrencyTestTool {

	
	@Test
	public void testSingle() {
		final int POS_COUNT    = 50000000;

		MapdbPositionStore<Integer, MemoryAisPosition> store = new MapdbPositionStore<Integer, MemoryAisPosition>("ais");
		store.init();

		ThreadLocalRandom rnd = ThreadLocalRandom.current();	
		IntStream.rangeClosed(1, POS_COUNT).forEach(i -> {
			store.save(new MemoryAisPosition(
					rnd.nextInt(100), 
					new Date(), 
					rnd.nextDouble(), 
					rnd.nextDouble(),
					rnd.nextDouble()
			    ));
		});
		
		store.close();
		
	}
	
	@Test
	public void testMulti() {
		final int POS_COUNT    = 50000000;

		MapdbPositionStore<Integer, MemoryAisPosition> store = new MapdbPositionStore<Integer, MemoryAisPosition>("ais");
		store.init();

		IntStream.rangeClosed(1, POS_COUNT).parallel().forEach(i -> {
			ThreadLocalRandom rnd = ThreadLocalRandom.current();	
			store.save(new MemoryAisPosition(
					rnd.nextInt(100), 
					new Date(), 
					rnd.nextDouble(), 
					rnd.nextDouble(),
					rnd.nextDouble()
			    ));
		});
		
		store.close();
		
	}
	
	
	@Test
	public void test() throws InterruptedException {
		int vesselCount = 5;
		int count = 100000;
		
		MapdbPositionStore<Integer, MemoryAisPosition> store = new MapdbPositionStore<Integer, MemoryAisPosition>("ais");
		store.init();
		
		ExecutorService save = Executors.newFixedThreadPool(200);
		IntStream.rangeClosed(1, count)
		.forEach(i -> save.execute(() -> 
				store.save(new MemoryAisPosition(
						ThreadLocalRandom.current().nextInt(vesselCount), 
						new Date(ThreadLocalRandom.current().nextLong()), 
						ThreadLocalRandom.current().nextDouble(), 
						ThreadLocalRandom.current().nextDouble(),
						ThreadLocalRandom.current().nextDouble()
				))
		));
		
		
		ExecutorService queryArea = Executors.newFixedThreadPool(200);
		IntStream.rangeClosed(1, count)
		.forEach(i -> queryArea.execute(() -> store.queryArea(
				Range.<Date>all(), 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY 
		)));

		ExecutorService queryVessel = Executors.newFixedThreadPool(200);
		IntStream.rangeClosed(1, count)
		.forEach(i -> queryVessel.execute(() -> store.queryVessel(
				ThreadLocalRandom.current().nextInt(vesselCount),
				Range.<Date>all() 
		)));
		
		queryArea.shutdown();
		queryArea.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		queryVessel.shutdown();
		queryVessel.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		save.shutdown();
		save.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		Assert.assertEquals(count, store.queryArea(
				Range.<Date>all(), 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY 
		).count());
		
		Assert.assertEquals(
				count, 
				IntStream.rangeClosed(0, vesselCount-1)
					.mapToLong(i -> store.queryVessel(
							i,
							Range.<Date>all()
					).count())
					.reduce(0, Long::sum)
		);
		
		store.close();
	}
	
	@Test
	public void testIds() throws InterruptedException {
	}
	
}
