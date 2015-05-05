package org.cwatch.backend.store;

import java.util.Date;

import org.cwatch.backend.message.TypedPosition;

public class DefaultTypedPosition<I> implements TypedPosition<I> {

	final I id;
	
	final Date timestamp;
	
	final double latitude;
	
	final double longitude;
	
	public DefaultTypedPosition(I id, Date timestamp, double latitude,
			double longitude) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public I getId() {
		return id;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

}
