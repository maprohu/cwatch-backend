package org.cwatch.backend.process;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public interface PositionFilter {
	
	boolean testDefault(DefaultPosition position);
	boolean testAis(AisPosition position);
	boolean testLrit(LritPosition position);
	boolean testVms(VmsPosition position);

}
