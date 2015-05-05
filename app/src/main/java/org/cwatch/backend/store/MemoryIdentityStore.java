package org.cwatch.backend.store;

import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class MemoryIdentityStore<V, I> implements DefaultIdentityStore<V, I> {

	final Map<V, Vessel> vessels = CacheBuilder.newBuilder().build(CacheLoader.from((V v) -> new Vessel(v))).asMap();
	
	@Override
	public Stream<IdentityPeriod<I>> queryIdentityfiers(V vessel, Range<Date> period) {
		return vessels.get(vessel).queryIdentityfiers(period);
	}

	@Override
	public void set(V vessel, Range<Date> period, I identifier) {
		vessels.get(vessel).set(period, identifier);
	}
	
	
	class Vessel {
		final V vessel;
		
		final RangeMap<Date, I> identifiers = TreeRangeMap.create();
		
		public Vessel(V vessel) {
			super();
			this.vessel = vessel;
		}

		public Stream<IdentityPeriod<I>> queryIdentityfiers(Range<Date> period) {
			return 
					identifiers.subRangeMap(period).asMapOfRanges().entrySet().stream()
					.map(e -> new DefaultIdentityPeriod<I>(e.getValue(), e.getKey()));
		}

		public void set(Range<Date> period, I identifier) {
			identifiers.put(period, identifier);
		}
	}

}
