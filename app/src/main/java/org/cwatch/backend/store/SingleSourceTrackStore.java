package org.cwatch.backend.store;

import java.util.Date;
import java.util.stream.Stream;

import org.cwatch.backend.message.TypedPosition;

import com.google.common.collect.Range;

public class SingleSourceTrackStore<V, I, P extends TypedPosition<I>> implements TrackStore<V> {

	final DefaultIdentityStore<V, I> identityStore;
	
	final DefaultPositionStore<I, P> positionStore;
	
	public SingleSourceTrackStore(DefaultIdentityStore<V, I> identityStore,
			DefaultPositionStore<I, P> positionStore) {
		super();
		this.identityStore = identityStore;
		this.positionStore = positionStore;
	}

	@Override
	public Stream<TrackElement> queryTrack(V vessel, Range<Date> period) {
		return 
				identityStore
				.queryIdentityfiers(vessel, period)
				.flatMap(
						(IdentityPeriod<I> ip) -> 
						positionStore
						.queryVessel(ip.getId(), ip.getPeriod())
						.map((P p) -> createElement(p))
				);
	}
	
	protected TrackElement createElement(P position) {
		return new DelegatingTrackElement<>(position);
	}


}
