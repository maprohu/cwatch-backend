package org.cwatch.backend;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.cwatch.backend.store.MemoryAisPosition;
import org.cwatch.backend.store.MemoryPositionStore;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Range;

public class MemoryConcurrencyTest {

	@Test
	public void test() throws InterruptedException {
		int vesselCount = 5;
		int count = 100000;
		
		MemoryPositionStore<Integer, MemoryAisPosition> store = new MemoryPositionStore<Integer, MemoryAisPosition>();
		
		ExecutorService save = Executors.newFixedThreadPool(200);
		IntStream.rangeClosed(1, count)
		.forEach(i -> save.execute(() -> 
				store.save(new MemoryAisPosition(
						ThreadLocalRandom.current().nextInt(vesselCount), 
						new Date(ThreadLocalRandom.current().nextLong()), 
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
	}
	
	@Test
	public void testIds() throws InterruptedException {
	}
	
}
