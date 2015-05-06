package org.cwatch.backend.store;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

import com.google.common.collect.Range;

public class CompositeTrackStore<V> implements TrackStore<V> {
	
	final Collection<? extends TrackStore<V>> stores;

	public CompositeTrackStore(Collection<? extends TrackStore<V>> stores) {
		super();
		this.stores = stores;
	}

	@Override
	public Stream<TrackElement> queryTrack(V vessel, Range<Date> period) {
		return 
				stores.stream()
				.flatMap(s -> s.queryTrack(vessel, period));
	}

	public Collection<? extends TrackStore<V>> getStores() {
		return stores;
	}

}
