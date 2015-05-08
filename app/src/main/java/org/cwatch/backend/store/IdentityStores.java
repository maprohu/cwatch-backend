package org.cwatch.backend.store;

public interface IdentityStores<V> {
	
	DefaultIdentityStore<V, Integer> mmsi();
	DefaultIdentityStore<V, Integer> imo();
	DefaultIdentityStore<V, String> ir();

}
