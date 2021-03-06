package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import org.apache.camel.Handler;
import org.cwatch.backend.message.TypedPosition;

import com.google.common.collect.Range;

public interface DefaultPositionStore<I, P extends TypedPosition<I>> {

	@Handler
	void save(P position);
	
	void pump(Stream<P> positions);
	
	Stream<P> queryVessel(I id, Range<Date> period);
	
	Stream<P> queryArea(Range<Date> period, double latitudeFrom, double latitudeTo, double longitudeFrom, double longitudeTo);
	
	/**
	 * 
	 * @param from
	 * @return decreasing by timestamp. 
	 */
	Stream<P> queryLatest(Date from);
	
	void flush();
	
}
