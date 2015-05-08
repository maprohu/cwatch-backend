package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeMap;

public class MemoryIdentityStore<V, I> implements DefaultIdentityStore<V, I> {

	final LoadingCache<V, DateRangeMap<V, I>> vesselToId = CacheBuilder.newBuilder().build(CacheLoader.from(DateRangeMap<V, I>::new));
	final LoadingCache<I, DateRangeMap<I, V>> idToVessel = CacheBuilder.newBuilder().build(CacheLoader.from(DateRangeMap<I, V>::new));
	
	@Override
	public Stream<IdentityPeriod<I>> queryIdentityfiers(V vessel, Range<Date> period) {
		return vesselToId.getUnchecked(vessel).queryIdentityfiers(period);
	}

	@Override
	public void set(V vessel, Range<Date> period, I identifier) {
		vesselToId.getUnchecked(vessel).set(period, identifier);
		idToVessel.getUnchecked(identifier).set(period, vessel);
	}
	
	@Override
	public I getIdentifier(V vessel, Date timestamp) {
		return vesselToId.getUnchecked(vessel).get(timestamp);
	}

	@Override
	public V getVessel(I identifier, Date timestamp) {
		return idToVessel.getUnchecked(identifier).get(timestamp);
	}

	@Override
	public V getLatestVessel(I identifier) {
		return idToVessel.getUnchecked(identifier).getLatest();
	}

	
//	class Identifier {
//		final I identifier;
//		
//		final RangeMap<Date, V> vesselLookup = TreeRangeMap.create();
//		
//		public Identifier(I identifier) {
//			super();
//			this.identifier = identifier;
//		}
//
//		public V get(Date timestamp) {
//			return vesselLookup.get(timestamp);
//		}
//
//		public Stream<IdentityPeriod<V>> queryIdentityfiers(Range<Date> period) {
//			return 
//					vesselLookup.subRangeMap(period).asMapOfRanges().entrySet().stream()
//					.map(e -> new DefaultIdentityPeriod<V>(e.getValue(), e.getKey()));
//		}
//
//		public void set(Range<Date> period, I identifier) {
//			identifiers.put(period, identifier);
//		}
//	}
//
//	class Vessel {
//		final V vessel;
//		
//		final RangeMap<Date, I> identifiers = TreeRangeMap.create();
//		
//		public Vessel(V vessel) {
//			super();
//			this.vessel = vessel;
//		}
//
//		public I get(Date timestamp) {
//			return identifiers.get(timestamp);
//		}
//
//		public Stream<IdentityPeriod<I>> queryIdentityfiers(Range<Date> period) {
//			return 
//					identifiers.subRangeMap(period).asMapOfRanges().entrySet().stream()
//					.map(e -> new DefaultIdentityPeriod<I>(e.getValue(), e.getKey()));
//		}
//
//		public void set(Range<Date> period, I identifier) {
//			identifiers.put(period, identifier);
//		}
//	}


	class DateRangeMap<K, T> {
		final K key;
		
		final TreeRangeMap<Date, T> lookup = TreeRangeMap.create();
		
		public DateRangeMap(K key) {
			super();
			this.key = key;
		}

		public T getLatest() {
			// TODO find a more efficient way to do this
			// either change the comparison order and get the first element
			// or try to add NavigableMap.lastEntry to RangeMap
			return get(new Date());
		}

		public T get(Date timestamp) {
			return lookup.get(timestamp);
		}

		public Stream<IdentityPeriod<T>> queryIdentityfiers(Range<Date> period) {
			return 
					lookup.subRangeMap(period).asMapOfRanges().entrySet().stream()
					.map(e -> new DefaultIdentityPeriod<T>(e.getValue(), e.getKey()));
		}

		public void set(Range<Date> period, T identifier) {
			lookup.put(period, identifier);
		}
	}


}
