package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

public interface TrackStore<V> {
	
	Stream<TrackElement> queryTrack(V vessel, Date from, Date to);

}
