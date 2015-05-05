package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import org.cwatch.backend.message.TypedPosition;

public interface DefaultPositionStore<I, P extends TypedPosition<I>> {

	void save(P position);
	
	Stream<P> queryVessel(I id, Date from, Date to);
	
	Stream<P> queryArea(Date from, Date to, double latitudeFrom, double latitudeTo, double longitudeFrom, double longitudeTo);
	
}
