package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

public class CompositeTrackStore<V> implements TrackStore<V> {
	
	final Stream<TrackStore<V>> stores;

	public CompositeTrackStore(Stream<TrackStore<V>> stores) {
		super();
		this.stores = stores;
	}

	@Override
	public Stream<TrackElement> queryTrack(V vessel, Date from, Date to) {
		return 
				stores
				.flatMap(s -> queryTrack(vessel, from, to));
	}

}
