package org.cwatch.backend.process;

public interface Profile<V> {
	
	EnrichedPositionFilter<V> getPositionFilter();

}
