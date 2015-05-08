package org.cwatch.backend.process;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public interface EnrichmentService<V> {

	EnrichedPosition<V, DefaultPosition> enrichDefault(DefaultPosition position);
	EnrichedPosition<V, AisPosition> enrichAis(AisPosition position);
	EnrichedPosition<V, LritPosition> enrichLrit(LritPosition position);
	EnrichedPosition<V, VmsPosition> enrichVms(VmsPosition position);
	
}
