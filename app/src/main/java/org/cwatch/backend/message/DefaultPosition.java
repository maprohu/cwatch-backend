package org.cwatch.backend.message;

import java.util.Date;

public interface DefaultPosition {

	public abstract Date getTimestamp();

	public abstract double getLatitude();

	public abstract double getLongitude();

}