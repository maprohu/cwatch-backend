package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import com.google.common.collect.Range;

public interface TrackStore<V> {
	
	Stream<TrackElement> queryTrack(V vessel, Range<Date> period);

}
