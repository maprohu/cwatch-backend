package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

public interface DefaultIdentityStore<V, I> {
	
	//V queryVessel(I id, Date when);
	
	Stream<IdentityPeriod<I>> queryIdentityfiers(V vessel, Date from, Date to);

}
