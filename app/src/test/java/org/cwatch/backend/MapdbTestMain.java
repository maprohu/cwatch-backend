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


public class MapdbTestMain {

	private DB db;
	private BTreeMap<Long, Object> pos;
	private Atomic.Long keyinc;

	@Before
	public void setup() {
		File file = new File("target/mapdb/append.db");
		file.getParentFile().mkdirs();
		db = DBMaker
		//.appendFileDB(file)
		.fileDB(file)
		.closeOnJvmShutdown()
		.mmapFileEnable()
		.make();

		pos = db.treeMapCreate("pos").keySerializer(BTreeKeySerializer.LONG).makeOrGet();
		keyinc = db.atomicLong("pos_keyinc");
	}
	
	@After
	public void teardown() {
		db.close();
	}

	int c = 0;
	
	@Test
	public void testInsert() {
		IntStream.rangeClosed(1, 100000000).forEach(i -> {
			pos.put(keyinc.incrementAndGet(), "boo"+i);
			if (c++ % 1000000 == 0) {
				db.commit();
			}
		});
		db.commit();
		
	}

	
	@Test
	public void testLoad() {
		IntStream.rangeClosed(1, 1000).forEach(i->{
			System.out.println(pos.get(ThreadLocalRandom.current().nextLong(keyinc.get())));
		});
	}
}
