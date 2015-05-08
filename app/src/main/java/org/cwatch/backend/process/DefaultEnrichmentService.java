package org.cwatch.backend.process;

import java.util.function.Supplier;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.IdentityStores;

import com.google.common.base.Suppliers;

public class DefaultEnrichmentService<V> implements EnrichmentService<V> {

	private final IdentityStores<V> identityStores;
	
	public DefaultEnrichmentService(IdentityStores<V> identityStores) {
		super();
		this.identityStores = identityStores;
	}
	
	private <P extends DefaultPosition> EnrichedPosition<V, P> enrich(
			P position,
			Supplier<V> identity
	) {
		com.google.common.base.Supplier<V> identityCache = Suppliers.memoize(identity::get);
		
		return new EnrichedPosition<V, P>() {

			@Override
			public P getPosition() {
				return position;
			}

			@Override
			public V getIdentity() {
				return identityCache.get();
			}
		};
	}
	
	@Override
	public EnrichedPosition<V, DefaultPosition> enrichDefault(
			DefaultPosition position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnrichedPosition<V, AisPosition> enrichAis(AisPosition position) {
		return enrich(position, () -> identityStores.mmsi().getLatestVessel(position.getId()));
	}

	@Override
	public EnrichedPosition<V, LritPosition> enrichLrit(LritPosition position) {
		return enrich(position, () -> identityStores.imo().getLatestVessel(position.getId()));
	}

	@Override
	public EnrichedPosition<V, VmsPosition> enrichVms(VmsPosition position) {
		return enrich(position, () -> identityStores.ir().getLatestVessel(position.getId()));
	}

}
