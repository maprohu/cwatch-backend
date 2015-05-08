package org.cwatch.backend.process;

import org.cwatch.backend.message.DefaultPosition;

public interface EnrichedPosition<V, P extends DefaultPosition> {
	
	P getPosition();
	
	V getIdentity();

}
