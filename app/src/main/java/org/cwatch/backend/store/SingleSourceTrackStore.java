package org.cwatch.backend.store;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.cwatch.backend.message.TypedPosition;

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
	public Stream<TrackElement> queryTrack(V vessel, Date from, Date to) {
//		Stream<IdentityPeriod<I>> identifiers = identityStore.queryIdentityfiers(vessel, from, to);
//		Stream<Stream<P>> positions = identifiers.map(ip -> positionStore.queryVessel(ip.getId(), ip.getFrom(), ip.getTo()));
//		return positions.flatMap(Function.identity()).map(p -> createElement(p));
		
		return 
				identityStore
				.queryIdentityfiers(vessel, from, to)
				.map(ip -> positionStore.queryVessel(ip.getId(), ip.getFrom(), ip.getTo()))
				.flatMap(Function.identity()).map(p -> createElement(p));
			
	}
	
	protected TrackElement createElement(P position) {
		return new DelegatingTrackElement<>(position);
	}


}
