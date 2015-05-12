package org.cwatch.backend.store;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Pair;

import com.google.common.base.Stopwatch;

public class MapDbPumpTestTool {
	
	@Test
	public void test() {
		int vessels = 100000;
		int days = 1;
		int reportingPeriodMinutes = 3;
		
		File file = new File("target/mapdb/pump.db");
		file.getParentFile().mkdirs();
		file.delete();
		
		Stopwatch sw = Stopwatch.createStarted();
		
		DB db = DBMaker
		.fileDB(file)
		.transactionDisable()
		.metricsEnable()
		.metricsExecutorEnable()
		.mmapFileEnable()
		.make();
		
		int recordCount = (int) (vessels * (TimeUnit.MINUTES.convert(days, TimeUnit.DAYS) / reportingPeriodMinutes));
		long millisDiff = TimeUnit.MILLISECONDS.convert(reportingPeriodMinutes, TimeUnit.MINUTES);
		
		Iterator<Pair<Long, MemoryAisPosition>> values = IntStream
		.rangeClosed(1, recordCount)
		.mapToObj(i -> new Fun.Pair<Long, MemoryAisPosition>((long) (recordCount-i), new MemoryAisPosition(
				ThreadLocalRandom.current().nextInt(vessels),
				new Date(System.currentTimeMillis()-i*millisDiff),
				ThreadLocalRandom.current().nextDouble(-85, 85),
				ThreadLocalRandom.current().nextDouble(-175, 175),
				ThreadLocalRandom.current().nextDouble(0, 360)
		)))
		.iterator();
		
		db
		.treeMapCreate("data")
		.pumpSource(values)
		.makeLongMap();
		
		db.commit();
		db.close();
	
		System.out.println(String.format(
				"Count = %,d msgs; Elapsed = %s; Rate = %f msg/sec", 
				recordCount, 
				sw.toString(), 
				recordCount * 1000.0 / sw.elapsed(TimeUnit.MILLISECONDS)));		
	}

}
