package org.cwatch.backend.store;

import java.io.Serializable;
import java.util.Date;

import org.cwatch.backend.message.TypedPosition;

@SuppressWarnings("serial")
public class DefaultTypedPosition<I> implements TypedPosition<I>, Serializable {

	final I id;
	
	final Date timestamp;
	
	final double latitude;
	
	final double longitude;
	
	final double heading;
	
	public DefaultTypedPosition(I id, Date timestamp, double latitude,
			double longitude, double heading) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.heading = heading;
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

	@Override
	public double getHeading() {
		return heading;
	}

}
