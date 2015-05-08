package org.cwatch.backend.store;

public class DefaultIdentityStores<V> implements IdentityStores<V> {

	private final DefaultIdentityStore<V, Integer> mmsi;
	private final DefaultIdentityStore<V, Integer> imo;
	private final DefaultIdentityStore<V, String> ir;
	
	public DefaultIdentityStores(DefaultIdentityStore<V, Integer> mmsi,
			DefaultIdentityStore<V, Integer> imo,
			DefaultIdentityStore<V, String> ir) {
		super();
		this.mmsi = mmsi;
		this.imo = imo;
		this.ir = ir;
	}

	@Override
	public DefaultIdentityStore<V, Integer> mmsi() {
		return mmsi;
	}

	@Override
	public DefaultIdentityStore<V, Integer> imo() {
		return imo;
	}

	@Override
	public DefaultIdentityStore<V, String> ir() {
		return ir;
	}

}
