package org.cwatch.backend.process;

import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public class MemoryLatestPositionProfile<V> implements LatestPositionProfile<V> {

	final Collection<LatestPositionView<V>> views = Collections.newSetFromMap(new WeakHashMap<>());
	
	
	final private EnrichedPositionFilter<V> positionFilter;
	

	public MemoryLatestPositionProfile(EnrichedPositionFilter<V> positionFilter) {
		this.positionFilter = positionFilter;
	}
	

	@Override
	public void register(LatestPositionView<V> view) {
		views.add(view);
	}

	@Override
	public void processDefault(EnrichedPosition<V, ? extends DefaultPosition> position) {
		// TODO Auto-generated method stub
	}

	@Override
	public void processAis(EnrichedPosition<V, AisPosition> position) {
		if (positionFilter.testAis(position)) {
			views.stream().forEach(v -> v.processAis(position));
		}
	}
	
	@Override
	public void processLrit(EnrichedPosition<V, LritPosition> position) {
		if (positionFilter.testLrit(position)) {
			views.stream().forEach(v -> v.processLrit(position));
		}
	}

	@Override
	public void processVms(EnrichedPosition<V, VmsPosition> position) {
		if (positionFilter.testVms(position)) {
			views.stream().forEach(v -> v.processVms(position));
		}
	}
	

}
