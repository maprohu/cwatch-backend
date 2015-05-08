package org.cwatch.backend.process;

public interface LatestPositionProfile<V> extends EnrichedPositionProcessor<V> {
	
	void register(LatestPositionView<V> view);

}
