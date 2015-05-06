package org.cwatch.backend.store;

import java.util.Date;

import org.cwatch.backend.message.VmsPosition;

public class MemoryVmsPosition extends DefaultTypedPosition<String> implements VmsPosition {

	public MemoryVmsPosition(String id, Date timestamp, double latitude,
			double longitude) {
		super(id, timestamp, latitude, longitude);
	}

}
