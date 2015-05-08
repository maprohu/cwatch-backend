package org.cwatch.backend.process;


public interface LatestPositionRegistry<V> {
	
	LatestPositionView<V> register(String profileName);

}
