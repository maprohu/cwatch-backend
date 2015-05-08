package org.cwatch.backend.process;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public interface EnrichedPositionProcessor<V> {
	
	void processDefault(EnrichedPosition<V, ? extends DefaultPosition> position);
	void processAis(EnrichedPosition<V, AisPosition> position);
	void processLrit(EnrichedPosition<V, LritPosition> position);
	void processVms(EnrichedPosition<V, VmsPosition> position);

}
