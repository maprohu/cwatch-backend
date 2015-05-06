package org.cwatch.backend.store;

import java.util.Date;

import org.cwatch.backend.message.LritPosition;

public class MemoryLritPosition extends DefaultTypedPosition<Integer> implements LritPosition {

	public MemoryLritPosition(Integer id, Date timestamp, double latitude,
			double longitude) {
		super(id, timestamp, latitude, longitude);
	}

}
