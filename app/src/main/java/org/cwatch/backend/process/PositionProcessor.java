package org.cwatch.backend.process;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public interface PositionProcessor {
	
	void processDefault(DefaultPosition position);
	void processAis(AisPosition position);
	void processLrit(LritPosition position);
	void processVms(VmsPosition position);

}
