package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import com.google.common.collect.Range;

public interface DefaultIdentityStore<V, I> {
	
	void set(V vessel, Range<Date> period, I identifier);
	
	I getIdentifier(V vessel, Date timestamp);
	
	V getVessel(I identifier, Date timestamp);

	V getLatestVessel(I identifier);
	
	//V queryVessel(I id, Date when);
	
	Stream<IdentityPeriod<I>> queryIdentityfiers(V vessel, Range<Date> period);

}
