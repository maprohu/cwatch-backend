package org.cwatch.backend.store;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.cwatch.backend.message.TypedPosition;

import com.google.common.collect.Range;

public class MemoryPositionStore<I, P extends TypedPosition<I>> implements DefaultPositionStore<I, P> {

	final Collection<P> items = new ConcurrentLinkedQueue<>();
	
	@Override
	public void save(P position) {
		items.add(position);
	}

	@Override
	public Stream<P> queryVessel(I id, Range<Date> period) {
		return items.stream().filter(p -> p.getId().equals(id) && period.contains(p.getTimestamp()));
	}

	@Override
	public Stream<P> queryArea(Range<Date> period, double latitudeFrom,
			double latitudeTo, double longitudeFrom, double longitudeTo) {
		return items.stream().filter(p -> 
			period.contains(p.getTimestamp()) &&
			latitudeFrom <= p.getLatitude() &&
			p.getLatitude() <= latitudeTo &&
			longitudeFrom <= p.getLongitude() &&
			p.getLongitude() <= longitudeTo
		);
	}
	
	public void clear() {
		items.clear();
	}
	

}
