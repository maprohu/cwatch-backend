package org.cwatch.backend.process;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public interface EnrichedPositionFilter<V> {

	boolean testDefault(EnrichedPosition<V, DefaultPosition> position);
	boolean testAis(EnrichedPosition<V, AisPosition> position);
	boolean testLrit(EnrichedPosition<V, LritPosition> position);
	boolean testVms(EnrichedPosition<V, VmsPosition> position);

	
	@SuppressWarnings("rawtypes")
	EnrichedPositionFilter ALL = new EnrichedPositionFilter() {

		@Override
		public boolean testDefault(EnrichedPosition position) {
			return true;
		}

		@Override
		public boolean testAis(EnrichedPosition position) {
			return true;
		}

		@Override
		public boolean testLrit(EnrichedPosition position) {
			return true;
		}

		@Override
		public boolean testVms(EnrichedPosition position) {
			return true;
		}
	};
	
	@SuppressWarnings("unchecked")
	static <V> EnrichedPositionFilter<V> all() {
		return ALL;
	}
}
