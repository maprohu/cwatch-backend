package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class MemoryIdentityStore<V, I> implements DefaultIdentityStore<V, I> {

	final LoadingCache<V, Vessel> vessels = CacheBuilder.newBuilder().build(CacheLoader.from(Vessel::new));
	
	@Override
	public Stream<IdentityPeriod<I>> queryIdentityfiers(V vessel, Range<Date> period) {
		return vessels.getUnchecked(vessel).queryIdentityfiers(period);
	}

	@Override
	public void set(V vessel, Range<Date> period, I identifier) {
		vessels.getUnchecked(vessel).set(period, identifier);
	}
	
	@Override
	public I get(V vessel, Date timestamp) {
		return vessels.getUnchecked(vessel).get(timestamp);
	}
	
	class Vessel {
		final V vessel;
		
		final RangeMap<Date, I> identifiers = TreeRangeMap.create();
		
		public Vessel(V vessel) {
			super();
			this.vessel = vessel;
		}

		public I get(Date timestamp) {
			return identifiers.get(timestamp);
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
