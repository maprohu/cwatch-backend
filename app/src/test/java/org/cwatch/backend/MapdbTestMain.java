package org.cwatch.backend;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.cwatch.backend.store.MemoryAisPosition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.Atomic;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.google.common.base.Stopwatch;


public class MapdbTestMain {

	private static final int POS_COUNT    = 10000000;
	private static final int COMMIT_COUNT = 1000000;
	private DB db;
	private BTreeMap<Long, Object> pos;
	private Atomic.Long keyinc;

	@Before
	public void setup() {

		file = new File("target/mapdb/tx.db");
		file.getParentFile().mkdirs();
		
	}

	public void notx() {
		stopwatch = Stopwatch.createStarted();
		
		db = DBMaker
		.fileDB(file)
		.mmapFileEnable()
		.transactionDisable()
		.asyncWriteEnable()
		.metricsEnable()
		.metricsExecutorEnable()
		.make();

		pos = db.treeMapCreate("pos").keySerializer(BTreeKeySerializer.LONG).makeOrGet();
		keyinc = db.atomicLong("pos_keyinc");
		
	}
	public void tx() {
		stopwatch = Stopwatch.createStarted();
		
		db = DBMaker
		.fileDB(file)
		.mmapFileEnable()
		.commitFileSyncDisable()
		.asyncWriteEnable()
		.make();

		pos = db.treeMapCreate("pos").keySerializer(BTreeKeySerializer.LONG).makeOrGet();
		keyinc = db.atomicLong("pos_keyinc");
	}
	
	@After
	public void teardown() {
		db.commit();
		db.close();
		System.out.println(stopwatch.stop());
	}

	int c = 0;
	private Stopwatch stopwatch;
	private File file;
	
	@Test
	public void testInsertNoTx() {
		file.delete();
		notx();
		ThreadLocalRandom rnd = ThreadLocalRandom.current();	
		IntStream.rangeClosed(1, POS_COUNT).forEach(i -> {
			pos.put(keyinc.incrementAndGet(), new MemoryAisPosition(
					rnd.nextInt(100), 
					new Date(), 
					rnd.nextDouble(), 
					rnd.nextDouble(),
					rnd.nextDouble()
			    ));
		});
		
	}
	@Test
	public void testInsert() {
		file.delete();
		tx();
		
		IntStream.rangeClosed(1, POS_COUNT).forEach(i -> {
			pos.put(keyinc.incrementAndGet(), "boo"+i);
			if (c++ % COMMIT_COUNT == 0) {
				db.commit();
			}
		});
	}

	
	@Test
	public void testLoad() {
		tx();
		
		IntStream.rangeClosed(1, 1000).forEach(i->{
			System.out.println(pos.get(ThreadLocalRandom.current().nextLong(keyinc.get())));
		});
	}
}
