package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import com.google.common.collect.Range;

public class CompositeTrackStore<V> implements TrackStore<V> {
	
	final Stream<TrackStore<V>> stores;

	public CompositeTrackStore(Stream<TrackStore<V>> stores) {
		super();
		this.stores = stores;
	}

	@Override
	public Stream<TrackElement> queryTrack(V vessel, Range<Date> period) {
		return 
				stores
				.flatMap(s -> s.queryTrack(vessel, period));
	}

}
