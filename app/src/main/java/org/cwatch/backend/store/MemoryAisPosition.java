package org.cwatch.backend.store;

import java.util.Date;

import org.cwatch.backend.message.AisPosition;

public class MemoryAisPosition extends DefaultTypedPosition<Integer> implements AisPosition {

	public MemoryAisPosition(Integer id, Date timestamp, double latitude,
			double longitude, double heading) {
		super(id, timestamp, latitude, longitude, heading);
	}


}
