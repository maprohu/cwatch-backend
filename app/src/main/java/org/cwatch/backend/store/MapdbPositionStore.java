
package org.cwatch.backend.store;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.NavigableSet;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.cwatch.backend.CwatchBackendProperties;
import org.cwatch.backend.message.TypedPosition;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Bind.MapListener;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Range;

public class MapdbPositionStore<I extends Serializable, P extends TypedPosition<I>&Serializable> implements DefaultPositionStore<I, P> {

	final String dataTypeName;
	final Serializer<? super I> idSerializer;
	
	public MapdbPositionStore(String dataTypeName, Serializer<? super I> idSerializer) {
		super();
		this.dataTypeName = dataTypeName;
		this.idSerializer = idSerializer;
	}

	public MapdbPositionStore(String dataTypeName) {
		this(dataTypeName, Serializer.BASIC);
	}

	private static final String dataDirRelativePath = "mapdb/position";
	
	@Autowired
	CwatchBackendProperties properties = new CwatchBackendProperties();

	private NavigableSet<Object[]> lookupTimestamp;
	
	@PostConstruct
	public void init() {
		File file = new File(new File(properties.getDataDir(), dataDirRelativePath), dataTypeName+".db");
		file.getParentFile().mkdirs();
		file.delete();
		
		db = DBMaker
				.fileDB(file)
				.mmapFileEnable()
				.transactionDisable()
				.asyncWriteEnable()
//				.commitFileSyncDisable()
				.metricsEnable()
				.metricsExecutorEnable()
				.make();
		
		table = db
				.treeMapCreate(dataTypeName)
				.keySerializer(BTreeKeySerializer.LONG)
				.makeOrGet();
		
		sequence = db
				.atomicLong(dataTypeName+"_seq");
		
		// TODO delta encoding  https://github.com/jankotek/MapDB/blob/master/src/main/java/org/mapdb/BTreeKeySerializer.java
		lookupIdTimestamp = db
				.treeSetCreate(dataTypeName+"_lookupIdTimestamp")
				.serializer(BTreeKeySerializer.ARRAY3)
//				.serializer(new ArrayKeySerializer(
//			            new Comparator[]{Fun.COMPARATOR,Fun.COMPARATOR,Fun.COMPARATOR},
//			            new Serializer[]{idSerializer, Serializer.LONG_PACKED, Serializer.LONG_PACKED}
//			    ))
				.makeOrGet();
		lookupTimestamp = db
				.treeSetCreate(dataTypeName+"_lookupTimstamp")
				.serializer(BTreeKeySerializer.ARRAY2)
//				.serializer(new ArrayKeySerializer(
//			            new Comparator[]{Fun.COMPARATOR,Fun.COMPARATOR},
//			            new Serializer[]{Serializer.LONG_PACKED, Serializer.LONG_PACKED}
//			    ))
				.makeOrGet();

		lookupListener = (key, oldVal, newVal) -> {
			lookupIdTimestamp.add(new Object[] {newVal.getId(), newVal.getTimestamp().getTime(), key});
			lookupTimestamp.add(new Object[] {newVal.getTimestamp().getTime(), key});
		};
		
		table.modificationListenerAdd(
				lookupListener
		);
		
	}
	
	@PreDestroy
	public void close() {
		db.commit();
		db.close();
	}
	
	private DB db;

	private BTreeMap<Long, P> table;

	private org.mapdb.Atomic.Long sequence;

	private NavigableSet<Object[]> lookupIdTimestamp;
	private MapListener<Long, P> lookupListener;
	
//	int counter = 0;
//	private static final int COMMIT_COUNT = 100000;
	
	@Override
	public void save(P position) {
		table.put(sequence.incrementAndGet(), position);
//		if (counter++ == COMMIT_COUNT) {
//			counter = 0;
//			db.commit();
//		}
	}

	@Override
	public Stream<P> queryVessel(I id, Range<Date> period) {
		return applyPeriod(
				lookupIdTimestamp.tailSet(new Object[]{id}, true), 
				period, 
				date -> new Object[] {id, date},
				s -> s.headSet(new Object[]{id, Long.MAX_VALUE}, true)
		)
		.stream()
		.map(element -> table.get(element[2]));
	}

//	private SortedSet<Object[]> getPeriod(
//			NavigableSet<Object[]> source,
//			Range<Date> period, 
//			LongFunction<Object[]> keyFunction
//	) {
//		SortedSet<Object[]> resultSet;
//		if (period.hasLowerBound() && period.hasUpperBound()) {
//			resultSet = source.subSet(
//					keyFunction.apply(period.lowerEndpoint().getTime()),
//					keyFunction.apply(period.upperEndpoint().getTime())
//			);
//		} else if (period.hasLowerBound()) {
//			resultSet = source.tailSet(
//					keyFunction.apply(period.lowerEndpoint().getTime())					
//			);
//		} else if (period.hasUpperBound()) {
//			resultSet = source.headSet(
//					keyFunction.apply(period.upperEndpoint().getTime())					
//			);
//		} else {
//			resultSet = source;
//		}
//		return resultSet;
//	}

	private NavigableSet<Object[]> applyPeriod(
			NavigableSet<Object[]> source,
			Range<Date> period, 
			LongFunction<Object[]> keyFunction,
			Function<NavigableSet<Object[]>, NavigableSet<Object[]>> noUpperBound
	) {
		if (period.hasLowerBound()) {
			source = source.tailSet(keyFunction.apply(period.lowerEndpoint().getTime()), true);
		}
		
		if (period.hasUpperBound()) {
			source = source.headSet(keyFunction.apply(period.upperEndpoint().getTime()), false);
		} else {
			source = noUpperBound.apply(source);
		}
		
		return source;
	}
	
	@Override
	public Stream<P> queryArea(Range<Date> period, double latitudeFrom,
			double latitudeTo, double longitudeFrom, double longitudeTo) {
		return 
				applyPeriod(
						lookupTimestamp, 
						period, 
						date -> new Object[] {date},
						Function.identity()
				)
				.stream()
				.map(element -> table.get(element[1]))
				.filter(p -> 
					latitudeFrom <= p.getLatitude() &&
					p.getLatitude() <= latitudeTo &&
					longitudeFrom <= p.getLongitude() &&
					p.getLongitude() <= longitudeTo
				);
	}
	
	public void clear() {
		if (!table.isEmpty()) {
			table.modificationListenerRemove(lookupListener);
			table.clear();
			lookupIdTimestamp.clear();
			lookupTimestamp.clear();
			table.modificationListenerAdd(lookupListener);
			db.commit();
		}
	}

	@Override
	public void flush() {
		db.commit();
	}
	

}

