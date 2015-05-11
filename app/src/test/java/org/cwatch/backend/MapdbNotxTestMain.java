package org.cwatch.backend;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.Atomic;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.google.common.base.Stopwatch;


public class MapdbNotxTestMain {

	private DB db;
	private BTreeMap<Long, Object> pos;
	private Atomic.Long keyinc;

	@Before
	public void setup() {
		
		dbfile = new File("target/mapdb/notx.db");
		dbfile.getParentFile().mkdirs();
		
		stopwatch = Stopwatch.createStarted();
		
		db = DBMaker
		//.appendFileDB(file)
		.fileDB(dbfile)
		.closeOnJvmShutdown()
		.mmapFileEnable()
		.transactionDisable()
		.asyncWriteEnable()
		.asyncWriteFlushDelay(1000)
		.commitFileSyncDisable()
		.make();

		pos = db.treeMapCreate("pos").keySerializer(BTreeKeySerializer.LONG).makeOrGet();
		keyinc = db.atomicLong("pos_keyinc");
	}
	
	@After
	public void teardown() {
		db.close();
		
		System.out.println(stopwatch.stop());
		
	}

	int c = 0;
	private Stopwatch stopwatch;
	private File dbfile;
	
	@Test
	public void testInsert() {
		db.close();
		dbfile.delete();
		setup();
		
		IntStream.rangeClosed(1, 5000000).forEach(i -> {
			pos.put(keyinc.incrementAndGet(), "boo"+i);
		});
	}

	
	@Test
	public void testLoad() {
		IntStream.rangeClosed(1, 1000).forEach(i->{
			System.out.println(pos.get(ThreadLocalRandom.current().nextLong(keyinc.get())));
		});
	}
}
