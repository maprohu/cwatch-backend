package org.cwatch.backend.process;

public interface LatestPositionView<V> extends EnrichedPositionProcessor<V> {
	
	int getViewId();

}
