package org.cwatch.backend.message;

import java.util.Date;

public interface DefaultPosition {

	Date getTimestamp();

	double getLatitude();

	double getLongitude();
	
}